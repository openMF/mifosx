/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.note.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.group.domain.GroupRepository;
import org.mifosplatform.portfolio.group.exception.GroupNotFoundException;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepository;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransaction;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.mifosplatform.portfolio.loanaccount.exception.LoanNotFoundException;
import org.mifosplatform.portfolio.loanaccount.exception.LoanTransactionNotFoundException;
import org.mifosplatform.portfolio.note.domain.Note;
import org.mifosplatform.portfolio.note.domain.NoteRepository;
import org.mifosplatform.portfolio.note.domain.NoteType;
import org.mifosplatform.portfolio.note.exception.NoteNotFoundException;
import org.mifosplatform.portfolio.note.exception.NoteResourceNotSupportedFoundException;
import org.mifosplatform.portfolio.note.serialization.NoteCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.savingsaccount.domain.SavingAccount;
import org.mifosplatform.portfolio.savingsaccount.domain.SavingAccountRepository;
import org.mifosplatform.portfolio.savingsaccount.exception.SavingAccountNotFoundException;
import org.mifosplatform.portfolio.savingsdepositaccount.domain.DepositAccount;
import org.mifosplatform.portfolio.savingsdepositaccount.domain.DepositAccountRepository;
import org.mifosplatform.portfolio.savingsdepositaccount.exception.DepositAccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteWritePlatformServiceJpaRepositoryImpl implements NoteWritePlatformService {

    private final NoteRepository noteRepository;
    private final ClientRepository clientRepository;
    private final GroupRepository groupRepository;
    private final DepositAccountRepository depositAccountRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final LoanRepository loanRepository;
    private final LoanTransactionRepository loanTransactionRepository;
    private final NoteCommandFromApiJsonDeserializer fromApiJsonDeserializer;

    @Autowired
    public NoteWritePlatformServiceJpaRepositoryImpl(final NoteRepository noteRepository, final ClientRepository clientRepository,
            final GroupRepository groupRepository, final DepositAccountRepository depositAccountRepository,
            final SavingAccountRepository savingAccountRepository, final LoanRepository loanRepository,
            final LoanTransactionRepository loanTransactionRepository, final NoteCommandFromApiJsonDeserializer fromApiJsonDeserializer) {
        this.noteRepository = noteRepository;
        this.clientRepository = clientRepository;
        this.groupRepository = groupRepository;
        this.depositAccountRepository = depositAccountRepository;
        this.savingAccountRepository = savingAccountRepository;
        this.loanRepository = loanRepository;
        this.loanTransactionRepository = loanTransactionRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
    }

    private CommandProcessingResult createClientNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();

        final Client client = this.clientRepository.findOne(resourceId);
        if (client == null) { throw new ClientNotFoundException(resourceId); }
        final Note newNote = Note.clientNoteFromJson(client, command);

        this.noteRepository.save(newNote);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(newNote.getId()) //
                .withClientId(client.getId()).withOfficeId(client.getOffice().getId()).build();

    }

    private CommandProcessingResult createGroupNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();

        final Group group = this.groupRepository.findOne(resourceId);
        if (group == null) { throw new GroupNotFoundException(resourceId); }
        final Note newNote = Note.groupNoteFromJson(group, command);

        this.noteRepository.save(newNote);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(newNote.getId()) //
                .withClientId(group.getId()).withOfficeId(group.getOfficeId()).build();
    }

    private CommandProcessingResult createLoanNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();

        final Loan loan = this.loanRepository.findOne(resourceId);
        if (loan == null) { throw new LoanNotFoundException(resourceId); }

        final String note = command.stringValueOfParameterNamed("note");
        final Note newNote = Note.loanNote(loan, note);

        this.noteRepository.save(newNote);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(newNote.getId()) //
                .withClientId(loan.getClientId()).withOfficeId(loan.getOfficeId()).withLoanId(loan.getId()).withGroupId(loan.getGroupId())// Loan
                                                                                                                                          // can
                                                                                                                                          // be
                                                                                                                                          // associated
                                                                                                                                          // to
                                                                                                                                          // group
                .build();
    }

    private CommandProcessingResult createLoanTransactionNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();

        final LoanTransaction loanTransaction = this.loanTransactionRepository.findOne(resourceId);
        if (loanTransaction == null) { throw new LoanTransactionNotFoundException(resourceId); }

        final Loan loan = loanTransaction.getLoan();

        final String note = command.stringValueOfParameterNamed("note");
        final Note newNote = Note.loanTransactionNote(loan, loanTransaction, note);

        this.noteRepository.save(newNote);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(newNote.getId()) //
                .withClientId(loan.getClientId()).withOfficeId(loan.getOfficeId()).withLoanId(loan.getId()).withGroupId(loan.getGroupId())// Loan
                                                                                                                                          // can
                                                                                                                                          // be
                                                                                                                                          // associated
                .build();
    }

    private CommandProcessingResult createDepositAccountNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();

        final DepositAccount depositAccount = this.depositAccountRepository.findOne(resourceId);
        if (depositAccount == null) { throw new DepositAccountNotFoundException(resourceId); }

        final String note = command.stringValueOfParameterNamed("note");
        final Note newNote = Note.depositNote(depositAccount, note);

        this.noteRepository.save(newNote);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(newNote.getId()) //
                .withClientId(depositAccount.client().getId()).withOfficeId(depositAccount.client().getOffice().getId()).build();
    }

    private CommandProcessingResult createSavingAccountNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();

        final SavingAccount savingAccount = this.savingAccountRepository.findOne(resourceId);
        if (savingAccount == null) { throw new SavingAccountNotFoundException(resourceId); }

        final String note = command.stringValueOfParameterNamed("note");
        final Note newNote = Note.savingNote(savingAccount, note);

        this.noteRepository.save(newNote);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(newNote.getId()) //
                .withClientId(savingAccount.getClient().getId()).withOfficeId(savingAccount.getClient().getOffice().getId()).build();
    }

    @Override
    public CommandProcessingResult createNote(final JsonCommand command) {

        this.fromApiJsonDeserializer.validateNote(command.json());

        final String resourceUrl = command.getSupportedEntityType();
        final NoteType type = NoteType.fromApiUrl(resourceUrl);
        switch (type) {
            case CLIENT: {
                return createClientNote(command);
            }
            case DEPOSIT: {
                return createDepositAccountNote(command);
            }
            case GROUP: {
                return createGroupNote(command);
            }
            case LOAN: {
                return createLoanNote(command);
            }
            case LOAN_TRANSACTION: {
                return createLoanTransactionNote(command);
            }
            case SAVING: {
                return createSavingAccountNote(command);
            }
            default:
                throw new NoteResourceNotSupportedFoundException(resourceUrl);
        }

    }

    private CommandProcessingResult updateClientNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();
        final Long noteId = command.entityId();
        final String resourceUrl = command.getSupportedEntityType();

        final NoteType type = NoteType.fromApiUrl(resourceUrl);

        final Client client = this.clientRepository.findOne(resourceId);
        if (client == null) { throw new ClientNotFoundException(resourceId); }

        final Note noteForUpdate = this.noteRepository.findByClientIdAndId(resourceId, noteId);
        if (noteForUpdate == null) { throw new NoteNotFoundException(noteId, resourceId, type.name().toLowerCase()); }

        final Map<String, Object> changes = noteForUpdate.update(command);

        if (!changes.isEmpty()) {
            this.noteRepository.saveAndFlush(noteForUpdate);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(noteForUpdate.getId()) //
                .withClientId(client.getId()).withOfficeId(client.getOffice().getId())
                .with(changes)
                .build();

    }

    private CommandProcessingResult updateDepositAccountNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();
        final Long noteId = command.entityId();
        final String resourceUrl = command.getSupportedEntityType();

        final NoteType type = NoteType.fromApiUrl(resourceUrl);

        final DepositAccount depositAccount = this.depositAccountRepository.findOne(resourceId);
        if (depositAccount == null) { throw new DepositAccountNotFoundException(resourceId); }
        final Note noteForUpdate = this.noteRepository.findByDepositAccountIdAndId(resourceId, noteId);

        if (noteForUpdate == null) { throw new NoteNotFoundException(noteId, resourceId, type.name().toLowerCase()); }

        final Map<String, Object> changes = noteForUpdate.update(command);

        if (!changes.isEmpty()) {
            this.noteRepository.saveAndFlush(noteForUpdate);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(noteForUpdate.getId()) //
                .withClientId(depositAccount.client().getId()).withOfficeId(depositAccount.client().getOffice().getId())
                .with(changes)
                .build();
    }

    private CommandProcessingResult updateGroupNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();
        final Long noteId = command.entityId();
        final String resourceUrl = command.getSupportedEntityType();

        final NoteType type = NoteType.fromApiUrl(resourceUrl);

        final Group group = this.groupRepository.findOne(resourceId);
        if (group == null) { throw new GroupNotFoundException(resourceId); }
        final Note noteForUpdate = this.noteRepository.findByGroupIdAndId(resourceId, noteId);

        if (noteForUpdate == null) { throw new NoteNotFoundException(noteId, resourceId, type.name().toLowerCase()); }

        final Map<String, Object> changes = noteForUpdate.update(command);

        if (!changes.isEmpty()) {
            this.noteRepository.saveAndFlush(noteForUpdate);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(noteForUpdate.getId()) //
                .withGroupId(group.getId()).withOfficeId(group.getOfficeId())
                .with(changes)
                .build();
    }

    private CommandProcessingResult updateLoanNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();
        final Long noteId = command.entityId();
        final String resourceUrl = command.getSupportedEntityType();

        final NoteType type = NoteType.fromApiUrl(resourceUrl);

        final Loan loan = this.loanRepository.findOne(resourceId);
        if (loan == null) { throw new LoanNotFoundException(resourceId); }
        final Note noteForUpdate = this.noteRepository.findByLoanIdAndId(resourceId, noteId);

        if (noteForUpdate == null) { throw new NoteNotFoundException(noteId, resourceId, type.name().toLowerCase()); }

        final Map<String, Object> changes = noteForUpdate.update(command);

        if (!changes.isEmpty()) {
            this.noteRepository.saveAndFlush(noteForUpdate);
        }

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(noteForUpdate.getId())
                .withClientId(loan.getClientId()).withLoanId(loan.getId()).withGroupId(loan.getGroupId()).withOfficeId(loan.getOfficeId())
                .with(changes)
                .build();
    }

    private CommandProcessingResult updateLoanTransactionNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();
        final Long noteId = command.entityId();
        final String resourceUrl = command.getSupportedEntityType();

        final NoteType type = NoteType.fromApiUrl(resourceUrl);

        final LoanTransaction loanTransaction = this.loanTransactionRepository.findOne(resourceId);
        if (loanTransaction == null) { throw new LoanTransactionNotFoundException(resourceId); }
        final Loan loan = loanTransaction.getLoan();

        final Note noteForUpdate = this.noteRepository.findByLoanTransactionIdAndId(resourceId, noteId);

        if (noteForUpdate == null) { throw new NoteNotFoundException(noteId, resourceId, type.name().toLowerCase()); }

        final Map<String, Object> changes = noteForUpdate.update(command);

        if (!changes.isEmpty()) {
            this.noteRepository.saveAndFlush(noteForUpdate);
        }

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(noteForUpdate.getId())
                .withClientId(loan.getClientId()).withLoanId(loan.getId()).withGroupId(loan.getGroupId()).withOfficeId(loan.getOfficeId())
                .with(changes)
                .build();
    }

    private CommandProcessingResult updateSavingAccountNote(final JsonCommand command) {

        final Long resourceId = command.getSupportedEntityId();
        final Long noteId = command.entityId();
        final String resourceUrl = command.getSupportedEntityType();

        final NoteType type = NoteType.fromApiUrl(resourceUrl);

        final SavingAccount savingAccount = this.savingAccountRepository.findOne(resourceId);
        if (savingAccount == null) { throw new SavingAccountNotFoundException(resourceId); }

        final Note noteForUpdate = this.noteRepository.findBySavingAccountIdAndId(resourceId, noteId);

        if (noteForUpdate == null) { throw new NoteNotFoundException(noteId, resourceId, type.name().toLowerCase()); }

        final Map<String, Object> changes = noteForUpdate.update(command);

        if (!changes.isEmpty()) {
            this.noteRepository.saveAndFlush(noteForUpdate);
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(noteForUpdate.getId()) //
                .withClientId(savingAccount.getClient().getId()).withOfficeId(savingAccount.getClient().getOffice().getId())
                .with(changes)
                .build();
    }

    @Override
    public CommandProcessingResult updateNote(final JsonCommand command) {

        this.fromApiJsonDeserializer.validateNote(command.json());

        final String resourceUrl = command.getSupportedEntityType();
        final NoteType type = NoteType.fromApiUrl(resourceUrl);

        switch (type) {
            case CLIENT: {
                return updateClientNote(command);
            }
            case DEPOSIT: {
                return updateDepositAccountNote(command);
            }
            case GROUP: {
                return updateGroupNote(command);
            }
            case LOAN: {
                return updateLoanNote(command);
            }
            case LOAN_TRANSACTION: {
                return updateLoanTransactionNote(command);
            }
            case SAVING: {
                return updateSavingAccountNote(command);
            }
            default:
                throw new NoteResourceNotSupportedFoundException(resourceUrl);
        }
    }

    @Override
    public CommandProcessingResult deleteNote(final JsonCommand command) {

        final Note noteForDelete = getNoteForDelete(command);

        this.noteRepository.delete(noteForDelete);
        return new CommandProcessingResultBuilder() //
                .withCommandId(null) //
                .withEntityId(command.entityId()) //
                .build();
    }

    private Note getNoteForDelete(final JsonCommand command) {
        final String resourceUrl = command.getSupportedEntityType();
        final Long resourceId = command.getSupportedEntityId();
        final Long noteId = command.entityId();
        final NoteType type = NoteType.fromApiUrl(resourceUrl);
        Note noteForUpdate = null;
        switch (type) {
            case CLIENT: {
                noteForUpdate = this.noteRepository.findByClientIdAndId(resourceId, noteId);
            }
            break;
            case DEPOSIT: {
                noteForUpdate = this.noteRepository.findByDepositAccountIdAndId(resourceId, noteId);
            }
            break;
            case GROUP: {
                noteForUpdate = this.noteRepository.findByGroupIdAndId(resourceId, noteId);
            }
            break;
            case LOAN: {
                noteForUpdate = this.noteRepository.findByLoanIdAndId(resourceId, noteId);
            }
            break;
            case LOAN_TRANSACTION: {
                noteForUpdate = this.noteRepository.findByLoanTransactionIdAndId(resourceId, noteId);
            }
            break;
            case SAVING: {
                noteForUpdate = this.noteRepository.findBySavingAccountIdAndId(resourceId, noteId);
            }
            break;
        }
        if (noteForUpdate == null) { throw new NoteNotFoundException(noteId, resourceId, type.name().toLowerCase()); }
        return noteForUpdate;
    }

}
