package org.mifosplatform.accounting.journalentry.data;

import org.mifosplatform.portfolio.note.data.NoteData;
import org.mifosplatform.portfolio.paymentdetail.data.PaymentDetailData;

public class TransactionDetailData {

    @SuppressWarnings("unused")
    private final Long transactionId;

    @SuppressWarnings("unused")
    private final PaymentDetailData paymentDetails;

    @SuppressWarnings("unused")
    private final NoteData noteData;

    @SuppressWarnings("unused")
    private final TransactionTypeEnumData transactionType;

    public TransactionDetailData(final Long transactionId, final PaymentDetailData paymentDetails, final NoteData noteData) {
        this.transactionId = transactionId;
        this.paymentDetails = paymentDetails;
        this.noteData = noteData;
        this.transactionType = null;

    }

    public TransactionDetailData(final Long transactionId, final PaymentDetailData paymentDetails, final NoteData noteData,
            final TransactionTypeEnumData transactionType) {
        this.transactionId = transactionId;
        this.paymentDetails = paymentDetails;
        this.noteData = noteData;
        this.transactionType = transactionType;
    }
}
