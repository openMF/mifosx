package org.mifosplatform.commands.service;

import org.mifosplatform.commands.domain.CommandSource;
import org.mifosplatform.commands.domain.CommandSourceRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.exception.CommandNotAwaitingApprovalException;
import org.mifosplatform.commands.exception.CommandNotFoundException;
import org.mifosplatform.commands.exception.RollbackTransactionAsCommandIsNotApprovedByCheckerException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;

@Service
public class TenantCommandSourceWritePlaftormServiceImpl implements TenantCommandSourceWritePlatformService {

	private final PlatformSecurityContext context;
	private final CommandSourceRepository commandSourceRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final CommandProcessingService processAndLogCommandService;
	
	@Autowired
	public TenantCommandSourceWritePlaftormServiceImpl(final PlatformSecurityContext context,
			final CommandSourceRepository commandSourceRepository, final FromJsonHelper fromApiJsonHelper,
			final CommandProcessingService processAndLogCommandService) {
		
		this.context = context;
		this.commandSourceRepository = commandSourceRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.processAndLogCommandService = processAndLogCommandService;
	}
	
	@Override
	public CommandProcessingResult logCommandSource(final CommandWrapper wrapper) {

        context.authenticatedUser().validateHasPermissionTo(wrapper.getTaskPermissionName());
        
        final String json = wrapper.getJson();
        CommandProcessingResult result = null;
        try {
            final JsonElement parsedCommand = this.fromApiJsonHelper.parse(json);
            final JsonCommand command = JsonCommand.from(json, parsedCommand, this.fromApiJsonHelper, wrapper.getEntityName(),
                    wrapper.getEntityId(), wrapper.getSubentityId(), wrapper.getGroupId(), wrapper.getClientId(), wrapper.getLoanId(),
                    wrapper.getSavingsId(), wrapper.getCodeId(), wrapper.getSupportedEntityType(), wrapper.getSupportedEntityId(),
                    wrapper.getTransactionId());

            result = this.processAndLogCommandService.processAndLogCommand(wrapper, command, false);
        } catch (RollbackTransactionAsCommandIsNotApprovedByCheckerException e) {

            result = this.processAndLogCommandService.logCommand(e.getCommandSourceResult());
        }

        return result;
	}

	@Override
	public CommandProcessingResult approveEntry(final Long makerCheckerId) {

        CommandSource commandSourceInput = validateMakerCheckerTransaction(makerCheckerId);

        final CommandWrapper wrapper = CommandWrapper.fromExistingCommand(makerCheckerId, commandSourceInput.getActionName(),
                commandSourceInput.getEntityName(), commandSourceInput.resourceId(), commandSourceInput.subresourceId(),
                commandSourceInput.getResourceGetUrl());

        final JsonElement parsedCommand = this.fromApiJsonHelper.parse(commandSourceInput.json());
        final JsonCommand command = JsonCommand.fromExistingCommand(makerCheckerId, commandSourceInput.json(), parsedCommand,
                this.fromApiJsonHelper, commandSourceInput.getEntityName(), commandSourceInput.resourceId(),
                commandSourceInput.subresourceId());

        final boolean makerCheckerApproval = true;
        return this.processAndLogCommandService.processAndLogCommand(wrapper, command, makerCheckerApproval);
    }

	@Transactional
    @Override
    public Long deleteEntry(final Long makerCheckerId) {

        validateMakerCheckerTransaction(makerCheckerId);

        this.commandSourceRepository.delete(makerCheckerId);

        return makerCheckerId;
    }
	
	private CommandSource validateMakerCheckerTransaction(final Long makerCheckerId) {

        final CommandSource commandSourceInput = this.commandSourceRepository.findOne(makerCheckerId);
        if (commandSourceInput == null) { throw new CommandNotFoundException(makerCheckerId); }
        if (!(commandSourceInput.isMarkedAsAwaitingApproval())) { throw new CommandNotAwaitingApprovalException(makerCheckerId); }

        context.authenticatedUser().validateHasCheckerPermissionTo(commandSourceInput.getPermissionCode());

        return commandSourceInput;
    }
	
}
