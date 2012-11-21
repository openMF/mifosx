package org.mifosng.platform.guarantor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosng.platform.api.commands.GuarantorCommand;
import org.mifosng.platform.api.data.ApiParameterError;
import org.mifosng.platform.client.domain.Client;
import org.mifosng.platform.client.domain.ClientRepository;
import org.mifosng.platform.exceptions.ClientNotFoundException;
import org.mifosng.platform.exceptions.InvalidGuarantorException;
import org.mifosng.platform.exceptions.LoanNotFoundException;
import org.mifosng.platform.exceptions.PlatformApiDataValidationException;
import org.mifosng.platform.loan.domain.Loan;
import org.mifosng.platform.loan.domain.LoanRepository;
import org.mifosng.platform.noncore.ReadWriteNonCoreDataService;
import org.mifosng.platform.organisation.domain.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuarantorWritePlatformServiceImpl implements
		GuarantorWritePlatformService {

	private final LoanRepository loanRepository;
	private final ClientRepository clientRepository;
	private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
	private final GuarantorReadPlatformService guarantorReadPlatformService;
	// Table for storing external Guarantor Details
	public static final String EXTERNAL_GUARANTOR_TABLE_NAME = "m_guarantor_external";

	@Autowired
	public GuarantorWritePlatformServiceImpl(
			final LoanRepository loanRepository,
			final ReadWriteNonCoreDataService readWriteNonCoreDataService,
			final OfficeRepository officeRepository,
			final ClientRepository clientRepository,
			final GuarantorReadPlatformService guarantorReadPlatformService) {
		this.loanRepository = loanRepository;
		this.readWriteNonCoreDataService = readWriteNonCoreDataService;
		this.clientRepository = clientRepository;
		this.guarantorReadPlatformService = guarantorReadPlatformService;
	}

	@Override
	@Transactional
	public void createGuarantor(Long loanId, GuarantorCommand command) {
		GuarantorCommandValidator validator = new GuarantorCommandValidator(
				command);
		validator.validateForCreate();
		saveOrUpdateGuarantor(loanId, command);
	}

	@Override
	@Transactional
	public void updateGuarantor(Long loanId, GuarantorCommand command) {
		GuarantorCommandValidator validator = new GuarantorCommandValidator(
				command);
		validator.validateForUpdate();
		saveOrUpdateGuarantor(loanId, command);
	}

	@Override
	@Transactional
	public void removeGuarantor(Long loanId) {
		Loan loan = validateLoanExists(loanId);
		// remove internal guarantor, if any
		if (loan.getGuarantor() != null) {
			loan.setGuarantor(null);
			loanRepository.saveAndFlush(loan);
		}
		// remove external guarantor, if any
		readWriteNonCoreDataService.deleteDatatableEntries(
				EXTERNAL_GUARANTOR_TABLE_NAME, loanId);
	}

	/**
	 * @param loanId
	 * @param command
	 */
	private void saveOrUpdateGuarantor(Long loanId, GuarantorCommand command) {
		Loan loan = validateLoanExists(loanId);
		// mark an existing client as a guarantor
		if (command.isExternalGuarantor() == null
				|| !command.isExternalGuarantor()) {
			// can't set client as a guarantor to himself
			if (loan.getClient().getId().equals(command.getExistingClientId())) {
				throw new InvalidGuarantorException(
						command.getExistingClientId(), loanId);
			}
			Client guarantor = clientRepository.findOne(command
					.getExistingClientId());
			if (guarantor == null) {
				throw new ClientNotFoundException(command.getExistingClientId());
			}
			loan.setGuarantor(guarantor);
			this.loanRepository.saveAndFlush(loan);
			// also delete any existing External Guarantors
			readWriteNonCoreDataService.deleteDatatableEntries(
					EXTERNAL_GUARANTOR_TABLE_NAME, loanId);
		}// or create an external guarantor
		else {
			Set<String> modifiedParameters = command.getModifiedParameters();
			Map<String, String> modifiedParametersMap = new HashMap<String, String>();
			/***
			 * TODO Vishwas: Check with JW/KW if using reflection here is a good
			 * idea
			 **/
			Class<? extends GuarantorCommand> guarantorCommandClass = command
					.getClass();
			for (String modifiedParameter : modifiedParameters) {
				Method method;
				try {
					if (modifiedParameter.equalsIgnoreCase("externalGuarantor")
							|| modifiedParameter
									.equalsIgnoreCase("existingClientId")) {
						continue;
					} else {
						method = guarantorCommandClass.getMethod("get"
								+ StringUtils.capitalize(modifiedParameter));
					}
					String columnName = StringUtils.join(StringUtils
							.splitByCharacterTypeCamelCase(modifiedParameter),
							'_');
					modifiedParametersMap.put(columnName.toLowerCase(), method
							.invoke(command).toString());
				} catch (Exception e) {
					// TODO: This block would ideally never be reached, could
					// use an
					// empty catch block instead?
					List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
					ApiParameterError apiParameterError = ApiParameterError
							.parameterError(
									"validation.msg.validation.errors.exist",
									"", modifiedParameter);
					dataValidationErrors.add(apiParameterError);
					throw new PlatformApiDataValidationException(
							"validation.msg.validation.errors.exist",
							"Invalid parameter passed for Creating updating Guarantor",
							dataValidationErrors);
				}

			}

			/*** update or create a new external Guarantor entry ***/
			modifiedParametersMap.put("locale", command.getLocale());
			modifiedParametersMap.put("dateFormat", command.getDateFormat());
			if (guarantorReadPlatformService.getExternalGuarantor(loanId) != null) {
				readWriteNonCoreDataService.updateDatatableEntryOnetoOne(
						EXTERNAL_GUARANTOR_TABLE_NAME, loanId,
						modifiedParametersMap);
			} else {
				readWriteNonCoreDataService.newDatatableEntry(
						EXTERNAL_GUARANTOR_TABLE_NAME, loanId,
						modifiedParametersMap);
			}
			// finally unset any existing internal guarantors
			if (loan.getGuarantor() != null) {
				loan.setGuarantor(null);
				this.loanRepository.saveAndFlush(loan);
			}
		}
	}

	/**
	 * @param loanId
	 * @return
	 */
	private Loan validateLoanExists(Long loanId) {
		Loan loan = loanRepository.findOne(loanId);
		if (loan == null) {
			throw new LoanNotFoundException(loanId);
		}
		return loan;
	}
}