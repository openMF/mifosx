package org.mifosplatform.portfolio.loanaccount.domain;

import java.util.HashMap;
import java.util.Map;

public enum PaymentType {

    CASH(1, "paymentType.cash"), CHECK(2, "paymentType.check"), RECEIPT(3, "paymentType.receipt"), //
    ELECTRONIC_FUND_TRANSFER(4, "paymentType.eft");// Electronic Funds Transfer

    private final Integer value;
    private final String code;

    private PaymentType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, PaymentType> intToEnumMap = new HashMap<Integer, PaymentType>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final PaymentType type : PaymentType.values()) {
            if (i == 0) {
                minValue = type.value;
            }
            intToEnumMap.put(type.value, type);
            if (minValue >= type.value) {
                minValue = type.value;
            }
            if (maxValue < type.value) {
                maxValue = type.value;
            }
            i = i + 1;
        }
    }

    public static PaymentType fromInt(final int i) {
        final PaymentType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

    public static int getMinValue() {
        return minValue;
    }

    public static int getMaxValue() {
        return maxValue;
    }

    @Override
    public String toString() {
        return name().toString().replaceAll("_", " ");
    }

    public boolean isCashPayment() {
        return this.value.equals(PaymentType.CASH.value);
    }

    public boolean isCheckPayment() {
        return this.value.equals(PaymentType.CHECK.value);
    }

    public boolean isReceiptPayment() {
        return this.value.equals(PaymentType.RECEIPT.value);
    }

    public boolean isEFTPayment() {
        return this.value.equals(PaymentType.ELECTRONIC_FUND_TRANSFER.value);
    }

}
