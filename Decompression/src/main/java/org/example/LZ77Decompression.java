package org.example;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LZ77Decompression {

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

    public static void main(String[] args) {
        String inputFilePath = "compressed_output.txt";
        String outputFilePath = "decompressed_output.txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String compressedData = reader.readLine();
            reader.close();

            String decompressedData = decompress(compressedData);

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            writer.write(decompressedData);
            writer.close();

            System.out.println("Decompressed data saved to " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}