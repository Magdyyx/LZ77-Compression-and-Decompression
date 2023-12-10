package algorithms;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

public class VectorQuantizationRGB extends Algorithm {
    private static final int CODEBOOK_SIZE = 16;  // Adjust as needed
    private static final int MAX_ITERATIONS = 100;

    public List<int[]> compressImage(BufferedImage image) {
        List<int[]> codebook = generateCodebook(image);

        List<int[]> compressedImage = new ArrayList<>();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int[] pixel = getRGBValues(image.getRGB(x, y));
                int[] nearestCodebookEntry = findNearestCodebookEntry(pixel, codebook);
                compressedImage.add(nearestCodebookEntry);
            }
        }

        return compressedImage;
    }

    public BufferedImage decompressImage(List<int[]> compressedImage, List<int[]> codebook, int width, int height) {
        BufferedImage decompressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] codebookEntry = codebook.get(compressedImage.get(index)[0]);
                int rgb = createRGBValue(codebookEntry[0], codebookEntry[1], codebookEntry[2]);
                decompressedImage.setRGB(x, y, rgb);
                index++;
            }
        }

        return decompressedImage;
    }

    List<int[]> generateCodebook(BufferedImage image) {
        List<int[]> codebook = new ArrayList<>();

        // Flatten the image into a list of pixels
        List<int[]> pixels = new ArrayList<>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int[] pixel = getRGBValues(image.getRGB(x, y));
                pixels.add(pixel);
            }
        }

        // Initialize codebook with k-means clustering
        Set<int[]> selectedCentroids = new HashSet<>();
        Random random = new Random();

        while (selectedCentroids.size() < CODEBOOK_SIZE) {
            int[] randomPixel = pixels.get(random.nextInt(pixels.size()));
            selectedCentroids.add(randomPixel);
        }

        List<int[]> centroids = new ArrayList<>(selectedCentroids);

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            Map<int[], List<int[]>> clusters = new HashMap<>();

            for (int[] pixel : pixels) {
                int[] closestCentroid = centroids.get(0);
                int minDistance = calculateRGBDistance(pixel, closestCentroid);

                for (int i = 1; i < centroids.size(); i++) {
                    int[] currentCentroid = centroids.get(i);
                    int distance = calculateRGBDistance(pixel, currentCentroid);

                    if (distance < minDistance) {
                        minDistance = distance;
                        closestCentroid = currentCentroid;
                    }
                }

                clusters.computeIfAbsent(closestCentroid, k -> new ArrayList<>()).add(pixel);
            }

            // Update centroids
            for (Map.Entry<int[], List<int[]>> entry : clusters.entrySet()) {
                int[] newCentroid = calculateCentroid(entry.getValue());
                centroids.set(centroids.indexOf(entry.getKey()), newCentroid);
            }
        }

        codebook.addAll(centroids);

        return codebook;
    }

    private int[] calculateCentroid(List<int[]> pixels) {
        int sumRed = 0;
        int sumGreen = 0;
        int sumBlue = 0;

        for (int[] pixel : pixels) {
            sumRed += pixel[0];
            sumGreen += pixel[1];
            sumBlue += pixel[2];
        }

        int size = pixels.size();
        return new int[]{sumRed / size, sumGreen / size, sumBlue / size};
    }

    private int[] findNearestCodebookEntry(int[] pixel, List<int[]> codebook) {
        int minDistance = Integer.MAX_VALUE;
        int[] nearestCodebookEntry = new int[3];

        for (int i = 0; i < codebook.size(); i++) {
            int[] codebookEntry = codebook.get(i);
            int distance = calculateRGBDistance(pixel, codebookEntry);

            if (distance < minDistance) {
                minDistance = distance;
                nearestCodebookEntry = codebookEntry;
            }
        }

        return new int[]{codebook.indexOf(nearestCodebookEntry)};
    }

    private int calculateRGBDistance(int[] pixel1, int[] pixel2) {
        return Math.abs(pixel1[0] - pixel2[0]) + Math.abs(pixel1[1] - pixel2[1]) + Math.abs(pixel1[2] - pixel2[2]);
    }

    private int[] getRGBValues(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return new int[]{red, green, blue};
    }

    private int createRGBValue(int red, int green, int blue) {
        return (red << 16) | (green << 8) | blue;
    }


    @Override
    public void compress(File inputFile, File outputFile) throws IOException {
        // Read the input image file
        BufferedImage inputImage = ImageIO.read(inputFile);

        // Compress the image
        List<int[]> codebook = compressImage(inputImage);

        // Write the compressed codebook to the output file
        writeCodebookToFile(codebook, outputFile);
    }

    @Override
    public void decompress(File inputFile, File outputFile) throws IOException {
        // Read the compressed codebook from the input file
        List<int[]> codebook = readCodebookFromFile(inputFile);

        // Decompress the image using the codebook
        BufferedImage decompressedImage = decompressImage(codebook, inputFile);

        // Write the decompressed image to the output file
        ImageIO.write(decompressedImage, "jpg", outputFile);
    }

    private void writeCodebookToFile(List<int[]> codebook, File outputFile) throws IOException {
        // Create a FileWriter and BufferedWriter to write the codebook to the file
        try (FileWriter fileWriter = new FileWriter(outputFile);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {
            for (int[] entry : codebook) {
                // Write each entry of the codebook as a line in the file
                writer.write(Arrays.toString(entry));
                writer.newLine();
            }
        }
    }

    private List<int[]> readCodebookFromFile(File inputFile) throws IOException {
        // Create a FileReader and BufferedReader to read the codebook from the file
        List<int[]> codebook = new ArrayList<>();
        try (FileReader fileReader = new FileReader(inputFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line to get the codebook entry
                String[] values = line.replaceAll("\\[|\\]", "").split(",");
                int[] entry = Arrays.stream(values)
                        .map(String::trim)
                        .mapToInt(Integer::parseInt)
                        .toArray();
                codebook.add(entry);
            }
        }
        return codebook;
    }

    private BufferedImage decompressImage(List<int[]> codebook, File inputFile) throws IOException {
        // Read the compressed image from the input file
        List<int[]> compressedImage = readCompressedImageFromFile(inputFile);

        // Get the dimensions of the decompressed image
        int width = compressedImage.size();
        int height = compressedImage.get(0).length;

        // Decompress the image using the codebook
        return decompressImage(compressedImage, codebook, width, height);
    }

    private List<int[]> readCompressedImageFromFile(File inputFile) throws IOException {
        // Create a FileReader and BufferedReader to read the compressed image from the file
        List<int[]> compressedImage = new ArrayList<>();
        try (FileReader fileReader = new FileReader(inputFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line to get the compressed image entry
                String[] values = line.replaceAll("\\[|\\]", "").split(",");
                int[] entry = Arrays.stream(values)
                        .map(String::trim)
                        .mapToInt(Integer::parseInt)
                        .toArray();
                compressedImage.add(entry);
            }
        }
        return compressedImage;
    }
}

class ImageCompressionDemo {
    public static void main(String[] args) {
        try {
            BufferedImage originalImage = ImageIO.read(new File("D:\\Study\\FCAI\\Third year\\Data Compression\\Assignment 1\\test.jpg"));

            VectorQuantizationRGB vectorQuantizationRGB = new VectorQuantizationRGB();
            List<int[]> compressedImage = vectorQuantizationRGB.compressImage(originalImage);

            // Assuming the original width and height
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            BufferedImage decompressedImage = vectorQuantizationRGB.decompressImage(compressedImage, vectorQuantizationRGB.generateCodebook(originalImage), width, height);

            // Save the compressed image
            saveImage(compressedImage, vectorQuantizationRGB.generateCodebook(originalImage), width, height, "D:\\Study\\FCAI\\Third year\\Data Compression\\Assignment 1\\compressed_image.jpg");

            // Save the decompressed image
            saveImage(decompressedImage, "D:\\Study\\FCAI\\Third year\\Data Compression\\Assignment 1\\decompressed_image.jpg");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveImage(List<int[]> compressedImage, List<int[]> codebook, int width, int height, String filePath) throws IOException {
        VectorQuantizationRGB vectorQuantizationRGB = new VectorQuantizationRGB();
        BufferedImage decompressedImage = vectorQuantizationRGB.decompressImage(compressedImage, codebook, width, height);
        ImageIO.write(decompressedImage, "jpg", new File(filePath));
    }

    private static void saveImage(BufferedImage image, String filePath) throws IOException {
        ImageIO.write(image, "jpg", new File(filePath));
    }
}
