package org.mifosng.platform.savingproduct.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.mifosng.platform.api.data.CurrencyData;
import org.mifosng.platform.api.data.DepositProductData;
import org.mifosng.platform.api.data.DepositProductLookup;
import org.mifosng.platform.api.data.EnumOptionData;
import org.mifosng.platform.infrastructure.JdbcSupport;
import org.mifosng.platform.infrastructure.TenantAwareRoutingDataSource;
import org.mifosng.platform.loan.domain.PeriodFrequencyType;
import org.mifosng.platform.security.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class DepositProductReadPlatformServiceImpl implements DepositProductReadPlatformService {
	
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public DepositProductReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context=context;
		jdbcTemplate=new JdbcTemplate(dataSource);
	}

	@Override
	public Collection<DepositProductData> retrieveAllDepositProducts() {
		this.context.authenticatedUser();
		DepositProductMapper depositProductMapper= new DepositProductMapper();
		String sql="select "+depositProductMapper.depositProductSchema() + " where dp.is_deleted=0";
		return this.jdbcTemplate.query(sql,depositProductMapper, new Object[]{});
	}

	@Override
	public Collection<DepositProductLookup> retrieveAllDepositProductsForLookup() {
		this.context.authenticatedUser();
		DepositProductLookupMapper depositProductLookupMapper=new DepositProductLookupMapper();
		String sql = "select "+depositProductLookupMapper.depositProductLookupSchema();
		return this.jdbcTemplate.query(sql, depositProductLookupMapper, new Object[]{});
	}

	@Override
	public DepositProductData retrieveDepositProductData(Long productId) {
		DepositProductMapper depositProductMapper=new DepositProductMapper();
		String sql = "select "+ depositProductMapper.depositProductSchema() +" where dp.id = ? and dp.is_deleted=0";
		
		return this.jdbcTemplate.queryForObject(sql, depositProductMapper, new Object[]{productId});
	}

	@Override
	public DepositProductData retrieveNewDepositProductDetails() {
		
		List<CurrencyData> currencyOptions = null;
		List<EnumOptionData> interestCompoundedEveryPeriodTypeOptions = null;
		EnumOptionData monthly = SavingsDepositEnumerations.interestCompoundingPeriodType(PeriodFrequencyType.MONTHS);
		
		return new DepositProductData(currencyOptions, monthly, interestCompoundedEveryPeriodTypeOptions);
	}
	
	private static final class DepositProductMapper implements RowMapper<DepositProductData>{
		
		public String depositProductSchema(){
			return " dp.id as id, dp.external_id as exernalId, dp.name as name, dp.description as description,dp.currency_code as currencyCode, dp.currency_digits as currencyDigits,dp.minimum_balance as minimumBalance,dp.maximum_balance as maximumBalance," +
					"dp.created_date as createdon, dp.lastmodified_date as modifiedon,dp.tenure_months as tenureMonths, " +
					"dp.interest_compounded_every as interestCompoundedEvery, dp.interest_compounded_every_period_enum as interestCompoundedEveryPeriodType, " +
					"dp.maturity_default_interest_rate as maturityDefaultInterestRate, dp.is_compounding_interest_allowed as interestCompoundingAllowed, "  +
					"dp.maturity_min_interest_rate as maturityMinInterestRate, dp.maturity_max_interest_rate as maturityMaxInterestRate, dp.is_renewal_allowed as canRenew, dp.is_preclosure_allowed as canPreClose, dp.pre_closure_interest_rate as preClosureInterestRate, " +
					"curr.name as currencyName, curr.internationalized_name_code as currencyNameCode, curr.display_symbol as currencyDisplaySymbol " +
					"from m_product_deposit dp join m_currency curr on curr.code = dp.currency_code ";
		}

		@Override
		public DepositProductData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			String exernalId = rs.getString("exernalId");
			String name = rs.getString("name");
			String description = rs.getString("description");
			
			String currencyCode = rs.getString("currencyCode");
			Integer currencyDigits = JdbcSupport.getInteger(rs,"currencyDigits");
			
			DateTime createdOn = JdbcSupport.getDateTime(rs, "createdon");
			DateTime lastModifedOn = JdbcSupport.getDateTime(rs, "modifiedon");
			
			BigDecimal minimumBalance=rs.getBigDecimal("minimumBalance");
			BigDecimal maximumBalance=rs.getBigDecimal("maximumBalance");
			
			Integer tenureMonths = JdbcSupport.getInteger(rs, "tenureMonths");
			Integer interestCompoundedEvery = JdbcSupport.getInteger(rs, "interestCompoundedEvery");
			Integer interestCompoundedEveryPeriodTypeValue = JdbcSupport.getInteger(rs, "interestCompoundedEveryPeriodType");
			EnumOptionData interestCompoundedEveryPeriodType = SavingsDepositEnumerations.interestCompoundingPeriodType(interestCompoundedEveryPeriodTypeValue);
			
			BigDecimal maturityDefaultInterestRate=rs.getBigDecimal("maturityDefaultInterestRate");
			BigDecimal maturityMinInterestRate= rs.getBigDecimal("maturityMinInterestRate");
			BigDecimal maturityMaxInterestRate = rs.getBigDecimal("maturityMaxInterestRate");
			
			Boolean canRenew = rs.getBoolean("canRenew");
			Boolean canPreClose=rs.getBoolean("canPreClose");
			Boolean interestCompoundingAllowed= rs.getBoolean("interestCompoundingAllowed");
			
			BigDecimal preClosureInterestRate=rs.getBigDecimal("preClosureInterestRate");
			
			return new DepositProductData(createdOn, lastModifedOn, id, exernalId, name, description, currencyCode, currencyDigits, minimumBalance, maximumBalance,
					tenureMonths, maturityDefaultInterestRate,maturityMinInterestRate,maturityMaxInterestRate, 
					interestCompoundedEvery, interestCompoundedEveryPeriodType,
					canRenew,canPreClose,preClosureInterestRate,interestCompoundingAllowed);
		}
		
	}
	
	private static final class DepositProductLookupMapper implements RowMapper<DepositProductLookup> {

		public String depositProductLookupSchema() {
			return "dp.id as id, dp.name as name from m_product_deposit dp";
		}

		@Override
		public DepositProductLookup mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String name = rs.getString("name");

			return new DepositProductLookup(id, name);
		}

	}
}