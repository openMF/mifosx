package org.mifosplatform.organisation.holiday.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.holiday.service.HolidayWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateHolidayCommandHandler implements NewCommandSourceHandler {

    private final HolidayWritePlatformService holidayWritePlatformService;

    @Autowired
    public UpdateHolidayCommandHandler(final HolidayWritePlatformService holidayWritePlatformService) {
        this.holidayWritePlatformService = holidayWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.holidayWritePlatformService.updateHoliday(command);
    }
}
