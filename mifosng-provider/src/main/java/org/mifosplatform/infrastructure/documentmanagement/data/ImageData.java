package org.mifosplatform.infrastructure.documentmanagement.data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.mifosplatform.infrastructure.documentmanagement.contentrepository.ContentRepositoryUtils;
import org.mifosplatform.infrastructure.documentmanagement.domain.StorageType;

public class ImageData {

    @SuppressWarnings("unused")
    private final Long imageId;
    private final String location;
    private final Integer storageType;
    private final String entityDisplayName;

    private File file;
    private String contentType;
    private InputStream inputStream;

    public ImageData(final Long imageId, final String location, final Integer storageType, final String entityDisplayName) {
        this.imageId = imageId;
        this.location = location;
        this.storageType = storageType;
        this.entityDisplayName = entityDisplayName;
    }

    public byte[] getContent() {
        // TODO Vishwas Fix error handling
        try {
            if (this.inputStream == null) {
                final FileInputStream fileInputStream = new FileInputStream(this.file);
                return IOUtils.toByteArray(fileInputStream);
            }

            return IOUtils.toByteArray(this.inputStream);
        } catch (final IOException e) {
            return null;
        }
    }
    
    public byte[] getContentOfSize(Integer maxWidth, Integer maxHeight) {
    	if (maxWidth == null && maxHeight == null) {
    		return getContent();
    	}
		try {
			BufferedImage originalImage = ImageIO.read(this.file);
			int originalWidth = originalImage.getWidth();
			int originalHeight = originalImage.getHeight();
			float originalAspect = ((float) originalWidth)/((float) originalHeight);
			int width;
			int height;
			if (maxWidth != null && maxHeight * originalAspect > maxWidth) {
				// Width bounded
				width = maxWidth;
				height = (int) (width / originalAspect);
			} else {
				// Height bounded
				height = maxHeight;
				width = (int) (height * originalAspect);
			}
  	    	Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
  	        BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  	        Graphics g = imageBuff.createGraphics();
  	        g.drawImage(scaledImage, 0, 0, new Color(0,0,0), null);
  	        g.dispose();
  	        ByteArrayOutputStream out = new ByteArrayOutputStream();
  	        ImageIO.write(imageBuff, "JPEG", out);
  	        return out.toByteArray();
		} catch (IOException e) {
			return null;
		}
    }

    private String setImageContentType() {
        String contentType = ContentRepositoryUtils.IMAGE_MIME_TYPE.JPEG.getValue();

        if (this.file != null) {
            final String fileName = this.file.getName();

            if (StringUtils.endsWith(fileName, ContentRepositoryUtils.IMAGE_FILE_EXTENSION.GIF.getValue())) {
                contentType = ContentRepositoryUtils.IMAGE_MIME_TYPE.GIF.getValue();
            } else if (StringUtils.endsWith(fileName, ContentRepositoryUtils.IMAGE_FILE_EXTENSION.PNG.getValue())) {
                contentType = ContentRepositoryUtils.IMAGE_MIME_TYPE.PNG.getValue();
            }
        }
        return contentType;
    }

    public String contentType() {
        return this.contentType;
    }

    public StorageType storageType() {
        return StorageType.fromInt(this.storageType);
    }

    public String name() {
        return this.file.getName();
    }

    public String location() {
        return this.location;
    }

    public void updateContent(final InputStream objectContent) {
        this.inputStream = objectContent;
    }

    public void updateContent(final File file) {
        this.file = file;
        setImageContentType();
    }

    public String getEntityDisplayName() {
        return this.entityDisplayName;
    }

}
