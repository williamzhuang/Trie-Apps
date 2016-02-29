import java.util.Scanner;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
/** AlphabetSort. Sorts a list of words into alphabetical order based on 
  * a permutation of some alphabet from stdin.
  * @author William Zhuang
  * Citation: http://stackoverflow.com/questions/13095983/getting-input-from-stdin 
  * http://stackoverflow.com/questions/5235401/split-string-into-array-of-character-strings
  */
public class AlphabetSort {

    /** Executes AlphabetSort and sorts a list of words into alphabetical order. 
      * @param args 
      */
    public static void main(String[] args) {
        SortedTrie trie = new SortedTrie();

        Scanner in = new Scanner(System.in);
        String alphabet = in.nextLine();

        HashSet<String> charactersHash = new HashSet<String>();
        String[] charactersArray = alphabet.split("(?!^)");
        charactersHash.addAll(Arrays.asList(charactersArray));

        if (charactersArray.length != charactersHash.size()) {
            throw new IllegalArgumentException("A letter appears multiple times in the alphabet.");
        }

        if (!in.hasNextLine()) {
            throw new IllegalArgumentException("No words or alphabet are given.");
        }

        while (in.hasNextLine()) {
            String output = in.nextLine();
            trie.insert(output);
        }

        String[] output = trie.alphabetize(alphabet);
        
        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(output));
        for (String x : arrayList) {
            System.out.println(x);
        }
    }

    /** 
      * Modified Trie that supports linear time find() and insert(). 
      * Additionally, supports alphabetize() which returns all words in trie alphabetically.
      */
    private static class SortedTrie {

        private Node root; 

        /**
          * Constructs an empty SortedTrie.
          */
        public SortedTrie() {
            root = new Node();
        }

        /** 
          * Traverses the trie to check if the String exists.
          * @param s String to be found.
          * @param isFullWord If true, will only return true if the String 
          *                   is a word that exists in the Trie.
          * @return Boolean designating whether String exists in the Trie. 
          */ 
        public boolean find(String s, boolean isFullWord) {

            Node pointer = root;
            for (int i = 0; i < s.length(); i += 1) {
                char c = s.charAt(i);
                pointer = pointer.links.get(c); 
                if (pointer == null) {
                    return false;
                }
            }

            if (!isFullWord) {
                return true;
            }

            if (isFullWord && pointer.exists) {
                return true;
            }

            return false;

        }
        
        /** 
          * Inserts a string into the tree.
          * @param s String to be inserted.
          */
        public void insert(String s) {
            if (s == null || s.isEmpty()) {
                throw new IllegalArgumentException("Null or empty strings"  
                                                   + "cannot be added to a Trie.");
            }

            insert(root, s, 0);
        }

        /**
          * Recursively inserts letters into the tree until the String s is fully inserted.
          * @param x Node that is currently being pointed at. 
          * @param key String that is to be inserted.
          * @param d Represents the dth letter of the string being added.
          * @return Returns the current node. 
          */
        private Node insert(Node x, String key, int d) {
        // If there is no node at that point.
            if (x == null) {
                x = new Node();
            }

        // Completed going through the word. 
            if (d == key.length()) {
                x.exists = true;
                return x;
            }

        // Gets the letter we are looking at and assigns the node accordingly.
            char c = key.charAt(d);
            x.links.put(c, insert(x.links.get(c), key, d + 1));
            return x;
        }

        /** 
          * Returns all contents in alphabetical order according to the given alphabet.
          * @param alphabet String of the alphabet by which to display the SortedTrie's contents.
          * @return String array of the alphabetized terms.
          */
        public String[] alphabetize(String alphabet) {
            String output = alphabetize(root, alphabet, "");
            String[] splitList = output.split(" ");
            return splitList;
        }

        /**
          * Traverses the SortedTrie recursively to extract words. 
          * @param x Node representing the current node.
          * @param alphabet String of alphabet by which to display the SortedTrie's contents.
          * @param wordbase String representing the letters traversed to get to the current node.
          * @return Single string containing all of the words that have been found so far, 
          *         separated by spaces. 
          */
        private String alphabetize(Node x, String alphabet, String wordbase) {
            Node pointer = x;
            String words = "";
            String trueBase = wordbase;

            if (pointer == null) {
                return "";
            }

        // Iterate through the alphabet. 
            for (int i = 0; i < alphabet.length(); i += 1) {
                trueBase = wordbase;
                char c = alphabet.charAt(i);
            // If the node exists. 
                if (pointer.links.get(c) != null) {
                    trueBase += c;
                    if (pointer.links.get(c).exists) {
                        words += trueBase + " ";
                    }
                    words += alphabetize(pointer.links.get(c), alphabet, trueBase);
                }
            }

            return words;
        }
        /** 
          * A single node in a Trie. Contains boolean exists that determines whether 
          * the node represents the last letter of a word in the tree. Also contains 
          * a HashMap linking to other Nodes.
          */
        private class Node {
            boolean exists;
            HashMap<Character, Node> links;

            /** 
              * Constructor for a single node.
              */
            public Node() {
                links = new HashMap<Character, Node>();
                exists = false; 
            }
        }
    }
}
