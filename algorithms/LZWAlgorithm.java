package algorithms;


import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZWAlgorithm implements Algorithm {
    private static final int MAX_TABLE_SIZE = 4096;
    public LZWAlgorithm() {
    }

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

    private List<Integer> compress(String uncompressed) {
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(Character.toString((char) i), i);
        }
        int dictionarySize = 256;
        String current = "";
        List<Integer> compressedData = new ArrayList<>();

        for (char c : uncompressed.toCharArray()) {
            String combined = current + c;
            if(dictionary.containsKey(combined)) {
                current = combined;
            } else {
                compressedData.add(dictionary.get(current));
                if (dictionarySize < MAX_TABLE_SIZE) {
                    dictionary.put(combined, dictionarySize++);
                }
                current = Character.toString(c);
            }
        }

        if(!current.equals("")) {
            compressedData.add(dictionary.get(current));
        }

        return compressedData;

    }

    public void compress(File inputFile, File outputFile){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
             BufferedReader br = new BufferedReader(new FileReader(inputFile));
        ){
            StringBuilder uncompressedData = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                uncompressedData.append(line);
            }
            List<Integer> compressedData = compress(uncompressedData.toString());

            for(int code : compressedData) {
                bw.write(Integer.toString(code));
                bw.write(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String getFormattedOutputFileName(String inputFileName, int action) {
        int dotIndex = inputFileName.lastIndexOf('.');
        String nameWithoutExtension = inputFileName.substring(0, dotIndex);
        String extension = inputFileName.substring(dotIndex + 1);
        if (dotIndex != -1){
            if (action == 0) {
                {
                    return nameWithoutExtension + ".daly." + extension;
                }
            } else {
                int customExtensionDotIndex = nameWithoutExtension.lastIndexOf('.');
                String nameWithoutCustomExtension = nameWithoutExtension.substring(0, customExtensionDotIndex);
                return nameWithoutCustomExtension + "." + extension;
            }
        }
        return inputFileName + action;
    }

    private String decompress(List<Integer> compressedData) {
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, Character.toString((char) i));
        }
        int dictionarySize = 256;
        StringBuilder decompressed = new StringBuilder();

        int currentCode = compressedData.get(0);
        String current = dictionary.get(currentCode);
        decompressed.append(current);

        for (int i = 1; i < compressedData.size(); i++) {
            int code = compressedData.get(i);
            String entry;
            if (dictionary.containsKey(code)) {
                entry = dictionary.get(code);
            } else if (code == dictionarySize) {
                entry = current + current.charAt(0);
            } else {
                throw new RuntimeException("Decompression error");
            }

            decompressed.append(entry);
            if (dictionarySize < MAX_TABLE_SIZE) {
                dictionary.put(dictionarySize++, current + entry.charAt(0));
            }
            current = entry;
        }

        return decompressed.toString();
    }

    public void decompress(File inputFile, File outputFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
             BufferedReader br = new BufferedReader(new FileReader(inputFile));
        ) {
            String line;
            List<Integer> compressedData = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(" ");
                for (String token : tokens) {
                    if (!token.isEmpty()) {
                        compressedData.add(Integer.parseInt(token));
                    }
                }
            }

            String decompressedData = decompress(compressedData);
            bw.write(decompressedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
