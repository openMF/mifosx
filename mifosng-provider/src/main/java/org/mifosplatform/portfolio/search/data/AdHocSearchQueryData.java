package org.mifosplatform.portfolio.search.data;

import java.math.BigDecimal;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.loanproduct.data.LoanProductData;

public class AdHocSearchQueryData {

    @SuppressWarnings("unused")
    private final String clientName;
    @SuppressWarnings("unused")
    private final String officeName;
    @SuppressWarnings("unused")
    private final String loanProductName;
    @SuppressWarnings("unused")
    private final String fundName;
    @SuppressWarnings("unused")
    private final Integer count;
    @SuppressWarnings("unused")
    private final BigDecimal loanOutStanding;
    @SuppressWarnings("unused")
    private final BigDecimal disburseAmount;
    @SuppressWarnings("unused")
	private final LocalDate disbursementDate;
    @SuppressWarnings("unused")
    private final Double percentage;
    @SuppressWarnings("unused")
    private final Integer loanId;
    
    @SuppressWarnings("unused")
    private final Collection<LoanProductData> loanProducts;
    @SuppressWarnings("unused")
    private final Collection<OfficeData> offices;

    public static AdHocSearchQueryData template(final Collection<LoanProductData> loanProducts, final Collection<OfficeData> offices) {
        final String officeName = null;
        final String loanProductName = null;
        final Integer count = null;
        final BigDecimal loanOutStanding = null;
        final Double percentage = null;
        return new AdHocSearchQueryData(null, officeName, loanProductName, null, count, null, null, loanOutStanding, percentage, null, loanProducts, offices);
    }

    public static AdHocSearchQueryData matchedResult(final String clientName, final String officeName, final String loanProductName, 
    		final String fundName, final Integer count, final BigDecimal disburseAmount, final LocalDate disbursementDate, 
    		final BigDecimal loanOutStanding, final Double percentage, final Integer loanId) {

        final Collection<LoanProductData> loanProducts = null;
        final Collection<OfficeData> offices = null;
        return new AdHocSearchQueryData(clientName, officeName, loanProductName, fundName, count, disburseAmount, disbursementDate,
        		loanOutStanding, percentage, loanId, loanProducts, offices);
    }

    private AdHocSearchQueryData(final String clientName, final String officeName, final String loanProductName, 
    		final String fundName, final Integer count, final BigDecimal disburseAmount, final LocalDate disbursementDate, 
    		final BigDecimal loanOutStanding, final Double percentage, final Integer loanId, final Collection<LoanProductData> loanProducts,
            final Collection<OfficeData> offices) {
    	
    	this.clientName = clientName;
        this.officeName = officeName;
        this.loanProductName = loanProductName;
        this.fundName = fundName;
        this.count = count;
        this.disburseAmount = disburseAmount;
        this.disbursementDate = disbursementDate;
        this.loanOutStanding = loanOutStanding;
        this.percentage = percentage;
        this.loanId = loanId;
        this.loanProducts = loanProducts;
        this.offices = offices;
    }
}
