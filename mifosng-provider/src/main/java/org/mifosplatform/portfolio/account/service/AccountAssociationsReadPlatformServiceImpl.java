package org.mifosplatform.portfolio.account.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.account.data.AccountAssociationsData;
import org.mifosplatform.portfolio.account.data.PortfolioAccountData;
import org.mifosplatform.portfolio.loanaccount.domain.LoanStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class AccountAssociationsReadPlatformServiceImpl implements AccountAssociationsReadPlatformService {

    private final static Logger logger = LoggerFactory.getLogger(AccountAssociationsReadPlatformServiceImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AccountAssociationsReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public PortfolioAccountData retriveLoanAssociation(final Long loanId) {
        PortfolioAccountData linkedAccount = null;
        AccountAssociationsMapper mapper = new AccountAssociationsMapper();
        String sql = "select " + mapper.schema() + " where aa.loan_account_id = ? ";
        try {
            AccountAssociationsData accountAssociationsData = jdbcTemplate.queryForObject(sql, mapper, loanId);
            if (accountAssociationsData != null) {
                linkedAccount = accountAssociationsData.linkedAccount();
            }
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Linking account is not configured");
        }
        return linkedAccount;
    }

    @Override
    public boolean isLinkedWithAnyActiveLoan(final Long savingsId) {
        boolean hasActiveLoan = false;
        String sql = "select loanAccount.loan_status_id as status from m_portfolio_account_associations aa " +
        		"left join m_loan loanAccount on loanAccount.id = aa.loan_account_id " +
        		"where aa.linked_savings_account_id = ?";
        
        List<Integer> statusList= this.jdbcTemplate.queryForList(sql,Integer.class, savingsId);
        for(Integer status:statusList){
            LoanStatus loanStatus= LoanStatus.fromInt(status);
              if(loanStatus.isActiveOrAwaitingApprovalOrDisbursal() || loanStatus.isUnderTransfer()){
                  hasActiveLoan = true;
                  break;
              }
        }
        return hasActiveLoan;
    }

    private static final class AccountAssociationsMapper implements RowMapper<AccountAssociationsData> {

        private final String schemaSql;

        public AccountAssociationsMapper() {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("aa.id as id,");
            // sqlBuilder.append("savingsAccount.id as savingsAccountId, savingsAccount.account_no as savingsAccountNo,");
            sqlBuilder.append("loanAccount.id as loanAccountId, loanAccount.account_no as loanAccountNo,");
            // sqlBuilder.append("linkLoanAccount.id as linkLoanAccountId, linkLoanAccount.account_no as linkLoanAccountNo, ");
            sqlBuilder.append("linkSavingsAccount.id as linkSavingsAccountId, linkSavingsAccount.account_no as linkSavingsAccountNo ");
            sqlBuilder.append("from m_portfolio_account_associations aa ");
            // sqlBuilder.append("left join m_savings_account savingsAccount on savingsAccount.id = aa.savings_account_id ");
            sqlBuilder.append("left join m_loan loanAccount on loanAccount.id = aa.loan_account_id ");
            sqlBuilder.append("left join m_savings_account linkSavingsAccount on linkSavingsAccount.id = aa.linked_savings_account_id ");
            // sqlBuilder.append("left join m_loan linkLoanAccount on linkLoanAccount.id = aa.linked_loan_account_id ");
            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public AccountAssociationsData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            // final Long savingsAccountId = JdbcSupport.getLong(rs,
            // "savingsAccountId");
            // final String savingsAccountNo = rs.getString("savingsAccountNo");
            final Long loanAccountId = JdbcSupport.getLong(rs, "loanAccountId");
            final String loanAccountNo = rs.getString("loanAccountNo");
            PortfolioAccountData account = PortfolioAccountData.lookup(loanAccountId, loanAccountNo);
            ;
            /*
             * if (savingsAccountId != null) { account =
             * PortfolioAccountData.lookup(savingsAccountId, savingsAccountNo);
             * } else if (loanAccountId != null) { account =
             * PortfolioAccountData.lookup(loanAccountId, loanAccountNo); }
             */
            final Long linkSavingsAccountId = JdbcSupport.getLong(rs, "linkSavingsAccountId");
            final String linkSavingsAccountNo = rs.getString("linkSavingsAccountNo");
            // final Long linkLoanAccountId = JdbcSupport.getLong(rs,
            // "linkLoanAccountId");
            // final String linkLoanAccountNo =
            // rs.getString("linkLoanAccountNo");
            PortfolioAccountData linkedAccount = PortfolioAccountData.lookup(linkSavingsAccountId, linkSavingsAccountNo);
            /*
             * if (linkSavingsAccountId != null) { linkedAccount =
             * PortfolioAccountData.lookup(linkSavingsAccountId,
             * linkSavingsAccountNo); } else if (linkLoanAccountId != null) {
             * linkedAccount = PortfolioAccountData.lookup(linkLoanAccountId,
             * linkLoanAccountNo); }
             */

            return new AccountAssociationsData(id, account, linkedAccount);
        }

    }
}
