package org.mifosplatform.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonNull;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepository;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.organisation.monetary.exception.CurrencyNotFoundException;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.group.data.GroupData;
import org.mifosplatform.portfolio.group.service.GroupReadPlatformService;
import org.mifosplatform.portfolio.loanaccount.data.DisbursementData;
import org.mifosplatform.portfolio.loanaccount.data.GroupLoanBasicDetailsData;
import org.mifosplatform.portfolio.loanaccount.data.LoanBasicDetailsData;
import org.mifosplatform.portfolio.loanaccount.data.LoanChargeData;
import org.mifosplatform.portfolio.loanaccount.data.LoanPermissionData;
import org.mifosplatform.portfolio.loanaccount.data.LoanTransactionData;
import org.mifosplatform.portfolio.loanaccount.domain.GroupLoan;
import org.mifosplatform.portfolio.loanaccount.domain.GroupLoanRepository;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepository;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransaction;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransactionRepository;
import org.mifosplatform.portfolio.loanaccount.domain.LoanTransactionType;
import org.mifosplatform.portfolio.loanaccount.exception.LoanNotFoundException;
import org.mifosplatform.portfolio.loanaccount.exception.LoanTransactionNotFoundException;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.mifosplatform.portfolio.loanproduct.data.LoanProductData;
import org.mifosplatform.portfolio.loanproduct.service.LoanEnumerations;
import org.mifosplatform.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class LoanReadPlatformServiceImpl implements LoanReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final LoanRepository loanRepository;
    private final GroupLoanRepository groupLoanRepository;
	private final ApplicationCurrencyRepository applicationCurrencyRepository;
	private final LoanProductReadPlatformService loanProductReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final GroupReadPlatformService groupReadPlatformService;
	private final LoanTransactionRepository loanTransactionRepository;

	@Autowired
	public LoanReadPlatformServiceImpl(
			final PlatformSecurityContext context,
			final LoanRepository loanRepository,
            final GroupLoanRepository groupLoanRepository,
			final LoanTransactionRepository loanTransactionRepository,
			final ApplicationCurrencyRepository applicationCurrencyRepository,
			final LoanProductReadPlatformService loanProductReadPlatformService,
			final ClientReadPlatformService clientReadPlatformService,
			final GroupReadPlatformService groupReadPlatformService,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.loanRepository = loanRepository;
        this.groupLoanRepository = groupLoanRepository;
		this.loanTransactionRepository = loanTransactionRepository;
		this.applicationCurrencyRepository = applicationCurrencyRepository;
		this.loanProductReadPlatformService = loanProductReadPlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
		this.groupReadPlatformService = groupReadPlatformService;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public LoanBasicDetailsData retrieveLoanAccountDetails(final Long loanId) {

		try {
			context.authenticatedUser();

			LoanMapper rm = new LoanMapper();
			
			String sql = "select " + rm.loanSchema() + " where l.id = ?";
			
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { loanId });

		} catch (EmptyResultDataAccessException e) {
			throw new LoanNotFoundException(loanId);
		}

	}

    @Override
    public GroupLoanBasicDetailsData retrieveGroupLoanAccountDetails(Long groupLoanId) {

        try {

            context.authenticatedUser();

            GroupLoanMapper rm = new GroupLoanMapper();

            String sql = "select " + rm.groupLoanSchema() + " where gl.id = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { groupLoanId });
        } catch (EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(groupLoanId);
        }

    }

    @Override
    public Collection<LoanBasicDetailsData> retrieveGroupLoanMembersAccountsDetails(Long groupLoanId) {

        this.context.authenticatedUser();

        LoanMapper rm = new LoanMapper();

        String sql = "select " + rm.loanSchema() + " where l.group_loan_id = ?";

        return this.jdbcTemplate.query(sql, rm, new Object[] {groupLoanId});
    }

    @Override
	public LoanScheduleData retrieveRepaymentSchedule(final Long loanId, 
			final CurrencyData currency, 
			final DisbursementData disbursement,
			final BigDecimal totalFeeChargesAtDisbursement,
			final BigDecimal inArrearsTolerance) {

		try {
			context.authenticatedUser();

			final LoanScheduleMapper rm = new LoanScheduleMapper(disbursement);
			final String sql = "select " + rm.loanScheduleSchema() + " where l.id = ? order by ls.loan_id, ls.installment";
			
			final LoanSchedulePeriodData disbursementPeriod = LoanSchedulePeriodData.disbursementOnlyPeriod(disbursement.disbursementDate(), disbursement.amount(), totalFeeChargesAtDisbursement, disbursement.isDisbursed());
			
			final Collection<LoanSchedulePeriodData> repaymentSchedulePeriods = this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
			
			final Collection<LoanSchedulePeriodData> periods = new ArrayList<LoanSchedulePeriodData>(repaymentSchedulePeriods.size()+1);
			periods.add(disbursementPeriod);
			periods.addAll(repaymentSchedulePeriods);
			
			LoanSchedulePeriodDataWrapper wrapper = new LoanSchedulePeriodDataWrapper(periods);
			
			final Integer loanTermInDays = wrapper.deriveCumulativeLoanTermInDays();
			
			final BigDecimal cumulativePrincipalDisbursed = wrapper.deriveCumulativePrincipalDisbursed();
			final BigDecimal cumulativePrincipalDue = wrapper.deriveCumulativePrincipalDue();
			final BigDecimal cumulativePrincipalPaid = wrapper.deriveCumulativePrincipalPaid();
			final BigDecimal cumulativePrincipalWrittenOff = wrapper.deriveCumulativePrincipalWrittenOff();
			final BigDecimal cumulativePrincipalOutstanding = wrapper.deriveCumulativePrincipalOutstanding();
			
			final BigDecimal cumulativeInterestExpected =  wrapper.deriveCumulativeInterestExpected();
			final BigDecimal cumulativeInterestPaid = wrapper.deriveCumulativeInterestPaid();
			final BigDecimal cumulativeInterestWaived = wrapper.deriveCumulativeInterestWaived();
			final BigDecimal cumulativeInterestWrittenOff = wrapper.deriveCumulativeInterestWrittenOff();
			final BigDecimal cumulativeInterestOutstanding = wrapper.deriveCumulativeInterestOutstanding();
			
			final BigDecimal cumulativeFeeChargesExpected = wrapper.deriveCumulativeFeeChargesToDate();
			final BigDecimal cumulativeFeeChargesPaid = wrapper.deriveCumulativeFeeChargesPaid();
			final BigDecimal cumulativeFeeChargesWaived = wrapper.deriveCumulativeFeeChargesWaived();
			final BigDecimal cumulativeFeeChargesWrittenOff = wrapper.deriveCumulativeFeeChargesWrittenOff();
			final BigDecimal cumulativeFeeChargesOutstanding = wrapper.deriveCumulativeFeeChargesOutstanding();
			
			final BigDecimal cumulativePenaltyChargesExpected = wrapper.deriveCumulativePenaltyChargesToDate();
			final BigDecimal cumulativePenaltyChargesPaid = wrapper.deriveCumulativePenaltyChargesPaid();
			final BigDecimal cumulativePenaltyChargesWaived = wrapper.deriveCumulativePenaltyChargesWaived();
			final BigDecimal cumulativePenaltyChargesWrittenOff = wrapper.deriveCumulativePenaltyChargesWrittenOff();
			final BigDecimal cumulativePenaltyChargesOutstanding = wrapper.deriveCumulativePenaltyChargesOutstanding();
			
			final BigDecimal totalExpectedCostOfLoan = cumulativeInterestExpected.add(cumulativeFeeChargesExpected).add(cumulativePenaltyChargesExpected);
			final BigDecimal totalExpectedRepayment = cumulativePrincipalDisbursed.add(totalExpectedCostOfLoan);
			
			final BigDecimal totalPaidToDate = cumulativePrincipalPaid.add(cumulativeInterestPaid).add(cumulativeFeeChargesPaid).add(cumulativePenaltyChargesPaid);
			final BigDecimal totalWaivedToDate = cumulativeInterestWaived.add(cumulativeFeeChargesWaived).add(cumulativePenaltyChargesWaived);
			final BigDecimal totalWrittenOffToDate = cumulativePrincipalWrittenOff.add(cumulativeInterestWrittenOff).add(cumulativeFeeChargesWrittenOff).add(cumulativePenaltyChargesWrittenOff);
			
			final BigDecimal totalOutstanding = cumulativePrincipalOutstanding.add(cumulativeInterestOutstanding).add(cumulativeFeeChargesOutstanding).add(cumulativePenaltyChargesOutstanding);

			final BigDecimal totalOverdue = wrapper.deriveCumulativeTotalOverdue();
			
			final MonetaryCurrency monetaryCurrency = new MonetaryCurrency(currency.code(), currency.decimalPlaces());
			final Money tolerance = Money.of(monetaryCurrency, inArrearsTolerance);
			final Money totalOverdueMoney = Money.of(monetaryCurrency, totalOverdue);
			boolean isWaiveAllowed = totalOverdueMoney.isGreaterThanZero() && (tolerance.isGreaterThan(totalOverdueMoney) || tolerance.isEqualTo(totalOverdueMoney));

			BigDecimal totalInArrears = null;
			if (!isWaiveAllowed) {
				totalInArrears = totalOverdueMoney.getAmount();
			}
			
			return new LoanScheduleData(currency, periods, loanTermInDays, 
					cumulativePrincipalDisbursed, cumulativePrincipalDue, cumulativePrincipalPaid, cumulativePrincipalWrittenOff, cumulativePrincipalOutstanding, 
					cumulativeInterestExpected, cumulativeInterestPaid, cumulativeInterestWaived, cumulativeInterestWrittenOff, cumulativeInterestOutstanding, 
					cumulativeFeeChargesExpected, cumulativeFeeChargesPaid, cumulativeFeeChargesWaived, cumulativeFeeChargesWrittenOff, cumulativeFeeChargesOutstanding, 
					cumulativePenaltyChargesExpected, cumulativePenaltyChargesPaid, cumulativePenaltyChargesWaived, cumulativePenaltyChargesWrittenOff, cumulativePenaltyChargesOutstanding, 
					totalExpectedCostOfLoan, totalExpectedRepayment, totalPaidToDate, totalWaivedToDate, totalWrittenOffToDate, totalOutstanding, totalInArrears);
		} catch (EmptyResultDataAccessException e) {
			throw new LoanNotFoundException(loanId);
		}
	}

    /**
     *  Currently, just aggregate member loan accounts schedule into one.
     */
    @Override
    public LoanScheduleData retrieveGroupRepaymentSchedule(Long groupLoanId, CurrencyData currency, DisbursementData disbursement,
            BigDecimal inArrearsTolerance) {

        try {
            this.context.authenticatedUser();
            GroupLoan groupLoan = this.groupLoanRepository.findOne(groupLoanId);

            final LoanScheduleMapper rm = new LoanScheduleMapper(disbursement);
            final String sql = "select " + rm.loanScheduleSchema() + " where l.id = ? order by ls.loan_id, ls.installment";

            final List<LoanSchedulePeriodData> groupPeriods = new ArrayList<LoanSchedulePeriodData>();
            final List<LoanSchedulePeriodData> membersDisbursementPeriods = new ArrayList<LoanSchedulePeriodData>();
            final List<List<LoanSchedulePeriodData>> membersPeriods = new ArrayList<List<LoanSchedulePeriodData>>();

            Integer groupPeriodsSize = null;

            for (Loan member : groupLoan.getMemberLoans()) {

                final LoanSchedulePeriodData memberDisbursementPeriod = LoanSchedulePeriodData.disbursementOnlyPeriod(
                        disbursement.disbursementDate(), member.getPrincpal().getAmount(), member.getTotalChargesDueAtDisbursement().getAmount(),
                        disbursement.isDisbursed());

                rm.updateDisbursementData(new DisbursementData(member.getExpectedDisbursedOnLocalDate(), member.getDisbursementDate(), member.getPrincpal().getAmount()));
                final Collection<LoanSchedulePeriodData> memberRepaymentSchedulePeriods = this.jdbcTemplate.query(sql, rm, new Object[] { member.getId() });

                final List<LoanSchedulePeriodData> memberPeriods = new ArrayList<LoanSchedulePeriodData>(memberRepaymentSchedulePeriods.size());

                memberPeriods.addAll(memberRepaymentSchedulePeriods);
                membersDisbursementPeriods.add(memberDisbursementPeriod);
                membersPeriods.add(memberPeriods);

                if (groupPeriodsSize == null){
                    groupPeriodsSize = memberPeriods.size();
                }
            }

            BigDecimal groupDisbursementAmount = BigDecimal.ZERO;
            BigDecimal groupFeeChargesAtDisbursement = BigDecimal.ZERO;

            for (LoanSchedulePeriodData memberDisbursementPeriod : membersDisbursementPeriods) {
                groupDisbursementAmount = groupDisbursementAmount.add(memberDisbursementPeriod.principalDisbursed());
                groupFeeChargesAtDisbursement = groupFeeChargesAtDisbursement.add(memberDisbursementPeriod.feeChargesOutstanding());
            }

            LoanSchedulePeriodData groupDisbursementPeriod = LoanSchedulePeriodData.disbursementOnlyPeriod(disbursement.disbursementDate(),
                    groupDisbursementAmount, groupFeeChargesAtDisbursement, disbursement.isDisbursed());

            groupPeriods.add(groupDisbursementPeriod);

            Integer period[] = new Integer[groupPeriodsSize];
            LocalDate fromDate[] = new LocalDate[groupPeriodsSize];
            LocalDate dueDate[] = new LocalDate[groupPeriodsSize];

            final BigDecimal principalDue[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal principalPaid[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal principalWrittenOff[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(principalDue, BigDecimal.ZERO);
            Arrays.fill(principalPaid, BigDecimal.ZERO);
            Arrays.fill(principalWrittenOff, BigDecimal.ZERO);

            final BigDecimal principalOutstanding[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(principalOutstanding, BigDecimal.ZERO);

            final BigDecimal interestExpectedDue[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal interestPaid[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal interestWaived[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal interestWrittenOff[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(interestExpectedDue, BigDecimal.ZERO);
            Arrays.fill(interestPaid, BigDecimal.ZERO);
            Arrays.fill(interestWaived, BigDecimal.ZERO);
            Arrays.fill(interestWrittenOff, BigDecimal.ZERO);

            final BigDecimal interestOutstanding[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(interestOutstanding, BigDecimal.ZERO);

            final BigDecimal feeChargesExpectedDue[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal feeChargesPaid[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal feeChargesWaived[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal feeChargesWrittenOff[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(feeChargesExpectedDue, BigDecimal.ZERO);
            Arrays.fill(feeChargesPaid, BigDecimal.ZERO);
            Arrays.fill(feeChargesWaived, BigDecimal.ZERO);
            Arrays.fill(feeChargesWrittenOff, BigDecimal.ZERO);

            final BigDecimal feeChargesOutstanding[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(feeChargesOutstanding, BigDecimal.ZERO);

            final BigDecimal penaltyChargesExpectedDue[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal penaltyChargesPaid[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal penaltyChargesWaived[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal penaltyChargesWrittenOff[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(penaltyChargesExpectedDue, BigDecimal.ZERO);
            Arrays.fill(penaltyChargesPaid, BigDecimal.ZERO);
            Arrays.fill(penaltyChargesWaived, BigDecimal.ZERO);
            Arrays.fill(penaltyChargesWrittenOff, BigDecimal.ZERO);

            final BigDecimal penaltyChargesOutstanding[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(penaltyChargesOutstanding, BigDecimal.ZERO);

            final BigDecimal totalDueForPeriod[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal totalPaidForPeriod[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal totalWaivedForPeriod[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal totalWrittenOffForPeriod[] = new BigDecimal[groupPeriodsSize];
            final BigDecimal totalOutstandingForPeriod[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(totalDueForPeriod, BigDecimal.ZERO);
            Arrays.fill(totalPaidForPeriod, BigDecimal.ZERO);
            Arrays.fill(totalWaivedForPeriod, BigDecimal.ZERO);
            Arrays.fill(totalWrittenOffForPeriod, BigDecimal.ZERO);
            Arrays.fill(totalOutstandingForPeriod, BigDecimal.ZERO);

            final BigDecimal totalActualCostOfLoanForPeriod[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(totalActualCostOfLoanForPeriod, BigDecimal.ZERO);

            final BigDecimal outstandingPrincipleBalanceOfLoan[] = new BigDecimal[groupPeriodsSize];
            Arrays.fill(outstandingPrincipleBalanceOfLoan, BigDecimal.ZERO);


            for (List<LoanSchedulePeriodData> memberPeriods : membersPeriods) {
                for (LoanSchedulePeriodData memberPeriod : memberPeriods) {
                    Integer i = memberPeriods.indexOf(memberPeriod);
                    if (period[i] == null) {
                        period[i] = memberPeriod.periodNumber();
                    }
                    if (fromDate[i] == null) {
                        fromDate[i] = memberPeriod.periodFromDate();
                    }
                    if (dueDate[i] == null) {
                        dueDate[i] = memberPeriod.periodDueDate();
                    }
                    principalDue[i] = principalDue[i].add(memberPeriod.principalDue());
                    principalPaid[i] = principalPaid[i].add(memberPeriod.principalPaid());
                    principalWrittenOff[i] = principalWrittenOff[i].add(memberPeriod.principalWrittenOff());

                    principalOutstanding[i] = principalOutstanding[i].add(memberPeriod.principalOutstanding());

                    interestExpectedDue[i] = interestExpectedDue[i].add(memberPeriod.interestDue());
                    interestPaid[i] = interestPaid[i].add(memberPeriod.interestPaid());
                    interestWaived[i] = interestWaived[i].add(memberPeriod.interestWaived());
                    interestWrittenOff[i] = interestWrittenOff[i].add(memberPeriod.interestWrittenOff());

                    interestOutstanding[i] = interestOutstanding[i].add(memberPeriod.interestOutstanding());

                    feeChargesExpectedDue[i] = feeChargesExpectedDue[i].add(memberPeriod.feeChargesDue());
                    feeChargesPaid[i] = feeChargesPaid[i].add(memberPeriod.feeChargesPaid());
                    feeChargesWaived[i] = feeChargesWaived[i].add(memberPeriod.feeChargesWaived());
                    feeChargesWrittenOff[i] = feeChargesWrittenOff[i].add(memberPeriod.feeChargesWrittenOff());

                    feeChargesOutstanding[i] = feeChargesOutstanding[i].add(memberPeriod.feeChargesOutstanding());

                    penaltyChargesExpectedDue[i] = penaltyChargesExpectedDue[i].add(memberPeriod.penaltyChargesDue());
                    penaltyChargesPaid[i] = penaltyChargesPaid[i].add(memberPeriod.penaltyChargesPaid());
                    penaltyChargesWaived[i] = penaltyChargesWaived[i].add(memberPeriod.penaltyChargesWaived());
                    penaltyChargesWrittenOff[i] = penaltyChargesWrittenOff[i].add(memberPeriod.penaltyChargesWrittenOff());

                    penaltyChargesOutstanding[i] = penaltyChargesOutstanding[i].add(memberPeriod.penaltyChargesOutstanding());

                    totalDueForPeriod[i] = totalDueForPeriod[i].add(memberPeriod.totalDueForPeriod());
                    totalPaidForPeriod[i] = totalPaidForPeriod[i].add(memberPeriod.totalPaidForPeriod());
                    totalWaivedForPeriod[i] = totalWaivedForPeriod[i].add(memberPeriod.totalWaivedForPeriod());
                    totalWrittenOffForPeriod[i] = totalWrittenOffForPeriod[i].add(memberPeriod.totalWrittenOffForPeriod());
                    totalOutstandingForPeriod[i] = totalOutstandingForPeriod[i].add(memberPeriod.totalOutstandingForPeriod());

                    totalActualCostOfLoanForPeriod[i] = totalActualCostOfLoanForPeriod[i].add(memberPeriod.totalActualCostOfLoanForPeriod());

                    outstandingPrincipleBalanceOfLoan[i] = outstandingPrincipleBalanceOfLoan[i].add(memberPeriod.principalLoanBalanceOutstanding());
                }
            }

            for (int i = 0; i < groupPeriodsSize; i++){
                groupPeriods.add(LoanSchedulePeriodData.repaymentPeriodWithPayments(groupLoanId, period[i], fromDate[i], dueDate[i], principalDue[i],
                        principalPaid[i], principalWrittenOff[i], principalOutstanding[i], outstandingPrincipleBalanceOfLoan[i], interestExpectedDue[i],
                        interestPaid[i], interestWaived[i], interestWrittenOff[i], interestOutstanding[i], feeChargesExpectedDue[i], feeChargesPaid[i],
                        feeChargesWaived[i], feeChargesWrittenOff[i], feeChargesOutstanding[i], penaltyChargesExpectedDue[i], penaltyChargesPaid[i],
                        penaltyChargesWaived[i], penaltyChargesWrittenOff[i], penaltyChargesOutstanding[i], totalDueForPeriod[i], totalPaidForPeriod[i],
                        totalWaivedForPeriod[i], totalWrittenOffForPeriod[i], totalOutstandingForPeriod[i], totalActualCostOfLoanForPeriod[i]));
            }

            LoanSchedulePeriodDataWrapper wrapper = new LoanSchedulePeriodDataWrapper(groupPeriods);

            final Integer loanTermInDays = wrapper.deriveCumulativeLoanTermInDays();

            final BigDecimal cumulativePrincipalDisbursed = wrapper.deriveCumulativePrincipalDisbursed();
            final BigDecimal cumulativePrincipalDue = wrapper.deriveCumulativePrincipalDue();
            final BigDecimal cumulativePrincipalPaid = wrapper.deriveCumulativePrincipalPaid();
            final BigDecimal cumulativePrincipalWrittenOff = wrapper.deriveCumulativePrincipalWrittenOff();
            final BigDecimal cumulativePrincipalOutstanding = wrapper.deriveCumulativePrincipalOutstanding();

            final BigDecimal cumulativeInterestExpected = wrapper.deriveCumulativeInterestExpected();
            final BigDecimal cumulativeInterestPaid = wrapper.deriveCumulativeInterestPaid();
            final BigDecimal cumulativeInterestWaived = wrapper.deriveCumulativeInterestWaived();
            final BigDecimal cumulativeInterestWrittenOff = wrapper.deriveCumulativeInterestWrittenOff();
            final BigDecimal cumulativeInterestOutstanding = wrapper.deriveCumulativeInterestOutstanding();

            final BigDecimal cumulativeFeeChargesExpected = wrapper.deriveCumulativeFeeChargesToDate();
            final BigDecimal cumulativeFeeChargesPaid = wrapper.deriveCumulativeFeeChargesPaid();
            final BigDecimal cumulativeFeeChargesWaived = wrapper.deriveCumulativeFeeChargesWaived();
            final BigDecimal cumulativeFeeChargesWrittenOff = wrapper.deriveCumulativeFeeChargesWrittenOff();
            final BigDecimal cumulativeFeeChargesOutstanding = wrapper.deriveCumulativeFeeChargesOutstanding();

            final BigDecimal cumulativePenaltyChargesExpected = wrapper.deriveCumulativePenaltyChargesToDate();
            final BigDecimal cumulativePenaltyChargesPaid = wrapper.deriveCumulativePenaltyChargesPaid();
            final BigDecimal cumulativePenaltyChargesWaived = wrapper.deriveCumulativePenaltyChargesWaived();
            final BigDecimal cumulativePenaltyChargesWrittenOff = wrapper.deriveCumulativePenaltyChargesWrittenOff();
            final BigDecimal cumulativePenaltyChargesOutstanding = wrapper.deriveCumulativePenaltyChargesOutstanding();

            final BigDecimal totalExpectedCostOfLoan = cumulativeInterestExpected.add(cumulativeFeeChargesExpected).add(
                    cumulativePenaltyChargesExpected);
            final BigDecimal totalExpectedRepayment = cumulativePrincipalDisbursed.add(totalExpectedCostOfLoan);

            final BigDecimal totalPaidToDate = cumulativePrincipalPaid.add(cumulativeInterestPaid).add(cumulativeFeeChargesPaid)
                    .add(cumulativePenaltyChargesPaid);
            final BigDecimal totalWaivedToDate = cumulativeInterestWaived.add(cumulativeFeeChargesWaived).add(
                    cumulativePenaltyChargesWaived);
            final BigDecimal totalWrittenOffToDate = cumulativePrincipalWrittenOff.add(cumulativeInterestWrittenOff)
                    .add(cumulativeFeeChargesWrittenOff).add(cumulativePenaltyChargesWrittenOff);

            final BigDecimal totalOutstanding = cumulativePrincipalOutstanding.add(cumulativeInterestOutstanding)
                    .add(cumulativeFeeChargesOutstanding).add(cumulativePenaltyChargesOutstanding);

            final BigDecimal totalOverdue = wrapper.deriveCumulativeTotalOverdue();

            final MonetaryCurrency monetaryCurrency = new MonetaryCurrency(currency.code(), currency.decimalPlaces());
            final Money tolerance = Money.of(monetaryCurrency, inArrearsTolerance);
            final Money totalOverdueMoney = Money.of(monetaryCurrency, totalOverdue);
            boolean isWaiveAllowed = totalOverdueMoney.isGreaterThanZero()
                    && (tolerance.isGreaterThan(totalOverdueMoney) || tolerance.isEqualTo(totalOverdueMoney));

            BigDecimal totalInArrears = null;
            if (!isWaiveAllowed) {
                totalInArrears = totalOverdueMoney.getAmount();
            }

            return new LoanScheduleData(currency, groupPeriods, loanTermInDays, cumulativePrincipalDisbursed, cumulativePrincipalDue,
                    cumulativePrincipalPaid, cumulativePrincipalWrittenOff, cumulativePrincipalOutstanding, cumulativeInterestExpected,
                    cumulativeInterestPaid, cumulativeInterestWaived, cumulativeInterestWrittenOff, cumulativeInterestOutstanding,
                    cumulativeFeeChargesExpected, cumulativeFeeChargesPaid, cumulativeFeeChargesWaived, cumulativeFeeChargesWrittenOff,
                    cumulativeFeeChargesOutstanding, cumulativePenaltyChargesExpected, cumulativePenaltyChargesPaid,
                    cumulativePenaltyChargesWaived, cumulativePenaltyChargesWrittenOff, cumulativePenaltyChargesOutstanding,
                    totalExpectedCostOfLoan, totalExpectedRepayment, totalPaidToDate, totalWaivedToDate, totalWrittenOffToDate,
                    totalOutstanding, totalInArrears);

        } catch (EmptyResultDataAccessException e) {
            throw new LoanNotFoundException(groupLoanId);
        }
    }

    @Override
	public Collection<LoanTransactionData> retrieveLoanTransactions(final Long loanId) {
		try {
			context.authenticatedUser();

			LoanTransactionsMapper rm = new LoanTransactionsMapper();

			// retrieve all loan transactions that are not invalid and have not been 'contra'ed by another transaction
			// repayments at time of disbursement (e.g. charges)
			String sql = "select "
					+ rm.LoanPaymentsSchema()
					+ " where tr.loan_id = ? and tr.transaction_type_enum not in (0, 3) and tr.contra_id is null order by tr.transaction_date ASC";
			return this.jdbcTemplate.query(sql, rm, new Object[] { loanId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public LoanPermissionData retrieveLoanPermissions(
			final LoanBasicDetailsData loanBasicDetails, 
			final boolean isWaiverAllowed,
			final int repaymentAndWaiveCount,
			final boolean existsGuarantor) {

		final boolean pendingApproval = (loanBasicDetails.getStatus().getId().equals(100L));
		final boolean waitingForDisbursal = (loanBasicDetails.getStatus().getId().equals(200L));
		final boolean isActive = (loanBasicDetails.getStatus().getId().equals(300L));
		final boolean closedObligationsMet = (loanBasicDetails.getStatus().getId().equals(600L));
		final boolean closedWrittenOff = (loanBasicDetails.getStatus().getId().equals(602L));
		final boolean closedRescheduled = (loanBasicDetails.getStatus().getId().equals(602L));
		
		final boolean closed = closedObligationsMet || closedWrittenOff || closedRescheduled;
		
		final boolean isOverpaid = (loanBasicDetails.getStatus().getId().equals(700L));
		boolean addLoanChargeAllowed = true;
		boolean setGuarantorAllowed = true;
		boolean editGuarantorAllowed = true;
		if (closed || isOverpaid) {
			addLoanChargeAllowed = false;
			setGuarantorAllowed = false;
			editGuarantorAllowed = false;
		}
		
		if(existsGuarantor){
			setGuarantorAllowed = false;
		}else{
			editGuarantorAllowed = false;
		}

		final boolean waiveAllowed = isWaiverAllowed && isActive;
		final boolean makeRepaymentAllowed = isActive;
		final boolean closeLoanAllowed = !closed && isActive;
		final boolean closeLoanAsRescheduledAllowed = !closed && !isOverpaid && isActive;
		
		final boolean rejectAllowed = pendingApproval;
		final boolean withdrawnByApplicantAllowed = waitingForDisbursal
				|| pendingApproval;

		final boolean undoApprovalAllowed = waitingForDisbursal;

		final boolean undoDisbursalAllowed = isActive
				&& (repaymentAndWaiveCount == 0);

		final boolean disbursalAllowed = waitingForDisbursal;

		return new LoanPermissionData(closeLoanAllowed, closeLoanAsRescheduledAllowed, 
				addLoanChargeAllowed, waiveAllowed, makeRepaymentAllowed,
				rejectAllowed, withdrawnByApplicantAllowed,
				undoApprovalAllowed, undoDisbursalAllowed, disbursalAllowed,
				setGuarantorAllowed,editGuarantorAllowed,
				pendingApproval, waitingForDisbursal, closedObligationsMet);
	}

	@Override
	public LoanBasicDetailsData retrieveClientAndProductDetails(final Long clientId, final Long productId) {

		context.authenticatedUser();

		final LocalDate expectedDisbursementDate = new LocalDate();
		final ClientData clientAccount = this.clientReadPlatformService.retrieveIndividualClient(clientId);
		
		LoanBasicDetailsData loanDetails = LoanBasicDetailsData.populateForNewIndividualClientLoanCreation(clientAccount.id(), clientAccount.displayName(), expectedDisbursementDate,
				clientAccount.officeId());
		
		if (productId != null) {
			LoanProductData selectedProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
			loanDetails = LoanBasicDetailsData.populateForNewLoanCreation(loanDetails, selectedProduct);
		}

		return loanDetails;
	}

    @Override
    public LoanBasicDetailsData retrieveGroupAndProductDetails(Long groupId, Long productId) {

		context.authenticatedUser();

		final LocalDate expectedDisbursementDate = new LocalDate();
		final GroupData groupAccount = this.groupReadPlatformService.retrieveGroup(groupId);

		LoanBasicDetailsData loanDetails = LoanBasicDetailsData.populateForNewGroupLoanCreation(groupAccount.getId(), groupAccount.getName(), expectedDisbursementDate,
				groupAccount.getOfficeId());

		if (productId != null) {
			LoanProductData selectedProduct = this.loanProductReadPlatformService.retrieveLoanProduct(productId);
			loanDetails = LoanBasicDetailsData.populateForNewLoanCreation(loanDetails, selectedProduct);
		}

		return loanDetails;
    }

    @Override
	public LoanTransactionData retrieveNewLoanRepaymentDetails(final Long loanId) {

		context.authenticatedUser();

		// TODO - OPTIMIZE - write simple sql query to fetch back date of
		// possible next transaction date.
		Loan loan = this.loanRepository.findOne(loanId);
		if (loan == null) {
			throw new LoanNotFoundException(loanId);
		}

		final String currencyCode = loan.repaymentScheduleDetail().getPrincipal().getCurrencyCode();
		ApplicationCurrency currency = this.applicationCurrencyRepository.findOneByCode(currencyCode);
		if (currency == null) {
			throw new CurrencyNotFoundException(currencyCode);
		}

		final CurrencyData currencyData = new CurrencyData(currency.getCode(),
				currency.getName(), currency.getDecimalPlaces(),
				currency.getDisplaySymbol(), currency.getNameCode());

		final LocalDate earliestUnpaidInstallmentDate = loan.possibleNextRepaymentDate();
		
		final Money possibleNextRepaymentAmount = loan.possibleNextRepaymentAmount();
		final EnumOptionData transactionType = LoanEnumerations.transactionType(LoanTransactionType.REPAYMENT);
		return new LoanTransactionData(null, transactionType, currencyData, earliestUnpaidInstallmentDate, possibleNextRepaymentAmount.getAmount(), null, null, null, null);
	}

	@Override
	public LoanTransactionData retrieveNewLoanWaiveInterestDetails(final Long loanId) {

		context.authenticatedUser();

		// TODO - OPTIMIZE - write simple sql query to fetch back overdue interest that can be waived along with the date of repayment period interest is overdue.
		final Loan loan = this.loanRepository.findOne(loanId);
		if (loan == null) {
			throw new LoanNotFoundException(loanId);
		}

		final String currencyCode = loan.repaymentScheduleDetail().getPrincipal().getCurrencyCode();
		final ApplicationCurrency currency = this.applicationCurrencyRepository.findOneByCode(currencyCode);
		if (currency == null) {
			throw new CurrencyNotFoundException(currencyCode);
		}
		
		final CurrencyData currencyData = new CurrencyData(currency.getCode(),
				currency.getName(), currency.getDecimalPlaces(),
				currency.getDisplaySymbol(), currency.getNameCode());

		final LoanTransaction waiveOfInterest = loan.deriveDefaultInterestWaiverTransaction();

		final EnumOptionData transactionType = LoanEnumerations.transactionType(LoanTransactionType.WAIVE_INTEREST);
		
		return new LoanTransactionData(null, transactionType, currencyData, waiveOfInterest.getTransactionDate(), waiveOfInterest.getAmount(), null, null, null, null);
	}
	
	@Override
	public LoanTransactionData retrieveNewClosureDetails() {
		
		context.authenticatedUser();

		EnumOptionData transactionType = LoanEnumerations.transactionType(LoanTransactionType.WRITEOFF);
		
		return new LoanTransactionData(null, transactionType, null, new LocalDate(), null, null, null, null, null);
	}

	@Override
	public LoanTransactionData retrieveLoanTransactionDetails(
			final Long loanId, final Long transactionId) {

		context.authenticatedUser();

		final Loan loan = this.loanRepository.findOne(loanId);
		if (loan == null) {
			throw new LoanNotFoundException(loanId);
		}

		final String currencyCode = loan.repaymentScheduleDetail().getPrincipal().getCurrencyCode();
		final ApplicationCurrency currency = this.applicationCurrencyRepository.findOneByCode(currencyCode);
		if (currency == null) {
			throw new CurrencyNotFoundException(currencyCode);
		}

		final LoanTransaction transaction = this.loanTransactionRepository.findOne(transactionId);
		if (transaction == null) {
			throw new LoanTransactionNotFoundException(transactionId);
		}

		if (transaction.isNotBelongingToLoanOf(loan)) {
			throw new LoanTransactionNotFoundException(transactionId, loanId);
		}

		final CurrencyData currencyData = new CurrencyData(currency.getCode(),
				currency.getName(), currency.getDecimalPlaces(),
				currency.getDisplaySymbol(), currency.getNameCode());
		
		return transaction.toData(currencyData);
	}

	private static final class LoanMapper implements
			RowMapper<LoanBasicDetailsData> {

		public String loanSchema() {
			return "l.id as id, l.external_id as externalId, l.fund_id as fundId, f.name as fundName, " 
					+ " lp.id as loanProductId, lp.name as loanProductName, lp.description as loanProductDescription, c.id as clientId, c.display_name as clientName, " 
					+ " c.office_id as clientOfficeId, g.id as groupId, g.name as groupName, g.office_id as groupOfficeId,"
					+ " l.submittedon_date as submittedOnDate,"
					+ " l.total_charges_due_at_disbursement_derived as totalDisbursementCharges,"
					+ " l.approvedon_date as approvedOnDate, l.expected_disbursedon_date as expectedDisbursementDate, l.disbursedon_date as actualDisbursementDate, l.expected_firstrepaymenton_date as expectedFirstRepaymentOnDate,"
					+ " l.interest_calculated_from_date as interestChargedFromDate, l.closedon_date as closedOnDate, l.expected_maturedon_date as expectedMaturityDate, "
					+ " l.principal_amount as principal, l.arrearstolerance_amount as inArrearsTolerance, l.number_of_repayments as numberOfRepayments, l.repay_every as repaymentEvery,"
					+ " l.nominal_interest_rate_per_period as interestRatePerPeriod, l.annual_nominal_interest_rate as annualInterestRate, "
					+ " l.repayment_period_frequency_enum as repaymentFrequencyType, l.interest_period_frequency_enum as interestRateFrequencyType, "
					+ " l.term_frequency as termFrequency, l.term_period_frequency_enum as termPeriodFrequencyType, "
					+ " l.amortization_method_enum as amortizationType, l.interest_method_enum as interestType, l.interest_calculated_in_period_enum as interestCalculationPeriodType,"
					+ " l.loan_status_id as lifeCycleStatusId, l.loan_transaction_strategy_id as transactionStrategyId, "
					+ " l.currency_code as currencyCode, l.currency_digits as currencyDigits, rc.`name` as currencyName, rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode, "
					+ " l.loan_officer_id as loanOfficerId, s.display_name as loanOfficerName"
					+ " from m_loan l"
					+ " left join m_client c on c.id = l.client_id"
					+ " left join m_group g on g.id = l.group_id"
					+ " join m_product_loan lp on lp.id = l.product_id"
					+ " join m_currency rc on rc.`code` = l.currency_code"
					+ " left join m_fund f on f.id = l.fund_id"
					+ " left join m_staff s on s.id = l.loan_officer_id ";
		}

		@Override
		public LoanBasicDetailsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			String currencyCode = rs.getString("currencyCode");
			String currencyName = rs.getString("currencyName");
			String currencyNameCode = rs.getString("currencyNameCode");
			String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
			Integer currencyDigits = JdbcSupport.getInteger(rs,"currencyDigits");
			CurrencyData currencyData = new CurrencyData(currencyCode,
					currencyName, currencyDigits, currencyDisplaySymbol,
					currencyNameCode);

			Long id = rs.getLong("id");
			String externalId = rs.getString("externalId");

			Long clientId = JdbcSupport.getLong(rs, "clientId");
			Long clientOfficeId = JdbcSupport.getLong(rs, "clientOfficeId");;
			String clientName = rs.getString("clientName");

			Long groupId = JdbcSupport.getLong(rs, "groupId");
			Long groupOfficeId = JdbcSupport.getLong(rs, "groupOfficeId");;
			String groupName = rs.getString("groupName");

			Long fundId = JdbcSupport.getLong(rs, "fundId");
			String fundName = rs.getString("fundName");
			Long loanOfficerId = JdbcSupport.getLong(rs, "loanOfficerId");
			String loanOfficerName = rs.getString("loanOfficerName");
			Long loanProductId = JdbcSupport.getLong(rs, "loanProductId");
			String loanProductName = rs.getString("loanProductName");
			String loanProductDescription = rs.getString("loanProductDescription");
			
			LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs,
					"submittedOnDate");
			LocalDate approvedOnDate = JdbcSupport.getLocalDate(rs,
					"approvedOnDate");
			LocalDate expectedDisbursementDate = JdbcSupport.getLocalDate(rs,
					"expectedDisbursementDate");
			LocalDate actualDisbursementDate = JdbcSupport.getLocalDate(rs,
					"actualDisbursementDate");
			LocalDate expectedFirstRepaymentOnDate = JdbcSupport.getLocalDate(
					rs, "expectedFirstRepaymentOnDate");
			LocalDate interestChargedFromDate = JdbcSupport.getLocalDate(rs,
					"interestChargedFromDate");
			LocalDate closedOnDate = JdbcSupport.getLocalDate(rs,
					"closedOnDate");
			LocalDate expectedMaturityDate = JdbcSupport.getLocalDate(rs,
					"expectedMaturityDate");

			BigDecimal principal = rs.getBigDecimal("principal");
			BigDecimal inArrearsTolerance = rs.getBigDecimal("inArrearsTolerance");
			BigDecimal totalDisbursementCharges = rs.getBigDecimal("totalDisbursementCharges");

			Integer numberOfRepayments = JdbcSupport.getInteger(rs,"numberOfRepayments");
			Integer repaymentEvery = JdbcSupport.getInteger(rs,"repaymentEvery");
			BigDecimal interestRatePerPeriod = rs.getBigDecimal("interestRatePerPeriod");
			BigDecimal annualInterestRate = rs.getBigDecimal("annualInterestRate");

			Integer termFrequency = JdbcSupport.getInteger(rs,"termFrequency");
			Integer termPeriodFrequencyTypeInt = JdbcSupport.getInteger(rs,"termPeriodFrequencyType");
			EnumOptionData termPeriodFrequencyType = LoanEnumerations.termFrequencyType(termPeriodFrequencyTypeInt);
			
			int repaymentFrequencyTypeInt = JdbcSupport.getInteger(rs,"repaymentFrequencyType");
			EnumOptionData repaymentFrequencyType = LoanEnumerations.repaymentFrequencyType(repaymentFrequencyTypeInt);
			
			int interestRateFrequencyTypeInt = JdbcSupport.getInteger(rs,"interestRateFrequencyType");
			EnumOptionData interestRateFrequencyType = LoanEnumerations.interestRateFrequencyType(interestRateFrequencyTypeInt);

			Integer transactionStrategyId = JdbcSupport.getInteger(rs,"transactionStrategyId");
			
			int amortizationTypeInt = JdbcSupport.getInteger(rs,"amortizationType");
			int interestTypeInt = JdbcSupport.getInteger(rs, "interestType");
			int interestCalculationPeriodTypeInt = JdbcSupport.getInteger(rs,"interestCalculationPeriodType");
			
			EnumOptionData amortizationType = LoanEnumerations.amortizationType(amortizationTypeInt);
			EnumOptionData interestType = LoanEnumerations.interestType(interestTypeInt);
			EnumOptionData interestCalculationPeriodType = LoanEnumerations.interestCalculationPeriodType(interestCalculationPeriodTypeInt);

			Integer lifeCycleStatusId = JdbcSupport.getInteger(rs, "lifeCycleStatusId");
			EnumOptionData status = LoanEnumerations.status(lifeCycleStatusId);
			
			LocalDate lifeCycleStatusDate = submittedOnDate;
			if (approvedOnDate != null) {
				lifeCycleStatusDate = approvedOnDate;
			}
			if (actualDisbursementDate != null) {
				lifeCycleStatusDate = actualDisbursementDate;
			}
			if (closedOnDate != null) {
				lifeCycleStatusDate = closedOnDate;
			}

			Collection<LoanChargeData> charges = null;
			return new LoanBasicDetailsData(id, externalId,
					clientId, clientName, clientOfficeId,
					groupId, groupName, groupOfficeId,
					loanProductId, loanProductName, loanProductDescription,
					fundId, fundName, 
					closedOnDate,
					submittedOnDate, approvedOnDate, expectedDisbursementDate,
					actualDisbursementDate, expectedMaturityDate,
					expectedFirstRepaymentOnDate, interestChargedFromDate,
					currencyData,
					principal, inArrearsTolerance, numberOfRepayments,
					repaymentEvery, interestRatePerPeriod, annualInterestRate,
					repaymentFrequencyType, interestRateFrequencyType,
					amortizationType, interestType,
					interestCalculationPeriodType, 
					status,
					lifeCycleStatusDate, termFrequency, termPeriodFrequencyType, transactionStrategyId, charges,
					loanOfficerId, loanOfficerName, totalDisbursementCharges);
		}
	}

    private static final class GroupLoanMapper implements
            RowMapper<GroupLoanBasicDetailsData> {

        public String groupLoanSchema() {
            return " gl.id as id, gl.external_id as externalId, gl.fund_id as fundId, f.name as fundName,  " +
                    "lp.id as loanProductId, lp.name as loanProductName, lp.description as loanProductDescription, " +
                    "g.id as groupId, g.name as groupName, g.office_id as groupOfficeId, " +
                    "SUM(l.principal_amount) as principal, gl.arrearstolerance_amount as inArrearsTolerance, " +
                    "gl.submittedon_date as submittedOnDate, gl.approvedon_date as approvedOnDate, " +
                    "gl.expected_disbursedon_date as expectedDisbursementDate, gl.disbursedon_date as actualDisbursementDate, " +
                    "gl.expected_firstrepaymenton_date as expectedFirstRepaymentOnDate, " +
                    "gl.interest_calculated_from_date as interestChargedFromDate, gl.closedon_date as closedOnDate, " +
                    "gl.expected_maturedon_date as expectedMaturityDate, gl.loan_status_id as lifeCycleStatusId, " +
                    "gl.loan_officer_id as loanOfficerId, s.display_name as loanOfficerName, " +
                    "l.currency_code as currencyCode, l.currency_digits as currencyDigits, rc.`name` as currencyName, " +
                    "rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode "  +
                    "from m_group_loan gl left join m_loan l on l.group_loan_id = gl.id " +
                    "left join m_group g on g.id = gl.group_id join m_product_loan lp on lp.id = gl.product_id " +
                    "join m_currency rc on rc.`code` = l.currency_code " +
                    "left join m_fund f on f.id = gl.fund_id left join m_staff s on s.id = gl.loan_officer_id ";
        }

        @Override
        public GroupLoanBasicDetailsData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
                throws SQLException {

            String currencyCode = rs.getString("currencyCode");
            String currencyName = rs.getString("currencyName");
            String currencyNameCode = rs.getString("currencyNameCode");
            String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            Integer currencyDigits = JdbcSupport.getInteger(rs,"currencyDigits");
            CurrencyData currencyData = new CurrencyData(currencyCode,
                    currencyName, currencyDigits, currencyDisplaySymbol,
                    currencyNameCode);

            Long id = rs.getLong("id");
            String externalId = rs.getString("externalId");

            Long groupId = JdbcSupport.getLong(rs, "groupId");
            Long groupOfficeId = JdbcSupport.getLong(rs, "groupOfficeId");;
            String groupName = rs.getString("groupName");

            Long fundId = JdbcSupport.getLong(rs, "fundId");
            String fundName = rs.getString("fundName");
            Long loanOfficerId = JdbcSupport.getLong(rs, "loanOfficerId");
            String loanOfficerName = rs.getString("loanOfficerName");
            Long loanProductId = JdbcSupport.getLong(rs, "loanProductId");
            String loanProductName = rs.getString("loanProductName");
            String loanProductDescription = rs.getString("loanProductDescription");

            LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs,
                    "submittedOnDate");
            LocalDate approvedOnDate = JdbcSupport.getLocalDate(rs,
                    "approvedOnDate");
            LocalDate expectedDisbursementDate = JdbcSupport.getLocalDate(rs,
                    "expectedDisbursementDate");
            LocalDate actualDisbursementDate = JdbcSupport.getLocalDate(rs,
                    "actualDisbursementDate");
            LocalDate expectedFirstRepaymentOnDate = JdbcSupport.getLocalDate(
                    rs, "expectedFirstRepaymentOnDate");
            LocalDate interestChargedFromDate = JdbcSupport.getLocalDate(rs,
                    "interestChargedFromDate");
            LocalDate closedOnDate = JdbcSupport.getLocalDate(rs,
                    "closedOnDate");
            LocalDate expectedMaturityDate = JdbcSupport.getLocalDate(rs,
                    "expectedMaturityDate");

            BigDecimal principal = rs.getBigDecimal("principal");
            BigDecimal inArrearsTolerance = rs.getBigDecimal("inArrearsTolerance");

            Integer lifeCycleStatusId = JdbcSupport.getInteger(rs, "lifeCycleStatusId");
            EnumOptionData status = LoanEnumerations.status(lifeCycleStatusId);

            LocalDate lifeCycleStatusDate = submittedOnDate;
            if (approvedOnDate != null) {
                lifeCycleStatusDate = approvedOnDate;
            }
            if (actualDisbursementDate != null) {
                lifeCycleStatusDate = actualDisbursementDate;
            }
            if (closedOnDate != null) {
                lifeCycleStatusDate = closedOnDate;
            }

            return new GroupLoanBasicDetailsData(id, externalId, groupId, groupOfficeId, groupName, loanProductId, loanProductName,
                    loanProductDescription, status, fundId, fundName, loanOfficerId, loanOfficerName, principal, inArrearsTolerance,
                    submittedOnDate, approvedOnDate, expectedDisbursementDate, actualDisbursementDate,
                    expectedFirstRepaymentOnDate, interestChargedFromDate, closedOnDate, expectedMaturityDate, lifeCycleStatusDate, currencyData);
        }
    }

	private static final class LoanScheduleMapper implements RowMapper<LoanSchedulePeriodData> {

		private LocalDate lastDueDate;
		private BigDecimal outstandingLoanPrincipalBalance;
		
		public LoanScheduleMapper(final DisbursementData disbursement) {
			this.lastDueDate = disbursement.disbursementDate();
			this.outstandingLoanPrincipalBalance = disbursement.amount();
		}

        public void updateDisbursementData(final DisbursementData disbursement){
            this.lastDueDate = disbursement.disbursementDate();
            this.outstandingLoanPrincipalBalance = disbursement.amount();
        }

		public String loanScheduleSchema() {

			return " ls.loan_id as loanId, ls.installment as period, ls.fromdate as fromDate, ls.duedate as dueDate, "
					+ " ls.principal_amount as principalDue, ls.principal_completed_derived as principalPaid, ls.principal_writtenoff_derived as principalWrittenOff, "
					+ " ls.interest_amount as interestDue, ls.interest_completed_derived as interestPaid, ls.interest_waived_derived as interestWaived, ls.interest_writtenoff_derived as interestWrittenOff, "
					+ " ls.fee_charges_amount as feeChargesDue, ls.fee_charges_completed_derived as feeChargesPaid, ls.fee_charges_waived_derived as feeChargesWaived, ls.fee_charges_writtenoff_derived as feeChargesWrittenOff, "
					+ " ls.penalty_charges_amount as penaltyChargesDue, ls.penalty_charges_completed_derived as penaltyChargesPaid, ls.penalty_charges_waived_derived as penaltyChargesWaived, ls.penalty_charges_writtenoff_derived as penaltyChargesWrittenOff "
					+ " from m_loan l "
					+ " join m_loan_repayment_schedule ls on ls.loan_id = l.id ";
		}

		@Override
		public LoanSchedulePeriodData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final Long loanId = rs.getLong("loanId");
			final Integer period = JdbcSupport.getInteger(rs, "period");
			LocalDate fromDate = JdbcSupport.getLocalDate(rs, "fromDate");
			final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
			final BigDecimal principalDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalDue");
			final BigDecimal principalPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalPaid");
			final BigDecimal principalWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principalWrittenOff");
			
			// TODO - KW - rather than calculate this here should we put calculation on derived column on table like other columns?
			final BigDecimal principalOutstanding = principalDue.subtract(principalPaid).subtract(principalWrittenOff);
			
			final BigDecimal interestExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestDue");
			final BigDecimal interestPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestPaid");
			final BigDecimal interestWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWaived");
			final BigDecimal interestWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interestWrittenOff");
			
			// TODO - KW - rather than calculate this here should we put calculation on derived column on table like other columns?
			final BigDecimal interestActualDue = interestExpectedDue.subtract(interestWaived).subtract(interestWrittenOff);
			final BigDecimal interestOutstanding = interestActualDue.subtract(interestPaid);
			
			final BigDecimal feeChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesDue");
			final BigDecimal feeChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesPaid");
			final BigDecimal feeChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWaived");
			final BigDecimal feeChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "feeChargesWrittenOff");
			
			final BigDecimal feeChargesActualDue = feeChargesExpectedDue.subtract(feeChargesWaived).subtract(feeChargesWrittenOff);
			final BigDecimal feeChargesOutstanding = feeChargesActualDue.subtract(feeChargesPaid);
			
			final BigDecimal penaltyChargesExpectedDue = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesDue");
			final BigDecimal penaltyChargesPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesPaid");
			final BigDecimal penaltyChargesWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWaived");
			final BigDecimal penaltyChargesWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penaltyChargesWrittenOff");
			
			final BigDecimal penaltyChargesActualDue = penaltyChargesExpectedDue.subtract(penaltyChargesWaived).subtract(penaltyChargesWrittenOff);
			final BigDecimal penaltyChargesOutstanding = penaltyChargesActualDue.subtract(penaltyChargesPaid);

			final BigDecimal totalExpectedCostOfLoanForPeriod = interestExpectedDue.add(feeChargesExpectedDue).add(penaltyChargesExpectedDue);
			
			final BigDecimal totalDueForPeriod = principalDue.add(totalExpectedCostOfLoanForPeriod);
			final BigDecimal totalPaidForPeriod = principalPaid.add(interestPaid).add(feeChargesPaid).add(penaltyChargesPaid);
			final BigDecimal totalWaivedForPeriod = interestWaived.add(feeChargesWaived).add(penaltyChargesWaived);
			final BigDecimal totalWrittenOffForPeriod = principalWrittenOff.add(interestWrittenOff).add(feeChargesWrittenOff).add(penaltyChargesWrittenOff);
			final BigDecimal totalOutstandingForPeriod = principalOutstanding.add(interestOutstanding).add(feeChargesOutstanding).add(penaltyChargesOutstanding);
			
			final BigDecimal totalActualCostOfLoanForPeriod = interestActualDue.add(feeChargesActualDue).add(penaltyChargesActualDue);
			
			if (fromDate == null) {
				fromDate = this.lastDueDate;
			}
			final BigDecimal outstandingPrincipleBalanceOfLoan = outstandingLoanPrincipalBalance.subtract(principalDue);
			
			// update based on current period values
			this.lastDueDate = dueDate;
			outstandingLoanPrincipalBalance = outstandingLoanPrincipalBalance.subtract(principalDue);
			
			return LoanSchedulePeriodData.repaymentPeriodWithPayments(loanId, period, fromDate, dueDate, 
					principalDue, principalPaid, principalWrittenOff, principalOutstanding, outstandingPrincipleBalanceOfLoan, 
					interestExpectedDue, interestPaid, interestWaived, interestWrittenOff, interestOutstanding, 
					feeChargesExpectedDue, feeChargesPaid, feeChargesWaived, feeChargesWrittenOff, feeChargesOutstanding,
					penaltyChargesExpectedDue, penaltyChargesPaid, penaltyChargesWaived, penaltyChargesWrittenOff, penaltyChargesOutstanding,
					totalDueForPeriod, totalPaidForPeriod, totalWaivedForPeriod, totalWrittenOffForPeriod, totalOutstandingForPeriod, totalActualCostOfLoanForPeriod);
		}
	}

	private static final class LoanTransactionsMapper implements RowMapper<LoanTransactionData> {

		public String LoanPaymentsSchema() {

			return " tr.id as id, tr.transaction_type_enum as transactionType, tr.transaction_date as `date`, tr.amount as total, "
					+ "tr.principal_portion_derived as principal, "
					+ "tr.interest_portion_derived as interest, "
					+ "tr.fee_charges_portion_derived as fees, "
					+ "tr.penalty_charges_portion_derived as penalties, "
					+ " l.currency_code as currencyCode, l.currency_digits as currencyDigits, rc.`name` as currencyName, " 
					+ " rc.display_symbol as currencyDisplaySymbol, rc.internationalized_name_code as currencyNameCode "
					+ " from m_loan l "
					+ " join m_loan_transaction tr on tr.loan_id = l.id"
					+ " join m_currency rc on rc.`code` = l.currency_code ";
		}

		@Override
		public LoanTransactionData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum) throws SQLException {

			final String currencyCode = rs.getString("currencyCode");
			final String currencyName = rs.getString("currencyName");
			final String currencyNameCode = rs.getString("currencyNameCode");
			final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
			final Integer currencyDigits = JdbcSupport.getInteger(rs, "currencyDigits");
			final CurrencyData currencyData = new CurrencyData(currencyCode,
					currencyName, currencyDigits, currencyDisplaySymbol,
					currencyNameCode);

			final Long id = rs.getLong("id");
			final int transactionTypeInt = JdbcSupport.getInteger(rs, "transactionType");
			final EnumOptionData transactionType = LoanEnumerations.transactionType(transactionTypeInt);
			final LocalDate date = JdbcSupport.getLocalDate(rs, "date");
			final BigDecimal totalAmount = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "total");
			final BigDecimal principalPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "principal");
			final BigDecimal interestPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "interest");
			final BigDecimal feeChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "fees");
			final BigDecimal penaltyChargesPortion = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "penalties");
			
			return new LoanTransactionData(id, transactionType, currencyData, date, totalAmount, principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
		}
	}
}