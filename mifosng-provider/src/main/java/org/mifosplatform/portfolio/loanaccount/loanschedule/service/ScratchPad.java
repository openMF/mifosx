/**
 * 
 */
package org.mifosplatform.portfolio.loanaccount.loanschedule.service;

import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.calendar.service.CalendarHelper;

/**
 * @author Ashok
 *
 */
public class ScratchPad {

	/**
	 * @param args
	 */
	public static void main(String[] args){
       LocalDate startDate = new LocalDate("2013-06-14");
       LocalDate seedDate = new LocalDate("2013-02-18");
       startDate = startDate.plusWeeks(2);
       String recRule = "FREQ=WEEKLY;INTERVAL=2;BYDAY=FR";
       
       Collection<LocalDate> recurringDates = CalendarHelper.getRecurringDatesFrom(recRule, seedDate, startDate);
       
       for (LocalDate localDate : recurringDates) {
		System.out.println(localDate.toString());
       }
       
       LocalDate nextDate = CalendarHelper.getNextRecurringDate(recRule, seedDate, startDate);
       
       //for (LocalDate localDate : recurringDates) {
		System.out.println("Next recurring date :" + nextDate.toString());
       //}
 
    }

}
