package trees;

import java.util.ArrayList;
import java.util.List;

import common.Check;

/*
 * Binary Search Tree (BST) rule: for every node,
 *   all values in the LEFT subtree  < node.value
 *   all values in the RIGHT subtree > node.value
 * So an inorder traversal visits values in sorted order.
 *
 * search / insert / delete / min / max / floor / ceiling are O(h), h = height.
 *   balanced tree: h = O(log n)
 *   inserting sorted data 1,2,3,4...: the tree becomes a linked list, h = O(n)
 * That worst case is why self-balancing trees exist (AVL - see trees/AVLTree.java, red-black - Java's TreeMap).
 *
 * Delete has three cases:
 *   1. leaf         -> just remove it
 *   2. one child    -> replace the node with its child
 *   3. two children -> copy the inorder successor (min of right subtree) into the node,
 *                      then delete the successor from the right subtree
 */
public class BinarySearchTree {

    static class Node {
        int value;
        Node left, right;
        int size = 1; // nodes in this subtree - enables kth-smallest and rank in O(h)

        Node(int value) {
            this.value = value;
        }
    }

    private Node root;

    public void insert(int value) {
        root = insert(root, value);
    }

    private Node insert(Node n, int value) {
        if (n == null) return new Node(value);
        if (value < n.value) n.left = insert(n.left, value);
        else if (value > n.value) n.right = insert(n.right, value);
        // equal: ignore duplicates
        n.size = 1 + size(n.left) + size(n.right);
        return n;
    }

    public boolean contains(int value) {
        Node n = root;
        while (n != null) {
            if (value == n.value) return true;
            n = value < n.value ? n.left : n.right;
        }
        return false;
    }

    public void delete(int value) {
        root = delete(root, value);
    }

    private Node delete(Node n, int value) {
        if (n == null) return null;
        if (value < n.value) {
            n.left = delete(n.left, value);
        } else if (value > n.value) {
            n.right = delete(n.right, value);
        } else {
            if (n.left == null) return n.right; // cases 1 and 2
            if (n.right == null) return n.left;
            Node successor = n.right; // case 3
            while (successor.left != null) successor = successor.left;
            n.value = successor.value;
            n.right = delete(n.right, successor.value);
        }
        n.size = 1 + size(n.left) + size(n.right);
        return n;
    }

    public int min() {
        Node n = root;
        while (n.left != null) n = n.left;
        return n.value;
    }

    public int max() {
        Node n = root;
        while (n.right != null) n = n.right;
        return n.value;
    }

    /* Largest value <= x, or null. */
    public Integer floor(int x) {
        Integer best = null;
        for (Node n = root; n != null; ) {
            if (n.value == x) return x;
            if (n.value < x) {
                best = n.value;
                n = n.right;
            } else {
                n = n.left;
            }
        }
        return best;
    }

    /* Smallest value >= x, or null. */
    public Integer ceiling(int x) {
        Integer best = null;
        for (Node n = root; n != null; ) {
            if (n.value == x) return x;
            if (n.value > x) {
                best = n.value;
                n = n.left;
            } else {
                n = n.right;
            }
        }
        return best;
    }

    /* k is 1-based. Uses subtree sizes, O(h). */
    public int kthSmallest(int k) {
        Node n = root;
        while (n != null) {
            int leftSize = size(n.left);
            if (k == leftSize + 1) return n.value;
            if (k <= leftSize) {
                n = n.left;
            } else {
                k -= leftSize + 1;
                n = n.right;
            }
        }
        throw new IllegalArgumentException("k out of range");
    }

    public int height() {
        return height(root);
    }

    private int height(Node n) {
        return n == null ? -1 : 1 + Math.max(height(n.left), height(n.right));
    }

    private static int size(Node n) {
        return n == null ? 0 : n.size;
    }

    public List<Integer> inorder() {
        List<Integer> out = new ArrayList<>();
        inorder(root, out);
        return out;
    }

    private void inorder(Node n, List<Integer> out) {
        if (n == null) return;
        inorder(n.left, out);
        out.add(n.value);
        inorder(n.right, out);
    }

    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        for (int v : new int[]{50, 30, 70, 20, 40, 60, 80, 35}) bst.insert(v);
        Check.eq("inorder is sorted", bst.inorder(), List.of(20, 30, 35, 40, 50, 60, 70, 80));
        Check.isTrue("contains 60", bst.contains(60));
        Check.eq("contains 65", bst.contains(65), false);
        Check.eq("min", bst.min(), 20);
        Check.eq("max", bst.max(), 80);
        Check.eq("floor 65", bst.floor(65), 60);
        Check.eq("ceiling 36", bst.ceiling(36), 40);
        Check.eq("floor below min", bst.floor(1), null);
        Check.eq("3rd smallest", bst.kthSmallest(3), 35);

        bst.delete(20); // leaf
        bst.delete(40); // one child (35)
        bst.delete(50); // two children - root replaced by successor 60
        Check.eq("after deletes", bst.inorder(), List.of(30, 35, 60, 70, 80));
        Check.eq("kth after deletes", bst.kthSmallest(3), 60);

        BinarySearchTree skewed = new BinarySearchTree();
        for (int i = 1; i <= 100; i++) skewed.insert(i);
        Check.eq("sorted inserts -> height n-1 (degenerate)", skewed.height(), 99);
    }
}
