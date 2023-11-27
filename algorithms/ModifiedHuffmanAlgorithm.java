package algorithms;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ModifiedHuffmanAlgorithm extends Algorithm {

    static class HuffmanNode implements Comparable<HuffmanNode> {
        int data;
        char character;
        HuffmanNode left, right;

        public HuffmanNode(int data, char character) {
            this.data = data;
            this.character = character;
            this.left = this.right = null;
        }

        public int compareTo(HuffmanNode node) {
            return this.data - node.data;
        }
    }

    private HuffmanNode rootOfHuffmanTree; // Added this line

    private void buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        // Build Huffman tree using priority queue
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.add(new HuffmanNode(entry.getValue(), entry.getKey()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            HuffmanNode newNode = new HuffmanNode(left.data + right.data, '\0');
            newNode.left = left;
            newNode.right = right;

            priorityQueue.add(newNode);
        }
        rootOfHuffmanTree = priorityQueue.poll(); // Assign the root of the tree
    }

    private void generateHuffmanCodes(HuffmanNode root, String code, Map<Character, String> huffmanCodes) {
        // Recursively generate Huffman codes
        if (root == null) {
            return;
        }

        if (root.character != '\0') {
            huffmanCodes.put(root.character, code);
        }

        generateHuffmanCodes(root.left, code + "0", huffmanCodes);
        generateHuffmanCodes(root.right, code + "1", huffmanCodes);
    }

    private Map<Character, String> buildHuffmanCodes(String input) {
        // Build a frequency map for characters in the input
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : input.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        // Build Huffman tree
        buildHuffmanTree(frequencyMap);

        // Generate Huffman codes
        Map<Character, String> huffmanCodes = new HashMap<>();
        generateHuffmanCodes(rootOfHuffmanTree, "", huffmanCodes);

        return huffmanCodes;
    }

    @Override
    public void compress(File inputFile, File outputFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
             BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

            StringBuilder uncompressedData = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                uncompressedData.append(line);
            }

            // Build Huffman tree
            buildHuffmanTree(buildFrequencyMap(uncompressedData.toString()));

            // Generate Huffman codes
            Map<Character, String> huffmanCodes = buildHuffmanCodes(uncompressedData.toString());

            // Write Huffman codes to the output file header
            for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            bw.write("\n"); // End of header

            // Write compressed data to the output file
            for (char c : uncompressedData.toString().toCharArray()) {
                bw.write(huffmanCodes.get(c));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decompress(File inputFile, File outputFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            Map<Character, String> huffmanCodes = readHuffmanCodes(br);

            buildHuffmanTree(buildFrequencyMapFromCodes(huffmanCodes));

            // Decompress data
            String compressedData = readCompressedData(br);
            String decompressedData = decompressData(compressedData);

            bw.write(decompressedData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Map<Character, String> readHuffmanCodes(BufferedReader br) throws IOException {
        Map<Character, String> huffmanCodes = new HashMap<>();

        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":");
            char symbol = parts[0].charAt(0);
            String code = parts[1];
            huffmanCodes.put(symbol, code);
        }

        return huffmanCodes;
    }

    private Map<Character, Integer> buildFrequencyMapFromCodes(Map<Character, String> huffmanCodes) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            char symbol = entry.getKey();
            int frequency = entry.getValue().length();
            frequencyMap.put(symbol, frequency);
        }
        return frequencyMap;
    }

    private String readCompressedData(BufferedReader br) throws IOException {
        StringBuilder compressedData = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            compressedData.append(line);
        }
        return compressedData.toString();
    }


    private Map<Character, Integer> buildFrequencyMap(String input) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : input.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    private String decompressData(String compressedData) {
        StringBuilder decompressedData = new StringBuilder();
        HuffmanNode current = rootOfHuffmanTree;

        for (int i = 0; i < compressedData.length(); i++) {
            char bit = compressedData.charAt(i);

            if (bit == '0') {
                current = current.left;
            } else if (bit == '1') {
                current = current.right;
            }

            if (current.left == null && current.right == null) {
                decompressedData.append(current.character);
                current = rootOfHuffmanTree;
            }
        }

        return decompressedData.toString();
    }
}

