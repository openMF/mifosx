/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.commands.service;

import java.util.Map;

import org.joda.time.DateTime;
import org.mifosplatform.commands.domain.CommandSource;
import org.mifosplatform.commands.domain.CommandSourceRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.exception.RollbackTransactionAsCommandIsNotApprovedByCheckerException;
import org.mifosplatform.commands.exception.UnsupportedCommandException;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.commands.provider.CommandHandlerProvider;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationDomainService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.hooks.event.HookEvent;
import org.mifosplatform.infrastructure.hooks.event.HookEventSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SynchronousCommandProcessingService implements CommandProcessingService {

    private PlatformSecurityContext context;
    private final ApplicationContext applicationContext;
    private final ToApiJsonSerializer<Map<String, Object>> toApiJsonSerializer;
    private final ToApiJsonSerializer<CommandProcessingResult> toApiResultJsonSerializer;
    private CommandSourceRepository commandSourceRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final CommandHandlerProvider commandHandlerProvider;

    @Autowired
    public SynchronousCommandProcessingService(final PlatformSecurityContext context, final ApplicationContext applicationContext,
            final ToApiJsonSerializer<Map<String, Object>> toApiJsonSerializer,
            final ToApiJsonSerializer<CommandProcessingResult> toApiResultJsonSerializer,
            final CommandSourceRepository commandSourceRepository, final ConfigurationDomainService configurationDomainService,
            final CommandHandlerProvider commandHandlerProvider) {
        this.context = context;
        this.context = context;
        this.applicationContext = applicationContext;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.toApiResultJsonSerializer = toApiResultJsonSerializer;
        this.commandSourceRepository = commandSourceRepository;
        this.commandSourceRepository = commandSourceRepository;
        this.configurationDomainService = configurationDomainService;
        this.commandHandlerProvider = commandHandlerProvider;
    }

    @Transactional
    @Override
    public CommandProcessingResult processAndLogCommand(final CommandWrapper wrapper, final JsonCommand command,
            final boolean isApprovedByChecker) {

        final boolean rollbackTransaction = this.configurationDomainService.isMakerCheckerEnabledForTask(wrapper.taskPermissionName());

        final NewCommandSourceHandler handler = findCommandHandler(wrapper);

        final CommandProcessingResult result = handler.processCommand(command);

        final AppUser maker = this.context.authenticatedUser(wrapper);

        CommandSource commandSourceResult = null;
        if (command.commandId() != null) {
            commandSourceResult = this.commandSourceRepository.findOne(command.commandId());
            commandSourceResult.markAsChecked(maker, DateTime.now());
        } else {
            commandSourceResult = CommandSource.fullEntryFrom(wrapper, command, maker);
        }
        commandSourceResult.updateResourceId(result.resourceId());
        commandSourceResult.updateForAudit(result.getOfficeId(), result.getGroupId(), result.getClientId(), result.getLoanId(),
                result.getSavingsId(), result.getProductId(), result.getTransactionId());

        String changesOnlyJson = null;
        if (result.hasChanges()) {
            changesOnlyJson = this.toApiJsonSerializer.serializeResult(result.getChanges());
            commandSourceResult.updateJsonTo(changesOnlyJson);
        }

        if (!result.hasChanges() && wrapper.isUpdateOperation() && !wrapper.isUpdateDatatable()) {
            commandSourceResult.updateJsonTo(null);
        }

        if (commandSourceResult.hasJson()) {
            this.commandSourceRepository.save(commandSourceResult);
        }

        if ((rollbackTransaction || result.isRollbackTransaction()) && !isApprovedByChecker) {
            /*
             * JournalEntry will generate a new transactionId every time.
             * Updating the transactionId with old transactionId, because as
             * there are no entries are created with new transactionId, will
             * throw an error when checker approves the transaction
             */
            commandSourceResult.updateTransaction(command.getTransactionId());
            /*
             * Update CommandSource json data with JsonCommand json data, line
             * 77 and 81 may update the json data
             */
            commandSourceResult.updateJsonTo(command.json());
            throw new RollbackTransactionAsCommandIsNotApprovedByCheckerException(commandSourceResult);
        }
        result.setRollbackTransaction(null);

        publishEvent(wrapper.entityName(), wrapper.actionName(), result);

        return result;
    }

    @Transactional
    @Override
    public CommandProcessingResult logCommand(CommandSource commandSourceResult) {

        commandSourceResult.markAsAwaitingApproval();
        commandSourceResult = this.commandSourceRepository.save(commandSourceResult);

        return new CommandProcessingResultBuilder().withCommandId(commandSourceResult.getId())
                .withEntityId(commandSourceResult.getResourceId()).build();
    }

    private NewCommandSourceHandler findCommandHandler(final CommandWrapper wrapper) {
        NewCommandSourceHandler handler = null;

        if (wrapper.isDatatableResource()) {
            if (wrapper.isCreateDatatable()) {
                handler = this.applicationContext.getBean("createDatatableCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDeleteDatatable()) {
                handler = this.applicationContext.getBean("deleteDatatableCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdateDatatable()) {
                handler = this.applicationContext.getBean("updateDatatableCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isCreate()) {
                handler = this.applicationContext.getBean("createDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdateMultiple()) {
                handler = this.applicationContext.getBean("updateOneToManyDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdateOneToOne()) {
                handler = this.applicationContext.getBean("updateOneToOneDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDeleteMultiple()) {
                handler = this.applicationContext.getBean("deleteOneToManyDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDeleteOneToOne()) {
                handler = this.applicationContext.getBean("deleteOneToOneDatatableEntryCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isRegisterDatatable()) {
                handler = this.applicationContext.getBean("registerDatatableCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isNoteResource()) {
            if (wrapper.isCreate()) {
                handler = this.applicationContext.getBean("createNoteCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isUpdate()) {
                handler = this.applicationContext.getBean("updateNoteCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isDelete()) {
                handler = this.applicationContext.getBean("deleteNoteCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isSurveyResource()) {
            if (wrapper.isRegisterSurvey()) {
                handler = this.applicationContext.getBean("registerSurveyCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.isFullFilSurvey()) {
                handler = this.applicationContext.getBean("fullFilSurveyCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else if (wrapper.isLoanDisburseDetailResource()) {
            if (wrapper.isUpdateDisbursementDate()) {
                handler = this.applicationContext.getBean("updateLoanDisbuseDateCommandHandler", NewCommandSourceHandler.class);
            } else if (wrapper.addAndDeleteDisbursementDetails()) {
                handler = this.applicationContext.getBean("addAndDeleteLoanDisburseDetailsCommandHandler", NewCommandSourceHandler.class);
            } else {
                throw new UnsupportedCommandException(wrapper.commandName());
            }
        } else {
            handler = this.commandHandlerProvider.getHandler(wrapper.entityName(), wrapper.actionName());
        }

        return handler;
    }

    @Override
    public boolean validateCommand(final CommandWrapper commandWrapper, final AppUser user) {
        boolean rollbackTransaction = this.configurationDomainService.isMakerCheckerEnabledForTask(commandWrapper.taskPermissionName());
        user.validateHasPermissionTo(commandWrapper.getTaskPermissionName());
        return rollbackTransaction;
    }

    private void publishEvent(final String entityName, final String actionName, final CommandProcessingResult result) {

        final String authToken = ThreadLocalContextUtil.getAuthToken();
        final String tenantIdentifier = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        final AppUser appUser = this.context.authenticatedUser();

        final HookEventSource hookEventSource = new HookEventSource(entityName, actionName);

        final String serializedResult = this.toApiResultJsonSerializer.serialize(result);

        final HookEvent applicationEvent = new HookEvent(hookEventSource, serializedResult, tenantIdentifier, appUser, authToken);

        applicationContext.publishEvent(applicationEvent);
    }
}
