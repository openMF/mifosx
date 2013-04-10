package org.mifosplatform.integrationtests.common;


public class AccountEntry {

    public enum TransactionType {
        CREDIT("CREDIT"),
        DEBIT("DEBIT");

        private TransactionType(final String type) {
            this.type = type;
        }

        private final String type;

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return type;
        }
    }
    private final Float transactionAmount;
    private final TransactionType transactionType;

    public  AccountEntry(float transactionAmount, TransactionType type){
        this.transactionAmount=transactionAmount;
        this.transactionType = type;
    }

    public Float getTransactionAmount(){
        return this.transactionAmount;
    }

    public String getTransactionType(){
        return this.transactionType.toString();
    }

}
