package org.mifosplatform.infrastructure.smsgateway.service;

import java.util.Map;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.smsgateway.data.SmsGatewayDataValidator;
import org.mifosplatform.infrastructure.smsgateway.domain.SmsGateway;
import org.mifosplatform.infrastructure.smsgateway.domain.SmsGatewayAssembler;
import org.mifosplatform.infrastructure.smsgateway.domain.SmsGatewayRepository;
import org.mifosplatform.infrastructure.smsgateway.domain.frontlinesms.FrontlineSMSMessage;
import org.mifosplatform.organisation.staff.domain.Staff;
import org.mifosplatform.organisation.staff.domain.StaffRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SmsGatewayWritePlatformServiceJpaRepositoryImpl implements SmsGatewayWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(SmsGatewayWritePlatformServiceJpaRepositoryImpl.class);
	private final PlatformSecurityContext context;
	private final SmsGatewayAssembler assembler;
	private final SmsGatewayRepository repository;
	private final SmsGatewayDataValidator validator;
	private final StaffRepositoryWrapper staffRepository;

	@Autowired
	public SmsGatewayWritePlatformServiceJpaRepositoryImpl(final SmsGatewayAssembler assembler, final SmsGatewayRepository repository,
			final SmsGatewayDataValidator validator, final PlatformSecurityContext context, final StaffRepositoryWrapper staffRepository) {
		this.assembler = assembler;
		this.repository = repository;
		this.validator = validator;
		this.context = context;
		this.staffRepository = staffRepository;
	}

	@Transactional
	@Override
	public CommandProcessingResult create(final JsonCommand command) {

		try {
			this.validator.validateForCreate(command.json());

			final SmsGateway gateway = this.assembler.assembleFromJson(command);

			this.repository.save(gateway);

			return new CommandProcessingResultBuilder() 
			.withCommandId(command.commandId()) 
			.withEntityId(gateway.getId()) 
			.build();
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult update(final Long resourceId, final JsonCommand command) {

		try {
			this.validator.validateForUpdate(command.json());

			final SmsGateway gateway = this.assembler.assembleFromResourceId(resourceId);
			final Map<String, Object> changes = gateway.update(command);
			if (!changes.isEmpty()) {
				this.repository.save(gateway);
			}

			return new CommandProcessingResultBuilder()
			.withCommandId(command.commandId())
			.withEntityId(resourceId) 
			.with(changes)
			.build();
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult delete(final Long resourceId) {

		try {
			final SmsGateway gateway = this.assembler.assembleFromResourceId(resourceId);
			this.repository.delete(gateway);
			this.repository.flush();
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(null, dve);
			return CommandProcessingResult.empty();
		}
		return new CommandProcessingResultBuilder().withEntityId(resourceId).build();
	}

	@Transactional
	@Override
	public CommandProcessingResult testSmsGateway(final Long resourceId, final JsonCommand command) {

		try {
			final Long staffId = this.context.authenticatedUser().getStaffId();
			final Staff staff = this.staffRepository.findOneWithNotFoundDetection(staffId);
			final String staffMobileNo = staff.mobileNo();
			final SmsGateway smsGateway = this.assembler.assembleFromResourceId(resourceId);
			final String messageString = "Your gateway has been configured successfully.";
			final FrontlineSMSMessage frontlineSMSMessage = new FrontlineSMSMessage(smsGateway.authToken(), messageString, staffMobileNo, smsGateway.url());
			
			frontlineSMSMessage.sendPostRequest();
			
			return new CommandProcessingResultBuilder()
			.withCommandId(command.commandId())
			.withEntityId(command.entityId())
			.build();
		} catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		} catch (Exception e) {
			return CommandProcessingResult.empty();
		}
	}

	/*
	 * Guaranteed to throw an exception no matter what the data integrity issue
	 * is.
	 */
	private void handleDataIntegrityIssues(@SuppressWarnings("unused") final JsonCommand command, final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();

		logger.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.sms.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}
}
