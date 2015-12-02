/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.creditcheck.data;

/** 
 * Immutable object representing the credit check stretchy report parameters
 * Each stretchy report can have one or more of these parameters
 **/
public class CreditCheckReportParamData {
    private final Long clientId;
    private final Long loanId;
    private final Long groupId;
    private final Long userId;
    private final Long staffId;
    private final Long officeId;
    private final Long productId;

    private CreditCheckReportParamData(final Long clientId, final Long loanId, final Long groupId, 
            final Long userId, final Long staffId, final Long officeId, final Long productId) {
        this.clientId = clientId;
        this.loanId = loanId;
        this.groupId = groupId;
        this.userId = userId;
        this.staffId = staffId;
        this.officeId = officeId;
        this.productId = productId;
    }
    
    public static CreditCheckReportParamData instance(final Long clientId, final Long loanId, final Long groupId, 
            final Long userId, final Long staffId, final Long officeId, final Long productId) {
        return new CreditCheckReportParamData(clientId, loanId, groupId, userId, staffId, officeId, productId);
    }

    /**
     * @return the clientId
     */
    public Long getClientId() {
        return clientId;
    }

    /**
     * @return the loanId
     */
    public Long getLoanId() {
        return loanId;
    }

    /**
     * @return the groupId
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @return the staffId
     */
    public Long getStaffId() {
        return staffId;
    }

    /**
     * @return the officeId
     */
    public Long getOfficeId() {
        return officeId;
    }

    /**
     * @return the productId
     */
    public Long getProductId() {
        return productId;
    }
}
