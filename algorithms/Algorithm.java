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
        compress(inputFile, outputFile);
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
        decompress(inputFile, outputFile);
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private String getFormattedOutputFileName(String inputFileName, int action) {
        int dotIndex = inputFileName.lastIndexOf('.');
        String nameWithoutExtension = inputFileName.substring(0, dotIndex);
        String extension = inputFileName.substring(dotIndex + 1);
        if (dotIndex != -1){
            if (action == 0) {
                {
                    return nameWithoutExtension + ".daly";
                }
            } else {
                return nameWithoutExtension + ".txt";
            }
        }
        return inputFileName + action;
    }

    public abstract void compress(File inputFile, File outputFile);
    public abstract void decompress(File inputFile, File outputFile);


}
