package org.mifosplatform.portfolio.group.data;

/**
 * Immutable data object for group loan accounts.
 */
public class GroupAccountSummaryData {

    private final Long id;
    private final String externalId;
    private final Long productId;
    private final String productName;
    private final Integer accountStatusId;

    private final Boolean groupLoan;

    public GroupAccountSummaryData(Long id, String externalId,
                                   Long productId,
                                   String productName,
                                   Integer accountStatusId,
                                   Boolean groupLoan) {
        this.id = id;
        this.externalId = externalId;
        this.productId = productId;
        this.productName = productName;
        this.accountStatusId = accountStatusId;
        this.groupLoan = groupLoan;
    }

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getAccountStatusId() {
        return accountStatusId;
    }

    public Boolean isGroupLoan() {
        return groupLoan;
    }
}
