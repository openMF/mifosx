package org.mifosplatform.infrastructure.core.service;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.mifosplatform.infrastructure.core.domain.Base64EncodedImage;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.infrastructure.documentmanagement.exception.DocumentManagementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class S3DocumentStore extends DocumentStore {

    private final static Logger logger = LoggerFactory.getLogger(S3DocumentStore.class);

    private final String s3BucketName;
    private AmazonS3 s3Client;

    public S3DocumentStore(String s3BucketName, AmazonS3 s3Client) {
        this.s3BucketName = s3BucketName;
        this.s3Client = s3Client;
    }

    @Override
    public String saveDocument(InputStream toUpload, DocumentCommand documentCommand) throws DocumentManagementException {
        String documentName = documentCommand.getFileName();
        validateFileSizeWithinPermissibleRange(documentCommand.getSize(), documentName, maxFileSize);

        String uploadDocFolder = generateFileParentDirectory(documentCommand.getParentEntityType(), documentCommand.getParentEntityId());
        String uploadDocFullPath = uploadDocFolder + File.separator + documentName;

        uploadDocument(toUpload, uploadDocFullPath);
        return uploadDocFullPath;
    }

    @Override
    public String saveImage(Base64EncodedImage base64EncodedImage, Long resourceId, String imageName) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String saveImage(InputStream uploadedInputStream, Long resourceId, String imageName, Long fileSize) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteImage(Long resourceId, String location) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    String generateFileParentDirectory(String entityType, Long entityId) {
        return "documents" + File.separator + entityType + File.separator + entityId + File.separator + RandomStringGenerator.generateRandomString();
    }

    String generateClientImageParentDirectory(Long resourceId) {
        return "images" + File.separator + "clients" + File.separator + resourceId;
    }



    private void uploadDocument(InputStream inputStream, String s3UploadLocation) throws DocumentManagementException{
        try {
            logger.info("Uploading a new object to S3 from a file to "+ s3UploadLocation);
            s3Client.putObject(new PutObjectRequest(this.s3BucketName, s3UploadLocation, inputStream, new ObjectMetadata()));

        } catch (AmazonServiceException ase) {
            String message = "Caught an AmazonServiceException, which " + "means your request made it " + "to Amazon S3, but was rejected with an error response" +
                    " for some reason. \n Error Message:    " + ase.getMessage() +
                    "HTTP Status Code: " + ase.getStatusCode() +
                    "AWS Error Code:   " + ase.getErrorCode() +
                    "Error Type:       " + ase.getErrorType() +
                    "Request ID:       " + ase.getRequestId();

            logger.error(message);
            throw new DocumentManagementException(message);
        } catch (AmazonClientException ace) {
            String message = "Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network." +
                    "Error Message: " + ace.getMessage();
            logger.error(message);
            throw new DocumentManagementException(message);
        }
    }

}
