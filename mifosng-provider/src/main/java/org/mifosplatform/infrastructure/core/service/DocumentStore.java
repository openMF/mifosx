package org.mifosplatform.infrastructure.core.service;

import org.mifosplatform.infrastructure.core.domain.Base64EncodedImage;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentStore {
    public String saveDocument(InputStream uploadedInputStream, DocumentCommand documentCommand) throws IOException;
    public String saveImage(Base64EncodedImage base64EncodedImage, Long resourceId, String imageName) throws IOException;
    public String saveImage(InputStream uploadedInputStream, Long resourceId, String imageName, Long fileSize) throws IOException;
    public void deleteClientImage(final Long resourceId, final String location);
}
