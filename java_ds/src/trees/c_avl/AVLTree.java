package trees.c_avl;

import java.util.ArrayList;
import java.util.List;

import common.Check;

/*
 * AVL tree: a BST that keeps itself balanced.
 * Invariant: for every node, |height(left) - height(right)| <= 1  ("balance factor" in -1..1).
 * Result: height <= ~1.44 log2(n), so search/insert/delete are always O(log n).
 *
 * After an insert, walk back up and fix the first unbalanced node with rotations:
 *
 *   Left-Left  (inserted into left child's left)   -> rotate right
 *   Right-Right                                    -> rotate left
 *   Left-Right (inserted into left child's right)  -> rotate left on child, then right on node
 *   Right-Left                                     -> rotate right on child, then left on node
 *
 *   rotateRight(y):        y                x
 *                         / \              / \
 *                        x   C    ==>     A   y
 *                       / \                  / \
 *                      A   B                B   C
 *
 * AVL vs red-black: AVL is more strictly balanced (faster lookups); red-black does fewer
 * rotations on insert/delete (Java TreeMap, C++ std::map, Linux kernel use red-black).
 */
public class AVLTree {

    static class Node {
        int value, height;
        Node left, right;

        Node(int value) {
            this.value = value;
        }
    }

    private Node root;
    int rotations; // counted only to show the tutorial what happened

    private int height(Node n) {
        return n == null ? -1 : n.height;
    }

    private void update(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private int balance(Node n) {
        return height(n.left) - height(n.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        y.left = x.right;
        x.right = y;
        update(y);
        update(x);
        rotations++;
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        y.left = x;
        update(x);
        update(y);
        rotations++;
        return y;
    }

    private Node rebalance(Node n) {
        update(n);
        int b = balance(n);
        if (b > 1) { // left heavy
            if (balance(n.left) < 0) n.left = rotateLeft(n.left); // Left-Right case
            return rotateRight(n);
        }
        if (b < -1) { // right heavy
            if (balance(n.right) > 0) n.right = rotateRight(n.right); // Right-Left case
            return rotateLeft(n);
        }
        return n;
    }

    public void insert(int value) {
        root = insert(root, value);
    }

    private Node insert(Node n, int value) {
        if (n == null) return new Node(value);
        if (value < n.value) n.left = insert(n.left, value);
        else if (value > n.value) n.right = insert(n.right, value);
        else return n;
        return rebalance(n);
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
            if (n.left == null) return n.right;
            if (n.right == null) return n.left;
            Node s = n.right;
            while (s.left != null) s = s.left;
            n.value = s.value;
            n.right = delete(n.right, s.value);
        }
        return rebalance(n);
    }

    public int height() {
        return height(root);
    }

    public boolean isBalanced() {
        return check(root);
    }

    private boolean check(Node n) {
        if (n == null) return true;
        return Math.abs(balance(n)) <= 1 && check(n.left) && check(n.right);
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
        AVLTree t = new AVLTree();
        t.insert(30);
        t.insert(20);
        t.insert(10); // Left-Left -> one right rotation
        Check.eq("LL root becomes 20", t.root.value, 20);
        Check.eq("rotations", t.rotations, 1);

        AVLTree lr = new AVLTree();
        lr.insert(30);
        lr.insert(10);
        lr.insert(20); // Left-Right -> two rotations
        Check.eq("LR root becomes 20", lr.root.value, 20);
        Check.eq("LR rotations", lr.rotations, 2);

        AVLTree big = new AVLTree();
        for (int i = 1; i <= 1000; i++) big.insert(i); // sorted input: a plain BST would have height 999
        Check.isTrue("height stays logarithmic (" + big.height() + ")", big.height() <= 14);
        Check.isTrue("balanced", big.isBalanced());
        for (int i = 1; i <= 1000; i += 2) big.delete(i);
        Check.isTrue("balanced after deletes", big.isBalanced());
        Check.eq("first values left", big.inorder().subList(0, 3), List.of(2, 4, 6));
    }
}
