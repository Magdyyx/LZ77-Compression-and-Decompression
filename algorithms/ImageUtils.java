package algorithms;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    public static int[][] readImage(File inputFile) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);
        int width = image.getWidth();
        int height = image.getHeight();

        int[][] pixels = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = image.getRGB(i, j) & 0xFF; // Extracting the grayscale value
            }
        }

        return pixels;
    }

    public static void writeImage(int[][] pixels, File outputFile) throws IOException {
        int width = pixels.length;
        int height = pixels[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixelValue = pixels[i][j] << 16 | pixels[i][j] << 8 | pixels[i][j];
                image.setRGB(i, j, pixelValue);
            }
        }

        ImageIO.write(image, "jpg", outputFile);
    }
}
