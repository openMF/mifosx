package org.mifosplatform.infrastructure.core.service;

import javax.sql.DataSource;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.domain.Tenant;
import org.mifosplatform.infrastructure.core.domain.TenantRepository;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.TenantCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.googlecode.flyway.core.Flyway;

@Service
public class TenantWritePlatformServiceJpaRepositoryImpl implements TenantWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(TenantWritePlatformServiceJpaRepositoryImpl.class);
	
	private final PlatformSecurityContext context;
	private final TenantCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final TenantRepository tenantRepository;
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public TenantWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
			final TenantCommandFromApiJsonDeserializer fromApiJsonDeserializer, final TenantRepository tenantRepository,
			@Qualifier("tenantDataSourceJndi") final DataSource dataSource) {
		
		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.tenantRepository = tenantRepository;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public CommandProcessingResult createTenant(JsonCommand command) {
		
		try {
			context.authenticatedUser();
			
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			
			final Tenant tenant = Tenant.fromJson(command);
			
			this.tenantRepository.save(tenant);
			
			createTenantDatabase(tenant);
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(tenant.getId()).build();
		} catch (DataIntegrityViolationException dve) {
			
			handleTenantDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}
	
	private void createTenantDatabase(Tenant tenant) {
		
		jdbcTemplate.execute("CREATE DATABASE `" + tenant.getSchemaName() + "`");
		
		Flyway flyway = new Flyway();
		flyway.setDataSource(tenant.databaseURL(), tenant.getSchemaUsername(), tenant.getSchemaPassword());
		flyway.setLocations("sql");
		flyway.migrate();
	}
	
	private void handleTenantDataIntegrityIssues(final JsonCommand command, DataIntegrityViolationException dve) {

        Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("tenant_identifier")) {
            final String identifier = command.stringValueOfParameterNamed("identifier");
            throw new PlatformDataIntegrityException("error.msg.tenant.duplicate.identifier", "A tenant with the identifier '" + identifier
                    + "' already exists", "identifier", identifier);
        } else if (realCause.getMessage().contains("tenant_schema_name")) {
            final String schemaName = command.stringValueOfParameterNamed("schema_name");
            throw new PlatformDataIntegrityException("error.msg.tenant.duplicate.schema.name", "A tenant with the schema name '" + schemaName
                    + "' already exists", "schema_name", schemaName);
        }

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.tenant.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }

}
