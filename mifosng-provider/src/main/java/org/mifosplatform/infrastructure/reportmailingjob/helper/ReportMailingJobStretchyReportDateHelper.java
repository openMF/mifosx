/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.reportmailingjob.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mifosplatform.infrastructure.reportmailingjob.domain.ReportMailingJobStretchyReportParamDateOption;

public class ReportMailingJobStretchyReportDateHelper {
    public static final String mysqlDateFormat = "yyyy-MM-dd";
    
    /** 
     * get the current date as string using the mysql date format yyyy-MM-dd 
     **/
    public static String getTodayDateAsString() {
        // get a calendar instance, which defaults to "now"
        Calendar calendar = Calendar.getInstance();
        
        // get a date to represent "today"
        Date today = calendar.getTime();
        
        // get a SimpleDateFormat instance, passing the mysql date format as parameter
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mysqlDateFormat);
        
        // return date as string
        return simpleDateFormat.format(today);
    }
    
    /** 
     * get the yesterday's date as string using the mysql date format yyyy-MM-dd 
     **/
    public static String getYesterdayDateAsString() {
        // get a calendar instance, which defaults to "now"
        Calendar calendar = Calendar.getInstance();
        
        // add one day to the date/calendar
        calendar.add(Calendar.DAY_OF_YEAR, -1);
         
        // now get "yesterday"
        Date yesterday = calendar.getTime();
        
        // get a SimpleDateFormat instance, passing the mysql date format as parameter
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mysqlDateFormat);
        
        // return date as string
        return simpleDateFormat.format(yesterday);
    }
    
    /** 
     * get the tomorrow's date as string using the mysql date format yyyy-MM-dd 
     **/
    public static String getTomorrowDateAsString() {
        // get a calendar instance, which defaults to "now"
        Calendar calendar = Calendar.getInstance();
        
        // add one day to the date/calendar
        calendar.add(Calendar.DAY_OF_YEAR, 1);
         
        // now get "tomorrow"
        Date tomorrow = calendar.getTime();
        
        // get a SimpleDateFormat instance, passing the mysql date format as parameter
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mysqlDateFormat);
        
        // return date as string
        return simpleDateFormat.format(tomorrow);
    }
    
    /** 
     * get date as string based on the value of the {@link ReportMailingJobStretchyReportParamDateOption} object
     * 
     * @param enumOption
     **/
    public static String getDateAsString(final ReportMailingJobStretchyReportParamDateOption enumOption) {
        String dateAsString = null;
        
        switch (enumOption) {
            case TODAY:
                dateAsString = getTodayDateAsString();
                break;
                
            case YESTERDAY:
                dateAsString = getYesterdayDateAsString();
                break;
                
            case TOMORROW:
                dateAsString = getTomorrowDateAsString();
                break;
                
            default:
                break;
        }
        
        return dateAsString;
    }
}
