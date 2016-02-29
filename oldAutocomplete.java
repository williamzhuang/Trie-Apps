import java.util.LinkedList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.SortedSet;
import java.util.NavigableSet;
import java.util.Comparator;
import java.util.Collections;
/**
 * Implements autocomplete on prefixes for a given dictionary of terms and weights.
 * @Author William Zhuang
 * Citation: http://stackoverflow.com/questions/
 *           1090969/treeset-to-order-elements-in-descending-order
 */
public class oldAutocomplete {
    WeightedTrie trie;
    HashMap<String, Double> weightMap;
    /**
     * Initializes required data structures from parallel arrays.
     * @param terms Array of terms.
     * @param weights Array of weights.
     */
    public oldAutocomplete(String[] terms, double[] weights) {
        if (terms.length != weights.length) {
            throw new IllegalArgumentException("The length of the terms and" + 
                                               "weights arrays are different.");
        }

        trie = new WeightedTrie();
        weightMap = new HashMap<String, Double>();
        for (int i = 0; i < terms.length; i += 1) {
            if (weights[i] < 0) {
                throw new IllegalArgumentException("Negative weight");
            }

            trie.insert(terms[i], weights[i]);
            weightMap.put(terms[i], weights[i]);
        }

        if (weightMap.size() != weights.length) {
            throw new IllegalArgumentException("Duplicate terms exist.");
        }
    }

    /**
     * Find the weight of a given term. If it is not in the dictionary, return 0.0
     * @param term String for which the weight is found.
     * @return double Weight of the term
     */
    public double weightOf(String term) {
        if (weightMap.get(term) == null) {
            return 0.0;
        } else {
            return weightMap.get(term);
        }
    }

    /**
     * Return the top match for given prefix, or null if there is no matching term.
     * @param prefix Input prefix to match against.
     * @return Best (highest weight) matching string in the dictionary.
     */
    public String topMatch(String prefix) {
        LinkedHashSet<String> output = (LinkedHashSet<String>) topMatches(prefix, 1);
        String answer = "";
        for (String x : output) {
            answer = x;
        }
        return answer;
    }

    /**
     * Returns the top k matching terms (in descending order of weight) as an iterable.
     * If there are less than k matches, return all the matching terms.
     * @param prefix Prefix from which words are found.
     * @param k      Number of terms to be returned.
     * @return Iterable of the k top matching terms in descending order.
     */
    public Iterable<String> topMatches(String prefix, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k cannot be negative.");
        }

        return trie.topMatches(prefix, k);
    }

    /**
     * Returns the highest weighted matches within k edit distance of the word.
     * If the word is in the dictionary, then return an empty list.
     * @param word The word to spell-check
     * @param dist Maximum edit distance to search
     * @param k    Number of results to return 
     * @return Iterable in descending weight order of the matches
     */
    public Iterable<String> spellCheck(String word, int dist, int k) {
        LinkedList<String> results = new LinkedList<String>();  
        /* YOUR CODE HERE; LEAVE BLANK IF NOT PURSUING BONUS */
        return results;
    }
    /**
     * Test client. Reads the data from the file, 
     * then repeatedly reads autocomplete queries from standard input and prints out the top 
     * k matching terms.
     * @param args takes the name of an input file and an integer k as command-line arguments
     */
    public static void main(String[] args) {
        // initialize autocomplete data structure
        In in = new In(args[0]);
        int N = in.readInt();
        String[] terms = new String[N];
        double[] weights = new double[N];
        for (int i = 0; i < N; i++) {
            weights[i] = in.readDouble();   // read the next weight
            in.readChar();                  // scan past the tab
            terms[i] = in.readLine();       // read the next term
        }

        Autocomplete autocomplete = new Autocomplete(terms, weights);

        // process queries from standard input
        int k = Integer.parseInt(args[1]);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            for (String term : autocomplete.topMatches(prefix, k)) {
                StdOut.printf("%14.1f  %s\n", autocomplete.weightOf(term), term);
            }
        }
    }

    /**
      * Modified Trie that supports linear time find() and insert(). 
      * Additionally, supports topMatches() to obtain best Autocomplete matches.
      */
    private class WeightedTrie {

        private Node root; 

        /** 
          * Constructs an empty WeightedTrie. 
          */
        public WeightedTrie() {
            root = new Node(0);
        }

        /** 
          * Traverses the trie to check if the String exists.
          * @param s String to be found.
          * @param isFullWord If true, will only return true if the String is a word that exists in the Trie.
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
        public void insert(String s, double weight) {
            if (s == null || s.isEmpty())  {
                throw new IllegalArgumentException("Null or empty strings cannot be added to a Trie.");
            }

            insert(root, s, weight, 0);
        }

        /**
          * Recursively inserts letters into the tree until the String s is fully inserted.
          * @param x Node that is currently being pointed at. 
          * @param key String that is to be inserted.
          * @param d Represents the dth letter of the string being added.
          * @return Returns the current node. 
          */
        private Node insert(Node x, String key, double weight, int d) {

            // If there is no node at that point.
            if (x == null) {
                x = new Node(0);
                x.maxWeight = weight;
            }

            // Completed going through the word. 
            if (d == key.length()) {
                x.exists = true;
                x.weight = weight;
                x.word = key;
                return x;
            }

            char c = key.charAt(d);
            // If current letter already exists, check and modify max weight.
            if (weight > x.maxWeight) {
                x.maxWeight = weight;
                x.maxChar = c;
            }

            // Assigns the next node in the word.
            x.links.put(c, insert(x.links.get(c), key, weight, d + 1));
            return x;
        }

        /** 
          * Finds the top k words that contain the given prefix. 
          * @param prefix String of the prefix 
          * @param k Number of top words wanted.
          */
        public Iterable<String> topMatches(String prefix, int k) {
            int origK = k;
            k = k * 2;

            TreeSet<Node> bestAnswer = new TreeSet<Node>(new DupeNodeComparator());
            Node pointer = root;
            String word = prefix;

            // Get through the prefix.
            if (!prefix.startsWith("\"") && !prefix.endsWith("\"")) { 
                for (int i = 0; i < prefix.length(); i += 1) {
                    char c = prefix.charAt(i);
                    pointer = pointer.links.get(c);
                }
            }

            // Find the best k answers.
            // Go through the children in order of greatest size.
            TreeSet<Node> nodeTree = new TreeSet<Node>(new NodeComparator());
            nodeTree.add(pointer);
            nodeTree.addAll(pointer.links.values());

            for (Node x : nodeTree) {

                if (x.exists) {
                    bestAnswer.add(x);
                }
                NavigableSet<Node> bestAbove = bestAnswer.headSet(x, true);
                if (bestAbove.size() > k) {
                    continue;
                } 
                nodeTraversal(x, bestAnswer, k);
            }
            // Resorting answers according to actual weight.
            TreeSet<Node> tempAnswers = new TreeSet<Node>(new NodeComparator());
            tempAnswers.addAll(bestAnswer);
            // Extract best words associated with bestAnswer.
            LinkedHashSet<String> answers = new LinkedHashSet<String>();

            // Sort bestAnswers by highest weight. 
            for (Node x : tempAnswers) {
                answers.add(x.word);
                if (answers.size() == origK) {
                    break;
                }
            }

            return answers;
        }

        /** 
          * Traverses the child nodes selectively by only traversing nodes that may contain 
          * candidates for the top k matches. 
          * @param x Current Node being examined. 
          * @param bestAnswer TreeSet containing all the Nodes that were possible candidates.
          * @param k int representing the number of topMatches wanted.
          */

        private void nodeTraversal(Node x, TreeSet<Node> bestAnswer, int k) {
            TreeSet<Node> nodeTree = new TreeSet<Node>(new NodeComparator());
            nodeTree.addAll(x.links.values());
            // Add the children to the best answer.
            // Check if there are k bestAnswers that are already greater than the child node.
            // If so, break.
            for (Node n : nodeTree) {
                if (n.exists) {
                    bestAnswer.add(n);

                }
                NavigableSet<Node> bestAbove = bestAnswer.headSet(n, true);
                if (bestAbove.size() > k) {
                    continue;
                }

                nodeTraversal(n, bestAnswer, k);
            }
        }

        /** 
          * Comparator that compares based on weights of nodes. 
          */
        private class NodeComparator implements Comparator<Node> {
            
            /** 
              * Compare method comparing weights. 
              * Actually returns less than if weight is greater and vice versa.
              * @param n1 
              * @param n2
              * @result int representing equal, greater, or less than corresponding to 0, 1, -1
              */
            public int compare(Node n1, Node n2) {
                int answer = (int) Math.signum(n2.weight - n1.weight);
                if (answer == 0) {
                    return 1;
                } else {
                    return answer;
                }
            }
        }

        private class DupeNodeComparator implements Comparator<Node> {
            public int compare(Node n1, Node n2) {
                int fin = (int) Math.signum(n2.maxWeight - n1.maxWeight);
                int tieBreak = (int) Math.signum(n2.weight - n1.weight);
                // Break ties to prevent equality issues with TreeSet.
                if (fin == 0) {
                    return -1;
                } else {
                    return fin;
                }
            }
        }

        /** 
          * A single node in a WeightedTree. 
          */
        private class Node implements Comparable<Node> {
            boolean exists;
            String word;
            double maxWeight;
            double weight;
            char maxChar;
            HashMap<Character, Node> links;

            /** 
              * Initializes the Node with an initial weight. Weight = 0 if it does not exist.
              */
            public Node(double weight0) {
                weight = weight0;
                links = new HashMap<Character, Node>();
                exists = false; 
            }

            /** 
              * Compares based on maxWeight with weight as a tie breaker. 
              * Actually returns less than if weight is greater than and vice versa.
              * @param o Node to be compared to.
              * @return int representing equal, greater, or less than corresponding to 0, 1, -1
              */
            public int compareTo(Node o) {
                int fin = (int) Math.signum(o.maxWeight - maxWeight);
                int tieBreak = (int) Math.signum(o.weight - weight);
                // Break ties to prevent equality issues with TreeSet.
                if (fin == 0) {
                    return tieBreak;
                } else {
                    return fin;
                }
            }
        }
    }

}

