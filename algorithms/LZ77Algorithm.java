package algorithms;

import java.io.*;
import java.util.ArrayList;

public class LZ77Algorithm extends Algorithm {


    public static class Tag {
        int position;
        int length;
        char nextSymbol;

        Tag(int position, int length, char nextSymbol) {
            this.position = position;
            this.length = length;
            this.nextSymbol = nextSymbol;
        }
    }


    public static String compress(String input) {
        ArrayList<Tag> compressed = new ArrayList<>();
        int currentIndex = 0;

        while (currentIndex < input.length()) {
            int maxMatchLength = 0;
            int bestMatchOffset = 0;
            char nextChar = input.charAt(currentIndex);

            for (int i = 0; i < currentIndex; i++) {
                int j = i;
                int k = currentIndex;
                int matchLength = 0;

                while (k < input.length() && input.charAt(j) == input.charAt(k) && matchLength < 255) {
                    j++;
                    k++;
                    matchLength++;
                }

                if (matchLength >= maxMatchLength) {
                    maxMatchLength = matchLength;
                    bestMatchOffset = currentIndex - i;
                    nextChar = (k < input.length()) ? input.charAt(k) : input.charAt(currentIndex);
                }
            }

            if (maxMatchLength == 0) {
                compressed.add(new Tag(0, 0, nextChar));
                currentIndex++;
            } else {
                compressed.add(new Tag(bestMatchOffset, maxMatchLength, nextChar));
                currentIndex += (maxMatchLength + 1);
            }
        }

        StringBuilder result = new StringBuilder();
        for (Tag tuple : compressed) {
            result.append("(")
                    .append(tuple.position)
                    .append(",")
                    .append(tuple.length)
                    .append(",")
                    .append(tuple.nextSymbol)
                    .append(")");
        }

        return result.toString();
    }

    public static String decompress(String input) {
        StringBuilder result = new StringBuilder();
        ArrayList<Tag> tags = parseTags(input);

        for (Tag tag : tags) {
            if (tag.length == 0) {
                result.append(tag.nextSymbol);
            } else {
                int startPos = result.length() - tag.position;
                for (int i = startPos; i < startPos + tag.length; i++) {
                    result.append(result.charAt(i));
                }
                result.append(tag.nextSymbol);
            }
        }

        return result.toString();
    }
    @Override
    public void compress(File inputFile, File outputFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
             BufferedReader br = new BufferedReader(new FileReader(inputFile));
        ){
            StringBuilder uncompressedData = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                uncompressedData.append(line);
            }
            String compressedData = compress(uncompressedData.toString());
            bw.write(compressedData);
            bw.write(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decompress(File inputFile, File outputFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
             BufferedReader br = new BufferedReader(new FileReader(inputFile));
        ) {
            String compressedData = br.readLine();
            String decompressedData = decompress(compressedData);
            bw.write(decompressedData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Tag> parseTags(String input) {
        ArrayList<Tag> tags = new ArrayList<>();

        String[] tagStrings = input.split("\\)\\(");

        for (String tagString : tagStrings) {
            tagString = tagString.replace("(", "").replace(")", "");
            String[] parts = tagString.split(",");

            int position = Integer.parseInt(parts[0]);
            int length = Integer.parseInt(parts[1]);
            char nextSymbol = parts[2].charAt(0);

            tags.add(new Tag(position, length, nextSymbol));
        }

        return tags;
    }
}
