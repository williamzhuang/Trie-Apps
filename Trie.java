import java.util.HashMap;
/**
 * Prefix-Trie. Supports linear time find() and insert(). 
 * Should support determining whether a word is a full word in the 
 * Trie or a prefix.
 * @author William Zhuang
 * Basic Trie and insert implementation adapted from Josh Hug's lecture on Tries
 * https://docs.google.com/presentation/d/1nrfO-8Skqj8aU69op1vEpNWcSJwr8wsBiDOB5gVhBTE/
 * pub?start=false&loop=false&delayms=3000#slide=id.g46b429e30_0110
 */
public class Trie {

    private Node root; 

    /** Constructs an empty Trie. */
    public Trie() {
        root = new Node();
    }

    /** 
      * Traverses the trie to check if the String exists.
      * @param s String to be found.
      * @param isFullWord If true, will only return true if the String is a word 
      *                   that exists in the Trie.
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
      *
      */
    public void insert(String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Null or empty strings cannot be added to a Trie.");
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
      * A single node in a Trie. Contains boolean exists that determines whether 
      * the node represents the last letter of a word in the tree. Also contains 
      * a HashMap linking to other Nodes.
      */
    private class Node {
        boolean exists;
        HashMap<Character, Node> links;

        /**
          * Constructor for the Node.
          */
        public Node() {
            links = new HashMap<Character, Node>();
            exists = false; 
        }
    }
}
