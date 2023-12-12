package algorithms;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VectorQuantizationRGB {

    public static void main(String[] args) {
        try {
            // Step 1: Read the input image
            BufferedImage originalImage = ImageIO.read(new File("test.jpg"));

            // Step 2: Compress the image using k-means clustering
            int k = 8; // Number of clusters (adjust as needed)
            Pair<BufferedImage, List<Color>> compressionResult = compressImage(originalImage, k);
            BufferedImage compressedImage = compressionResult.getKey();
            List<Color> centroids = compressionResult.getValue();

            // Step 3: Decompress the image
            BufferedImage decompressedImage = decompressImage(compressedImage, centroids);

            // Step 4: Save the results to files
            saveImage(originalImage, "original.jpg");
            saveImage(compressedImage, "compressed.jpg");
            saveImage(decompressedImage, "decompressed.jpg");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Pair<BufferedImage, List<Color>> compressImage(BufferedImage image, int k) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Step 1: Extract RGB vectors from the image
        List<Color> rgbVectors = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rgbVectors.add(new Color(image.getRGB(i, j)));
            }
        }

        // Step 2: Apply k-means clustering
        List<Color> centroids = kMeansClustering(rgbVectors, k);

        // Step 3: Assign each pixel to the nearest cluster centroid
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color pixelColor = new Color(image.getRGB(i, j));
                Color nearestCentroid = findNearestCentroid(pixelColor, centroids);
                compressedImage.setRGB(i, j, nearestCentroid.getRGB());
            }
        }

        return new Pair<>(compressedImage, centroids);
    }

    private static List<Color> kMeansClustering(List<Color> data, int k) {
        List<Color> centroids = new ArrayList<>();
        Random random = new Random();

        // Initialize centroids randomly
        for (int i = 0; i < k; i++) {
            int randomIndex = random.nextInt(data.size());
            centroids.add(data.get(randomIndex));
        }

        int maxIterations = 100;
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            // Assign each data point to the nearest centroid
            List<List<Color>> clusters = new ArrayList<>(k);
            for (int i = 0; i < k; i++) {
                clusters.add(new ArrayList<>());
            }

            for (Color dataPoint : data) {
                Color nearestCentroid = findNearestCentroid(dataPoint, centroids);
                int clusterIndex = centroids.indexOf(nearestCentroid);
                clusters.get(clusterIndex).add(dataPoint);
            }

            // Update centroids based on the mean of each cluster
            for (int i = 0; i < k; i++) {
                if (!clusters.get(i).isEmpty()) {
                    double sumRed = 0;
                    double sumGreen = 0;
                    double sumBlue = 0;

                    for (Color dataPoint : clusters.get(i)) {
                        sumRed += dataPoint.getRed();
                        sumGreen += dataPoint.getGreen();
                        sumBlue += dataPoint.getBlue();
                    }

                    int meanRed = (int) (sumRed / clusters.get(i).size());
                    int meanGreen = (int) (sumGreen / clusters.get(i).size());
                    int meanBlue = (int) (sumBlue / clusters.get(i).size());

                    centroids.set(i, new Color(meanRed, meanGreen, meanBlue));
                }
            }
        }

        return centroids;
    }

    private static Color findNearestCentroid(Color point, List<Color> centroids) {
        double minDistance = Double.MAX_VALUE;
        Color nearestCentroid = null;

        for (Color centroid : centroids) {
            double distance = Math.pow(point.getRed() - centroid.getRed(), 2) +
                    Math.pow(point.getGreen() - centroid.getGreen(), 2) +
                    Math.pow(point.getBlue() - centroid.getBlue(), 2);

            if (distance < minDistance) {
                minDistance = distance;
                nearestCentroid = centroid;
            }
        }

        return nearestCentroid;
    }

    private static BufferedImage decompressImage(BufferedImage compressedImage, List<Color> centroids) {
        int width = compressedImage.getWidth();
        int height = compressedImage.getHeight();
        BufferedImage decompressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Use the mean color of the pixels assigned to each cluster for decompression
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color pixelColor = new Color(compressedImage.getRGB(i, j));
                Color nearestCentroid = findNearestCentroid(pixelColor, centroids);
                decompressedImage.setRGB(i, j, nearestCentroid.getRGB());
            }
        }

        return decompressedImage;
    }


    private static void saveImage(BufferedImage image, String fileName) {
        try {
            File outputImageFile = new File(fileName);
            ImageIO.write(image, "jpg", outputImageFile);
            System.out.println("Saved: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
