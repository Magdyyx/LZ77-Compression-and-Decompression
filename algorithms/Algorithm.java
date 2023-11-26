package algorithms;

import jdk.jshell.execution.JdiExecutionControl;

import java.io.File;

public interface Algorithm {
    boolean compress(File inputFile);
    boolean decompress(File inputFile);
    private String getFormattedOutputFileName(String inputFileName) {
        return null;
    }
}
