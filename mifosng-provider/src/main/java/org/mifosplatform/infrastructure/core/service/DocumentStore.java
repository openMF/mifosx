package org.mifosplatform.infrastructure.core.service;

import org.mifosplatform.infrastructure.core.api.ApiConstants;
import org.mifosplatform.infrastructure.core.domain.Base64EncodedImage;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.infrastructure.documentmanagement.exception.DocumentManagementException;

import java.io.InputStream;

public abstract class DocumentStore {
    protected Integer maxFileSize = ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB;
    protected Integer maxImageSize = ApiConstants.MAX_IMAGE_UPLOAD_SIZE_IN_MB;

    public abstract String saveDocument(InputStream uploadedInputStream, DocumentCommand documentCommand) throws DocumentManagementException;
    public abstract String saveImage(InputStream uploadedInputStream, Long resourceId, String imageName, Long fileSize) throws DocumentManagementException;
    public abstract String saveImage(Base64EncodedImage base64EncodedImage, Long resourceId, String imageName) throws DocumentManagementException;
    public abstract void deleteImage(final Long resourceId, final String location);


    protected void validateFileSizeWithinPermissibleRange(Long fileSize, String name, int maxFileSize) {
        /**
         * Using Content-Length gives me size of the entire request, which is
         * good enough for now for a fast fail as the length of the rest of the
         * content i.e name and description while compared to the uploaded file
         * size is negligible
         **/
        if (fileSize != null && ((fileSize / (1024 * 1024)) > maxFileSize)) {
            throw new DocumentManagementException(name, fileSize,
                    maxFileSize);
        }
    }
}
