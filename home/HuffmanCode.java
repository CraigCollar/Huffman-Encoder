import java.util.*;
import java.io.*;
/*
 * Craig Collar
 * 5/29/24
 * CSE 123
 * TA: Connor Sun
 * P3: Huffman
 */

/*
 * This class represents a system that compresses and decompresses a file
 * using multiple methods for this process
 */
public class HuffmanCode {
    private HuffmanNode overallRoot;

    /*
     * This method creates a HuffmanCode object using a list of frequencies for each
     * character in a file. Characters are represented using the ascii chart
     * 
     * @param int[] frequencies    The frequencies of each character in a file. The
     *                             index of each frequency represents the ascii value
     *                             of that character
     */
    public HuffmanCode(int[] frequencies) {
        Queue<HuffmanNode> pq = new PriorityQueue<>();

        for(int i = 0; i < frequencies.length; i++) {
            if(frequencies[i] > 0) {
                HuffmanNode curr = new HuffmanNode(frequencies[i], (char)i, null, null);
                pq.add(curr);
            }
        }

        while(pq.size() > 1) {
            HuffmanNode curr1 = pq.remove();
            HuffmanNode curr2 = pq.remove();
            int totalFreq = curr1.frequency + curr2.frequency;

            HuffmanNode parent = new HuffmanNode(totalFreq, '\0', curr1, curr2);
            pq.add(parent);
        }
        //only 1 node left by the end of the loop(root)
        overallRoot = pq.remove();
    }

    /*
     * This method constructs a HuffmanCode object by taking a file containing the
     * ascii values of each character and each value's respective code
     * 
     * @param Scanner input     A scanner that reads an inputted file used to construct
     *                          the HuffmanCode
     */
    public HuffmanCode(Scanner input) {
        overallRoot = new HuffmanNode(0, '\0', null, null);
        while(input.hasNextLine()) {
            int asciiValue = Integer.parseInt(input.nextLine());
            String path = input.nextLine();
            //System.out.println(asciiValue + path);
            overallRoot = scannerConstructor(overallRoot, asciiValue, path, 0);
        }
    }

    /*
     * This method constructs a branch of a HuffmanCode by taking an individual
     * ascii value and its code.
     * 
     * @param HuffmanNode root    a node representing part of the path or the final
     *                            node that will hold the ascii value
     * @param int asciiValue      The integer value representing a specific character
     * @param String path         The code used to represent the ascii value when compressed
     * @param int index           The index of the path used to determine when we are at the
     *                            end of the code
     *                     
     * @return HuffmanNode        a node representing part of the path or the final
     *                            node that holds the ascii value
     */
    private HuffmanNode scannerConstructor(HuffmanNode root, int asciiValue, String path, int index)  {
            if(root == null) {
                if(index == path.length()) {
                    //root = new HuffmanNode(0, (char)asciiValue, null, null);
                    return new HuffmanNode(0, (char)asciiValue, null, null);
                }else {
                    root = new HuffmanNode(0, '\0', null, null);
                } 
                
            }

                char currChar = path.charAt(index);
                if(currChar == '0') {
                    root.left = scannerConstructor(root.left, asciiValue, path, index + 1);

                } else if(currChar == '1') {
                    root.right = scannerConstructor(root.right, asciiValue, path, index + 1);
                }
        return root;
    }

    /*
     * This method saves a HuffmanCode into a file using pre-order traversal
     * 
     * @param PrintStream output       A PrintStream used to print each value into
     *                                 the desired file
     */
    public void save(PrintStream output) {
        save(overallRoot, "", output);
    }

    /*
     * This method saves a HuffmanCode into a file using pre-order traversal
     * 
     * @param HuffmanNode root         A node representing part of the path or the final
     *                                 node that will hold the ascii value
     * @param String path              The code representing the code for each ascii value
     * @param PrintStream output       A PrintStream used to print each value into
     *                                 the desired file
     */
    private void save(HuffmanNode root, String path, PrintStream output) {
        if(root != null) {
            if(root.left == null && root.right == null) {
                output.println((int)root.letter);
                output.println(path);
            } else {
                save(root.left, path + "0", output);
                save(root.right, path + "1", output);
            }
            
        }
    }

    /*
     * This method takes in a previously compressed method and outputs 
     * the original decompressed method into a desired file.
     * 
     * @param BitInputStream input      Used like a scanner to read individual
     *                                  bits representing the compressed message
     * @param PrintStream output        A PrintStream used to print each value into
     *                                 the desired file
     */
    public void translate(BitInputStream input, PrintStream output) {
        //take input bits and translate to output
        //stop reading when inputStream empty
        while(input.hasNextBit()) {
            
            translate(overallRoot, input, output);
        }
            
    }

    /*
     * This method takes in a previously compressed method and outputs 
     * the original decompressed method into a desired file.
     * 
     * @param BitInputStream input      Used like a scanner to read individual
     *                                  bits representing the compressed message
     * @param PrintStream output        A PrintStream used to print each value into
     *                                 the desired file
     * @param HuffmanNode root         A node representing part of the path or the final
     *                                 node that will hold the ascii value
     */
    private void translate(HuffmanNode root, BitInputStream input, PrintStream output) {
        if(root != null) {
            if(root.left == null && root.right == null) {
                output.write((int)root.letter);
            } else {
                int currBit = (int)input.nextBit();
                if(currBit == 0) {
                    translate(root.left, input, output);
                } else if(currBit == 1) {
                    translate(root.right, input, output);
                }
            }
        }

    }

    /*
     * This class represents an indiviudual node part of a code/path used
     * to represent characters when compressed
     */
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        //left field
        public HuffmanNode left;
        //right field
        public HuffmanNode right;
        //frequency field
        public final int frequency;
        //letter field
        public final char letter;

        /*
         * This method constructs a HuffmanNode
         * 
         * @param frequency         The frequency of each character in a message
         * @param letter            An individual character present in a message
         * @param HuffmanNode left      The left child of the HuffmanNode
         * @param HuffmaneNode right    The right child of the HuffmanNode
         */
        private HuffmanNode(int frequency, char letter, HuffmanNode left, HuffmanNode right)  {
            this.frequency = frequency;
            this.letter = letter;
            this.left = left;
            this.right = right;
        }

        /*
         * This method makes it so HuffmanNodes are able to be compared
         * to each other using their frequencies. HuffmanNodes with a smaller 
         * frequency are prioritized over ones with larger frequencies
         * 
         * @param HuffmanNode other     The HuffmanNode being compared to This
         *                              HuffmanNode
         * 
         * @return int           An integer representing priority of the HuffmanNodes.
         *                       A negative integer means This node is prioritize, an
         *                       integer greater than 0 means the Other node is 
         *                       prioritized, and 0 means that they are equal.
         */
        public int compareTo(HuffmanNode other) {
            if(this.frequency < other.frequency) {
                return -1;
            } else if(this.frequency == other.frequency) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}

