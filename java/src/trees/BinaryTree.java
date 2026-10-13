package trees;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import common.Check;

/*
 * Binary tree: each node has at most two children (left, right).
 *
 *            1
 *          /   \
 *         2     3
 *        / \     \
 *       4   5     6
 *
 * Vocabulary: root (1), leaf (4, 5, 6), height = edges on the longest root-to-leaf path (2),
 * depth of a node = edges from the root, full / complete / perfect / balanced trees.
 *
 * Traversals (all O(n)):
 *   Preorder  (root, left, right) : 1 2 4 5 3 6  - copy/serialise a tree
 *   Inorder   (left, root, right) : 4 2 5 1 3 6  - sorted order for a BST
 *   Postorder (left, right, root) : 4 5 2 6 3 1  - delete a tree, evaluate expressions
 *   Level order (BFS by depth)    : 1 2 3 4 5 6  - shortest path in levels, tree views
 * Recursive DFS uses O(h) stack space; each traversal is also shown iteratively.
 */
public class BinaryTree {

    public static class Node {
        public int value;
        public Node left, right;

        public Node(int value) {
            this.value = value;
        }

        public Node(int value, Node left, Node right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }
    }

    static void preorder(Node n, List<Integer> out) {
        if (n == null) return;
        out.add(n.value);
        preorder(n.left, out);
        preorder(n.right, out);
    }

    static void inorder(Node n, List<Integer> out) {
        if (n == null) return;
        inorder(n.left, out);
        out.add(n.value);
        inorder(n.right, out);
    }

    static void postorder(Node n, List<Integer> out) {
        if (n == null) return;
        postorder(n.left, out);
        postorder(n.right, out);
        out.add(n.value);
    }

    static List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> levels = new ArrayList<>();
        if (root == null) return levels;
        Deque<Node> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            List<Integer> level = new ArrayList<>();
            for (int size = queue.size(); size > 0; size--) { // everything in the queue now is one level
                Node n = queue.poll();
                level.add(n.value);
                if (n.left != null) queue.offer(n.left);
                if (n.right != null) queue.offer(n.right);
            }
            levels.add(level);
        }
        return levels;
    }

    static List<Integer> preorderIterative(Node root) {
        List<Integer> out = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        if (root != null) stack.push(root);
        while (!stack.isEmpty()) {
            Node n = stack.pop();
            out.add(n.value);
            if (n.right != null) stack.push(n.right); // push right first so left is processed first
            if (n.left != null) stack.push(n.left);
        }
        return out;
    }

    static List<Integer> inorderIterative(Node root) {
        List<Integer> out = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        Node cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) { // go as far left as possible
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            out.add(cur.value);
            cur = cur.right;
        }
        return out;
    }

    /* Postorder = reverse of (root, right, left). */
    static List<Integer> postorderIterative(Node root) {
        List<Integer> out = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        if (root != null) stack.push(root);
        while (!stack.isEmpty()) {
            Node n = stack.pop();
            out.add(0, n.value);
            if (n.left != null) stack.push(n.left);
            if (n.right != null) stack.push(n.right);
        }
        return out;
    }

    static int height(Node n) {
        return n == null ? -1 : 1 + Math.max(height(n.left), height(n.right));
    }

    static int countNodes(Node n) {
        return n == null ? 0 : 1 + countNodes(n.left) + countNodes(n.right);
    }

    static int countLeaves(Node n) {
        if (n == null) return 0;
        if (n.left == null && n.right == null) return 1;
        return countLeaves(n.left) + countLeaves(n.right);
    }

    /* Balanced = for every node, heights of left and right differ by at most 1. O(n) single pass. */
    static boolean isBalanced(Node n) {
        return checkedHeight(n) != Integer.MIN_VALUE;
    }

    private static int checkedHeight(Node n) {
        if (n == null) return -1;
        int l = checkedHeight(n.left), r = checkedHeight(n.right);
        if (l == Integer.MIN_VALUE || r == Integer.MIN_VALUE || Math.abs(l - r) > 1) return Integer.MIN_VALUE;
        return 1 + Math.max(l, r);
    }

    static Node mirror(Node n) {
        if (n == null) return null;
        return new Node(n.value, mirror(n.right), mirror(n.left));
    }

    public static void main(String[] args) {
        Node root = new Node(1,
                new Node(2, new Node(4), new Node(5)),
                new Node(3, null, new Node(6)));
        List<Integer> out = new ArrayList<>();
        preorder(root, out);
        Check.eq("preorder", out, List.of(1, 2, 4, 5, 3, 6));
        out.clear();
        inorder(root, out);
        Check.eq("inorder", out, List.of(4, 2, 5, 1, 3, 6));
        out.clear();
        postorder(root, out);
        Check.eq("postorder", out, List.of(4, 5, 2, 6, 3, 1));
        Check.eq("level order", levelOrder(root), List.of(List.of(1), List.of(2, 3), List.of(4, 5, 6)));
        Check.eq("preorder iterative", preorderIterative(root), List.of(1, 2, 4, 5, 3, 6));
        Check.eq("inorder iterative", inorderIterative(root), List.of(4, 2, 5, 1, 3, 6));
        Check.eq("postorder iterative", postorderIterative(root), List.of(4, 5, 2, 6, 3, 1));
        Check.eq("height", height(root), 2);
        Check.eq("nodes", countNodes(root), 6);
        Check.eq("leaves", countLeaves(root), 3);
        Check.isTrue("balanced", isBalanced(root));
        Check.eq("unbalanced", isBalanced(new Node(1, new Node(2, new Node(3), null), null)), false);
        Check.eq("mirror inorder", inorderIterative(mirror(root)), List.of(6, 3, 1, 5, 2, 4));
    }
}
