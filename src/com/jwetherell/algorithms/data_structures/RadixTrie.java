package com.jwetherell.algorithms.data_structures;

/**
 * A radix trie or radix tree is a space-optimized trie data structure where
 * each node with only one child is merged with its child. The result is that
 * every internal node has at least two children. Unlike in regular tries, edges
 * can be labeled with sequences of characters as well as single characters.
 * This makes them much more efficient for small sets (especially if the strings
 * are long) and for sets of strings that share long prefixes. This particular
 * radix tree is used to represent an associative array.
 * 
 * http://en.wikipedia.org/wiki/Radix_tree
 * http://en.wikipedia.org/wiki/Associative_array
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class RadixTrie<K extends CharSequence, V> extends PatriciaTrie<K> {


    /**
     * Default constructor.
     */
    public RadixTrie() {
        super();
    }

    /**
     * Put the key/value pair in the trie.
     * 
     * @param key to represent the value.
     * @param value to store in the key.
     * @return True is added to the trie or false if it already exists.
     */
    @SuppressWarnings("unchecked")
    public boolean put(K key, V value) {
        Node node = this.addNode(key);
        if (node == null) return false;

        if (node instanceof RadixNode) {
            RadixNode<V> radix = (RadixNode<V>) node;
            radix.value = value;
        } else {
            // Really shouldn't get here
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node createNewNode(Node parent, char[] seq, boolean type) {
        return (new RadixNode<V>(parent, seq, type));
    }

    /**
     * Get the value stored with the key.
     * 
     * @param key to get value for.
     * @return value stored at key.
     */
    @SuppressWarnings("unchecked")
    public V get(K key) {
        Node k = this.getNode(key);
        if (k instanceof RadixNode) {
            RadixNode<V> r = (RadixNode<V>) k;
            return r.value;
        }
        return null;
    }

    /**
     * WARNING: This is an unsupported operation.
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean add(K key) {
        // This should not be used
        throw new RuntimeException("This method is not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return RadixTreePrinter.getString(this);
    }


    protected static final class RadixNode<V> extends Node implements Comparable<Node> {

        protected V value = null;


        protected RadixNode(Node node, V value) {
            super(node.parent, node.string, node.type);
            this.value = value;
            for (int i=0; i<node.getChildrenSize(); i++) {
                Node c = node.getChild(i);
                this.addChild(c);
            }
        }

        protected RadixNode(Node parent, char[] string, boolean type) {
            super(parent, string, type);
        }

        protected RadixNode(Node parent, char[] string, boolean type, V value) {
            super(parent, string, type);
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("string = ").append(string).append("\n");
            builder.append("type = ").append(type).append("\n");
            return builder.toString();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Node node) {
            if (node == null) return -1;

            int length = string.length;
            if (node.string.length < length) length = node.string.length;
            for (int i = 0; i < length; i++) {
                Character a = string[i];
                Character b = node.string[i];
                int c = a.compareTo(b);
                if (c != 0) return c;
            }

            if (this.type==BLACK && node.type==WHITE) return -1;
            else if (node.type==BLACK && this.type==WHITE) return 1;

            if (this.getChildrenSize() < node.getChildrenSize()) return -1;
            else if (this.getChildrenSize() > node.getChildrenSize()) return 1;

            return 0;
        }
    }

    protected static class RadixTreePrinter<K extends CharSequence, V> {

        public static <C extends CharSequence, V> String getString(RadixTrie<C, V> tree) {
            return getString(tree.root, "", null, true);
        }

        @SuppressWarnings("unchecked")
        protected static <V> String getString(Node node, String prefix, String previousString, boolean isTail) {
            StringBuilder builder = new StringBuilder();
            String string = null;
            if (node.string!=null) {
                String temp = String.valueOf(node.string);
                if (previousString!=null) string = previousString + temp;
                else string = temp;
            }
            if (node instanceof RadixNode) {
                RadixNode<V> radix = (RadixNode<V>) node;
                builder.append(prefix + (isTail ? "└── " : "├── ") + 
                    ((radix.string != null) ? 
                        "(" + String.valueOf(radix.string) + ") " + "[" + ((node.type==WHITE)?"WHITE":"BLACK") + "] " + string + 
                            ((radix.value!=null)?
                                " = " + radix.value
                            :
                                "")
                    : 
                        "[" + node.type + "]") + 
                "\n");
            }
            if (node.getChildrenSize()>0) {
                for (int i = 0; i < node.getChildrenSize() - 1; i++) {
                    builder.append(getString(node.getChild(i), prefix + (isTail ? "    " : "│   "), string, false));
                }
                if (node.getChildrenSize() >= 1) {
                    builder.append(getString(node.getChild(node.getChildrenSize() - 1), prefix + (isTail ? "    " : "│   "), string, true));
                }
            }
            return builder.toString();
        }
    }
}