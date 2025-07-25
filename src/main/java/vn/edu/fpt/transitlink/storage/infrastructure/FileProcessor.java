package vn.edu.fpt.transitlink.storage.infrastructure;

import org.springframework.stereotype.Component;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Component
public class FileProcessor {

    private static final int MAX_WIDTH = 1920;
    private static final int MAX_HEIGHT = 1080;

    public InputStream processImage(InputStream inputStream, String contentType) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) return inputStream;

            // Resize if too large
            if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
                image = resizeImage(image, MAX_WIDTH, MAX_HEIGHT);
            }

            // Convert back to InputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String format = contentType.contains("png") ? "png" : "jpg";
            ImageIO.write(image, format, baos);

            return new ByteArrayInputStream(baos.toByteArray());

        } catch (Exception e) {
            // Return original on error
            try {
                inputStream.reset();
                return inputStream;
            } catch (Exception resetEx) {
                throw new RuntimeException("Image processing failed", e);
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        // Calculate new dimensions
        double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resized;
    }
}
