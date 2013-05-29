/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.accounting.glaccount.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.accounting.common.AccountingEnumerations;
import org.mifosplatform.accounting.glaccount.data.GLAccountData;
import org.mifosplatform.accounting.glaccount.data.GLAccountDataForLookup;
import org.mifosplatform.accounting.glaccount.domain.GLAccountType;
import org.mifosplatform.accounting.glaccount.domain.GLAccountUsage;
import org.mifosplatform.accounting.glaccount.exception.GLAccountInvalidClassificationException;
import org.mifosplatform.accounting.glaccount.exception.GLAccountNotFoundException;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GLAccountReadPlatformServiceImpl implements GLAccountReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final static String nameDecoratedBaseOnHierarchy = "concat(substring('........................................', 1, ((LENGTH(hierarchy) - LENGTH(REPLACE(hierarchy, '.', '')) - 1) * 4)), name)";

    @Autowired
    public GLAccountReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class GLAccountMapper implements RowMapper<GLAccountData> {

        public String schema() {
            return " gl.id as id, name as name, parent_id as parentId, gl_code as glCode, disabled as disabled, manual_journal_entries_allowed as manualEntriesAllowed, "
                    + "classification_enum as classification, account_usage as accountUsage, description as description, "
                    + nameDecoratedBaseOnHierarchy
                    + "as nameDecorated, "
                    + "cv.id as codeId, cv.code_value as codeValue from acc_gl_account gl left join m_code_value cv on tag_id=cv.id";
        }

        @Override
        public GLAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final Long parentId = JdbcSupport.getLong(rs, "parentId");
            final String glCode = rs.getString("glCode");
            final boolean disabled = rs.getBoolean("disabled");
            final boolean manualEntriesAllowed = rs.getBoolean("manualEntriesAllowed");
            final int accountTypeId = JdbcSupport.getInteger(rs, "classification");
            final EnumOptionData accountType = AccountingEnumerations.gLAccountType(accountTypeId);
            final int usageId = JdbcSupport.getInteger(rs, "accountUsage");
            final EnumOptionData usage = AccountingEnumerations.gLAccountUsage(usageId);
            final String description = rs.getString("description");
            final String nameDecorated = rs.getString("nameDecorated");
            final Long codeId = rs.wasNull() ? null : rs.getLong("codeId");
            final String codeValue = rs.getString("codeValue");
            final CodeValueData tagId = CodeValueData.instance(codeId, codeValue);
            return new GLAccountData(id, name, parentId, glCode, disabled, manualEntriesAllowed, accountType, usage, description,
                    nameDecorated, tagId);
        }
    }

    @Override
    public List<GLAccountData> retrieveAllGLAccounts(final Integer accountClassification, final String searchParam, final Integer usage,
            final Boolean manualTransactionsAllowed, final Boolean disabled) {
        if (accountClassification != null) {
            if (!checkValidGLAccountType(accountClassification)) { throw new GLAccountInvalidClassificationException(accountClassification); }
        }

        if (usage != null) {
            if (!checkValidGLAccountUsage(usage)) { throw new GLAccountInvalidClassificationException(accountClassification); }
        }

        final GLAccountMapper rm = new GLAccountMapper();
        String sql = "select " + rm.schema();
        final Object[] paramaterArray = new Object[3];
        int arrayPos = 0;
        boolean filtersPresent = false;
        if ((accountClassification != null) || StringUtils.isNotBlank(searchParam) || (usage != null)
                || (manualTransactionsAllowed != null) || (disabled != null)) {
            filtersPresent = true;
            sql += " where";
        }

        if (filtersPresent) {
            boolean firstWhereConditionAdded = false;
            if (accountClassification != null) {
                sql += " classification_enum like ?";
                paramaterArray[arrayPos] = accountClassification;
                arrayPos = arrayPos + 1;
                firstWhereConditionAdded = true;
            }
            if (StringUtils.isNotBlank(searchParam)) {
                if (firstWhereConditionAdded) {
                    sql += " and ";
                }
                sql += " ( name like %?% or gl_code like %?% )";
                paramaterArray[arrayPos] = searchParam;
                arrayPos = arrayPos + 1;
                paramaterArray[arrayPos] = searchParam;
                arrayPos = arrayPos + 1;
                firstWhereConditionAdded = true;
            }
            if (usage != null) {
                if (firstWhereConditionAdded) {
                    sql += " and ";
                }
                if (GLAccountUsage.HEADER.getValue().equals(usage)) {
                    sql += " account_usage = 2 ";
                } else if (GLAccountUsage.DETAIL.getValue().equals(usage)) {
                    sql += " account_usage = 1 ";
                }
                firstWhereConditionAdded = true;
            }
            if (manualTransactionsAllowed != null) {
                if (firstWhereConditionAdded) {
                    sql += " and ";
                }

                if (manualTransactionsAllowed) {
                    sql += " manual_journal_entries_allowed = 1";
                } else {
                    sql += " manual_journal_entries_allowed = 0";
                }
                firstWhereConditionAdded = true;
            }
            if (disabled != null) {
                if (firstWhereConditionAdded) {
                    sql += " and ";
                }

                if (disabled) {
                    sql += " disabled = 1";
                } else {
                    sql += " disabled = 0";
                }
                firstWhereConditionAdded = true;
            }
        }

        sql = sql + " order by glCode";
        final Object[] finalObjectArray = Arrays.copyOf(paramaterArray, arrayPos);
        return this.jdbcTemplate.query(sql, rm, finalObjectArray);
    }

    @Override
    public GLAccountData retrieveGLAccountById(final long glAccountId) {
        try {

            final GLAccountMapper rm = new GLAccountMapper();
            final String sql = "select " + rm.schema() + " where gl.id = ?";

            final GLAccountData glAccountData = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { glAccountId });

            return glAccountData;
        } catch (final EmptyResultDataAccessException e) {
            throw new GLAccountNotFoundException(glAccountId);
        }
    }

    @Override
    public List<GLAccountData> retrieveAllEnabledDetailGLAccounts(final GLAccountType accountType) {
        return retrieveAllGLAccounts(accountType.getValue(), null, GLAccountUsage.DETAIL.getValue(), null, false);
    }

    @Override
    public List<GLAccountData> retrieveAllEnabledDetailGLAccounts() {
        return retrieveAllGLAccounts(null, null, GLAccountUsage.DETAIL.getValue(), null, false);
    }

    private static boolean checkValidGLAccountType(final int type) {
        for (final GLAccountType accountType : GLAccountType.values()) {
            if (accountType.getValue().equals(type)) { return true; }
        }
        return false;
    }

    private static boolean checkValidGLAccountUsage(final int type) {
        for (final GLAccountUsage accountUsage : GLAccountUsage.values()) {
            if (accountUsage.getValue().equals(type)) { return true; }
        }
        return false;
    }

    @Override
    public GLAccountData retrieveNewGLAccountDetails(final Integer type) {
        return GLAccountData.sensibleDefaultsForNewGLAccountCreation(type);
    }

    @Override
    public List<GLAccountData> retrieveAllEnabledHeaderGLAccounts(final GLAccountType accountType) {
        return retrieveAllGLAccounts(accountType.getValue(), null, GLAccountUsage.HEADER.getValue(), null, false);
    }

    @Override
    public List<GLAccountDataForLookup> retrieveAccountsByTagId(final Long ruleId, final Integer transactionType) {
        final GLAccountDataLookUpMapper mapper = new GLAccountDataLookUpMapper();
        final String sql = "Select " + mapper.schema() + " where rule.id=? and tags.acc_type_enum=?";
        return this.jdbcTemplate.query(sql, mapper, new Object[] { ruleId, transactionType });
    }

    private static final class GLAccountDataLookUpMapper implements RowMapper<GLAccountDataForLookup> {

        public String schema() {
            return " gl.id as id from acc_accounting_rule rule join acc_rule_tags tags on tags.acc_rule_id = rule.id join acc_gl_account gl on gl.tag_id=tags.tag_id";
        }

        @Override
        public GLAccountDataForLookup mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            return new GLAccountDataForLookup(id);
        }

    }
}