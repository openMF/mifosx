package org.mifosplatform.portfolio.client.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_client_non_person")
public class ClientNonPerson extends AbstractPersistable<Long> {
	
	@OneToOne(optional = false)
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false, unique = true)
    private Client client;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constitution_cv_id", nullable = false)
    private CodeValue constitution;
	
	@Column(name = "incorp_no", length = 50, nullable = true)
	private String incorpNumber;
	
	@Column(name = "incorp_validity_till", nullable = true)
	@Temporal(TemporalType.DATE)
	private Date incorpValidityTill;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_business_line_cv_id", nullable = true)
    private CodeValue mainBusinessLine;
	
	@Column(name = "remarks", length = 150, nullable = true)
	private String remarks;
	

	public static ClientNonPerson createNew(final Client client, final CodeValue constitution, final CodeValue mainBusinessLine, String incorpNumber, LocalDate incorpValidityTill, String remarks)
	{				       
		return new ClientNonPerson(client, constitution, mainBusinessLine, incorpNumber, incorpValidityTill, remarks);		
	}
	
	protected ClientNonPerson() {
        //
    }
	
	private ClientNonPerson(final Client client, final CodeValue constitution, final CodeValue mainBusinessLine, final String incorpNumber, final LocalDate incorpValidityTill, final String remarks)
	{
		if(client != null)
			this.client = client;
		
		if(constitution != null)
			this.constitution = constitution;
		
		if(mainBusinessLine != null)
			this.mainBusinessLine = mainBusinessLine;
		
		if (StringUtils.isNotBlank(incorpNumber)) {
            this.incorpNumber = incorpNumber.trim();
        } else {
            this.incorpNumber = null;
        }
		
		if (incorpValidityTill != null) {
            this.incorpValidityTill = incorpValidityTill.toDateTimeAtStartOfDay().toDate();
        }
		
		if (StringUtils.isNotBlank(remarks)) {
            this.remarks = remarks.trim();
        } else {
            this.remarks = null;
        }
		
		validate(client);
	}
	
	private void validate(final Client client) {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        validateIncorpValidityTillDate(client, dataValidationErrors);

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

    }
	
	private void validateIncorpValidityTillDate(final Client client, final List<ApiParameterError> dataValidationErrors) {
        if (getIncorpValidityTillLocalDate() != null && client.dateOfBirthLocalDate() != null && client.dateOfBirthLocalDate().isAfter(getIncorpValidityTillLocalDate())) {

            final String defaultUserMessage = "incorpvaliditytill date cannot be after the incorporation date";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.incorpValidityTill.after.incorp.date",
                    defaultUserMessage, ClientApiConstants.incorpValidityTillParamName, this.incorpValidityTill);

            dataValidationErrors.add(error);
        }
    }
	
	public LocalDate getIncorpValidityTillLocalDate() {
        LocalDate incorpValidityTillLocalDate = null;
        if (this.incorpValidityTill != null) {
            incorpValidityTillLocalDate = LocalDate.fromDateFields(this.incorpValidityTill);
        }
        return incorpValidityTillLocalDate;
    }
	
	public Long constitutionId() {
        Long constitutionId = null;
        if (this.constitution != null) {
            constitutionId = this.constitution.getId();
        }
        return constitutionId;
    }
	
	public Long mainBusinessLineId() {
        Long mainBusinessLineId = null;
        if (this.mainBusinessLine != null) {
            mainBusinessLineId = this.mainBusinessLine.getId();
        }
        return mainBusinessLineId;
    }
	
	public void updateConstitution(CodeValue constitution) {
        this.constitution = constitution;
    }
	
	public void updateMainBusinessLine(CodeValue mainBusinessLine) {
        this.mainBusinessLine = mainBusinessLine;
    }
	
	public Map<String, Object> update(final JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<>(9);
		
		if (command.isChangeInStringParameterNamed(ClientApiConstants.incorpNumberParamName, this.incorpNumber)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.incorpNumberParamName);
            actualChanges.put(ClientApiConstants.incorpNumberParamName, newValue);
            this.incorpNumber = StringUtils.defaultIfEmpty(newValue, null);
        }
		
		if (command.isChangeInStringParameterNamed(ClientApiConstants.remarksParamName, this.remarks)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.remarksParamName);
            actualChanges.put(ClientApiConstants.remarksParamName, newValue);
            this.remarks = StringUtils.defaultIfEmpty(newValue, null);
        }
		
		final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();
        
		if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.incorpValidityTillParamName, getIncorpValidityTillLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.incorpValidityTillParamName);
            actualChanges.put(ClientApiConstants.incorpValidityTillParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.incorpValidityTillParamName);
            this.incorpValidityTill = newValue.toDate();
        }
		
		if (command.isChangeInLongParameterNamed(ClientApiConstants.constitutionIdParamName, constitutionId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.constitutionIdParamName);
            actualChanges.put(ClientApiConstants.constitutionIdParamName, newValue);
        }
		
		if (command.isChangeInLongParameterNamed(ClientApiConstants.mainBusinessLineIdParamName, mainBusinessLineId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.mainBusinessLineIdParamName);
            actualChanges.put(ClientApiConstants.mainBusinessLineIdParamName, newValue);
        }
		
		//validate();

        return actualChanges;
			
	}
}