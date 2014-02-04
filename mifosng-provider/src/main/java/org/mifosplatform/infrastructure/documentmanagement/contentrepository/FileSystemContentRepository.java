/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.documentmanagement.contentrepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mifosplatform.infrastructure.core.domain.Base64EncodedImage;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.infrastructure.documentmanagement.data.DocumentData;
import org.mifosplatform.infrastructure.documentmanagement.data.FileData;
import org.mifosplatform.infrastructure.documentmanagement.data.ImageData;
import org.mifosplatform.infrastructure.documentmanagement.domain.StorageType;
import org.mifosplatform.infrastructure.documentmanagement.exception.ContentManagementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.codec.Base64;

public class FileSystemContentRepository implements ContentRepository {

    private final static Logger logger = LoggerFactory.getLogger(FileSystemContentRepository.class);

    public static final String MIFOSX_BASE_DIR = System.getProperty("user.home") + File.separator + ".mifosx";

    @Override
    public String saveFile(final InputStream uploadedInputStream, final DocumentCommand documentCommand) {
        final String fileName = documentCommand.getFileName();
        final String uploadDocumentLocation = generateFileParentDirectory(documentCommand.getParentEntityType(),
                documentCommand.getParentEntityId());

        ContentRepositoryUtils.validateFileSizeWithinPermissibleRange(documentCommand.getSize(), fileName);
        makeDirectories(uploadDocumentLocation);

        final String fileLocation = uploadDocumentLocation + File.separator + fileName;

        writeFileToFileSystem(fileName, uploadedInputStream, fileLocation);
        return fileLocation;
    }

    @Override
    public String saveImage(final InputStream uploadedInputStream, final Long resourceId, final String imageName, final Long fileSize) {
        final String uploadImageLocation = generateClientImageParentDirectory(resourceId);

        ContentRepositoryUtils.validateFileSizeWithinPermissibleRange(fileSize, imageName);
        makeDirectories(uploadImageLocation);

        final String fileLocation = uploadImageLocation + File.separator + imageName;

        writeFileToFileSystem(imageName, uploadedInputStream, fileLocation);
        return fileLocation;
    }

    @Override
    public String saveImage(final Base64EncodedImage base64EncodedImage, final Long resourceId, final String imageName) {
        final String uploadImageLocation = generateClientImageParentDirectory(resourceId);

        makeDirectories(uploadImageLocation);

        final String fileLocation = uploadImageLocation + File.separator + imageName + base64EncodedImage.getFileExtension();
        try {
            final OutputStream out = new FileOutputStream(new File(fileLocation));
            final byte[] imgBytes = Base64.decode(base64EncodedImage.getBase64EncodedString());
            out.write(imgBytes);
            out.flush();
            out.close();
        } catch (final IOException ioe) {
            throw new ContentManagementException(imageName, ioe.getMessage());
        }
        return fileLocation;
    }

    @Override
    public void deleteImage(final Long resourceId, final String location) {
        final boolean fileDeleted = deleteFile(location);
        if (!fileDeleted) {
            // no need to throw an Error, simply log a warning
            logger.warn("Unable to delete image associated with clients with Id " + resourceId);
        }
    }

    @Override
    public void deleteFile(final String fileName, final String documentPath) {
        final boolean fileDeleted = deleteFile(documentPath);
        if (!fileDeleted) { throw new ContentManagementException(fileName, null); }
    }

    private boolean deleteFile(final String documentPath) {
        final File fileToBeDeleted = new File(documentPath);
        return fileToBeDeleted.delete();
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.FILE_SYSTEM;
    }

    @Override
    public FileData fetchFile(final DocumentData documentData) {
        final File file = new File(documentData.fileLocation());
        return new FileData(file, documentData.fileName(), documentData.contentType());
    }

    @Override
    public ImageData fetchImage(final ImageData imageData) {
        final File file = new File(imageData.location());
        imageData.updateContent(file);
        return imageData;
    }

    /**
     * Generate the directory path for storing the new document
     * 
     * @param entityType
     * @param entityId
     * @return
     */
    private String generateFileParentDirectory(final String entityType, final Long entityId) {
        return FileSystemContentRepository.MIFOSX_BASE_DIR + File.separator
                + ThreadLocalContextUtil.getTenant().getName().replaceAll(" ", "").trim() + File.separator + "documents" + File.separator
                + entityType + File.separator + entityId + File.separator + ContentRepositoryUtils.generateRandomString();
    }

    /**
     * Generate directory path for storing new Image
     */
    private String generateClientImageParentDirectory(final Long resourceId) {
        return FileSystemContentRepository.MIFOSX_BASE_DIR + File.separator
                + ThreadLocalContextUtil.getTenant().getName().replaceAll(" ", "").trim() + File.separator + "images" + File.separator
                + "clients" + File.separator + resourceId;
    }

    /**
     * Recursively create the directory if it does not exist *
     */
    private void makeDirectories(final String uploadDocumentLocation) {
        if (!new File(uploadDocumentLocation).isDirectory()) {
            new File(uploadDocumentLocation).mkdirs();
        }
    }

    private void writeFileToFileSystem(final String fileName, final InputStream uploadedInputStream, final String fileLocation) {
        try {
            final OutputStream out = new FileOutputStream(new File(fileLocation));
            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (final IOException ioException) {
            throw new ContentManagementException(fileName, ioException.getMessage());
        }
    }
}