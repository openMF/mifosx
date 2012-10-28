package org.mifosng.platform.exceptions;


public class DocumentManagementException extends
		AbstractPlatformDomainRuleException {

	public DocumentManagementException(final String name) {
		super("error.msg.document.save", "Error while manipulating file "
				+ name + " due to a File system / Amazon S3 issue", name);
	}

	public DocumentManagementException(final String name, final Long fileSize,
			final int maxFileSize) {
		super("error.msg.document.file.too.big",
				"Unable to save the document with name" + name
						+ " since its file Size of " + fileSize / (1024 * 1024)
						+ " MB exceeds the max permissable file size  of "
						+ maxFileSize
						+ " MB", name, fileSize);
	}

}
