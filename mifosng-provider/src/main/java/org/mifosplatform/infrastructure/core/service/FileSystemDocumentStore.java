/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.core.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mifosplatform.infrastructure.core.api.ApiConstants;
import org.mifosplatform.infrastructure.core.domain.Base64EncodedImage;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.infrastructure.documentmanagement.exception.DocumentManagementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.codec.Base64;

public class FileSystemDocumentStore implements DocumentStore {

    private final static Logger logger = LoggerFactory.getLogger(FileSystemDocumentStore.class);

    public static final String MIFOSX_BASE_DIR = System.getProperty("user.home") + File.separator + ".mifosx";
    private Integer maxFileSize = ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB;
    private Integer maxImageSize = ApiConstants.MAX_IMAGE_UPLOAD_SIZE_IN_MB;

    /**
     * Generate the directory path for storing the new document
     *
     * @param entityType
     * @param entityId
     * @return
     */
    private String generateFileParentDirectory(String entityType, Long entityId) {
        return FileSystemDocumentStore.MIFOSX_BASE_DIR + File.separator + ThreadLocalContextUtil.getTenant().getName().replaceAll(" ", "").trim()
                + File.separator + "documents" + File.separator + entityType + File.separator + entityId + File.separator
                + RandomStringGenerator.generateRandomString();
    }

    /**
     * Generate directory path for storing new Image
     */
    private String generateClientImageParentDirectory(final Long resourceId) {
        return FileSystemDocumentStore.MIFOSX_BASE_DIR + File.separator + ThreadLocalContextUtil.getTenant().getName().replaceAll(" ", "").trim()
                + File.separator + "images" + File.separator + "clients" + File.separator + resourceId;
    }

    /**
     * @param uploadedInputStream
     * @param documentCommand
     * @return
     * @throws IOException
     */
    public String saveDocument(InputStream uploadedInputStream, DocumentCommand documentCommand) throws IOException {
        String documentName = documentCommand.getFileName();
        String uploadDocumentLocation = generateFileParentDirectory(documentCommand.getParentEntityType(), documentCommand.getParentEntityId());

        validateFileSizeWithinPermissibleRange(documentCommand.getSize(), documentName, maxFileSize);
        makeDirectories(uploadDocumentLocation);

        String fileLocation = uploadDocumentLocation + File.separator + documentName;

        writeFileToFileSystem(uploadedInputStream, fileLocation);
        return fileLocation;
    }

    /**
     * Recursively create the directory if it does not exist *
     */
    private void makeDirectories(String uploadDocumentLocation) {
        if (!new File(uploadDocumentLocation).isDirectory()) {
            new File(uploadDocumentLocation).mkdirs();
        }
    }


    @Override
    public String saveImage(InputStream uploadedInputStream, Long resourceId, String imageName, Long fileSize) throws IOException {
        String uploadImageLocation = generateClientImageParentDirectory(resourceId);

        validateFileSizeWithinPermissibleRange(fileSize, imageName, maxImageSize);
        makeDirectories(uploadImageLocation);

        String fileLocation = uploadImageLocation + File.separator + imageName;

        writeFileToFileSystem(uploadedInputStream, fileLocation);
        return fileLocation;
    }

    private void writeFileToFileSystem(InputStream uploadedInputStream, String fileLocation) throws IOException {
        OutputStream out = new FileOutputStream(new File(fileLocation));
        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = uploadedInputStream.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        out.flush();
        out.close();
    }

    /**
     *
     *
     *
     * @param base64EncodedImage
     * @param resourceId
     * @param imageName
     * @throws IOException
     */
    public String saveImage(Base64EncodedImage base64EncodedImage, Long resourceId, String imageName)
            throws IOException {
        String uploadImageLocation = generateClientImageParentDirectory(resourceId);

        makeDirectories(uploadImageLocation);

        String fileLocation = uploadImageLocation + File.separator + imageName + base64EncodedImage.getFileExtension();
        OutputStream out = new FileOutputStream(new File(fileLocation));
        byte[] imgBytes = Base64.decode(base64EncodedImage.getBase64EncodedString());
        out.write(imgBytes);
        out.flush();
        out.close();
        return fileLocation;
    }

    /**
     * @param fileSize
     * @param name
     */
    public void validateFileSizeWithinPermissibleRange(Long fileSize, String name, int maxFileSize) {
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

    public void deleteClientImage(final Long resourceId, final String location) {
        File fileToBeDeleted = new File(location);
        boolean fileDeleted = fileToBeDeleted.delete();
        if (!fileDeleted) {
            // no need to throw an Error, simply log a warning
            logger.warn("Unable to delete image associated with clients with Id " + resourceId);
        }
    }
}