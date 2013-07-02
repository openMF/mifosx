/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    public static final String FIND_GROUP_LOANS_DISBURSED_AFTER = "from Loan l where l.actualDisbursementDate > :disbursementDate and "
            + "l.group.id = :groupId and l.loanType = :loanType order by l.actualDisbursementDate";

    public static final String FIND_CLIENT_OR_JLG_LOANS_DISBURSED_AFTER = "from Loan l where l.actualDisbursementDate > :disbursementDate and "
            + "l.client.id = :clientId and (l.loanType = :indvidualLoanType or l.loanType = :jlgLoanType) order by l.actualDisbursementDate";

    public static final String FIND_MAX_GROUP_LOAN_COUNTER_QUERY = "Select MAX(l.loanCounter) from Loan l where l.group.id = :groupId "
            + "and l.loanType = :loanType";

    public static final String FIND_MAX_GROUP_LOAN_PRODUCT_COUNTER_QUERY = "Select MAX(l.loanProductCounter) from Loan l where "
            + "l.group.id = :groupId and l.loanType = :loanType and l.loanProduct.id = :productId";

    public static final String FIND_MAX_CLIENT_OR_JLG_LOAN_COUNTER_QUERY = "Select MAX(l.loanCounter) from Loan l where "
            + "l.client.id = :clientId and (l.loanType = :indvidualLoanType or l.loanType = :jlgLoanType)";

    public static final String FIND_MAX_CLIENT_OR_JLG_LOAN_PRODUCT_COUNTER_QUERY = "Select MAX(l.loanProductCounter) from Loan l where "
            + "l.client.id = :clientId and (l.loanType = :indvidualLoanType or l.loanType = :jlgLoanType) and l.loanProduct.id = :productId";
    
    public static final String FIND_GROUP_LOANS_TO_UPDATE = "from Loan loan where loan.loanCounter > :loanCounter and "
            + "loan.group.id = :groupId and loan.loanType = :groupLoanType order by loan.loanCounter";
    
    public static final String FIND_CLIENT_OR_JLG_LOANS_TO_UPDATE = "from Loan loan where loan.loanCounter > :loanCounter and "
            + "loan.client.id = :clientId and (loan.loanType = :indvidualLoanType OR loan.loanType = :jlgLoanType)  order by loan.loanCounter";

    @Query("from Loan loan where loan.client.id = :clientId and loan.group.id = :groupId")
    List<Loan> findByClientIdAndGroupId(@Param("clientId") Long clientId, @Param("groupId") Long groupId);

    @Query("from Loan loan where loan.client.id = :clientId")
    List<Loan> findLoanByClientId(@Param("clientId") Long clientId);

    @Query("from Loan loan where loan.id IN :ids and loan.loanStatus IN :loanStatuses and loan.loanType IN :loanTypes")
    List<Loan> findByIdsAndLoanStatusAndLoanType(@Param("ids") Collection<Long> ids,
            @Param("loanStatuses") Collection<Integer> loanStatuses, @Param("loanTypes") Collection<Integer> loanTypes);

    @Query(FIND_GROUP_LOANS_DISBURSED_AFTER)
    List<Loan> getGroupLoansDisbursedAfter(@Param("disbursementDate") Date disbursementDate, @Param("groupId") Long groupId,
            @Param("loanType") Integer loanType);

    @Query(FIND_CLIENT_OR_JLG_LOANS_DISBURSED_AFTER)
    List<Loan> getClientOrJLGLoansDisbursedAfter(@Param("disbursementDate") Date disbursementDate, @Param("clientId") Long clientId,
            @Param("indvidualLoanType") Integer indvidualLoanType, @Param("jlgLoanType") Integer jlgLoanType);

    @Query(FIND_MAX_GROUP_LOAN_COUNTER_QUERY)
    Integer getMaxGroupLoanCounter(@Param("groupId") Long groupId, @Param("loanType") Integer loanType);

    @Query(FIND_MAX_GROUP_LOAN_PRODUCT_COUNTER_QUERY)
    Integer getMaxGroupLoanProductCounter(@Param("productId") Long productId, @Param("groupId") Long groupId,
            @Param("loanType") Integer loanType);

    @Query(FIND_MAX_CLIENT_OR_JLG_LOAN_COUNTER_QUERY)
    Integer getMaxClientOrJLGLoanCounter(@Param("clientId") Long clientId, @Param("indvidualLoanType") Integer indvidualLoanType,
            @Param("jlgLoanType") Integer jlgLoanType);

    @Query(FIND_MAX_CLIENT_OR_JLG_LOAN_PRODUCT_COUNTER_QUERY)
    Integer getMaxClientOrJLGLoanProductCounter(@Param("productId") Long productId, @Param("clientId") Long clientId,
            @Param("indvidualLoanType") Integer indvidualLoanType, @Param("jlgLoanType") Integer jlgLoanType);

    @Query(FIND_GROUP_LOANS_TO_UPDATE)
    List<Loan> getGroupLoansToUpdateLoanCounter(@Param("loanCounter") Integer loanCounter, @Param("groupId") Long groupId,
            @Param("groupLoanType") Integer groupLoanType);

    @Query(FIND_CLIENT_OR_JLG_LOANS_TO_UPDATE)
    List<Loan> getClientOrJLGLoansToUpdateLoanCounter(@Param("loanCounter") Integer loanCounter, @Param("clientId") Long clientId,
            @Param("indvidualLoanType") Integer indvidualLoanType, @Param("jlgLoanType") Integer jlgLoanType);
}