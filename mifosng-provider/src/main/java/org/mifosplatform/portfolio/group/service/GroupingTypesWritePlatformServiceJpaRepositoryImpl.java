/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.group.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.InvalidOfficeException;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.organisation.staff.domain.StaffRepositoryWrapper;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.group.api.GroupingTypesApiConstants;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.group.domain.GroupLevel;
import org.mifosplatform.portfolio.group.domain.GroupLevelRepository;
import org.mifosplatform.portfolio.group.domain.GroupRepositoryWrapper;
import org.mifosplatform.portfolio.group.domain.GroupTypes;
import org.mifosplatform.portfolio.group.exception.GroupHasNoStaffException;
import org.mifosplatform.portfolio.group.exception.GroupLoanExistsException;
import org.mifosplatform.portfolio.group.exception.GroupMustBePendingToBeDeletedException;
import org.mifosplatform.portfolio.group.exception.InvalidGroupLevelException;
import org.mifosplatform.portfolio.group.serialization.GroupingTypesDataValidator;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepository;
import org.mifosplatform.portfolio.note.domain.Note;
import org.mifosplatform.portfolio.note.domain.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
public class GroupingTypesWritePlatformServiceJpaRepositoryImpl implements GroupingTypesWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(GroupingTypesWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final GroupRepositoryWrapper groupRepository;
    private final ClientRepositoryWrapper clientRepository;
    private final OfficeRepository officeRepository;
    private final StaffRepositoryWrapper staffRepository;
    private final NoteRepository noteRepository;
    private final GroupLevelRepository groupLevelRepository;
    private final GroupingTypesDataValidator fromApiJsonDeserializer;
    private final LoanRepository loanRepository;

    @Autowired
    public GroupingTypesWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final GroupRepositoryWrapper groupRepository, final ClientRepositoryWrapper clientRepository,
            final OfficeRepository officeRepository, final StaffRepositoryWrapper staffRepository, final NoteRepository noteRepository,
            final GroupLevelRepository groupLevelRepository, final GroupingTypesDataValidator fromApiJsonDeserializer,
            final LoanRepository loanRepository) {
        this.context = context;
        this.groupRepository = groupRepository;
        this.clientRepository = clientRepository;
        this.officeRepository = officeRepository;
        this.staffRepository = staffRepository;
        this.noteRepository = noteRepository;
        this.groupLevelRepository = groupLevelRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.loanRepository = loanRepository;
    }

    private CommandProcessingResult createGroupingType(final JsonCommand command, final GroupTypes groupingType, final Long centerId) {
        try {
            final String name = command.stringValueOfParameterNamed(GroupingTypesApiConstants.nameParamName);
            final String externalId = command.stringValueOfParameterNamed(GroupingTypesApiConstants.externalIdParamName);

            Long officeId = null;
            Group parentGroup = null;

            if (centerId == null) {
                officeId = command.longValueOfParameterNamed(GroupingTypesApiConstants.officeIdParamName);
            } else {
                parentGroup = this.groupRepository.findOneWithNotFoundDetection(centerId);
                officeId = parentGroup.officeId();
            }

            final Office groupOffice = this.officeRepository.findOne(officeId);
            if (groupOffice == null) { throw new OfficeNotFoundException(officeId); }

            Staff staff = null;
            final Long staffId = command.longValueOfParameterNamed(GroupingTypesApiConstants.staffIdParamName);
            if (staffId != null) {
                staff = this.staffRepository.findByOfficeWithNotFoundDetection(staffId, officeId);
            }

            final Set<Client> clientMembers = assembleSetOfClients(officeId, command);

            final Set<Group> groupMembers = assembleSetOfChildGroups(officeId, command);

            final boolean active = command.booleanPrimitiveValueOfParameterNamed(GroupingTypesApiConstants.activeParamName);
            final LocalDate activationDate = command.localDateValueOfParameterNamed(GroupingTypesApiConstants.activationDateParamName);
            final GroupLevel groupLevel = this.groupLevelRepository.findOne(groupingType.getId());
            final Group newGroup = Group.newGroup(groupOffice, staff, parentGroup, groupLevel, name, externalId, active, activationDate,
                    clientMembers, groupMembers);

            // pre-save to generate id for use in group hierarchy
            this.groupRepository.save(newGroup);

            newGroup.generateHierarchy();

            this.groupRepository.saveAndFlush(newGroup);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(groupOffice.getId()) //
                    .withGroupId(newGroup.getId()) //
                    .withEntityId(newGroup.getId()) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleGroupDataIntegrityIssues(command, dve, groupingType);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult createCenter(final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForCreateCenter(command);

        final Long centerId = null;
        return createGroupingType(command, GroupTypes.CENTER, centerId);
    }

    @Transactional
    @Override
    public CommandProcessingResult createGroup(final Long centerId, final JsonCommand command) {

        if (centerId != null) {
            this.fromApiJsonDeserializer.validateForCreateCenterGroup(command);
        } else {
            this.fromApiJsonDeserializer.validateForCreateGroup(command);
        }

        return createGroupingType(command, GroupTypes.GROUP, centerId);
    }

    @Transactional
    @Override
    public CommandProcessingResult activateGroupOrCenter(final Long groupId, final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForActivation(command, GroupingTypesApiConstants.GROUP_RESOURCE_NAME);

            final Group group = this.groupRepository.findOneWithNotFoundDetection(groupId);

            final Locale locale = command.extractLocale();
            final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);
            final LocalDate activationDate = command.localDateValueOfParameterNamed("activationDate");

            group.activate(fmt, activationDate);

            this.groupRepository.saveAndFlush(group);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(group.officeId()) //
                    .withGroupId(groupId) //
                    .withEntityId(groupId) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleGroupDataIntegrityIssues(command, dve, GroupTypes.GROUP);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateCenter(final Long centerId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForUpdateCenter(command);

        return updateGroupingType(centerId, command, GroupTypes.CENTER);
    }

    @Transactional
    @Override
    public CommandProcessingResult updateGroup(final Long groupId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForUpdateGroup(command);

        return updateGroupingType(groupId, command, GroupTypes.GROUP);
    }

    private CommandProcessingResult updateGroupingType(final Long groupId, final JsonCommand command, final GroupTypes groupingType) {

        try {
            this.context.authenticatedUser();
            final Group groupForUpdate = this.groupRepository.findOneWithNotFoundDetection(groupId);
            final Long officeId = groupForUpdate.officeId();
            final Office groupOffice = groupForUpdate.getOffice();
            final String groupHierarchy = groupOffice.getHierarchy();

            this.context.validateAccessRights(groupHierarchy);

            final Map<String, Object> actualChanges = groupForUpdate.update(command);

            if (actualChanges.containsKey(GroupingTypesApiConstants.staffIdParamName)) {
                final Long newValue = command.longValueOfParameterNamed(GroupingTypesApiConstants.staffIdParamName);

                Staff newStaff = null;
                if (newValue != null) {
                    newStaff = this.staffRepository.findByOfficeWithNotFoundDetection(newValue, officeId);
                }
                groupForUpdate.updateStaff(newStaff);
            }

            final GroupLevel groupLevel = this.groupLevelRepository.findOne(groupForUpdate.getGroupLevel().getId());

            /*
             * Ignoring parentId param, if group for update is super parent.
             * TODO Need to check: Ignoring is correct or need throw unsupported
             * param
             */
            if (!groupLevel.isSuperParent()) {

                Long parentId = null;
                final Group presentParentGroup = groupForUpdate.getParent();

                if (presentParentGroup != null) {
                    parentId = presentParentGroup.getId();
                }

                if (command.isChangeInLongParameterNamed(GroupingTypesApiConstants.centerIdParamName, parentId)) {

                    final Long newValue = command.longValueOfParameterNamed(GroupingTypesApiConstants.centerIdParamName);
                    actualChanges.put(GroupingTypesApiConstants.centerIdParamName, newValue);
                    Group newParentGroup = null;
                    if (newValue != null) {
                        newParentGroup = this.groupRepository.findOneWithNotFoundDetection(newValue);

                        if (!newParentGroup.isOfficeIdentifiedBy(officeId)) {
                            final String errorMessage = "Group and parent group must have the same office";
                            throw new InvalidOfficeException("group", "attach.to.parent.group", errorMessage);
                        }
                        /*
                         * If Group is not super parent then validate group
                         * level's parent level is same as group parent's level
                         * this check makes sure new group is added at immediate
                         * next level in hierarchy
                         */

                        if (!groupForUpdate.getGroupLevel().isIdentifiedByParentId(newParentGroup.getGroupLevel().getId())) {
                            final String errorMessage = "Parent group's level is  not equal to child level's parent level ";
                            throw new InvalidGroupLevelException("add", "invalid.level", errorMessage);
                        }
                    }

                    groupForUpdate.setParent(newParentGroup);

                    // Parent has changed, re-generate 'Hierarchy' as parent is
                    // changed
                    groupForUpdate.generateHierarchy();

                }
            }

            /*
             * final Set<Client> clientMembers = assembleSetOfClients(officeId,
             * command); List<String> changes =
             * groupForUpdate.updateClientMembersIfDifferent(clientMembers); if
             * (!changes.isEmpty()) {
             * actualChanges.put(GroupingTypesApiConstants
             * .clientMembersParamName, changes); }
             */

            this.groupRepository.saveAndFlush(groupForUpdate);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(groupForUpdate.officeId()) //
                    .withGroupId(groupForUpdate.getId()) //
                    .withEntityId(groupForUpdate.getId()) //
                    .with(actualChanges) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleGroupDataIntegrityIssues(command, dve, groupingType);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult unassignGroupOrCenterStaff(final Long grouptId, final JsonCommand command) {

        this.context.authenticatedUser();

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(9);

        this.fromApiJsonDeserializer.validateForUnassignStaff(command.json());

        final Group groupForUpdate = this.groupRepository.findOneWithNotFoundDetection(grouptId);

        final Staff presentStaff = groupForUpdate.getStaff();
        Long presentStaffId = null;
        if (presentStaff == null) { throw new GroupHasNoStaffException(grouptId); }
        presentStaffId = presentStaff.getId();
        final String staffIdParamName = "staffId";
        if (!command.isChangeInLongParameterNamed(staffIdParamName, presentStaffId)) {
            groupForUpdate.unassignStaff();
        }
        this.groupRepository.saveAndFlush(groupForUpdate);

        actualChanges.put(staffIdParamName, null);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(groupForUpdate.getId()) //
                .withGroupId(groupForUpdate.officeId()) //
                .withEntityId(groupForUpdate.getId()) //
                .with(actualChanges) //
                .build();

    }

    @Transactional
    @Override
    public CommandProcessingResult deleteGroup(final Long groupId) {

        final Group groupForDelete = this.groupRepository.findOneWithNotFoundDetection(groupId);

        if (groupForDelete.isNotPending()) { throw new GroupMustBePendingToBeDeletedException(groupId); }

        final List<Note> relatedNotes = this.noteRepository.findByGroupId(groupId);
        this.noteRepository.deleteInBatch(relatedNotes);

        this.groupRepository.delete(groupForDelete);

        return new CommandProcessingResultBuilder() //
                .withOfficeId(groupForDelete.getId()) //
                .withGroupId(groupForDelete.officeId()) //
                .withEntityId(groupForDelete.getId()) //
                .build();
    }

    private Set<Client> assembleSetOfClients(final Long groupOfficeId, final JsonCommand command) {

        final Set<Client> clientMembers = new HashSet<Client>();
        final String[] clientMembersArray = command.arrayValueOfParameterNamed(GroupingTypesApiConstants.clientMembersParamName);

        if (!ObjectUtils.isEmpty(clientMembersArray)) {
            for (final String clientId : clientMembersArray) {
                final Long id = Long.valueOf(clientId);
                final Client client = this.clientRepository.findOneWithNotFoundDetection(id);
                if (!client.isOfficeIdentifiedBy(groupOfficeId)) {
                    final String errorMessage = "Client with identifier " + clientId + " must have the same office as group.";
                    throw new InvalidOfficeException("client", "attach.to.group", errorMessage, clientId, groupOfficeId);
                }
                clientMembers.add(client);
            }
        }

        return clientMembers;
    }

    private Set<Group> assembleSetOfChildGroups(final Long officeId, final JsonCommand command) {

        final Set<Group> childGroups = new HashSet<Group>();
        final String[] childGroupsArray = command.arrayValueOfParameterNamed(GroupingTypesApiConstants.groupMembersParamName);

        if (!ObjectUtils.isEmpty(childGroupsArray)) {
            for (final String groupId : childGroupsArray) {
                final Long id = Long.valueOf(groupId);
                final Group group = this.groupRepository.findOneWithNotFoundDetection(id);

                if (!group.isOfficeIdentifiedBy(officeId)) {
                    final String errorMessage = "Group and child groups must have the same office.";
                    throw new InvalidOfficeException("group", "attach.to.parent.group", errorMessage);
                }
                childGroups.add(group);
            }
        }

        return childGroups;
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleGroupDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve,
            final GroupTypes groupLevel) {

        String levelName = "Invalid";
        switch (groupLevel) {
            case CENTER:
                levelName = "Center";
            break;
            case GROUP:
                levelName = "Group";
            break;
            case INVALID:
            break;
        }

        final Throwable realCause = dve.getMostSpecificCause();
        String errorMessageForUser = null;
        String errorMessageForMachine = null;

        if (realCause.getMessage().contains("external_id")) {

            final String externalId = command.stringValueOfParameterNamed(GroupingTypesApiConstants.externalIdParamName);
            errorMessageForUser = levelName + " with externalId `" + externalId + "` already exists.";
            errorMessageForMachine = "error.msg." + levelName.toLowerCase() + ".duplicate.externalId";
            throw new PlatformDataIntegrityException(errorMessageForMachine, errorMessageForUser,
                    GroupingTypesApiConstants.externalIdParamName, externalId);
        } else if (realCause.getMessage().contains("name")) {

            final String name = command.stringValueOfParameterNamed(GroupingTypesApiConstants.nameParamName);
            errorMessageForUser = levelName + " with name `" + name + "` already exists.";
            errorMessageForMachine = "error.msg." + levelName.toLowerCase() + ".duplicate.name";
            throw new PlatformDataIntegrityException(errorMessageForMachine, errorMessageForUser, GroupingTypesApiConstants.nameParamName,
                    name);
        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.group.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    @Override
    public CommandProcessingResult associateClientsToGroup(final Long groupId, final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForAssociateClients(command.json());

        final Group groupForUpdate = this.groupRepository.findOneWithNotFoundDetection(groupId);
        final Set<Client> clientMembers = assembleSetOfClients(groupForUpdate.officeId(), command);
        final Map<String, Object> actualChanges = new HashMap<String, Object>();

        final List<String> changes = groupForUpdate.associateClients(clientMembers);
        if (!changes.isEmpty()) {
            actualChanges.put(GroupingTypesApiConstants.clientMembersParamName, changes);
        }

        this.groupRepository.saveAndFlush(groupForUpdate);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(groupForUpdate.officeId()) //
                .withGroupId(groupForUpdate.getId()) //
                .withEntityId(groupForUpdate.getId()) //
                .with(actualChanges) //
                .build();
    }

    @Transactional
    @Override
    public CommandProcessingResult disassociateClientsFromGroup(final Long groupId, final JsonCommand command) {
        this.fromApiJsonDeserializer.validateForDisassociateClients(command.json());

        final Group groupForUpdate = this.groupRepository.findOneWithNotFoundDetection(groupId);
        final Set<Client> clientMembers = assembleSetOfClients(groupForUpdate.officeId(), command);

        // check if any client has got group loans
        validateForJLGLoan(groupForUpdate.getId(), clientMembers);
        final Map<String, Object> actualChanges = new HashMap<String, Object>();

        final List<String> changes = groupForUpdate.disassociateClients(clientMembers);
        if (!changes.isEmpty()) {
            actualChanges.put(GroupingTypesApiConstants.clientMembersParamName, changes);
        }

        this.groupRepository.saveAndFlush(groupForUpdate);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withOfficeId(groupForUpdate.officeId()) //
                .withGroupId(groupForUpdate.getId()) //
                .withEntityId(groupForUpdate.getId()) //
                .with(actualChanges) //
                .build();
    }

    @Transactional
    private void validateForJLGLoan(final Long groupId, final Set<Client> clientMembers) {
        for (final Client client : clientMembers) {
            final Collection<Loan> loans = this.loanRepository.findByClientIdAndGroupId(client.getId(), groupId);
            if (!CollectionUtils.isEmpty(loans)) {
                final String defaultUserMessage = "Client with identifier " + client.getId()
                        + " cannot be disassociated it has group loans.";
                throw new GroupLoanExistsException("disassociate", "client.has.group.loan", defaultUserMessage, client.getId(), groupId);
            }
        }
    }

}