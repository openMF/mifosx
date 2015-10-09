/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.dsa.service;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.dsa.domain.Dsa;
import org.mifosplatform.organisation.dsa.domain.DsaRepository;
import org.mifosplatform.organisation.dsa.exception.DsaNotFoundException;
import org.mifosplatform.organisation.dsa.serialization.DsaCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DsaWritePlatformServiceJpaRepositoryImpl implements DsaWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DsaWritePlatformServiceJpaRepositoryImpl.class);

    private final DsaCommandFromApiJsonDeserializer fromApiJsonDeserializer;
    private final DsaRepository dsaRepository;
    private final OfficeRepository officeRepository;

    @Autowired
    public DsaWritePlatformServiceJpaRepositoryImpl(final DsaCommandFromApiJsonDeserializer fromApiJsonDeserializer,
            final DsaRepository staffRepository, final OfficeRepository officeRepository) {
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.dsaRepository = staffRepository;
        this.officeRepository = officeRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createDsa(final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForCreate(command.json());

            final Long officeId = command.longValueOfParameterNamed("officeId");

            final Office staffOffice = this.officeRepository.findOne(officeId);
            if (staffOffice == null) { throw new OfficeNotFoundException(officeId); }

            final Dsa dsa = Dsa.fromJson(staffOffice, command);

            this.dsaRepository.save(dsa);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(dsa.getId()).withOfficeId(officeId) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleStaffDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateDsa(final Long dsaId, final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForUpdate(command.json());

            final Dsa dsaForUpdate = this.dsaRepository.findOne(dsaId);
            if (dsaForUpdate == null) { throw new DsaNotFoundException(dsaId); }

            final Map<String, Object> changesOnly = dsaForUpdate.update(command);

            if (changesOnly.containsKey("officeId")) {
                final Long officeId = (Long) changesOnly.get("officeId");
                final Office newOffice = this.officeRepository.findOne(officeId);
                if (newOffice == null) { throw new OfficeNotFoundException(officeId); }

                dsaForUpdate.changeOffice(newOffice);
            }

            if (!changesOnly.isEmpty()) {
                this.dsaRepository.saveAndFlush(dsaForUpdate);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(dsaId)
                    .withOfficeId(dsaForUpdate.officeId()).with(changesOnly).build();
        } catch (final DataIntegrityViolationException dve) {
            handleStaffDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleStaffDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();

         if (realCause.getMessage().contains("display_name")) {
            final String lastname = command.stringValueOfParameterNamed("lastname");
            String displayName = lastname;
            if (!StringUtils.isBlank(displayName)) {
                final String firstname = command.stringValueOfParameterNamed("firstname");
                displayName = lastname + ", " + firstname;
            }
            throw new PlatformDataIntegrityException("error.msg.staff.duplicate.displayName", "A Dsa with the given display name '"
                    + displayName + "' already exists", "displayName", displayName);
        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.staff.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}