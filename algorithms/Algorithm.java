package algorithms;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public abstract class Algorithm {
    public boolean compress(File inputFile) {
        Path inputPath = inputFile.toPath();
        String inputFileName = inputPath.getFileName().toString();
        String outputFileName = getFormattedOutputFileName(inputFileName, 0);
        File outputFile = new File(inputPath.getParent().toString(), outputFileName);
        try {
            compress(inputFile, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean decompress(File inputFile) {
        Path inputPath = inputFile.toPath();
        String inputFileName = inputPath.getFileName().toString();
        String outputFileName = getFormattedOutputFileName(inputFileName, 1);
        File outputFile = new File(inputPath.getParent().toString(), outputFileName);
        try {
            decompress(inputFile, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public String getFormattedOutputFileName(String inputFileName, int action) {
        int dotIndex = inputFileName.lastIndexOf('.');
        String nameWithoutExtension = (dotIndex != -1) ? inputFileName.substring(0, dotIndex) : inputFileName;

        if (action == 0) {
            return nameWithoutExtension + ".compressed.txt";
        } else {
            return nameWithoutExtension + ".decompressedoutput.txt";
        }
    }


    public abstract void compress(File inputFile, File outputFile) throws IOException;
    public abstract void decompress(File inputFile, File outputFile) throws IOException;


}
