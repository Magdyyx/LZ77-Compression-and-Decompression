package algorithms;

import java.io.*;

public class TwoDPredictiveCoding extends Algorithm {
    private static final int PREDICTION_MODES = 4;

    @Override
    public void compress(File inputFile, File outputFile) throws IOException {
        int[][] image = ImageUtils.readImage(inputFile);
        int width = image.length;
        int height = image[0].length;

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile))) {
            out.writeInt(width);
            out.writeInt(height);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int predictedValue = predictValue(image, i, j);
                    int diff = image[i][j] - predictedValue;
                    out.writeInt(diff);
                }
            }
        }
    }

    @Override
    public void decompress(File inputFile, File outputFile) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(inputFile))) {
            int width = in.readInt();
            int height = in.readInt();

            int[][] decompressedImage = new int[width][height];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int predictedValue = predictValue(decompressedImage, i, j);
                    int diff = in.readInt();
                    decompressedImage[i][j] = predictedValue + diff;
                }
            }

            ImageUtils.writeImage(decompressedImage, outputFile);
        }
    }

    private int predictValue(int[][] image, int x, int y) {
        if (x == 0 || y == 0) {
            return 0;
        }

        int a = image[x - 1][y];
        int b = image[x][y - 1];
        int c = image[x - 1][y - 1];

        int[] predictions = {a, b, c, (a + b - c)};
        return predictions[comparePredictionModes(a, b, c, (a + b - c))];
    }

    private int comparePredictionModes(int a, int b, int c, int d) {
        int[] predictions = {Math.abs(a), Math.abs(b), Math.abs(c), Math.abs(d)};
        int minIndex = 0;
        for (int i = 1; i < PREDICTION_MODES; i++) {
            if (predictions[i] < predictions[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    public static void main(String[] args) {
        try {
            TwoDPredictiveCoding compressor = new TwoDPredictiveCoding();

            // Compression
            File inputImage = new File("D:\\Study\\FCAI\\Third year\\Data Compression\\Assignment 1\\test.jpg");
            File compressedFile = new File("D:\\Study\\FCAI\\Third year\\Data Compression\\Assignment 1\\compressed.bin");
            compressor.compress(inputImage, compressedFile);

            // Decompression
            File decompressedFile = new File("D:\\Study\\FCAI\\Third year\\Data Compression\\Assignment 1\\decompressed.jpg");
            compressor.decompress(compressedFile, decompressedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
