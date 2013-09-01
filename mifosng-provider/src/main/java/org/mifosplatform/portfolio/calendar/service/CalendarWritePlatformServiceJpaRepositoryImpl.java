/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.calendar.service;

import java.util.Collection;
import java.util.Map;

import org.mifosplatform.infrastructure.configuration.domain.ConfigurationDomainService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.portfolio.calendar.domain.Calendar;
import org.mifosplatform.portfolio.calendar.domain.CalendarEntityType;
import org.mifosplatform.portfolio.calendar.domain.CalendarInstance;
import org.mifosplatform.portfolio.calendar.domain.CalendarInstanceRepository;
import org.mifosplatform.portfolio.calendar.domain.CalendarRepository;
import org.mifosplatform.portfolio.calendar.exception.CalendarNotFoundException;
import org.mifosplatform.portfolio.calendar.serialization.CalendarCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.loanaccount.service.LoanWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class CalendarWritePlatformServiceJpaRepositoryImpl implements CalendarWritePlatformService {

    private final CalendarRepository calendarRepository;
    private final CalendarCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final LoanWritePlatformService loanWritePlatformService;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public CalendarWritePlatformServiceJpaRepositoryImpl(final CalendarRepository calendarRepository,
            final CalendarCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final CalendarInstanceRepository calendarInstanceRepository, final LoanWritePlatformService loanWritePlatformService, final ConfigurationDomainService configurationDomainService) {
        this.calendarRepository = calendarRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.loanWritePlatformService = loanWritePlatformService;
        this.configurationDomainService = configurationDomainService;
    }

    @Override
    public CommandProcessingResult createCalendar(final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForCreate(command.json());

        final Calendar newCalendar = Calendar.fromJson(command);
        this.calendarRepository.save(newCalendar);

        final CalendarInstance newCalendarInstance = CalendarInstance.fromJson(newCalendar, command);
        this.calendarInstanceRepository.save(newCalendarInstance);

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(newCalendar.getId()) //
                .build();

    }

    @Override
    public CommandProcessingResult updateCalendar(final JsonCommand command) {

        this.fromApiJsonDeserializer.validateForUpdate(command.json());

        final Long calendarId = command.entityId();
        final Calendar calendarForUpdate = this.calendarRepository.findOne(calendarId);
        if (calendarForUpdate == null) { throw new CalendarNotFoundException(calendarId); }

        final Map<String, Object> changes = calendarForUpdate.update(command);

        if (!changes.isEmpty()) {
            this.calendarRepository.saveAndFlush(calendarForUpdate);
            
            if(this.configurationDomainService.isRescheduleFutureRepaymentsEnabled()){
                //In this approach loans following this meeting calendar will be affected immediately.
                //get all calendar instances following this meeting calendar and calendar entity type is loan    
                final Collection<CalendarInstance> loanCalendarInstances = this.calendarInstanceRepository.findByCalendarIdAndEntityTypeId(calendarId, CalendarEntityType.LOANS.getValue());
                
                if (!CollectionUtils.isEmpty(loanCalendarInstances)) {
                    // update all loans which are following this meeting calendar
                    this.loanWritePlatformService.applyMeetingDateChanges(calendarForUpdate, loanCalendarInstances);
                    //
                }
            }
        }

        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(calendarForUpdate.getId()) //
                .with(changes) //
                .build();
    }

    @Override
    public CommandProcessingResult deleteCalendar(final Long calendarId) {
        final Calendar calendarForDelete = this.calendarRepository.findOne(calendarId);
        if (calendarForDelete == null) { throw new CalendarNotFoundException(calendarId); }

        this.calendarRepository.delete(calendarForDelete);
        return new CommandProcessingResultBuilder() //
                .withCommandId(null) //
                .withEntityId(calendarId) //
                .build();
    }

    @Override
    public CommandProcessingResult createCalendarInstance(final Long calendarId, final Long entityId, final Integer entityTypeId) {

        final Calendar calendarForUpdate = this.calendarRepository.findOne(calendarId);
        if (calendarForUpdate == null) { throw new CalendarNotFoundException(calendarId); }
        
        final CalendarInstance newCalendarInstance = new CalendarInstance(calendarForUpdate, entityId, entityTypeId);
        this.calendarInstanceRepository.save(newCalendarInstance);
        
        return new CommandProcessingResultBuilder() //
        .withCommandId(null) //
        .withEntityId(calendarForUpdate.getId()) //
        .build();
    }

    @Override
    public CommandProcessingResult updateCalendarInstance(Long calendarId, Long entityId, Integer entityTypeId) {
        final Calendar calendarForUpdate = this.calendarRepository.findOne(calendarId);
        if (calendarForUpdate == null) { throw new CalendarNotFoundException(calendarId); }
        
        final CalendarInstance calendarInstanceForUpdate = this.calendarInstanceRepository.findByCalendarIdAndEntityIdAndEntityTypeId(calendarId, entityId, entityTypeId);
        this.calendarInstanceRepository.saveAndFlush(calendarInstanceForUpdate);
        return new CommandProcessingResultBuilder() //
        .withCommandId(null) //
        .withEntityId(calendarForUpdate.getId()) //
        .build();
    }

    
}
