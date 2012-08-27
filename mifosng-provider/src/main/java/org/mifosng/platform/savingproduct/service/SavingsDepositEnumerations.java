package org.mifosng.platform.savingproduct.service;

import org.mifosng.platform.api.data.EnumOptionData;
import org.mifosng.platform.loan.domain.PeriodFrequencyType;
import org.mifosng.platform.saving.domain.DepositStatus;

public class SavingsDepositEnumerations {
	
	public static EnumOptionData interestCompoundingPeriodType(final int id) {
		return interestCompoundingPeriodType(PeriodFrequencyType.fromInt(id));
	}
	
	public static EnumOptionData interestCompoundingPeriodType(final PeriodFrequencyType type) {
		final String codePrefix = "deposit.interest.compounding.period.";
		EnumOptionData optionData = null;
		switch (type) {
		case DAYS:
			optionData = new EnumOptionData(PeriodFrequencyType.DAYS.getValue().longValue(), codePrefix + PeriodFrequencyType.DAYS.getCode(), "Days");
			break;
		case WEEKS:
			optionData = new EnumOptionData(PeriodFrequencyType.WEEKS.getValue().longValue(), codePrefix + PeriodFrequencyType.WEEKS.getCode(), "Weeks");
			break;
		case MONTHS:
			optionData = new EnumOptionData(PeriodFrequencyType.MONTHS.getValue().longValue(), codePrefix + PeriodFrequencyType.MONTHS.getCode(), "Months");
			break;
		default:
			optionData = new EnumOptionData(PeriodFrequencyType.INVALID.getValue().longValue(), PeriodFrequencyType.INVALID.getCode(), "Invalid");
			break;
		}
		return optionData;
	}
	
	public static EnumOptionData depositStatusEnumType(final int id) {
				return depositStatusEnum(DepositStatus.fromInt(id));
			}
	
	public static EnumOptionData depositStatusEnum(final DepositStatus type){
				final String codePrefix = "deposit.interest.compounding.period.";
				EnumOptionData optionData=null;
				
				switch (type) {
				case SUBMITED_AND_PENDING_APPROVAL:
					 optionData = new EnumOptionData(DepositStatus.SUBMITED_AND_PENDING_APPROVAL.getValue().longValue(), codePrefix + DepositStatus.SUBMITED_AND_PENDING_APPROVAL.getCode(), "SUBMITED_AND_PENDING_APPROVAL");
					break;
				case ACTIVE:
					optionData = new EnumOptionData(DepositStatus.ACTIVE.getValue().longValue(), codePrefix + DepositStatus.ACTIVE.getCode(), "ACTIVE");
					break;
				case APPROVED:
					optionData = new EnumOptionData(DepositStatus.APPROVED.getValue().longValue(), codePrefix + DepositStatus.APPROVED.getCode(), "APPROVED");
					break;
				case CLOSED:
					optionData = new EnumOptionData(DepositStatus.CLOSED.getValue().longValue(), codePrefix + DepositStatus.CLOSED.getCode(), "CLOSED");
					break;
				case INVALID:
					optionData = new EnumOptionData(DepositStatus.INVALID.getValue().longValue(), codePrefix + DepositStatus.INVALID.getCode(), "INVALID");
					break;
				case MATURED:
					optionData = new EnumOptionData(DepositStatus.MATURED.getValue().longValue(), codePrefix + DepositStatus.MATURED.getCode(), "MATURED");
					break;
				case REJECTED:
					optionData = new EnumOptionData(DepositStatus.REJECTED.getValue().longValue(), codePrefix + DepositStatus.REJECTED.getCode(), "REJECTED");
					break;
				case WITHDRAWN_BY_CLIENT:
					optionData = new EnumOptionData(DepositStatus.WITHDRAWN_BY_CLIENT.getValue().longValue(), codePrefix + DepositStatus.WITHDRAWN_BY_CLIENT.getCode(), "WITHDRAWN_BY_CLIENT");
		 			break;
		 		}
		return optionData;
	}	 		
}