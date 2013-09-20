package org.mifosplatform.portfolio.loanaccount;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.mifosplatform.portfolio.loanaccount.domain.transactionprocessor.impl.RBILoanRepaymentScheduleTransactionProcessor;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("unused")
@RunWith(MockitoJUnitRunner.class)
public class AdhikarLoanRepaymentScheduleTransactionProcessorTest {

    // class under test
    private RBILoanRepaymentScheduleTransactionProcessor processor;

    //
    private final LocalDate july2nd = new LocalDate(2012, 7, 2);
    private final MonetaryCurrency usDollars = new MonetaryCurrencyBuilder().withCode("USD").withDigitsAfterDecimal(2).build();
    private List<LoanRepaymentScheduleInstallment> installments;

    @Before
    public void setUpForEachTestCase() {

        this.installments = LoanScheduleTestDataHelper.createSimpleLoanSchedule(this.july2nd, this.usDollars);

        this.processor = new RBILoanRepaymentScheduleTransactionProcessor();
    }

    /**
     * Scenario 1: Given no overdue installments, current interest due is paid
     * before principal.
     */
    @Test
    public void givenNoOverdueInstallmentsOnTimeRepaymentPaysOffInterestDueFirst() {

        // // setup
        // Money repaymentAmount = new
        // MoneyBuilder().with(usDollars).with("100.00").build();
        // LoanTransaction onTimePartialRepayment = new
        // LoanTransactionBuilder().repayment().with(july2nd).with(repaymentAmount).build();
        //
        // // execute test
        // processor.handleTransaction(onTimePartialRepayment, usDollars,
        // installments);
        //
        // // verification of derived fields on installments
        // LoanRepaymentScheduleInstallment firstInstallment =
        // this.installments.get(0);
        // assertThatLoanInstallmentHasDerivedFieldsOf(principalOf("0.00"),
        // interestOf("100.00"), interestWaivedOf("0.00"), firstInstallment,
        // usDollars);
        //
        // assertThatLoanTransactionHasDerivedFieldsOf(principalOf("0.00"),
        // interestOf("100.00"), onTimePartialRepayment, usDollars);
    }

    // /**
    // * Scenario 2: Given overdue interest exists on overdue installment, a
    // late
    // * repayment results in overdue interest and current interest being paid
    // * before principal component for overdue or current installments.
    // */
    // @Test
    // public void
    // givenOverdueInterestOnOverdueInstallmentsLateRepaymentPaysOffOverdueInterestAndCurrentInterestDue()
    // {
    //
    // // setup
    // Money repaymentAmount = new
    // MoneyBuilder().with(usDollars).with("300.00").build();
    // LoanTransaction lateRepayment = new
    // LoanTransactionBuilder().repayment().with(july2nd.plusMonths(1)).with(repaymentAmount).build();
    //
    // // execute test
    // processor.handleTransaction(lateRepayment, usDollars, installments);
    //
    // // verification of derived fields on installments
    // LoanRepaymentScheduleInstallment firstInstallment =
    // this.installments.get(0);
    // assertThatLoanInstallmentHasDerivedFieldsOf(principalOf("0.00"),
    // interestOf("200.00"), interestWaivedOf("0.00"), firstInstallment,
    // usDollars);
    //
    // LoanRepaymentScheduleInstallment secondInstallment =
    // this.installments.get(1);
    // assertThatLoanInstallmentHasDerivedFieldsOf(principalOf("0.00"),
    // interestOf("100.00"), interestWaivedOf("0.00"), secondInstallment,
    // usDollars);
    //
    // LoanRepaymentScheduleInstallment thirdInstallment =
    // this.installments.get(2);
    // assertThatLoanInstallmentHasDerivedFieldsOf(principalOf("0.00"),
    // interestOf("0.00"), interestWaivedOf("0.00"), thirdInstallment,
    // usDollars);
    //
    // assertThatLoanTransactionHasDerivedFieldsOf(principalOf("0.00"),
    // interestOf("300.00"), lateRepayment, usDollars);
    // }
    //
    // /**
    // * Scenario 3: Given an overdue installment with overdue interest and
    // principal, a late
    // * repayment results in overdue interest and current interest being paid
    // * before principal component for overdue or current installments.
    // */
    // @Test
    // public void
    // givenAFullyOverdueInstallmentALateRepaymentShouldPayOffOverdueInterestAndCurrentInterestDueFirstThenOverduePrincipal()
    // {
    //
    // // setup
    // Money repaymentAmount = new
    // MoneyBuilder().with(usDollars).with("900.00").build();
    // LoanTransaction lateRepayment = new
    // LoanTransactionBuilder().repayment().with(july2nd.plusMonths(1)).with(repaymentAmount).build();
    //
    // // execute test
    // processor.handleTransaction(lateRepayment, usDollars, installments);
    //
    // // verification of derived fields on installments
    // LoanRepaymentScheduleInstallment firstInstallment =
    // this.installments.get(0);
    // assertThatLoanInstallmentHasDerivedFieldsOf(principalOf("500.00"),
    // interestOf("200.00"), interestWaivedOf("0.00"), firstInstallment,
    // usDollars);
    //
    // LoanRepaymentScheduleInstallment secondInstallment =
    // this.installments.get(1);
    // assertThatLoanInstallmentHasDerivedFieldsOf(principalOf("0.00"),
    // interestOf("200.00"), interestWaivedOf("0.00"), secondInstallment,
    // usDollars);
    //
    // LoanRepaymentScheduleInstallment thirdInstallment =
    // this.installments.get(2);
    // assertThatLoanInstallmentHasDerivedFieldsOf(principalOf("0.00"),
    // interestOf("0.00"), interestWaivedOf("0.00"), thirdInstallment,
    // usDollars);
    //
    // assertThatLoanTransactionHasDerivedFieldsOf(principalOf("500.00"),
    // interestOf("400.00"), lateRepayment, usDollars);
    // }
    //
    // private void assertThatLoanTransactionHasDerivedFieldsOf(
    // final String principal,
    // final String interest,
    // final LoanTransaction transaction, final MonetaryCurrency currency) {
    //
    // Money expectedPrincipalPortion = new
    // MoneyBuilder().with(currency).with(principal).build();
    // assertThat("Principal portion of transaction not as expected: ",
    // transaction.getPrincipalPortion(currency).toString(),
    // is(expectedPrincipalPortion.toString()));
    //
    // Money expectedInterestPortion = new
    // MoneyBuilder().with(currency).with(interest).build();
    // assertThat("Interest portion of transaction not as expected: ",
    // transaction.getInterestPortion(currency).toString(),
    // is(expectedInterestPortion.toString()));
    // }
    //
    // private void assertThatLoanInstallmentHasDerivedFieldsOf(
    // final String principal,
    // final String interest,
    // final String interestWaived,
    // final LoanRepaymentScheduleInstallment installment, final
    // MonetaryCurrency currency) {
    //
    // Money expectedPrincipalCompleted = new
    // MoneyBuilder().with(currency).with(principal).build();
    // assertThat("principal not as expected",
    // installment.getPrincipalCompleted(currency).toString(),
    // is(expectedPrincipalCompleted.toString()));
    //
    // Money expectedInterestCompleted = new
    // MoneyBuilder().with(currency).with(interest).build();
    // assertThat("interest completed not as expected: ",installment.getInterestCompleted(currency).toString(),
    // is(expectedInterestCompleted.toString()));
    //
    // Money expectedInterestWaived = new
    // MoneyBuilder().with(currency).with(interestWaived).build();
    // assertThat("interest waived not as expected: ",
    // installment.getInterestWaived(currency).toString(),
    // is(expectedInterestWaived.toString()));
    // }
    //
    // private String principalOf(final String value) {
    // return value;
    // }
    //
    // private String interestOf(final String value) {
    // return value;
    // }
    //
    // private String interestWaivedOf(final String value) {
    // return value;
    // }
}