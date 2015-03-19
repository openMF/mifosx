/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.search.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.loanproduct.data.LoanProductData;

public class AdHocSearchQueryData {

    @SuppressWarnings("unused")
    private final String officeName;
    @SuppressWarnings("unused")
    private final Integer count;
    @SuppressWarnings("unused")
    private final BigDecimal loanOutStanding;
    @SuppressWarnings("unused")
    private final Double percentage;
    @SuppressWarnings("unused")
    private final Collection<LoanProductData> loanProducts;
    @SuppressWarnings("unused")
    private final Collection<OfficeData> offices;
    @SuppressWarnings("unused")
    private final String clientAccountNo;
    @SuppressWarnings("unused")
	private final String clientName;
    @SuppressWarnings("unused")
    private Integer loanProductId;
    @SuppressWarnings("unused")
    private final String loanProductName;
    @SuppressWarnings("unused")
	private Date disbursedDate;
    @SuppressWarnings("unused")
	private final BigDecimal disbursementAmount;
    @SuppressWarnings("unused")
	private final Date maturedDate;
    @SuppressWarnings("unused")
	private final BigDecimal principalOutstanding;
    @SuppressWarnings("unused")
	private final BigDecimal principalRepaid;
    @SuppressWarnings("unused")
	private final BigDecimal arrearsAmount;
    @SuppressWarnings("unused")
	private final BigDecimal interestOutstanding;
    @SuppressWarnings("unused")
	private final BigDecimal interestRepaid;
    
    
  
    public static AdHocSearchQueryData template(final Collection<LoanProductData> loanProducts, final Collection<OfficeData> offices) {
        final String officeName = null;
        final String loanProductName = null;
        final Integer count = null;
        final BigDecimal loanOutStanding = null;
        final Double percentage = null;
        final Integer loanProductId = null;
        final String clientAccountNo  = null;
        final String clientName = null;
        Date disbursedDate = null;
        final BigDecimal disbursementAmount = null;
        final Date maturedDate = null;
        final BigDecimal principalOutstanding = null;
        final BigDecimal principalRepaid = null;
        final BigDecimal arrearsAmount = null;
        final BigDecimal interestOutstanding = null;
        final BigDecimal interestRepaid = null;
        return new AdHocSearchQueryData(officeName, count, loanOutStanding, percentage, loanProducts, offices ,clientAccountNo, clientName, loanProductId,loanProductName, disbursedDate,
        		disbursementAmount, maturedDate, principalOutstanding, principalRepaid, arrearsAmount, interestOutstanding, interestRepaid);
    }

    public static AdHocSearchQueryData matchedResult(final String officeName, final String loanProductName, final Integer count,
            final BigDecimal loanOutStanding, final Double percentage, Integer loanProductId) {

        final Collection<LoanProductData> loanProducts = null;
        final Collection<OfficeData> offices = null;
        final String clientAccountNo  = null;
        final String clientName = null;
        Date disbursedDate = null;
        final BigDecimal disbursementAmount = null;
        final Date maturedDate = null;
        final BigDecimal principalOutstanding = null;
        final BigDecimal principalRepaid = null;
        final BigDecimal arrearsAmount = null;
        final BigDecimal interestOutstanding = null;
        final BigDecimal interestRepaid = null;
        return new AdHocSearchQueryData(officeName, count, loanOutStanding, percentage, loanProducts, offices, clientAccountNo, clientName,loanProductId, loanProductName, disbursedDate,
        		disbursementAmount, maturedDate, principalOutstanding, principalRepaid, arrearsAmount, interestOutstanding, interestRepaid);
    }

    private AdHocSearchQueryData(final String officeName, final Integer count,
            final BigDecimal loanOutStanding, final Double percentage, final Collection<LoanProductData> loanProducts,
            final Collection<OfficeData> offices,final String clientAccountNo,final String clientName, final Integer loanProductId,final String loanProductName,final Date disbursedDate,final BigDecimal disbursementAmount,final Date maturedDate,final BigDecimal principalOutstanding,
			final BigDecimal principalRepaid,final BigDecimal arrearsAmount, final BigDecimal interestOutstanding,final BigDecimal interestRepaid) {

        this.officeName = officeName;
        this.count = count;
        this.loanOutStanding = loanOutStanding;
        this.percentage = percentage;
        this.offices = offices;
        this.clientAccountNo = clientAccountNo;
        this.clientName = clientName;
        this.loanProductId = loanProductId;
        this.loanProductName = loanProductName;
        this.loanProducts = loanProducts;
        this.disbursedDate = disbursedDate;
        this.disbursementAmount = disbursementAmount;
        this.maturedDate = maturedDate;
        this.principalOutstanding = principalOutstanding;
        this.principalRepaid = principalRepaid;
        this.arrearsAmount = arrearsAmount;
        this.interestOutstanding = interestOutstanding;
        this.interestRepaid = interestRepaid;
    }

	public static AdHocSearchQueryData matchedClientsResult(final String clientAccountNo,final String clientName,final Integer loanProductId,final String productName,final Date disbursedDate,final BigDecimal disbursementAmount,final Date maturedDate,final BigDecimal principalOutstanding,
			final BigDecimal principalRepaid,final BigDecimal arrearsAmount, final BigDecimal interestOutstanding,final BigDecimal interestRepaid) {
		 final String officeName = null;
	     final Integer count = null;
	     final BigDecimal loanOutStanding = null;
	     final Double percentage = null;
	     final Collection<LoanProductData> loanProducts = null;
	     final Collection<OfficeData> offices = null;
		return new AdHocSearchQueryData(officeName,count,loanOutStanding,percentage,loanProducts, offices,clientAccountNo,clientName,loanProductId,productName,disbursedDate,disbursementAmount,maturedDate,principalOutstanding,principalRepaid,arrearsAmount,interestOutstanding,interestRepaid);
	}
}
