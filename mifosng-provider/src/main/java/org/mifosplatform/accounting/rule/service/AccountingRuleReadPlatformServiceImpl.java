/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.rule.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.accounting.rule.data.AccountingRuleData;
import org.mifosplatform.accounting.rule.exception.AccountingRuleNotFoundException;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class AccountingRuleReadPlatformServiceImpl implements AccountingRuleReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AccountingRuleReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class AccountingRuleMapper implements RowMapper<AccountingRuleData> {

        private final String schemaSql;

        public AccountingRuleMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(400);
            sqlBuilder.append("distinct rule.id as id,rule.name as name, rule.office_id as officeId,office.name as officeName,")
                    .append(" rule.debit_account_id as debitAccountId,rule.credit_account_id as creditAccountId,")
                    .append(" rule.description as description, rule.system_defined as systemDefined, ")
                    .append("debitAccount.glName as debitAccountName, debitAccount.glCode as debitAccountGLCode,")
                    .append("creditAccount.glName as creditAccountName, creditAccount.glCode as creditAccountGLCode ")
                    .append("from (Select gl.id as id,gl.name as glName, gl.gl_code as glCode from acc_gl_account gl,acc_accounting_rule rule ")
                    .append("where gl.id=rule.debit_account_id ) as debitAccount,")
                    .append("(Select gl.id as id,gl.name as glName, gl.gl_code as glCode from acc_gl_account gl,acc_accounting_rule rule ")
                    .append("where gl.id=rule.credit_account_id ) as creditAccount,")
                    .append("acc_accounting_rule rule left join m_office office on rule.office_id=office.id ")
            		.append("where debitAccount.id=rule.debit_account_id and creditAccount.id = rule.credit_account_id");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public AccountingRuleData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long officeId = JdbcSupport.getLong(rs, "officeId");
            final String officeName = rs.getString("officeName");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final Long accountToDebitId = rs.getLong("debitAccountId");
            final Long accountToCreditId = rs.getLong("creditAccountId");
            final boolean systemDefined = rs.getBoolean("systemDefined");
            final String debitAccountName = rs.getString("debitAccountName");
        	final String creditAccountName = rs.getString("creditAccountName");
        	final String debitAccountGLCode = rs.getString("debitAccountGLCode");
        	final String creditAccountGLCode = rs.getString("creditAccountGLCode");
        	
        	final GLAccountData debitAccountData = new GLAccountData(accountToDebitId, debitAccountName, debitAccountGLCode);
        	final GLAccountData creditAccountData = new GLAccountData(accountToCreditId, creditAccountName, creditAccountGLCode);

            return new AccountingRuleData(id, officeId, officeName, name, description, systemDefined, debitAccountData, creditAccountData);
        }
    }

    @Override
    public List<AccountingRuleData> retrieveAllAccountingRules(Long officeId) {
        final AccountingRuleMapper rm = new AccountingRuleMapper();
        Object[] arguments = new Object[]{};
        String sql = "select " + rm.schema() +" and system_defined=0 ";
        if(officeId != null) {
	        sql = sql +" and office.id = ?";
	        arguments = new Object[] { officeId };
        }
		sql = sql + " order by rule.id asc";
		return this.jdbcTemplate.query(sql, rm, arguments);
    }

    @Override
    public AccountingRuleData retrieveAccountingRuleById(long accountingRuleId) {
        try {

            final AccountingRuleMapper rm = new AccountingRuleMapper();
            final String sql = "select " + rm.schema() + " and rule.id = ?";

            final AccountingRuleData accountingRuleData = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { accountingRuleId });
            return accountingRuleData;
        } catch (final EmptyResultDataAccessException e) {
            throw new AccountingRuleNotFoundException(accountingRuleId);
        }
    }
}
