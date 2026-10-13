package trie.a_basics;

import java.util.ArrayList;
import java.util.List;

import common.Check;

/*
 * Trie (prefix tree): each edge is a character, each path from the root spells a prefix.
 *
 *   words: car, cat, cart, dog
 *
 *            (root)
 *            /    \
 *           c      d
 *           |      |
 *           a      o
 *          / \     |
 *        r*   t*   g*        (* = end of a word)
 *        |
 *        t*
 *
 * insert / search / startsWith: O(L) for a word of length L - independent of how many words are stored.
 * Hash set lookup is also O(L) (hashing reads every character) but cannot answer prefix questions.
 *
 * Uses: autocomplete, spell check, IP routing (longest prefix match), word games (Boggle).
 * Memory: Node[26] per node is fast but wasteful; a HashMap of children saves space for sparse alphabets.
 */
public class Trie {

    static class Node {
        Node[] children = new Node[26];
        boolean isEndOfWord;
        int prefixCount; // how many words pass through this node
    }

    private final Node root = new Node();

    public void insert(String word) {
        if (search(word)) return; // keep counts correct for duplicates
        Node n = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (n.children[i] == null) n.children[i] = new Node();
            n = n.children[i];
            n.prefixCount++;
        }
        n.isEndOfWord = true;
    }

    public boolean search(String word) {
        Node n = walk(word);
        return n != null && n.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        return walk(prefix) != null;
    }

    public int countWordsWithPrefix(String prefix) {
        Node n = walk(prefix);
        return n == null ? 0 : n.prefixCount;
    }

    public boolean delete(String word) {
        if (!search(word)) return false;
        Node n = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (--n.children[i].prefixCount == 0) { // nobody else uses this branch: cut it off
                n.children[i] = null;
                return true;
            }
            n = n.children[i];
        }
        n.isEndOfWord = false;
        return true;
    }

    public List<String> wordsWithPrefix(String prefix) {
        List<String> out = new ArrayList<>();
        Node n = walk(prefix);
        if (n != null) collect(n, new StringBuilder(prefix), out);
        return out;
    }

    private void collect(Node n, StringBuilder sb, List<String> out) {
        if (n.isEndOfWord) out.add(sb.toString());
        for (int i = 0; i < 26; i++) {
            if (n.children[i] != null) {
                sb.append((char) ('a' + i));
                collect(n.children[i], sb, out);
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }

    private Node walk(String s) {
        Node n = root;
        for (char c : s.toCharArray()) {
            n = n.children[c - 'a'];
            if (n == null) return null;
        }
        return n;
    }

    public static void main(String[] args) {
        Trie t = new Trie();
        for (String w : new String[]{"car", "cat", "cart", "dog"}) t.insert(w);
        Check.isTrue("search car", t.search("car"));
        Check.eq("search ca (prefix only)", t.search("ca"), false);
        Check.isTrue("startsWith ca", t.startsWith("ca"));
        Check.eq("count prefix ca", t.countWordsWithPrefix("ca"), 3);
        Check.eq("autocomplete car", t.wordsWithPrefix("car"), List.of("car", "cart"));
        Check.isTrue("delete car", t.delete("car"));
        Check.eq("car gone", t.search("car"), false);
        Check.isTrue("cart still there", t.search("cart"));
        t.delete("dog");
        Check.eq("dog branch removed", t.startsWith("d"), false);
        Check.eq("count prefix c", t.countWordsWithPrefix("c"), 2);
    }
}
