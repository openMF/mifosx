package org.mifosng.platform.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mifosng.platform.api.data.ApiParameterError;
import org.mifosng.platform.exceptions.DocumentManagementException;
import org.mifosng.platform.exceptions.ImageUploadException;
import org.mifosng.platform.exceptions.PlatformApiDataValidationException;
import org.mifosng.platform.infrastructure.ThreadLocalContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

	private final static Logger logger = LoggerFactory
			.getLogger(FileUtils.class);

	public static final String MIFOSX_BASE_DIR = System
			.getProperty("user.home") + File.separator + ".mifosx";

	public static Random random = new Random();

	/**
	 * Generate a random String
	 * 
	 * @return
	 */
	public static String generateRandomString() {
		String characters = "abcdefghijklmnopqrstuvwxyz123456789";
		int length = generateRandomNumber();
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(random.nextInt(characters.length()));
		}
		return new String(text);
	}

	/**
	 * Generate a random number between 5 to 16
	 * 
	 * @return
	 */
	public static int generateRandomNumber() {
		Random randomGenerator = new Random();
		return randomGenerator.nextInt(11) + 5;
	}

	/**
	 * Generate the directory path for storing the new document
	 * 
	 * @param entityType
	 * @param entityId
	 * @return
	 */
	public static String generateFileParentDirectory(String entityType,
			Long entityId) {
		return FileUtils.MIFOSX_BASE_DIR
				+ File.separator
				+ ThreadLocalContextUtil.getTenant().getName()
						.replaceAll(" ", "").trim() + File.separator
				+ "documents" + File.separator + entityType + File.separator
				+ entityId + File.separator + FileUtils.generateRandomString();
	}

	/**
	 * Generate directory path for storing new Image
	 * 
	 * @param entityType
	 * @param entityId
	 * @return
	 */
	public static String generateImageParentDirectory(
			ApplicationConstants.IMAGE_MANAGEMENT_ENTITY entityType,
			Long entityId) {
		return FileUtils.MIFOSX_BASE_DIR
				+ File.separator
				+ ThreadLocalContextUtil.getTenant().getName()
						.replaceAll(" ", "").trim() + File.separator + "images"
				+ File.separator + entityType.toString() + File.separator
				+ entityId;
	}

	/**
	 * @param uploadedInputStream
	 * @param uploadedFileLocation
	 * @return
	 * @throws IOException
	 */
	public static String saveToFileSystem(InputStream uploadedInputStream,
			String uploadedFileLocation, String fileName) throws IOException {
		String fileLocation = uploadedFileLocation + File.separator + fileName;
		OutputStream out = new FileOutputStream(new File(fileLocation));
		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = uploadedInputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
		return fileLocation;
	}

	/**
	 * @param fileSize
	 * @param name
	 */
	public static void validateFileSizeWithinPermissibleRange(Long fileSize,
			String name, int maxFileSize) {
		/**
		 * Using Content-Length gives me size of the entire request, which is
		 * good enough for now for a fast fail as the length of the rest of the
		 * content i.e name and description while compared to the uploaded file
		 * size is negligible
		 **/
		if (fileSize != null && ((fileSize / (1024 * 1024)) > maxFileSize)) {
			throw new DocumentManagementException(name, fileSize, maxFileSize);
		}
	}

	/**
	 * Validates that passed in Mime type maps to known image mime types
	 * 
	 * @param mimeType
	 */
	public static void validateImageMimeType(String mimeType) {
		if (!(mimeType.equalsIgnoreCase("image/gif")
				|| mimeType.equalsIgnoreCase("image/jpeg") || mimeType
					.equalsIgnoreCase("image/png"))) {
			throw new ImageUploadException();
		}
	}
	
	
	public static void validateClientImageNotEmpty(String imageFileName) {
		List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		if (imageFileName == null) {
			StringBuilder validationErrorCode = new StringBuilder(
					"validation.msg.clientImage.cannot.be.blank");
			StringBuilder defaultEnglishMessage = new StringBuilder(
					"The parameter image cannot be blank.");
			ApiParameterError error = ApiParameterError.parameterError(
					validationErrorCode.toString(),
					defaultEnglishMessage.toString(), "image");
			dataValidationErrors.add(error);
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}

	/**
	 * @param entityType
	 * @param entityId
	 * @param location
	 */
	public static void deleteImage(
			ApplicationConstants.IMAGE_MANAGEMENT_ENTITY entityType,
			Long entityId, String location) {
		File fileToBeDeleted = new File(location);
		boolean fileDeleted = fileToBeDeleted.delete();
		if (!fileDeleted) {
			// no need to throw an Error, simply log a warning
			logger.warn("Unable to delete image associated with "
					+ entityType.toString() + " with Id " + entityId);
		}
	}
}
