import java.util.LinkedList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeMap;
/**
 * Implements autocomplete on prefixes for a given dictionary of terms and weights.
 * @author William Zhuang
 * Citation: http://stackoverflow.com/questions/
 *           1090969/treeset-to-order-elements-in-descending-order
 * http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
 * TST adapted from http://algs4.cs.princeton.edu/52trie/TST.java.html
 */
public class Autocomplete {
    WeightedTST trie;
    HashMap<String, Double> weightMap;
    TreeMap<String, Double> stringMap;
    /**
     * Initializes required data structures from parallel arrays.
     * @param terms Array of terms.
     * @param weights Array of weights.
     */
    public Autocomplete(String[] terms, double[] weights) {
        if (terms.length != weights.length) {
            throw new IllegalArgumentException("The length of the terms and" 
                                               + "weights arrays are different.");
        }

        trie = new WeightedTST();
        weightMap = new HashMap<String, Double>();
        stringMap = new TreeMap<String, Double>(new ConflictComparator(weightMap));
        for (int i = 0; i < terms.length; i += 1) {
            if (weights[i] < 0) {
                throw new IllegalArgumentException("Negative weight");
            }
            trie.insert(terms[i], weights[i]);
            weightMap.put(terms[i], weights[i]);
            stringMap.put(terms[i], weights[i]);
        }

        if (weightMap.size() != weights.length) {
            throw new IllegalArgumentException("Duplicate terms exist.");
        }
    }

    /** 
      * Specialized reverse order comparator that orders a map according to its values. 
      */
    private class ConflictComparator implements Comparator<String> {
        
        private HashMap<String, Double> origMap;

        /** 
          * Constructor for the comparator. 
          * @param origMap0 Original HashMap to be ordered.
          */ 
        public ConflictComparator(HashMap<String, Double> origMap0) {
            origMap = origMap0;
        }

        /**
          * Modified compare method. Does not return 0 to allow for multiple keys 
          * with equal values to exist in the same map. Note that the order is reversed.
          * @param s1 First string to be compared.
          * @param s2 Second String to be compared.
          * @return int Returns -1 if s1 > s2 and 1 if s1 <= s2.
          */
        public int compare(String s1, String s2) {
            int answer = (int) Math.signum(origMap.get(s2) - origMap.get(s1));
            if (answer == 0) {
                return 1;
            } else {
                return answer;
            }
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
     * @param prefix Ininsert prefix to match against.
     * @return Best (highest weight) matching string in the dictionary.
     */
    public String topMatch(String prefix) {
        LinkedHashSet<String> outinsert = (LinkedHashSet<String>) topMatches(prefix, 1);
        String answer = "";
        for (String x : outinsert) {
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

        LinkedHashSet<String> output = new LinkedHashSet<String>();

        if (prefix.length() == 0) {
            int counter = 0;
            for (String x : stringMap.keySet()) {
                output.add(x);
                counter += 1;
                if (counter == k) {
                    break;
                }
            }
            return output;

        } else {
            return trie.topMatches(prefix, k);
        }
        
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
     * then repeatedly reads autocomplete queries from standard ininsert and prints out the top 
     * k matching terms.
     * @param args takes the name of an ininsert file and an integer k as command-line arguments
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

        // process queries from standard ininsert
        int k = Integer.parseInt(args[1]);
        while (StdIn.hasNextLine()) {
            String prefix = StdIn.readLine();
            for (String term : autocomplete.topMatches(prefix, k)) {
                StdOut.printf("%14.1f  %s\n", autocomplete.weightOf(term), term);
            }
        }
    }

    /** 
      * SpecializedTST that supports insert and traversal to find the maximum valued words
      * with a given prefix.
      */
    private class WeightedTST {
        private Node root; 

        /**
          * A single node in the TST.
          */
        private class Node implements Comparable<Node> {
            private boolean exists;
            private char letter;
            private Node left, mid, right;
            private String word;
            private double weight;
            private double maxWeight;

            /**
              * Comparison method to compare nodes by weight.
              * @param n2 Node to be compared to.
              * @return int Returns 1 if this weight >= n2.weight. Otherwise returns -1.
              */
            public int compareTo(Node n2) {
                if (word.equals(n2.word)) {
                    return 0;
                }

                int answer = (int) Math.signum(weight - n2.weight);
                if (answer == 0) {
                    return 1;
                } else {
                    return answer;
                }
            }
        }

        /** 
          * Constructor for the WeightedTST.
          */
        public WeightedTST() {
            root = new Node();
        }

        /** 
          * Insert method to insert a word with a weight into the Trie.
          * @param key Word to be inserted.
          * @param weight Weight of the word.
          */
        public void insert(String key, double weight) {
            root = insert(root, key, weight, 0);
        }

        /**
          * Recursive insert method to insert the word into the Trie.
          * @param x Current Node
          * @param key Word to be inserted.
          * @param weight Weight of the word.
          * @param d Represents index of letter in the word represented by the current node.
          * @return current Node
          */
        private Node insert(Node x, String key, double weight, int d) {

            char c = key.charAt(d); 
            // If there is no node at this point. 
            if (x == null) {
                x = new Node();
                x.letter = c;
                x.weight = 0;
                x.maxWeight = weight;
            }

            // If the word being added has a weight greater than the max.
            if (x.maxWeight < weight) {
                x.maxWeight = weight;
            }

            // If the word has been fully iterated through. 
            if (c < x.letter) {
                x.left = insert(x.left, key, weight, d);
            } else if (c > x.letter) {
                x.right = insert(x.right, key, weight, d);
            } else if (d < key.length() - 1) {
                x.mid = insert(x.mid, key, weight, d + 1);
            } else {
                x.exists = true;
                x.weight = weight;
                x.word = key;
                return x;
            }

            return x;
        }

        /** 
          * Traverses the Trie to get to the node that represents the end of the 
          * given key.
          * @param x Node to start from.
          * @param key Word to traverse to the end of.
          * @param d Represents index of letter in word represented by current node.
          * @return Node representing end of the key.
          */
        private Node getNode(Node x, String key, int d) {
            if (x == null) {
                return null;
            }

            char c = ' ';
            if (key.substring(d, d).equals(" ")) {
                c = ' ';
            } else {
                c = key.charAt(d);
            }
            
            if (c < x.letter) {
                return getNode(x.left, key, d);
            } else if (c > x.letter) {
                return getNode(x.right, key, d);
            } else if (d < key.length() - 1) {
                return getNode(x.mid, key, d + 1);
            } else {
                return x;
            }
        }

        /**
          * Finds the k most heavily weighted words with the given prefix.
          * @param prefix Prefix from which to get the words.
          * @param k Number of words to return.
          * @return Iterable of the k heaviest weighted words matching the prefix.
          */

        public Iterable<String> topMatches(String prefix, int k) {

            // Make pq maximally oriented.
            PriorityQueue<Node> pq = new PriorityQueue<Node>(3, new WeightComparator());
            TreeSet<Node> bestAnswer = new TreeSet<Node>(new WeightComparator());
            
            Node pointer = root;
            pointer = getNode(root, prefix, 0);

            if (pointer != null) {
                bestAnswer.add(pointer);
            } else {
                return new LinkedHashSet<String>();
            }

            if (pointer.mid != null) {
                bestAnswer.add(pointer.mid); 
            }

            nodeTraversal(pointer.mid, bestAnswer, pq, k);

            LinkedHashSet<String> answers = new LinkedHashSet<String>();


            for (Node x : bestAnswer) {
                if ((x == null) || (x.word == null)) {
                    continue;
                }
                answers.add(x.word);
                if (answers.size() == k) {
                    break;
                }
            }

            return answers;
        }

        /** 
          * Traverses the child nodes selectively by only traversing nodes that may contain 
          * candidates for the top k matches. 
          * @param pointer Current Node
          * @param bestAnswer TreeSet of the bestAnswers so far.
          * @param pq PriorityQueue representing the heaviest nodes that have 
          *           not been added to bestAnswer.
          * @param k Number of top weighted terms to find.
          */
        private void nodeTraversal(Node pointer, TreeSet<Node> bestAnswer, 
                                   PriorityQueue<Node> pq, int k) {
            TreeSet<Node> nodeSet = new TreeSet<Node>(new MaxWeightComparator());
            if (pointer == null) {
                return;
            }

            if (pointer.left != null) {
                nodeSet.add(pointer.left);
            }

            if (pointer.right != null) {
                nodeSet.add(pointer.right);
            }

            if (pointer.mid != null) {
                nodeSet.add(pointer.mid);
            }

            for (Node x : nodeSet) {
                pq.add(x);

                Node lastNode = null;
                if (bestAnswer.size() < k) {
                    if (x.exists) {
                        bestAnswer.add(x);
                        pq.poll();
                    }
                } else {
                    lastNode = (Node) bestAnswer.toArray()[k - 1];
                    if (x.exists) {
                        if (lastNode.weight <= x.weight) {
                            bestAnswer.remove(lastNode);
                            bestAnswer.add(x);
                            pq.poll();
                        }
                    }
                    if (x.maxWeight <= lastNode.weight) {
                        continue;
                    }
                }
                
                nodeTraversal(x, bestAnswer, pq, k);
            }
        }

        /**
          * Reverse order comparator that compares by weight. Does not support
          * equal values based on weight. 
          */
        private class WeightComparator implements Comparator<Node> {
            /**
              * Modified compare method.
              * @param n1 First node to be compared.
              * @param n2 Second node to be compared.
              * @return int Returns -1 if n1 > n2 and 1 if n1 <= n2.
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

        /** 
          * Reverse order comparator that compares by maxWeight. Does not support
          * equal values based on maxWeight.
          */
        private class MaxWeightComparator implements Comparator<Node> {
            /**
              * Modified compare method.
              * @param n1 First node to be compared.
              * @param n2 Second node to be compared.
              * @return int Returns -1 if n1 > n2 and 1 if n1 <= n2.
              */
            public int compare(Node n1, Node n2) {
                int answer = (int) Math.signum(n2.maxWeight - n1.maxWeight); 
                if (answer == 0) {
                    return 1;
                } else {
                    return answer;
                }
            }
        }
    }
}
