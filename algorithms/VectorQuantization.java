package algorithms;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class VectorQuantization extends Algorithm {

    private static class Vector {
        private ArrayList<Integer> list;

        public ArrayList<Integer> getList() {
            return list;
        }

        public void setList(ArrayList<Integer> list) {
            this.list = list;
        }

        public Vector(ArrayList<Integer> list) {
            this.list = list;
        }

        public Vector() {
            list = new ArrayList<>();
        }
    }

    public boolean compress(File inputFile) {
        File compressedFile = getOutputFile(inputFile, 0);
        compress(inputFile, compressedFile);
        return true;
    }

    public boolean decompress(File inputFile) {
        File decompressedFile = getOutputFile(inputFile, 1);
        decompress(inputFile, decompressedFile);
        return true;
    }
    private File getOutputFile(File inputFile, int action) {
        Path inputPath = inputFile.toPath();
        String inputFileName = inputPath.getFileName().toString();
        String outputFileName = getFormattedOutputFileName(inputFileName, action);
        return new File(inputPath.getParent().toString(), outputFileName);
    }

    @Override
    public String getFormattedOutputFileName(String inputFileName, int action) {
        int dotIndex = inputFileName.lastIndexOf('.');
        String nameWithoutExtension = (dotIndex != -1) ? inputFileName.substring(0, dotIndex) : inputFileName;

        if (action == 0) {
            return nameWithoutExtension + ".compressed_custom.txt";
        } else {
            return nameWithoutExtension + ".decompressedoutput_custom.jpg";
        }
    }

    @Override
    public void compress(File inputFile, File outputFile) {
        try {
            int codeBookLength = 16; // You can set this based on your requirement
            int vectRows = 8; // You can set this based on your requirement
            compression(inputFile, codeBookLength, vectRows);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decompress(File inputFile, File outputFile) {
        try {
            int vectRows = 8; // You can set this based on your requirement
            decompression(inputFile, vectRows);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compression(File inputFile, int codeBookLength, int vectRows) throws IOException {
        ArrayList<Integer> inputPixels = readImage(inputFile);
        ArrayList<Vector> codeBook = new ArrayList<>();
        ArrayList<Vector> inputVectors = divideIntoVectors(inputPixels, vectRows);
        ArrayList<Integer> outputLevels = new ArrayList<>();

        codeBook = createCodeBook(inputVectors, codeBookLength, vectRows);
        outputLevels = makeLevel(inputVectors, codeBook, vectRows);

        writeArrayListToFile(new File("compressionOutput.txt"), outputLevels);
        writeCodeBookToFile(new File("CodeBook.txt"), codeBook);
        writeArrayListToFile(new File("OriginalPixels.txt"), inputPixels);
    }

    private void decompression(File inputFile, int vectRows) throws IOException {
        ArrayList<Integer> levels = readArrayListFromFile(inputFile);
        ArrayList<Integer> codeBookInt = readArrayListFromFile(new File("CodeBook.txt"));
        ArrayList<Vector> codeBookVectors = divideIntoVectors(codeBookInt, vectRows);
        ArrayList<Vector> decompOutVectors = new ArrayList<>();
        ArrayList<Integer> decompOutInt = new ArrayList<>();

        for (int level : levels) {
            decompOutVectors.add(codeBookVectors.get(level));
        }

        for (Vector vector : decompOutVectors) {
            decompOutInt.addAll(vector.getList());
        }

        writeImage(decompOutInt, new File("Decompression.jpg"));
        writeArrayListToFile(new File("DecompressedPixels.txt"), decompOutInt);
    }

    private ArrayList<Vector> divideIntoVectors(ArrayList<Integer> input, int vectorRows) {
        ArrayList<Vector> result = new ArrayList<>();
        for (int i = 0; i < input.size(); i += vectorRows) {
            int end = Math.min(i + vectorRows, input.size());
            ArrayList<Integer> temp = new ArrayList<>(input.subList(i, end));
            result.add(new Vector(temp));
        }
        return result;
    }
    private ArrayList<Integer> readImage(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();

        ArrayList<Integer> list = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb >> 0) & 0xff;
                list.add(r);
                list.add(g);
                list.add(b);
            }
        }

        return list;
    }



    private Vector calculateAverageFunction(ArrayList<Vector> input, int vectorRows) {
        if (input.isEmpty()) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (int i = 0; i < vectorRows; i++) {
                temp.add(0);
            }
            return new Vector(temp);
        }

        ArrayList<Integer> temp = new ArrayList<>();
        int sum = 0;
        int ctr = vectorRows;
        int index = 0;
        while (ctr > 0) {
            for (Vector vector : input) {
                sum += vector.getList().get(index);
            }
            temp.add(sum / input.size());
            sum = 0;
            index++;
            ctr--;
        }

        return new Vector(temp);
    }

    private ArrayList<ArrayList<Vector>> compareVectorsFunction(ArrayList<Vector> originalInput, ArrayList<Vector> comparingVectors, int vectRows) {
        ArrayList<ArrayList<Vector>> result = new ArrayList<>(comparingVectors.size());

        for (int i = 0; i < comparingVectors.size(); i++) {
            result.add(new ArrayList<Vector>());
        }
        ArrayList<Integer> temp = new ArrayList<>();
        ArrayList<Integer> tempSorted = new ArrayList<>();
        for (Vector vector : originalInput) {
            for (Vector comparingVector : comparingVectors) {
                int subsum = 0;
                for (int k = 0; k < vectRows; k++) {
                    subsum += Math.pow(vector.getList().get(k) - comparingVector.getList().get(k), 2);
                }
                temp.add(subsum);
                tempSorted.add(subsum);
            }
            Collections.sort(tempSorted);
            int index = temp.indexOf(tempSorted.get(0));
            result.get(index).add(vector);
            temp.clear();
            tempSorted.clear();
        }
        return result;
    }

    private ArrayList<Vector> splitFunction(ArrayList<Vector> initialCodeBook, int vectRow) {
        ArrayList<Vector> initialCodeBookResult = new ArrayList<>();
        for (Vector vector : initialCodeBook) {
            ArrayList<Integer> subVect1 = new ArrayList<>();
            ArrayList<Integer> subVect2 = new ArrayList<>();
            for (int j = 0; j < vectRow; j++) {
                subVect1.add(vector.getList().get(j) - 1);
                subVect2.add(vector.getList().get(j) + 1);
            }
            initialCodeBookResult.add(new Vector(subVect1));
            initialCodeBookResult.add(new Vector(subVect2));
        }
        return initialCodeBookResult;
    }

    private ArrayList<Vector> createCodeBook(ArrayList<Vector> originalInput, int codeBookLength, int vectRows) {
        ArrayList<Vector> codeBook = new ArrayList<>();
        ArrayList<ArrayList<Vector>> middleVectorsGroups = new ArrayList<>();

        codeBook.add(calculateAverageFunction(originalInput, vectRows));

        int ctr = 0;
        while (codeBook.size() < codeBookLength) {
            codeBook = splitFunction(codeBook, vectRows);
            middleVectorsGroups = compareVectorsFunction(originalInput, codeBook, vectRows);
            codeBook.clear();
            for (ArrayList<Vector> middleVectorsGroup : middleVectorsGroups) {
                codeBook.add(calculateAverageFunction(middleVectorsGroup, vectRows));
            }
            ctr++;
        }
        return codeBook;
    }

    private ArrayList<Integer> makeLevel(ArrayList<Vector> originalInput, ArrayList<Vector> codeBook, int vectRows) {
        ArrayList<Integer> levels = new ArrayList<>();
        ArrayList<Integer> temp = new ArrayList<>();
        ArrayList<Integer> tempSorted = new ArrayList<>();
        for (Vector vector : originalInput) {
            for (Vector aCodeBook : codeBook) {
                int subsum = 0;
                for (int k = 0; k < vectRows; k++) {
                    subsum += Math.pow(vector.getList().get(k) - aCodeBook.getList().get(k), 2);
                }
                temp.add(subsum);
                tempSorted.add(subsum);
            }
            Collections.sort(tempSorted);
            int index = temp.indexOf(tempSorted.get(0));
            levels.add(index);
            temp.clear();
            tempSorted.clear();
        }
        return levels;
    }

    private void writeArrayListToFile(File file, ArrayList<Integer> list) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Integer integer : list) {
                writer.print(integer.toString() + " ");
            }
        }
    }

    private void writeCodeBookToFile(File file, ArrayList<Vector> codeBook) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Vector aCodeBook : codeBook) {
                ArrayList<Integer> list = aCodeBook.getList();
                for (Integer integer : list) {
                    writer.print(integer.toString() + " ");
                }
                writer.println();
            }
        }
    }

    private ArrayList<Integer> readArrayListFromFile(File file) throws IOException {
        ArrayList<Integer> list = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextInt()) {
                list.add(scanner.nextInt());
            }
        }
        return list;
    }



    private void writeImage(ArrayList<Integer> pixel, File outputFilePath) throws IOException {
        int width = 512; // You can set this based on your requirement
        int height = 512; // You can set this based on your requirement
        int[][] pixels = new int[height][width];
        int counter = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = pixel.get(counter);
                counter++;
            }
        }

        File fileout = outputFilePath;
        BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image2.setRGB(x, y, (pixels[y][x] << 16) | (pixels[y][x] << 8) | (pixels[y][x]));
            }
        }
        ImageIO.write(image2, "jpg", fileout);
    }

//    public static void main(String[] args) {
//        // Example usage
//        VectorQuantization vectorQuantization = new VectorQuantization();
//        File inputFile = new File("input.jpg");
//        File compressedFile = new File("compressed.txt");
//        File decompressedFile = new File("decompressed.jpg");
//
//        // Compression
//        vectorQuantization.compress(inputFile, compressedFile);
//
//        // Decompression
//        vectorQuantization.decompress(compressedFile, decompressedFile);
//    }
}
