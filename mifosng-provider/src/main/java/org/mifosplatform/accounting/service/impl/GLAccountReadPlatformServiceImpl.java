package org.mifosplatform.accounting.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.accounting.AccountingConstants;
import org.mifosplatform.accounting.AccountingConstants.GL_ACCOUNT_CLASSIFICATION;
import org.mifosplatform.accounting.AccountingConstants.GL_ACCOUNT_USAGE;
import org.mifosplatform.accounting.api.data.GLAccountData;
import org.mifosplatform.accounting.exceptions.GLAccountInvalidClassificationException;
import org.mifosplatform.accounting.exceptions.GLAccountNotFoundException;
import org.mifosplatform.accounting.service.GLAccountReadPlatformService;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class GLAccountReadPlatformServiceImpl implements GLAccountReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GLAccountReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class GLAccountMapper implements RowMapper<GLAccountData> {

        public String schema() {
            return " id as id, name as name, parent_id as parentId, gl_code as glCode, disabled as disabled, manual_journal_entries_allowed as manualEntriesAllowed, "
                    + "classification as classification, header_account as headerAccount, description as description "
                    + "from acc_gl_account";
        }

        @Override
        public GLAccountData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            Long id = rs.getLong("id");
            String name = rs.getString("name");
            Long parentId = rs.getLong("parentId");
            String glCode = rs.getString("glCode");
            boolean disabled = rs.getBoolean("disabled");
            boolean manualEntriesAllowed = rs.getBoolean("manualEntriesAllowed");
            String classification = rs.getString("classification");
            boolean headerAccount = rs.getBoolean("headerAccount");
            String description = rs.getString("description");

            return new GLAccountData(id, name, parentId, glCode, disabled, manualEntriesAllowed, classification, headerAccount, description);
        }
    }

    @Override
    public List<GLAccountData> retrieveAllGLAccounts(String accountClassification, String searchParam, String usage,
            Boolean manualTransactionsAllowed, Boolean disabled) {
        if (StringUtils.isNotBlank(accountClassification)) {
            if (!checkValidGLAccountClassification(accountClassification)) { throw new GLAccountInvalidClassificationException(
                    accountClassification); }
        }

        GLAccountMapper rm = new GLAccountMapper();
        String sql = "select " + rm.schema();
        Object[] paramaterArray = new Object[3];
        int arrayPos = 0;
        boolean filtersPresent = false;
        if (StringUtils.isNotBlank(accountClassification) || StringUtils.isNotBlank(searchParam) || StringUtils.isNotBlank(usage)
                || (manualTransactionsAllowed != null) || (disabled != null)) {
            filtersPresent = true;
            sql += " where";
        }

        if (filtersPresent) {
            boolean firstWhereConditionAdded = false;
            if (StringUtils.isNotBlank(accountClassification)) {
                sql += " classification like ?";
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
            if (StringUtils.isNotBlank(usage)) {
                if (firstWhereConditionAdded) {
                    sql += " and ";
                }
                if (usage.equalsIgnoreCase(GL_ACCOUNT_USAGE.HEADER.toString())) {
                    sql += " header_account = 1 ";
                } else if (usage.equalsIgnoreCase(GL_ACCOUNT_USAGE.DETAIL.toString())) {
                    sql += " header_account = 0 ";
                } else {

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
        Object[] finalObjectArray = Arrays.copyOf(paramaterArray, arrayPos);
        return this.jdbcTemplate.query(sql, rm, finalObjectArray);
    }

    @Override
    public GLAccountData retrieveGLAccountById(long glAccountId) {
        try {

            GLAccountMapper rm = new GLAccountMapper();
            String sql = "select " + rm.schema() + " where id = ?";

            GLAccountData glAccountData = this.jdbcTemplate.queryForObject(sql, rm, new Object[] { glAccountId });

            return glAccountData;
        } catch (EmptyResultDataAccessException e) {
            throw new GLAccountNotFoundException(glAccountId);
        }
    }

    @Override
    public List<GLAccountData> retrieveAllEnabledDetailGLAccounts(GL_ACCOUNT_CLASSIFICATION classification) {
        return retrieveAllGLAccounts(classification.toString(), null, AccountingConstants.GL_ACCOUNT_USAGE.DETAIL.toString(), null, false);
    }

    private static boolean checkValidGLAccountClassification(final String entityType) {
        for (GL_ACCOUNT_CLASSIFICATION classification : GL_ACCOUNT_CLASSIFICATION.values()) {
            if (classification.name().toString().equalsIgnoreCase(entityType)) { return true; }
        }
        return false;
    }

}
