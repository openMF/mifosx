package org.mifosplatform.xbrl.mapping.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.mifosplatform.xbrl.taxonomy.data.TaxonomyData;
import org.mifosplatform.xbrl.taxonomy.service.ReadTaxonomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class ReadTaxonomyMappingServiceImpl implements
ReadTaxonomyMappingService {
	private final JdbcTemplate jdbcTemplate;
	private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");

	private final ReadTaxonomyService readTaxonomyService;
	
	@Autowired
	public ReadTaxonomyMappingServiceImpl(final RoutingDataSource dataSource, final ReadTaxonomyService readTaxonomyService) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.readTaxonomyService = readTaxonomyService;
	}

	private static final class TaxonomyMappingMapper implements RowMapper<TaxonomyMappingData> {

		public String schema() {
			return "identifier, config "
					+ "from m_taxonomy_mapping";
		}

		@Override
		public TaxonomyMappingData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final String identifier = rs.getString("identifier");
			final String config = rs.getString("config");
			return new TaxonomyMappingData(identifier, config);
		}

	}

	@Override
	public TaxonomyMappingData retrieveTaxonomyMapping() {
		try {
			final TaxonomyMappingMapper rm = new TaxonomyMappingMapper();
			String sqlString = "select " + rm.schema();
			return this.jdbcTemplate.queryForObject(sqlString, rm);
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<TaxonomyData, BigDecimal> retrieveTaxonomyConfig() {
		TaxonomyMappingData taxonomyMapping = this.retrieveTaxonomyMapping();
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
