package org.mifosplatform.accounting.exceptions;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when journal entry resources are not found.
 */
public class GLJournalEntriesNotFoundException extends AbstractPlatformResourceNotFoundException {

    public GLJournalEntriesNotFoundException(final String transactionId) {
        super("error.msg.journalEntries.transactionId.invalid", "Journal Entries with transaction Identifier " + transactionId
                + " does not exist or are not system generated/reversible ", transactionId);
    }

    public GLJournalEntriesNotFoundException(final Long entryId) {
        super("error.msg.journalEntries.id.invalid", "Journal Entry with entry Id " + entryId + " does not exist ", entryId);
    }
}