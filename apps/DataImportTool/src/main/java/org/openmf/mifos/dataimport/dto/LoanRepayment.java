package org.openmf.mifos.dataimport.dto;

import java.util.Locale;

public class LoanRepayment {

	 private final transient Integer rowIndex;
	 
	 private final transient Integer loanAccountId;
	 
	 private final String transactionAmount;

	 private final String transactionDate;
	
	 private final String paymentTypeId;
	
	 private final String dateFormat;
	
	 private final Locale locale;
	 
	 private final String note;
	 
	 private final String accountNumber;
	 
	 private final String routingCode;
	 
	 private final String receiptNumber;
	 
	 private final String bankNumber;
	 
	 private final String checkNumber;
	 
	    public LoanRepayment(String transactionAmount, String transactionDate, String paymentTypeId, String accountNumber,
	    		String checkNumber, String routingCode, String receiptNumber, String bankNumber, Integer loanAccountId, Integer rowIndex) {
		    this.transactionAmount = transactionAmount;
	        this.transactionDate = transactionDate;
	        this.paymentTypeId = paymentTypeId;
	        this.accountNumber = accountNumber;
	        this.checkNumber = checkNumber;
	        this.routingCode = routingCode;
	        this.receiptNumber = receiptNumber;
	        this.bankNumber = bankNumber;
	        this.loanAccountId = loanAccountId;
	        this.rowIndex = rowIndex;
	        this.dateFormat = "dd MMMM yyyy";
	        this.locale = Locale.ENGLISH;
	        this.note = "";
	    }
	 
	    public LoanRepayment(String transactionAmount, String transactionDate, String paymentTypeId, Integer rowIndex) {
	    	this(transactionAmount, transactionDate, paymentTypeId, "", "", "", "", "", 0, rowIndex);
	    }
	 
	    public String getTransactionAmount() {
	    	return transactionAmount;
	    }
	 
	    public String getTransactionDate() {
		    return transactionDate;
	    }
	 
	    public String getPaymentTypeId() {
		 return paymentTypeId;
	    }
	 
	    public Locale getLocale() {
	    	return locale;
	    }
	    
	    public String getDateFormat() {
	    	return dateFormat;
	    }
	    
	    public String getNote() {
	    	return note;
	    }
	    
	    public String getAccountNumber() {
	    	return this.accountNumber;
	    }
	    
	    public String getRoutingCode() {
	    	return this.routingCode;
	    }
	    
	    public String getReceiptNumber() {
	    	return this.receiptNumber;
	    }
	    
	    public String getBankNumber() {
	    	return this.bankNumber;
	    }
	    
	    public String getCheckNumber() {
	    	return this.checkNumber;
	    }

	    public Integer getRowIndex() {
	        return rowIndex;
	    }
	    
	    public Integer getLoanAccountId() {
	    	return loanAccountId;
	    }
	    
}
