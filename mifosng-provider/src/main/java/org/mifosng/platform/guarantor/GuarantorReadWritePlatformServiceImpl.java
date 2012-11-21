package org.mifosng.platform.guarantor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosng.platform.api.commands.GuarantorCommand;
import org.mifosng.platform.api.data.ApiParameterError;
import org.mifosng.platform.api.data.GenericResultsetData;
import org.mifosng.platform.api.data.GuarantorData;
import org.mifosng.platform.client.domain.Client;
import org.mifosng.platform.client.domain.ClientRepository;
import org.mifosng.platform.exceptions.ClientNotFoundException;
import org.mifosng.platform.exceptions.GuarantorNotFoundException;
import org.mifosng.platform.exceptions.InvalidGuarantorException;
import org.mifosng.platform.exceptions.LoanNotFoundException;
import org.mifosng.platform.exceptions.PlatformApiDataValidationException;
import org.mifosng.platform.loan.domain.Loan;
import org.mifosng.platform.loan.domain.LoanRepository;
import org.mifosng.platform.noncore.ReadWriteNonCoreDataService;
import org.mifosng.platform.organisation.domain.OfficeRepository;
import org.mifosng.platform.security.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuarantorReadWritePlatformServiceImpl implements
		GuarantorReadWritePlatformService {

	private final PlatformSecurityContext context;
	private final LoanRepository loanRepository;
	private final ClientRepository clientRepository;
	private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
	// Table for storing external Guarantor Details
	public static final String EXTERNAL_GUARANTOR_TABLE_NAME = "m_guarantor_external";

	@Autowired
	public GuarantorReadWritePlatformServiceImpl(
			final PlatformSecurityContext context,
			final LoanRepository loanRepository,
			final ReadWriteNonCoreDataService readWriteNonCoreDataService,
			final OfficeRepository officeRepository,
			final ClientRepository clientRepository) {
		this.context = context;
		this.loanRepository = loanRepository;
		this.readWriteNonCoreDataService = readWriteNonCoreDataService;
		this.clientRepository = clientRepository;
	}

	@Override
	public boolean existsGuarantor(Long loanId) {
		Loan loan = validateLoanExists(loanId);
		// return if internal guarantor exists
		if (loan.getGuarantor() != null) {
			return true;
		}
		// return if an external guarantor exists
		if (null != getExternalGuarantor(loanId)) {
			return true;
		}
		// else no guarantor exists
		return false;
	}

	@Override
	public GuarantorData retrieveGuarantor(Long loanId) {
		context.authenticatedUser();
		GuarantorData guarantorData = null;
		Loan loan = validateLoanExists(loanId);
		// does an internal guarantor exist
		if (loan.getGuarantor() != null) {
			Client guarantor = loan.getGuarantor();
			LocalDate localDate = new LocalDate(guarantor.getJoiningDate());
			guarantorData = new GuarantorData(guarantor.getId(),
					guarantor.getFirstName(), guarantor.getLastName(),
					guarantor.getExternalId(), guarantor.getOffice().getName(),
					localDate);
		} else {
			guarantorData = getExternalGuarantor(loanId);
		}
		// throw error if guarantor does not exist
		if (guarantorData == null) {
			throw new GuarantorNotFoundException(loanId);
		}
		return guarantorData;
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
			if (getExternalGuarantor(loanId) != null) {
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

	/**
	 * @param loanId
	 * @return
	 */
	private GuarantorData getExternalGuarantor(Long loanId) {
		GenericResultsetData genericResultDataSet = readWriteNonCoreDataService
				.retrieveDataTableGenericResultSet(
						EXTERNAL_GUARANTOR_TABLE_NAME, loanId, null, null);
		if (genericResultDataSet.getData().size() == 1) {
			List<String> guarantorRow = genericResultDataSet.getData().get(0)
					.getRow();
			String firstname = guarantorRow.get(1);
			String lastname = guarantorRow.get(2);

			LocalDate dateOfBirth = null;
			if (!StringUtils.isBlank(guarantorRow.get(3))) {
				dateOfBirth = new LocalDate(guarantorRow.get(3));
			}
			String addressLine1 = guarantorRow.get(4);
			String addressLine2 = guarantorRow.get(5);
			String city = guarantorRow.get(6);
			String state = guarantorRow.get(7);
			String country = guarantorRow.get(8);
			String zip = guarantorRow.get(9);
			String housePhoneNumber = guarantorRow.get(10);
			String mobileNumber = guarantorRow.get(11);
			String comment = guarantorRow.get(12);
			GuarantorData guarantorData = new GuarantorData(firstname,
					lastname, dateOfBirth, addressLine1, addressLine2, city,
					state, zip, country, mobileNumber, housePhoneNumber,
					comment);
			return guarantorData;
		}
		return null;
	}

}