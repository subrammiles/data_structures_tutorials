"""Binary tree basics and traversals (all O(n)).

            1
          /   \
         2     3
        / \     \
       4   5     6

Preorder  (root, left, right): 1 2 4 5 3 6   - copy / serialise
Inorder   (left, root, right): 4 2 5 1 3 6   - sorted order for a BST
Postorder (left, right, root): 4 5 2 6 3 1   - delete / evaluate
Level order (BFS):             1 2 3 4 5 6
Recursive versions use O(h) call stack; iterative versions use an explicit stack.
"""
from collections import deque


class Node:
    def __init__(self, value, left=None, right=None):
        self.value, self.left, self.right = value, left, right


def preorder(n):
    return [n.value] + preorder(n.left) + preorder(n.right) if n else []


def inorder(n):
    return inorder(n.left) + [n.value] + inorder(n.right) if n else []


def postorder(n):
    return postorder(n.left) + postorder(n.right) + [n.value] if n else []


def level_order(root):
    levels, queue = [], deque([root] if root else [])
    while queue:
        level = []
        for _ in range(len(queue)):  # everything in the queue now is one level
            n = queue.popleft()
            level.append(n.value)
            if n.left:
                queue.append(n.left)
            if n.right:
                queue.append(n.right)
        levels.append(level)
    return levels


def preorder_iterative(root):
    out, stack = [], [root] if root else []
    while stack:
        n = stack.pop()
        out.append(n.value)
        if n.right:
            stack.append(n.right)  # right first so left is processed first
        if n.left:
            stack.append(n.left)
    return out


def inorder_iterative(root):
    out, stack, cur = [], [], root
    while cur or stack:
        while cur:  # go as far left as possible
            stack.append(cur)
            cur = cur.left
        cur = stack.pop()
        out.append(cur.value)
        cur = cur.right
    return out


def postorder_iterative(root):
    """Postorder is the reverse of (root, right, left)."""
    out, stack = [], [root] if root else []
    while stack:
        n = stack.pop()
        out.append(n.value)
        if n.left:
            stack.append(n.left)
        if n.right:
            stack.append(n.right)
    return out[::-1]


def height(n):
    return -1 if n is None else 1 + max(height(n.left), height(n.right))


def count_nodes(n):
    return 0 if n is None else 1 + count_nodes(n.left) + count_nodes(n.right)


def count_leaves(n):
    if n is None:
        return 0
    if n.left is None and n.right is None:
        return 1
    return count_leaves(n.left) + count_leaves(n.right)


def is_balanced(n):
    """Single O(n) pass: return -2 as soon as any subtree is unbalanced."""
    def checked_height(node):
        if node is None:
            return -1
        l, r = checked_height(node.left), checked_height(node.right)
        if l == -2 or r == -2 or abs(l - r) > 1:
            return -2
        return 1 + max(l, r)
    return checked_height(n) != -2


def mirror(n):
    return Node(n.value, mirror(n.right), mirror(n.left)) if n else None


if __name__ == "__main__":
    root = Node(1, Node(2, Node(4), Node(5)), Node(3, None, Node(6)))
    assert preorder(root) == [1, 2, 4, 5, 3, 6]
    assert inorder(root) == [4, 2, 5, 1, 3, 6]
    assert postorder(root) == [4, 5, 2, 6, 3, 1]
    assert level_order(root) == [[1], [2, 3], [4, 5, 6]]
    assert preorder_iterative(root) == preorder(root)
    assert inorder_iterative(root) == inorder(root)
    assert postorder_iterative(root) == postorder(root)
    assert height(root) == 2
    assert count_nodes(root) == 6 and count_leaves(root) == 3
    assert is_balanced(root)
    assert not is_balanced(Node(1, Node(2, Node(3))))
    assert inorder(mirror(root)) == [6, 3, 1, 5, 2, 4]
    print("all checks passed")
