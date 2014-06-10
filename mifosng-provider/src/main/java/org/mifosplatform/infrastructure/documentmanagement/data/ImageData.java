package org.mifosplatform.infrastructure.documentmanagement.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(this.file);
        byte[] out = resizeImage(
            fis,
            maxWidth != null ? maxWidth : Integer.MAX_VALUE,
            maxHeight != null ? maxHeight : Integer.MAX_VALUE);
        return (out == null) ? getContent() : out;
      } catch (FileNotFoundException ex) {
        return getContent();
      } finally {
        if (fis != null) {
          try { fis.close(); } catch (IOException ex) {}
        }
      }
    }
    
    public byte[] resizeImage(InputStream in, int maxWidth, int maxHeight) {
      return resizeImage(in, maxWidth, maxHeight, "jpg");
    }

    public byte[] resizeImage(InputStream in, int maxWidth, int maxHeight, String imageType) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      resizeImage(in, out, maxWidth, maxHeight, imageType);
      byte[] result = out.toByteArray();
      if (result != null && result.length > 0) {
        return result;
      } else {
        return null;
      }
    }

    public void resizeImage(InputStream in, OutputStream out, int maxWidth, int maxHeight) {
      resizeImage(in, out, maxWidth, maxHeight, "jpg");
    }

    public void resizeImage(
        InputStream in, OutputStream out, int maxWidth, int maxHeight, String imageType) {

      try {
        BufferedImage src = ImageIO.read(in);
        if (src.getWidth() < maxWidth && src.getHeight() < maxHeight) {
          return;
        }
        float widthRatio = (float)src.getWidth() / maxWidth;
        float heightRatio = (float)src.getHeight() / maxHeight;
        if (widthRatio < 1.0f) {
          widthRatio = 1.0f;
        }
        if (heightRatio < 1.0f) {
          heightRatio = 1.0f;
        }
        float scaleRatio = widthRatio > heightRatio ? widthRatio : heightRatio;

        int newWidth = (int)(src.getWidth() / scaleRatio);
        int newHeight = (int)(src.getWidth() / scaleRatio);
        int colorModel = imageType.matches("jpe?g|JPE?G")
            ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage target = new BufferedImage(newWidth, newHeight, colorModel);
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, newWidth, newHeight, Color.BLACK, null);
        g.dispose();
        ImageIO.write(target, imageType, out);
      } catch (IOException ex) {
        // No image, sending null upstream
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
