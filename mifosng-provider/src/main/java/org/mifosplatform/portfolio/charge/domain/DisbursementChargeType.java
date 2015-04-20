package org.mifosplatform.portfolio.charge.domain;


public enum DisbursementChargeType {
    
    FIRST_DISBURSEMENT(0, "disbursementchargetype.firstdisbursement"), //
    WITH_EACH_DISBURSEMENT(1, "disbursementchargetype.witheachdisbursement");
    
    private final Integer value;
    private final String code;
    
    private DisbursementChargeType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    public static Object[] validValues() {
        return new Integer[] { DisbursementChargeType.FIRST_DISBURSEMENT.getValue(), DisbursementChargeType.WITH_EACH_DISBURSEMENT.getValue() };
    }
    
    public static DisbursementChargeType fromInt(final Integer disbursementChargeType) {
        DisbursementChargeType chargeAppliesToType = DisbursementChargeType.FIRST_DISBURSEMENT;
        switch (disbursementChargeType) {
            case 1:
                chargeAppliesToType = WITH_EACH_DISBURSEMENT;
            break;
            default:
                chargeAppliesToType = FIRST_DISBURSEMENT;
            break;
        }
        return chargeAppliesToType;
    }

}
