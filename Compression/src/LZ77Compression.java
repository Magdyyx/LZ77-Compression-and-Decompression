import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LZ77Compression {

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



    public static void main(String[] args) {
        String input = "abracadabrad";
        String compressed = compress(input);
        System.out.println("Input: " + input);
        System.out.println("Compressed: " + compressed);

        String filePath = "compressed_output.txt";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            writer.write(compressed);

            writer.close();

            System.out.println("Compressed data saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}