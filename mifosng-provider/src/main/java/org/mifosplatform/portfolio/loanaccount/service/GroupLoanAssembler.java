package org.mifosplatform.portfolio.loanaccount.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.portfolio.charge.domain.Charge;
import org.mifosplatform.portfolio.charge.domain.ChargeRepository;
import org.mifosplatform.portfolio.charge.exception.ChargeIsNotActiveException;
import org.mifosplatform.portfolio.charge.exception.ChargeNotFoundException;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.fund.domain.Fund;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.group.domain.GroupRepository;
import org.mifosplatform.portfolio.group.exception.GroupNotFoundException;
import org.mifosplatform.portfolio.loanaccount.command.GroupLoanApplicationCommand;
import org.mifosplatform.portfolio.loanaccount.command.GroupLoanChargeCommand;
import org.mifosplatform.portfolio.loanaccount.command.LoanApplicationCommand;
import org.mifosplatform.portfolio.loanaccount.command.MemberLoanChargeCommand;
import org.mifosplatform.portfolio.loanaccount.domain.DefaultLoanLifecycleStateMachine;
import org.mifosplatform.portfolio.loanaccount.domain.GroupLoan;
import org.mifosplatform.portfolio.loanaccount.domain.GroupLoanCharge;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanCharge;
import org.mifosplatform.portfolio.loanaccount.domain.LoanLifecycleStateMachine;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.mifosplatform.portfolio.loanaccount.domain.LoanStatus;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.mifosplatform.portfolio.loanaccount.loanschedule.service.CalculationPlatformService;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProduct;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProductRelatedDetail;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProductRepository;
import org.mifosplatform.portfolio.loanproduct.domain.LoanTransactionProcessingStrategy;
import org.mifosplatform.portfolio.loanproduct.domain.PeriodFrequencyType;
import org.mifosplatform.portfolio.loanproduct.exception.LoanProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupLoanAssembler {

    private final LoanProductRepository loanProductRepository;
    private final ClientRepository clientRepository;
    private final GroupRepository groupRepository;
    private final ChargeRepository chargeRepository;
    private final CalculationPlatformService calculationPlatformService;
    private final LoanAssembler loanAssembler;

    @Autowired
    public GroupLoanAssembler(LoanProductRepository loanProductRepository, ClientRepository clientRepository,
            GroupRepository groupRepository, ChargeRepository chargeRepository, CalculationPlatformService calculationPlatformService,
            LoanAssembler loanAssembler) {
        this.loanProductRepository = loanProductRepository;
        this.clientRepository = clientRepository;
        this.groupRepository = groupRepository;
        this.chargeRepository = chargeRepository;
        this.calculationPlatformService = calculationPlatformService;
        this.loanAssembler = loanAssembler;
    }

    public GroupLoan assembleFrom(final GroupLoanApplicationCommand command) {

        LoanProduct loanProduct = this.loanProductRepository.findOne(command.getProductId());
        if (loanProduct == null) { throw new LoanProductNotFoundException(command.getProductId()); }

        Group group = this.groupRepository.findOne(command.getGroupId());
        if (group == null || group.isDeleted()) { throw new GroupNotFoundException(command.getGroupId()); }

        final MonetaryCurrency currency = loanProduct.getCurrency();
        final Integer loanTermFrequency = command.getLoanTermFrequency();
        final PeriodFrequencyType loanTermFrequencyType = PeriodFrequencyType.fromInt(command.getLoanTermFrequencyType());

        // associating fund with loan product at creation is optional for now.
        final Fund fund = loanAssembler.findFundByIdIfProvided(command.getFundId());
        final LoanTransactionProcessingStrategy loanTransactionProcessingStrategy = loanAssembler.findStrategyByIdIfProvided(command
                .getTransactionProcessingStrategyId());

        // optionally associate a loan officer to the loan
        final Staff loanOfficer = loanAssembler.findLoanOfficerByIdIfProvided(command.getLoanOfficerId());

        // assemble member loans
        Set<Loan> memberLoans = new HashSet<Loan>();
        for (LoanApplicationCommand loanApplicationCommand : command.getMembersLoanApplicationCommands()) {
            Client client = this.clientRepository.findOne(loanApplicationCommand.getClientId());
            if (client == null || client.isDeleted()) { throw new ClientNotFoundException(loanApplicationCommand.getClientId()); }

            final LoanProductRelatedDetail loanRepaymentScheduleDetail = loanAssembler.assembleLoanProductRelatedDetailFrom(
                    loanApplicationCommand, currency);

            Loan loan = Loan.createNew(fund, loanOfficer, loanTransactionProcessingStrategy, loanProduct, client,
                    loanRepaymentScheduleDetail, new HashSet<LoanCharge>());
            loan.setExternalId(loanApplicationCommand.getExternalId());

            final LoanScheduleData loanSchedule = this.calculationPlatformService.calculateLoanSchedule(loanApplicationCommand
                    .toCalculateLoanScheduleCommand());

            for (LoanSchedulePeriodData scheduledLoanInstallment : loanSchedule.getPeriods()) {
                if (scheduledLoanInstallment.isRepaymentPeriod()) {

                    final LoanRepaymentScheduleInstallment installment = new LoanRepaymentScheduleInstallment(loan,
                            scheduledLoanInstallment.periodNumber(), scheduledLoanInstallment.periodFromDate(),
                            scheduledLoanInstallment.periodDueDate(), scheduledLoanInstallment.principalDue(),
                            scheduledLoanInstallment.interestDue(), scheduledLoanInstallment.feeChargesDue(),
                            scheduledLoanInstallment.penaltyChargesDue());

                    loan.addRepaymentScheduleInstallment(installment);
                }
            }

            memberLoans.add(loan);
        }

        Set<GroupLoanCharge> groupLoanCharges = assembleGroupLoanChargesFrom(command.getCharges(), memberLoans);

        GroupLoan groupLoan = GroupLoan.createNew(fund, loanOfficer, loanTransactionProcessingStrategy, loanProduct, group, memberLoans,
                groupLoanCharges);
        groupLoan.submitGroupApplication(loanTermFrequency, loanTermFrequencyType, command.getSubmittedOnDate(),
                command.getExpectedDisbursementDate(), command.getRepaymentsStartingFromDate(), command.getInterestChargedFromDate(),
                defaultLoanLifecycleStateMachine());
        return groupLoan;
    }

    public Set<GroupLoanCharge> assembleGroupLoanChargesFrom(final GroupLoanChargeCommand[] groupCharges, Set<Loan> memberLoans) {
        final Set<GroupLoanCharge> groupLoanCharges = new HashSet<GroupLoanCharge>();

        if (groupCharges != null) {
            for (GroupLoanChargeCommand groupLoanChargeCommand : groupCharges) {
                Set<LoanCharge> memberLoanCharges = new HashSet<LoanCharge>();

                final Long chargeDefinitionId = groupLoanChargeCommand.getChargeId();
                final Charge chargeDefinition = this.chargeRepository.findOne(chargeDefinitionId);

                for (MemberLoanChargeCommand loanChargeCommand : groupLoanChargeCommand.getMemberCharges()) {

                    if (chargeDefinition == null || chargeDefinition.isDeleted()) { throw new ChargeNotFoundException(chargeDefinitionId); }

                    if (!chargeDefinition.isActive()) { throw new ChargeIsNotActiveException(chargeDefinitionId, chargeDefinition.getName()); }
                    final LoanCharge loanCharge = LoanCharge.createNew(
                            findMemberLoanByClientId(memberLoans, loanChargeCommand.getClientId()), chargeDefinition, loanChargeCommand);
                    memberLoanCharges.add(loanCharge);
                }

                groupLoanCharges.add(GroupLoanCharge.createNew(chargeDefinition, memberLoanCharges, groupLoanChargeCommand));
            }
        }
        return groupLoanCharges;
    }

    private Loan findMemberLoanByClientId(Set<Loan> memberLoans, Long clientId) {
        Loan memberLoan = null;
        for (Loan loan : memberLoans) {
            if (loan.client().getId().equals(clientId)) {
                memberLoan = loan;
                break;
            }
        }
        return memberLoan;
    }

    private LoanLifecycleStateMachine defaultLoanLifecycleStateMachine() {
        List<LoanStatus> allowedLoanStatuses = Arrays.asList(LoanStatus.values());
        return new DefaultLoanLifecycleStateMachine(allowedLoanStatuses);
    }
}
