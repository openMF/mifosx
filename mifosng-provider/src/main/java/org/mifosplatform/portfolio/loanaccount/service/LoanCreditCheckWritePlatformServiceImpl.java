/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetRowData;
import org.mifosplatform.infrastructure.dataqueries.domain.Report;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.creditcheck.CreditCheckConstants;
import org.mifosplatform.portfolio.creditcheck.data.CreditCheckReportParamData;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheck;
import org.mifosplatform.portfolio.creditcheck.domain.CreditCheckSeverityLevel;
import org.mifosplatform.portfolio.creditcheck.service.CreditCheckReportParamReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCreditCheck;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCreditCheckRepository;
import org.mifosplatform.portfolio.loanaccount.exception.LoanCreditCheckFailedException;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanCreditCheckWritePlatformServiceImpl implements LoanCreditCheckWritePlatformService {
    private final PlatformSecurityContext platformSecurityContext;
    private final CreditCheckReportParamReadPlatformService creditCheckReportParamReadPlatformService;
    private final LoanCreditCheckRepository loanCreditCheckRepository;
    private final GenericDataService genericDataService;
    private final static Logger logger = LoggerFactory.getLogger(LoanCreditCheckWritePlatformServiceImpl.class);
    
    @Autowired
    public LoanCreditCheckWritePlatformServiceImpl(final PlatformSecurityContext platformSecurityContext, 
            final CreditCheckReportParamReadPlatformService creditCheckReportParamReadPlatformService, 
            final LoanCreditCheckRepository loanCreditCheckRepository, 
            final GenericDataService genericDataService) {
        this.platformSecurityContext = platformSecurityContext;
        this.creditCheckReportParamReadPlatformService = creditCheckReportParamReadPlatformService;
        this.loanCreditCheckRepository = loanCreditCheckRepository;
        this.genericDataService = genericDataService;
    }

    @Override
    @Transactional
    public Collection<LoanCreditCheck> triggerLoanCreditChecks(final Long loanId) {
        final AppUser appUser = this.platformSecurityContext.authenticatedUser();
        final Collection<LoanCreditCheck> currentLoanCreditChecks = this.loanCreditCheckRepository.findByLoanId(loanId);
        final Collection<LoanCreditCheck> loanCreditChecks = new ArrayList<>();
        
        for (LoanCreditCheck loanCreditCheck : currentLoanCreditChecks) {
            if (!loanCreditCheck.hasBeenTriggered() && loanCreditCheck.isActive()) {
                final CreditCheck creditCheck = loanCreditCheck.getCreditCheck();
                final Report stretchyReport = creditCheck.getStretchyReport();
                final GenericResultsetData genericResultset = retrieveGenericResultsetForCreditCheck(stretchyReport, 
                        loanId, appUser.getId());
                String creditCheckResult = null;
                final List<ResultsetRowData> resultsetData = genericResultset.getData();
                List<String> resultsetRow = null;
                
                if (resultsetData != null && (resultsetData.get(0) != null)) {
                    resultsetRow = resultsetData.get(0).getRow();
                    creditCheckResult = resultsetRow.get(0);
                }
                
                loanCreditCheck.trigger(appUser, new LocalDate(), creditCheckResult);
                
                this.loanCreditCheckRepository.saveAndFlush(loanCreditCheck);
            }
            
            loanCreditChecks.add(loanCreditCheck);
        }
        
        return loanCreditChecks;
    }
    
    private GenericResultsetData retrieveGenericResultsetForCreditCheck(final Report stretchyReport, final Long loanId, 
            final Long userId) {
        final long startTime = System.currentTimeMillis();
        logger.info("STARTING REPORT: " + stretchyReport.getReportName() + "   Type: " + stretchyReport.getReportType());
        
        final String sql = searchAndReplaceParamsInSQLString(loanId, userId, stretchyReport.getReportSql());
        final GenericResultsetData result = this.genericDataService.fillGenericResultSet(sql);
        
        logger.info("SQL: " + sql);
        
        final long elapsed = System.currentTimeMillis() - startTime;
        logger.info("FINISHING REPORT: " + stretchyReport.getReportName() + " - " + stretchyReport.getReportType() 
                + "     Elapsed Time: " + elapsed + " milliseconds");
        
        return result;
    }
    
    private String searchAndReplaceParamsInSQLString(final Long loanId, final Long userId, String sql) {
        CreditCheckReportParamData creditCheckReportParamData = this.creditCheckReportParamReadPlatformService
                .retrieveCreditCheckReportParameters(loanId, userId);
        
        sql = this.genericDataService.replace(sql, CreditCheckConstants.CLIENT_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getClientId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.GROUP_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getGroupId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.LOAN_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getLoanId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.USER_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getUserId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.STAFF_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getStaffId().toString());
        sql = this.genericDataService.replace(sql, CreditCheckConstants.OFFICE_ID_PARAM_PATTERN, 
                creditCheckReportParamData.getOfficeId().toString());
        
        return this.genericDataService.wrapSQL(sql);
    }
    
    /** 
     * Iterate through list of loan credit checks and throw an exception if anyone with error severity failed
     * 
     * @param loanId -- the identifier of the loan to be checked
     * @return None 
     **/
    @Override
    public void runLoanCreditChecks(final Long loanId) {
        final Collection<LoanCreditCheck> loanCreditCheckList = triggerLoanCreditChecks(loanId);
        
        for (LoanCreditCheck loanCreditCheck : loanCreditCheckList) {
            final CreditCheckSeverityLevel severityLevel = CreditCheckSeverityLevel.fromInt(loanCreditCheck.getSeverityLevelEnumValue());
            
            if (!loanCreditCheck.actualResultEqualsExpectedResult() && severityLevel.isError()) {
                CreditCheck creditCheck = loanCreditCheck.getCreditCheck();
                
                throw new LoanCreditCheckFailedException(loanId, creditCheck.getId(), loanCreditCheck.getMessage());
            }
        }
    }
}
