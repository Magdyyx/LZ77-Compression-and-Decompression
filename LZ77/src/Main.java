import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose an operation:");
        System.out.println("1. Compress");
        System.out.println("2. Decompress");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (choice == 1) {
            System.out.println("Enter the path to the input file to compress:");
            String inputFilePath = scanner.nextLine();

            try {
                BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
                String input = reader.readLine(); // Read the first line as the input string
                reader.close();

                String compressed = LZ77Compression.compress(input);

                String compressedFilePath = "compressed_output.txt";

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(compressedFilePath));
                    writer.write(compressed);
                    writer.close();
                    System.out.println("Compressed data saved to " + compressedFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (choice == 2) {
            System.out.println("Enter the path to the compressed file:");
            String inputFilePath = scanner.nextLine();
            String outputFilePath = "decompressed_output.txt";

            try {
                BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
                String compressedData = reader.readLine(); // Read the first line as the compressed data
                reader.close();

                String decompressedData = LZ77Decompression.decompress(compressedData);

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
                    writer.write(decompressedData);
                    writer.close();

                    System.out.println("Decompressed data saved to " + outputFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid choice. Please choose 1 for compression or 2 for decompression.");
        }
    }
}
