package org.mifosplatform.xbrl.report.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.xbrl.mapping.data.TaxonomyMappingData;
import org.mifosplatform.xbrl.mapping.service.ReadTaxonomyMappingService;
import org.mifosplatform.xbrl.taxonomy.data.TaxonomyData;
import org.mifosplatform.xbrl.taxonomy.data.XBRLData;
import org.mifosplatform.xbrl.taxonomy.service.ReadTaxonomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class XBRLResultServiceImpl implements XBRLResultService {
	
	private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");

	private final ReadTaxonomyMappingService readTaxonomyMappingService;
	private final ReadTaxonomyService readTaxonomyService;
	private final JdbcTemplate jdbcTemplate;
	
	
	@Autowired
	public XBRLResultServiceImpl(final RoutingDataSource dataSource, 
			final ReadTaxonomyMappingService readTaxonomyMappingService,
			final ReadTaxonomyService readTaxonomyService) {
		this.readTaxonomyMappingService = readTaxonomyMappingService;
		this.readTaxonomyService = readTaxonomyService;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/* (non-Javadoc)
	 * @see org.mifosplatform.xbrl.report.service.XBRLResultService#getXBRLResult(java.sql.Date, java.sql.Date, java.lang.String)
	 */
	@Override
	public XBRLData getXBRLResult(Date startDate, Date endDate, String currency) {
		
		return new XBRLData(this.retrieveTaxonomyConfig(),startDate,endDate,currency);
	}

	@SuppressWarnings("unchecked")
	public HashMap<TaxonomyData, BigDecimal> retrieveTaxonomyConfig() {
		TaxonomyMappingData taxonomyMapping = this.readTaxonomyMappingService.retrieveTaxonomyMapping();
		if (taxonomyMapping != null) {
			String config = taxonomyMapping.getConfig();
			if (config != null) {
				// <taxonomyId, mapping>
				HashMap<String,String> configMap = new HashMap<String,String>();
				configMap = new Gson().fromJson(config, configMap.getClass());

				// <taxonomyId, value>
				HashMap<TaxonomyData,BigDecimal> resultMap = new HashMap<TaxonomyData,BigDecimal>();
				for (Entry<String,String> entry : configMap.entrySet()) {
					BigDecimal value = this.processMappingString(entry.getValue());
					if(value != null) {
						TaxonomyData taxonomy = readTaxonomyService.retrieveTaxonomyById(Long.parseLong(entry.getKey()));
						resultMap.put(taxonomy, value);
					}

				}
				return resultMap;
			}

		}

		return null;
	}

	private HashMap<String,BigDecimal> setupBalanceMap() {
		HashMap<String,BigDecimal> accountBalanceMap = new HashMap<String,BigDecimal>();
		String sql = "select debits.glcode as 'glcode', debits.name as 'name', (ifnull(debits.debitamount,0)-ifnull(credits.creditamount,0)) as 'balance' " +
				"from (select acc_gl_account.gl_code as 'glcode',name,sum(amount) as 'debitamount' " +
				"from acc_gl_journal_entry,acc_gl_account " +
				"where acc_gl_account.id = acc_gl_journal_entry.account_id " +
				"and acc_gl_journal_entry.type_enum=2 " +
				"and acc_gl_account.classification_enum in (1) " +
//				"and acc_gl_journal_entry.entry_date <= ${date} " +
//				"and (acc_gl_journal_entry.office_id=${branch} or ${branch}=1) " +
				"group by glcode " +
				"order by glcode) debits " +
				"LEFT OUTER JOIN " +
				"(select acc_gl_account.gl_code as 'glcode',name,sum(amount) as 'creditamount' " +
				"from acc_gl_journal_entry,acc_gl_account " +
				"where acc_gl_account.id = acc_gl_journal_entry.account_id " +
				"and acc_gl_journal_entry.type_enum=1 " +
				"and acc_gl_account.classification_enum in (1) " +
//				"and acc_gl_journal_entry.entry_date <= ${date} " +
//				"and (acc_gl_journal_entry.office_id=${branch} or ${branch}=1) " +
				"group by glcode " +
				"order by glcode) credits " +
				"on debits.glcode=credits.glcode " +
				"union " +
				"select credits.glcode as 'glcode', credits.name as 'name', (ifnull(debits.debitamount,0)-ifnull(credits.creditamount,0)) as 'balance' " +
				"from (select acc_gl_account.gl_code as 'glcode',name,sum(amount) as 'debitamount' " +
				"from acc_gl_journal_entry,acc_gl_account " +
				"where acc_gl_account.id = acc_gl_journal_entry.account_id " +
				"and acc_gl_journal_entry.type_enum=2 " +
				"and acc_gl_account.classification_enum in (1) " +
//				"and acc_gl_journal_entry.entry_date <= ${date} " +
//				"and (acc_gl_journal_entry.office_id=${branch} or ${branch}=1) " +
				"group by glcode " +
				"order by glcode) debits " +
				"RIGHT OUTER JOIN " +
				"(select acc_gl_account.gl_code as 'glcode',name,sum(amount) as 'creditamount' " +
				"from acc_gl_journal_entry,acc_gl_account " +
				"where acc_gl_account.id = acc_gl_journal_entry.account_id " +
				"and acc_gl_journal_entry.type_enum=1 " +
				"and acc_gl_account.classification_enum in (1) " +
//				"and acc_gl_journal_entry.entry_date <= ${date} " +
//				"and (acc_gl_journal_entry.office_id=${branch} or ${branch}=1) " +
				"group by name " +
				"order by glcode) credits " +
				"on debits.glcode=credits.glcode;";
		SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql);
		while (rs.next()) {
			accountBalanceMap.put(rs.getString("glcode"), rs.getBigDecimal("balance"));
		}
		return accountBalanceMap;
	}


	// Calculate Taxonomy value from expression
	BigDecimal processMappingString(String mappingString) {
		ArrayList<String> glCodes = this.getGLCodes(mappingString);
		HashMap<String,BigDecimal> balanceMap = this.setupBalanceMap();
		for (String glcode : glCodes) {

			BigDecimal balance = balanceMap.get(glcode);
			mappingString = mappingString.replaceAll("\\{"+glcode+"\\}", balance!=null ? balance.toString() : "0");
		}

		// evaluate the expression
		Float eval = 0f;
		try {
			Number value = (Number) SCRIPT_ENGINE.eval(mappingString);
			if (value != null) {
				eval = value.floatValue();
			}
		} catch (ScriptException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}

		return new BigDecimal(eval);
	}



	ArrayList<String> getGLCodes(String template) {

		ArrayList<String> placeholders = new ArrayList<String>();

		if (template != null) {

			Pattern p = Pattern.compile("\\{(.*?)\\}");
			Matcher m = p.matcher(template);

			while (m.find()) { // find next match
				String match = m.group();
				String code = match.substring(1, match.length() - 1);
				placeholders.add(code);
			}

		}
		return placeholders;
	}

}
