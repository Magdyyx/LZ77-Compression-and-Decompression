package algorithms;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanAlgorithm extends Algorithm {

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

            Map<Character, String> huffmanCodes = buildHuffmanCodes(uncompressedData.toString());

            // Write Huffman codes to the output file
            for (char c : uncompressedData.toString().toCharArray()) {
                bw.write(huffmanCodes.get(c));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decompress(File inputFile, File outputFile) {
        // TODO: Implement decompression
    }
}

