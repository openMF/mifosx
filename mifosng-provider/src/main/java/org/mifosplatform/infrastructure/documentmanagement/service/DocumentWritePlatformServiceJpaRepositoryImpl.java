/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.documentmanagement.service;

import java.io.InputStream;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommandValidator;
import org.mifosplatform.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.mifosplatform.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.mifosplatform.infrastructure.documentmanagement.domain.Document;
import org.mifosplatform.infrastructure.documentmanagement.domain.DocumentRepository;
import org.mifosplatform.infrastructure.documentmanagement.domain.StorageType;
import org.mifosplatform.infrastructure.documentmanagement.exception.ContentManagementException;
import org.mifosplatform.infrastructure.documentmanagement.exception.DocumentNotFoundException;
import org.mifosplatform.infrastructure.documentmanagement.exception.InvalidEntityTypeForDocumentManagementException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentWritePlatformServiceJpaRepositoryImpl implements DocumentWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(DocumentWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final DocumentRepository documentRepository;
    private final ContentRepositoryFactory contentRepositoryFactory;

    @Autowired
    public DocumentWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final DocumentRepository documentRepository, final ContentRepositoryFactory documentStoreFactory) {
        this.context = context;
        this.documentRepository = documentRepository;
        this.contentRepositoryFactory = documentStoreFactory;
    }

    @Transactional
    @Override
    public Long createDocument(final DocumentCommand documentCommand, final InputStream inputStream) {
        try {
            this.context.authenticatedUser();

            final DocumentCommandValidator validator = new DocumentCommandValidator(documentCommand);

            validateParentEntityType(documentCommand);

            validator.validateForCreate();

            ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();

            final String fileLocation = contentRepository.saveFile(inputStream, documentCommand);

            final Document document = Document.createNew(documentCommand.getParentEntityType(), documentCommand.getParentEntityId(),
                    documentCommand.getName(), documentCommand.getFileName(), documentCommand.getSize(), documentCommand.getType(),
                    documentCommand.getDescription(), fileLocation, contentRepository.getStorageType());

            this.documentRepository.save(document);

            return document.getId();
        } catch (final DataIntegrityViolationException dve) {
            logger.error(dve.getMessage(), dve);
            throw new PlatformDataIntegrityException("error.msg.document.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateDocument(final DocumentCommand documentCommand, final InputStream inputStream) {
        try {
            this.context.authenticatedUser();

            String oldLocation = null;
            final DocumentCommandValidator validator = new DocumentCommandValidator(documentCommand);
            validator.validateForUpdate();
            // TODO check if entity id is valid and within data scope for the
            // user
            final Document documentForUpdate = this.documentRepository.findOne(documentCommand.getId());
            if (documentForUpdate == null) { throw new DocumentNotFoundException(documentCommand.getParentEntityType(),
                    documentCommand.getParentEntityId(), documentCommand.getId()); }

            StorageType documentStoreType = documentForUpdate.storageType();
            oldLocation = documentForUpdate.getLocation();
            if (inputStream != null && documentCommand.isFileNameChanged()) {
                ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
                documentCommand.setLocation(contentRepository.saveFile(inputStream, documentCommand));
                documentCommand.setStorageType(contentRepository.getStorageType().getValue());
            }

            documentForUpdate.update(documentCommand);

            if (inputStream != null && documentCommand.isFileNameChanged()) {
                ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(documentStoreType);
                contentRepository.deleteFile(documentCommand.getName(), oldLocation);
            }

            this.documentRepository.saveAndFlush(documentForUpdate);

            return new CommandProcessingResult(documentForUpdate.getId());
        } catch (final DataIntegrityViolationException dve) {
            logger.error(dve.getMessage(), dve);
            throw new PlatformDataIntegrityException("error.msg.document.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource.");
        } catch (final ContentManagementException cme) {
            logger.error(cme.getMessage(), cme);
            throw new ContentManagementException(documentCommand.getName(), cme.getMessage());
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteDocument(final DocumentCommand documentCommand) {
        this.context.authenticatedUser();

        validateParentEntityType(documentCommand);
        // TODO: Check document is present under this entity Id
        final Document document = this.documentRepository.findOne(documentCommand.getId());
        if (document == null) { throw new DocumentNotFoundException(documentCommand.getParentEntityType(),
                documentCommand.getParentEntityId(), documentCommand.getId()); }
        this.documentRepository.delete(document);

        ContentRepository contentRepository = this.contentRepositoryFactory.getRepository(document.storageType());
        contentRepository.deleteFile(document.getName(), document.getLocation());
        return new CommandProcessingResult(document.getId());
    }

    private void validateParentEntityType(final DocumentCommand documentCommand) {
        if (!checkValidEntityType(documentCommand.getParentEntityType())) { throw new InvalidEntityTypeForDocumentManagementException(
                documentCommand.getParentEntityType()); }
    }

    private static boolean checkValidEntityType(final String entityType) {
        for (final DOCUMENT_MANAGEMENT_ENTITY entities : DOCUMENT_MANAGEMENT_ENTITY.values()) {
            if (entities.name().equalsIgnoreCase(entityType)) { return true; }
        }
        return false;
    }

    /*** Entities for document Management **/
    public static enum DOCUMENT_MANAGEMENT_ENTITY {
        CLIENTS, CLIENT_IDENTIFIERS, STAFF, LOANS, SAVINGS, GROUPS;

        @Override
        public String toString() {
            return name().toString().toLowerCase();
        }
    }
}