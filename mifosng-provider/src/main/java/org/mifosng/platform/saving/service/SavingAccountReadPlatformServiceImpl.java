package org.mifosng.platform.saving.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosng.platform.api.data.CurrencyData;
import org.mifosng.platform.api.data.EnumOptionData;
import org.mifosng.platform.api.data.SavingAccountData;
import org.mifosng.platform.client.service.ClientReadPlatformService;
import org.mifosng.platform.infrastructure.JdbcSupport;
import org.mifosng.platform.infrastructure.TenantAwareRoutingDataSource;
import org.mifosng.platform.savingproduct.service.SavingProductEnumerations;
import org.mifosng.platform.savingproduct.service.SavingProductReadPlatformService;
import org.mifosng.platform.security.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SavingAccountReadPlatformServiceImpl implements
		SavingAccountReadPlatformService {
	
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	private final SavingProductReadPlatformService savingProductReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	
	@Autowired
	public SavingAccountReadPlatformServiceImpl(
			final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource,
			SavingProductReadPlatformService savingProductReadPlatformService,
			final ClientReadPlatformService clientReadPlatformService ) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.savingProductReadPlatformService = savingProductReadPlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
	}

	@Override
	public Collection<SavingAccountData> retrieveAllSavingsAccounts() {

		this.context.authenticatedUser();
		SavingAccountMapper mapper = new SavingAccountMapper();
		String sql = "select " + mapper.schema() + " where sa.is_deleted=0";
		return this.jdbcTemplate.query(sql,mapper, new Object[]{});
	
	}

	@Override
	public SavingAccountData retrieveSavingsAccount(Long accountId) {
		
		this.context.authenticatedUser();
		SavingAccountMapper mapper = new SavingAccountMapper();
		String sql = "select " + mapper.schema() + " where sa.id = ? and sa.is_deleted=0";
		SavingAccountData savingAccountData = this.jdbcTemplate.queryForObject(sql,mapper, new Object[]{accountId});
		return savingAccountData;
	}

	@Override
	public SavingAccountData retrieveNewSavingsAccountDetails(Long clientId,
			Long productId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final class SavingAccountMapper implements RowMapper<SavingAccountData> {

		public String schema() {
			return "sa.id AS id, sa.status_enum AS status, sa.external_id AS externalId,"
       +  " sa.client_id AS clientId, sa.product_id AS productId, sa.deposit_amount_per_period AS savingsDepostiAmountPerPeriod,"
       +  " sa.savings_product_type AS savingsProductType, sa.currency_code AS currencyCode, sa.currency_digits AS currencyDigits,"
       +  " sa.total_deposit_amount AS totalDepositAmount, sa.reccuring_nominal_interest_rate AS reccuringInterestRate,"
       +  " sa.regular_saving_nominal_interest_rate AS savingInterestRate, sa.tenure AS tenure, sa.tenure_type AS tenureType,"
       +  " sa.frequency AS savingsFrequencyType, sa.interest_type AS interestType, sa.interest_calculation_method AS interestCalculationMethod,"
       +  " sa.projected_commencement_date AS projectedCommencementDate, sa.actual_commencement_date AS actualCommencementDate,"
       +  " sa.matures_on_date AS maturesOnDate, sa.projected_interest_accrued_on_maturity AS projectedInterestAccuredOnMaturity,"
       +  " sa.actual_interest_accrued AS actualInterestAccured, sa.projected_total_maturity_amount AS projectedTotalMaturityAmount,"
       +  " sa.actual_total_amount AS actualTotalAmount, sa.is_preclosure_allowed AS isPreclosureAllowed,"
       +  " sa.pre_closure_interest_rate AS preClosureInterestRate, sa.is_lock_in_period_allowed AS isLockinPeriodAllowed,"
       +  " sa.lock_in_period AS lockinPeriod, sa.lock_in_period_type AS lockinPeriodType, sa.withdrawnon_date AS withdrawnonDate," 
       +  " sa.rejectedon_date AS rejectedonDate, sa.closedon_date AS closedonDate, ps.name AS productName,"
       +  " c.firstname AS firstname, c.lastname AS lastname, curr.name AS currencyName, "
       +  " curr.internationalized_name_code AS currencyNameCode, curr.display_symbol AS currencyDisplaySymbol "
       +  " FROM m_saving_account sa "
       +  " JOIN m_client c ON c.id = sa.client_id"
       +  " JOIN m_currency curr ON curr.code = sa.currency_code "
       +  " JOIN m_product_savings ps ON sa.product_id = ps.id ";
	}
		 
		@Override
		public SavingAccountData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Long id = rs.getLong("id");
			String externalId = rs.getString("externalId");
			Long clientId = rs.getLong("clientId");
			String clientName = rs.getString("firstname") + " " + rs.getString("lastname");
			
			Long productId = rs.getLong("productId");
			String productName = rs.getString("productName");
			EnumOptionData productType = SavingProductEnumerations.savingProductType(JdbcSupport.getInteger(rs, "savingsProductType"));
			
			Integer statusId = JdbcSupport.getInteger(rs, "status");
			EnumOptionData status = DepositAccountEnumerations.status(statusId);
			
			String currencyCode = rs.getString("currencyCode");
			String currencyName = rs.getString("currencyName");
			String currencyNameCode = rs.getString("currencyNameCode");
			String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
			Integer currencyDigits = JdbcSupport.getInteger(rs,"currencyDigits");
			CurrencyData currencyData = new CurrencyData(currencyCode,currencyName, currencyDigits, currencyDisplaySymbol,currencyNameCode);
			
			BigDecimal savingsDepostiAmountPerPeriod = rs.getBigDecimal("savingsDepostiAmountPerPeriod");
			EnumOptionData savingsFrequencyType = SavingProductEnumerations.interestFrequencyType(JdbcSupport.getInteger(rs, "savingsFrequencyType"));
			BigDecimal totalDepositAmount = rs.getBigDecimal("totalDepositAmount");
			
			BigDecimal reccuringInterestRate = rs.getBigDecimal("reccuringInterestRate");
			BigDecimal savingInterestRate = rs.getBigDecimal("savingInterestRate");
			EnumOptionData interestType = SavingProductEnumerations.savingInterestType(JdbcSupport.getInteger(rs, "interestType"));
			EnumOptionData interestCalculationMethod = SavingProductEnumerations.savingInterestCalculationMethod(JdbcSupport.getInteger(rs, "interestCalculationMethod"));
			
			Integer tenure = JdbcSupport.getInteger(rs,"tenure");
			EnumOptionData tenureType = SavingProductEnumerations.tenureTypeEnum(JdbcSupport.getInteger(rs, "tenureType"));
			
			LocalDate projectedCommencementDate = JdbcSupport.getLocalDate(rs, "projectedCommencementDate");
			LocalDate actualCommencementDate = JdbcSupport.getLocalDate(rs, "actualCommencementDate");
			LocalDate maturesOnDate = JdbcSupport.getLocalDate(rs, "maturesOnDate");
			BigDecimal projectedInterestAccuredOnMaturity = rs.getBigDecimal("projectedInterestAccuredOnMaturity");
			BigDecimal actualInterestAccured = rs.getBigDecimal("actualInterestAccured");
			
			BigDecimal projectedMaturityAmount = rs.getBigDecimal("projectedTotalMaturityAmount");
			BigDecimal actualMaturityAmount = rs.getBigDecimal("actualTotalAmount");
			
			boolean preClosureAllowed = rs.getBoolean("isPreclosureAllowed");
			BigDecimal preClosureInterestRate = rs.getBigDecimal("preClosureInterestRate");
			
			LocalDate withdrawnonDate = JdbcSupport.getLocalDate(rs, "withdrawnonDate");
			LocalDate rejectedonDate = JdbcSupport.getLocalDate(rs, "rejectedonDate");
			LocalDate closedonDate = JdbcSupport.getLocalDate(rs, "closedonDate");
			
			boolean isLockinPeriodAllowed = rs.getBoolean("isLockinPeriodAllowed");
			Integer lockinPeriod = JdbcSupport.getInteger(rs, "lockinPeriod");
			Integer lockinPeriodTypeValue = JdbcSupport.getInteger(rs, "lockinPeriodType");
			EnumOptionData lockinPeriodType = SavingProductEnumerations.savingsLockinPeriod(lockinPeriodTypeValue);
			
			return new SavingAccountData(id, status, externalId, clientId, clientName, productId, productName, productType, currencyData,
					savingsDepostiAmountPerPeriod, savingsFrequencyType, totalDepositAmount, reccuringInterestRate, savingInterestRate,
					interestType, interestCalculationMethod, tenure, tenureType, projectedCommencementDate, actualCommencementDate,
					maturesOnDate,projectedInterestAccuredOnMaturity, actualInterestAccured, 
					projectedMaturityAmount, actualMaturityAmount, preClosureAllowed, preClosureInterestRate, withdrawnonDate, 
					rejectedonDate, closedonDate, isLockinPeriodAllowed, lockinPeriod, lockinPeriodType);
		}
		
	}

}
