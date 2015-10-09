/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.service;

import java.util.Map;

import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.codes.exception.CodeValueNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.command.ClientAddressCommand;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientAddress;
import org.mifosplatform.portfolio.client.domain.ClientAddressRepository;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.client.exception.ClientAddressNotFoundException;
import org.mifosplatform.portfolio.client.exception.DuplicateClientAddressException;
import org.mifosplatform.portfolio.client.serialization.ClientAddressCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientAddressWritePlatformServiceJpaRepositoryImpl implements ClientAddressWritePlatformService{
	
	private final static Logger logger = LoggerFactory.getLogger(ClientAddressWritePlatformServiceJpaRepositoryImpl.class);
	
	private final PlatformSecurityContext context;
    private final ClientRepositoryWrapper clientRepository;
    private final ClientAddressRepository clientAddressRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final ClientAddressCommandFromApiJsonDeserializer clientAddressCommandFromApiJsonDeserializer;
    
    @Autowired
    public ClientAddressWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final ClientRepositoryWrapper clientRepository, final ClientAddressRepository clientAddressRepository,
            final CodeValueRepositoryWrapper codeValueRepository,
            final ClientAddressCommandFromApiJsonDeserializer clientAddressCommandFromApiJsonDeserializer) {
        this.context = context;
        this.clientRepository = clientRepository;
        this.clientAddressRepository = clientAddressRepository;
        this.codeValueRepository = codeValueRepository;
        this.clientAddressCommandFromApiJsonDeserializer = clientAddressCommandFromApiJsonDeserializer;
    }
    
    @Transactional
    @Override
    public CommandProcessingResult addClientAddress(final Long clientId, final JsonCommand command){
    	this.context.authenticatedUser();
    	final ClientAddressCommand clientAddressCommand = this.clientAddressCommandFromApiJsonDeserializer
                .commandFromApiJson(command.json());
        clientAddressCommand.validateForCreate();
        
        final String address_line = clientAddressCommand.getAddressLine();
        String addressTypeLabel = null;
        Long addressTypeId = null;
        String stateTypeLabel = null;
        Long stateTypeId = null;
        
        try {
        	
        	final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
        	final CodeValue addressType = this.codeValueRepository.findOneWithNotFoundDetection(clientAddressCommand.getAddressTypeId());
        	addressTypeId = addressType.getId();
        	addressTypeLabel = addressType.label();
        	final CodeValue stateType = this.codeValueRepository.findOneWithNotFoundDetection(clientAddressCommand.getStateTypeId());
        	stateTypeId = stateType.getId();
        	stateTypeLabel = stateType.label();
        
        	final ClientAddress clientAddress = ClientAddress.fromJson(client, addressType, stateType, command);
        	
        	this.clientAddressRepository.save(clientAddress);
        	
        	return new CommandProcessingResultBuilder() //
        	 		.withCommandId(command.commandId())//
        	 		.withOfficeId(client.officeId())//
        	 		.withClientId(clientId)//
        	 		.withEntityId(clientAddress.getId())//
        	 		.build();
        } 
        catch (final DataIntegrityViolationException dve){
        	handleClientAddressDataIntegrityViolation(addressTypeLabel,addressTypeId,address_line, dve);
        	return CommandProcessingResult.empty();
        }
       
        	
    }
    
    @Transactional
    @Override
    public CommandProcessingResult updateClientAddress(final Long clientId, final Long addressId, final JsonCommand command ){
    	
    	this.context.authenticatedUser();
        final ClientAddressCommand clientAddressCommand = this.clientAddressCommandFromApiJsonDeserializer
                .commandFromApiJson(command.json());
        clientAddressCommand.validateForUpdate();
        
        String addressTypeLabel = null;
        Long addressTypeId = clientAddressCommand.getAddressTypeId();
        String stateTypeLabel = null;
        Long stateTypeId = clientAddressCommand.getStateTypeId();
        String address_line = null;
        try {
        	
        	CodeValue addressType = null;
        	final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
            final ClientAddress clientAddressForUpdate = this.clientAddressRepository.findOne(addressId);
            if (clientAddressForUpdate == null) {
            	throw new ClientAddressNotFoundException(addressId); 
            	}
            
            final Map<String, Object> changes = clientAddressForUpdate.update(command);
            
            if (changes.containsKey("addressTypeId")) {
                addressType = this.codeValueRepository.findOneWithNotFoundDetection(addressTypeId);
                if (addressType == null) { throw new CodeValueNotFoundException(addressTypeId); }

                addressTypeId = addressType.getId();
                addressTypeLabel = addressType.label();
                clientAddressForUpdate.update(addressType, addressType);
            }
            if (changes.containsKey("addressTypeId") && changes.containsKey("address_line") && changes.containsKey("stateTypeId")) {
                addressTypeId = clientAddressCommand.getAddressTypeId();
                address_line = clientAddressCommand.getAddressLine();
                stateTypeId = clientAddressCommand.getStateTypeId();
            }else if (changes.containsKey("addressTypeId") && !changes.containsKey("address_line") && !changes.containsKey("stateTypeId")) {
            	addressTypeId = clientAddressCommand.getAddressTypeId();
                address_line = clientAddressCommand.getAddressLine();
                stateTypeId = clientAddressCommand.getStateTypeId();
            }else if (!changes.containsKey("addressTypeId") && changes.containsKey("address_line") && changes.containsKey("stateTypeId")) {
            	addressTypeId = clientAddressCommand.getAddressTypeId();
                address_line = clientAddressCommand.getAddressLine();
                stateTypeId = clientAddressCommand.getStateTypeId();
            }
            if (!changes.isEmpty()) {
                this.clientAddressRepository.saveAndFlush(clientAddressForUpdate);
            }
            
            return new CommandProcessingResultBuilder() //
            .withCommandId(command.commandId()) //
            .withOfficeId(client.officeId()) //
            .withClientId(clientId) //
            .withEntityId(addressId) //
            .with(changes) //
            .build();
        
        } catch (final DataIntegrityViolationException dve) {
            handleClientAddressDataIntegrityViolation(addressTypeLabel, addressTypeId,address_line, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }
        
        @Transactional
        @Override
        public CommandProcessingResult deleteClientAddress(final Long clientId, final Long addressId, final Long commandId){
        	
        	final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
        	
        	final ClientAddress clientAddress = this.clientAddressRepository.findOne(addressId);
            if (clientAddress == null) { throw new ClientAddressNotFoundException(addressId); }
            this.clientAddressRepository.delete(clientAddress);
            
            return new CommandProcessingResultBuilder() //
            .withCommandId(commandId) //
            .withOfficeId(client.officeId()) //
            .withClientId(clientId) //
            .withEntityId(addressId) //
            .build();

        }
        
        private void handleClientAddressDataIntegrityViolation(final String addressTypeLabel, final Long addressTypeId,final String address_line,
        	 final DataIntegrityViolationException dve){
        	
        	 if (dve.getMostSpecificCause().getMessage().contains("unique_address_type")) {
        		 
                 throw new DuplicateClientAddressException(addressTypeLabel);
             }
        	 else if (dve.getMostSpecificCause().getMessage().contains("unique_address_line")) { throw new DuplicateClientAddressException(
                    addressTypeId, addressTypeLabel,address_line); }
        	 logAsErrorUnexpectedDataIntegrityException(dve);	
        	 throw new PlatformDataIntegrityException("error.msg.clientaddress.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
        	 
        }
        
        private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
            logger.error(dve.getMessage(), dve);

}
               
    }
    
