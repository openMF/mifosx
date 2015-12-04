/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.domain;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.dataqueries.domain.Report;
import org.mifosplatform.infrastructure.sms.data.SmsCampaignValidator;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.*;

@Entity
@Table(name = "sms_campaign")
public class SmsCampaign extends AbstractPersistable<Long> {

    @Column(name = "campaign_name", nullable = false)
    private String campaignName;

    @Column(name = "campaign_type", nullable = false)
    private Integer campaignType;

    @ManyToOne
    @JoinColumn(name = "runreport_id", nullable = false)
    private Report businessRuleId ;

    @Column(name = "param_value")
    private String paramValue;

    @Column(name = "status_enum", nullable = false)
    private Integer status;

    @Column(name ="message", nullable =false)
    private String message;

    @Column(name = "closedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date closureDate;

    @ManyToOne(optional = true)
    @JoinColumn(name = "closedon_userid", nullable = true)
    private AppUser closedBy;

    @Column(name = "submittedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date submittedOnDate;

    @ManyToOne(optional = true)
    @JoinColumn(name = "submittedon_userid", nullable = true)
    private AppUser submittedBy;

    @Column(name = "approvedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date approvedOnDate;

    @ManyToOne(optional = true)
    @JoinColumn(name = "approvedon_userid", nullable = true)
    private AppUser approvedBy;

    @Column(name = "recurrence", nullable = false)
    private String recurrence;

    @Column(name = "next_trigger_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextTriggerDate;

    @Column(name = "last_trigger_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastTriggerDate;

    @Column(name = "recurrence_start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date recurrenceStartDate;

    @Column(name="is_visible",nullable = true)
    private boolean isVisible;

    public SmsCampaign() {
    }

    private SmsCampaign(final String campaignName, final Integer campaignType,
                        final Report businessRuleId, final String paramValue,
                        final String message,final LocalDate submittedOnDate,
                        final AppUser submittedBy,final String recurrence,final LocalDateTime localDateTime) {
        this.campaignName = campaignName;
        this.campaignType = SmsCampaignType.fromInt(campaignType).getValue();
        this.businessRuleId = businessRuleId;
        this.paramValue = paramValue;
        this.status     = SmsCampaignStatus.PENDING.getValue();
        this.message    = message;
        this.submittedOnDate = submittedOnDate.toDate();
        this.submittedBy = submittedBy;
        this.recurrence = recurrence;
        LocalDateTime recurrenceStartDate = new LocalDateTime();
        this.isVisible = true;
        if(localDateTime != null){
            this.recurrenceStartDate = localDateTime.toDate();
        }else{
            this.recurrenceStartDate = recurrenceStartDate.toDate();
        }

    }

    public static SmsCampaign instance(final AppUser submittedBy,final Report report,final JsonCommand command){

        final String campaignName = command.stringValueOfParameterNamed(SmsCampaignValidator.campaignName);
        final Long  campaignType = command.longValueOfParameterNamed(SmsCampaignValidator.campaignType);

        final String paramValue = command.stringValueOfParameterNamed(SmsCampaignValidator.paramValue);

        final String message   = command.stringValueOfParameterNamed(SmsCampaignValidator.message);
        LocalDate submittedOnDate = new LocalDate();
        if (command.hasParameter(SmsCampaignValidator.submittedOnDateParamName)) {
            submittedOnDate = command.localDateValueOfParameterNamed(SmsCampaignValidator.submittedOnDateParamName);
        }

        final String recurrence   = command.stringValueOfParameterNamed(SmsCampaignValidator.recurrenceParamName);
        final Locale locale = command.extractLocale();
        final  DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        LocalDateTime recurrenceStartDate = new  LocalDateTime();
        if(SmsCampaignType.fromInt(campaignType.intValue()).isSchedule()) {
            if(command.hasParameter(SmsCampaignValidator.recurrenceStartDate)){
                recurrenceStartDate = LocalDateTime.parse(command.stringValueOfParameterNamed(SmsCampaignValidator.recurrenceStartDate),fmt);
            }
        } else{recurrenceStartDate = null;}


        return new SmsCampaign(campaignName,campaignType.intValue(),report,paramValue,message,submittedOnDate,submittedBy,recurrence,recurrenceStartDate);
    }

    public Map<String,Object> update(JsonCommand command){

        final Map<String, Object> actualChanges = new LinkedHashMap<>(5);

        if (command.isChangeInStringParameterNamed(SmsCampaignValidator.campaignName, this.campaignName)) {
            final String newValue = command.stringValueOfParameterNamed(SmsCampaignValidator.campaignName);
            actualChanges.put(SmsCampaignValidator.campaignName, newValue);
            this.campaignName = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(SmsCampaignValidator.message, this.message)) {
            final String newValue = command.stringValueOfParameterNamed(SmsCampaignValidator.message);
            actualChanges.put(SmsCampaignValidator.message, newValue);
            this.message = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInStringParameterNamed(SmsCampaignValidator.paramValue, this.paramValue)) {
            final String newValue = command.stringValueOfParameterNamed(SmsCampaignValidator.paramValue);
            actualChanges.put(SmsCampaignValidator.paramValue, newValue);
            this.paramValue = StringUtils.defaultIfEmpty(newValue, null);
        }
        if (command.isChangeInIntegerParameterNamed(SmsCampaignValidator.campaignType, this.campaignType)) {
            final Integer newValue = command.integerValueOfParameterNamed(SmsCampaignValidator.campaignType);
            actualChanges.put(SmsCampaignValidator.campaignType, SmsCampaignType.fromInt(newValue));
            this.campaignType = SmsCampaignType.fromInt(newValue).getValue();
        }
        if (command.isChangeInLongParameterNamed(SmsCampaignValidator.runReportId, (this.businessRuleId != null) ? this.businessRuleId.getId() : null)) {
            final String newValue = command.stringValueOfParameterNamed(SmsCampaignValidator.runReportId);
            actualChanges.put(SmsCampaignValidator.runReportId, newValue);
        }
        if (command.isChangeInStringParameterNamed(SmsCampaignValidator.recurrenceParamName, this.recurrence)) {
            final String newValue = command.stringValueOfParameterNamed(SmsCampaignValidator.recurrenceParamName);
            actualChanges.put(SmsCampaignValidator.recurrenceParamName, newValue);
            this.recurrence = StringUtils.defaultIfEmpty(newValue, null);
        }
        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();
        final Locale locale = command.extractLocale();
        final  DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        if (command.isChangeInLocalDateParameterNamed(SmsCampaignValidator.recurrenceStartDate, getRecurrenceStartDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(SmsCampaignValidator.recurrenceStartDate);
            actualChanges.put(SmsCampaignValidator.recurrenceStartDate, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDateTime newValue =  LocalDateTime.parse(valueAsInput, fmt);

            this.recurrenceStartDate = newValue.toDate();
        }

        return actualChanges;
    }


    public void activate(final AppUser currentUser, final DateTimeFormatter formatter, final LocalDate activationLocalDate){

        if(isActive()){
            //handle errors if already activated
            final String defaultUserMessage = "Cannot activate campaign. Campaign is already active.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.already.active", defaultUserMessage,
                    SmsCampaignValidator.activationDateParamName, activationLocalDate.toString(formatter));

            final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
        this.approvedOnDate = activationLocalDate.toDate();
        this.approvedBy = currentUser;
        this.status = SmsCampaignStatus.ACTIVE.getValue();

       validate();
    }

    public void close(final AppUser currentUser,final DateTimeFormatter dateTimeFormatter, final LocalDate closureLocalDate){
        if(isClosed()){
            //handle errors if already activated
            final String defaultUserMessage = "Cannot close campaign. Campaign already in closed state.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.already.closed", defaultUserMessage,
                    SmsCampaignValidator.statusParamName, SmsCampaignStatus.fromInt(this.status).getCode());

            final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
        if(this.campaignType.intValue() == SmsCampaignType.SCHEDULE.getValue()){
            this.nextTriggerDate = null;
            this.lastTriggerDate = null;
        }
        this.closedBy = currentUser;
        this.closureDate = closureLocalDate.toDate();
        this.status     = SmsCampaignStatus.CLOSED.getValue();
        validateClosureDate();
    }

    public void reactivate(final AppUser currentUser, final DateTimeFormatter dateTimeFormat,final LocalDate reactivateLocalDate){

        if(!isClosed()){
            //handle errors if already activated
            final String defaultUserMessage = "Cannot reactivate campaign. Campaign must be in closed state.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.must.be.closed", defaultUserMessage,
                    SmsCampaignValidator.statusParamName, SmsCampaignStatus.fromInt(this.status).getCode());

            final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }

        this.approvedOnDate = reactivateLocalDate.toDate();
        this.status  = SmsCampaignStatus.ACTIVE.getValue();
        this.approvedBy =currentUser;
        this.closureDate = null;
        this.isVisible = true;
        this.closedBy = null;

        validateReactivate();
    }

    public void delete(){
        if(!isClosed()){
            //handle errors if already activated
            final String defaultUserMessage = "Cannot delete campaign. Campaign must be in closed state.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.must.be.closed", defaultUserMessage,
                    SmsCampaignValidator.statusParamName, SmsCampaignStatus.fromInt(this.status).getCode());

            final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
        this.isVisible = false;
    }




    public boolean isActive(){
        return SmsCampaignStatus.fromInt(this.status).isActive();
    }

    public boolean isPending(){
        return  SmsCampaignStatus.fromInt(this.status).isPending();
    }

    public boolean isClosed(){
        return SmsCampaignStatus.fromInt(this.status).isClosed();
    }

    public boolean isDirect(){
        return SmsCampaignType.fromInt(this.campaignType).isDirect();
    }

    public boolean isSchedule(){
        return SmsCampaignType.fromInt(this.campaignType).isSchedule();
    }

    private void validate() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        validateActivationDate(dataValidationErrors);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    private void validateReactivate(){
        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        validateReactivationDate(dataValidationErrors);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    private void validateClosureDate(){
        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        validateClosureDate(dataValidationErrors);
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    private void validateActivationDate(final List<ApiParameterError> dataValidationErrors) {

        if (getSubmittedOnDate() != null && isDateInTheFuture(getSubmittedOnDate())) {

            final String defaultUserMessage = "submitted date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.submittedOnDate.in.the.future",
                    defaultUserMessage, SmsCampaignValidator.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null && getSubmittedOnDate() != null && getSubmittedOnDate().isAfter(getActivationLocalDate())) {

            final String defaultUserMessage = "submitted date cannot be after the activation date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.submittedOnDate.after.activation.date",
                    defaultUserMessage, SmsCampaignValidator.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

        if (getActivationLocalDate() != null && isDateInTheFuture(getActivationLocalDate())) {

            final String defaultUserMessage = "Activation date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.activationDate.in.the.future",
                    defaultUserMessage, SmsCampaignValidator.activationDateParamName, getActivationLocalDate());

            dataValidationErrors.add(error);
        }

    }

    private void validateReactivationDate(final List<ApiParameterError> dataValidationErrors){
        if (getActivationLocalDate() != null && isDateInTheFuture(getActivationLocalDate())) {

            final String defaultUserMessage = "Activation date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.activationDate.in.the.future",
                    defaultUserMessage, SmsCampaignValidator.activationDateParamName, getActivationLocalDate());

            dataValidationErrors.add(error);
        }
        if (getActivationLocalDate() != null && getSubmittedOnDate() != null && getSubmittedOnDate().isAfter(getActivationLocalDate())) {

            final String defaultUserMessage = "submitted date cannot be after the activation date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.submittedOnDate.after.activation.date",
                    defaultUserMessage, SmsCampaignValidator.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }
        if (getSubmittedOnDate() != null && isDateInTheFuture(getSubmittedOnDate())) {

            final String defaultUserMessage = "submitted date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.submittedOnDate.in.the.future",
                    defaultUserMessage, SmsCampaignValidator.submittedOnDateParamName, this.submittedOnDate);

            dataValidationErrors.add(error);
        }

    }

    private void validateClosureDate(final List<ApiParameterError> dataValidationErrors){
        if (getClosureDate() != null && isDateInTheFuture(getClosureDate())) {
            final String defaultUserMessage = "closure date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.campaign.closureDate.in.the.future",
                    defaultUserMessage, SmsCampaignValidator.closureDateParamName, this.closureDate);

            dataValidationErrors.add(error);
        }
    }





    public LocalDate getSubmittedOnDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.submittedOnDate), null);

    }
    public LocalDate getClosureDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.closureDate), null);
    }

    public LocalDate getActivationLocalDate() {
        LocalDate activationLocalDate = null;
        if (this.approvedOnDate != null) {
            activationLocalDate = LocalDate.fromDateFields(this.approvedOnDate);
        }
        return activationLocalDate;
    }
    private boolean isDateInTheFuture(final LocalDate localDate) {
        return localDate.isAfter(DateUtils.getLocalDateOfTenant());
    }

    public Report getBusinessRuleId() {
        return this.businessRuleId;
    }

    public String getCampaignName() {
        return this.campaignName;
    }


    public String getMessage() {
        return this.message;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public String getRecurrence() {
        return this.recurrence;
    }

    public LocalDate getRecurrenceStartDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.recurrenceStartDate), null);
    }
    public LocalDateTime getRecurrenceStartDateTime() {
        return (LocalDateTime) ObjectUtils.defaultIfNull(new LocalDateTime(this.recurrenceStartDate), null);
    }



    public void setLastTriggerDate(Date lastTriggerDate) {
        this.lastTriggerDate = lastTriggerDate;
    }

    public void setNextTriggerDate(Date nextTriggerDate) {
        this.nextTriggerDate = nextTriggerDate;
    }

    public LocalDateTime getNextTriggerDate() {
        return (LocalDateTime) ObjectUtils.defaultIfNull(new LocalDateTime(this.nextTriggerDate), null);

    }

    public Date getNextTriggerDateInDate(){
        return this.nextTriggerDate;
    }

    public LocalDate getLastTriggerDate() {
        return (LocalDate) ObjectUtils.defaultIfNull(new LocalDate(this.lastTriggerDate), null);
    }

    public void updateIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void updateBusinessRuleId(final Report report){
        this.businessRuleId = report;
    }
}
