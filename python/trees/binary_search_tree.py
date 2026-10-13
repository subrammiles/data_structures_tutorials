"""Binary search tree: left subtree < node < right subtree, so inorder = sorted.

search / insert / delete / min / max / floor / ceiling: O(h).
Balanced: h = O(log n). Sorted inserts degrade it to a linked list: h = O(n) -> see avl_tree.py.

Delete cases: 1) leaf -> remove  2) one child -> replace with child
              3) two children -> copy the inorder successor (min of right subtree), delete it there.
Each node stores its subtree size so kth_smallest is O(h).
"""


class Node:
    def __init__(self, value):
        self.value = value
        self.left = self.right = None
        self.size = 1


def _size(n):
    return n.size if n else 0


class BinarySearchTree:
    def __init__(self):
        self.root = None

    def insert(self, value):
        def _insert(n):
            if n is None:
                return Node(value)
            if value < n.value:
                n.left = _insert(n.left)
            elif value > n.value:
                n.right = _insert(n.right)
            n.size = 1 + _size(n.left) + _size(n.right)
            return n
        self.root = _insert(self.root)

    def contains(self, value):
        n = self.root
        while n:
            if value == n.value:
                return True
            n = n.left if value < n.value else n.right
        return False

    def delete(self, value):
        def _delete(n, value):
            if n is None:
                return None
            if value < n.value:
                n.left = _delete(n.left, value)
            elif value > n.value:
                n.right = _delete(n.right, value)
            else:
                if n.left is None:
                    return n.right
                if n.right is None:
                    return n.left
                successor = n.right
                while successor.left:
                    successor = successor.left
                n.value = successor.value
                n.right = _delete(n.right, successor.value)
            n.size = 1 + _size(n.left) + _size(n.right)
            return n
        self.root = _delete(self.root, value)

    def min(self):
        n = self.root
        while n.left:
            n = n.left
        return n.value

    def max(self):
        n = self.root
        while n.right:
            n = n.right
        return n.value

    def floor(self, x):
        """Largest value <= x, or None."""
        best, n = None, self.root
        while n:
            if n.value == x:
                return x
            if n.value < x:
                best, n = n.value, n.right
            else:
                n = n.left
        return best

    def ceiling(self, x):
        """Smallest value >= x, or None."""
        best, n = None, self.root
        while n:
            if n.value == x:
                return x
            if n.value > x:
                best, n = n.value, n.left
            else:
                n = n.right
        return best

    def kth_smallest(self, k):
        n = self.root
        while n:
            left = _size(n.left)
            if k == left + 1:
                return n.value
            if k <= left:
                n = n.left
            else:
                k -= left + 1
                n = n.right
        raise ValueError("k out of range")

    def height(self):
        def h(n):
            return -1 if n is None else 1 + max(h(n.left), h(n.right))
        return h(self.root)

    def inorder(self):
        out, stack, cur = [], [], self.root
        while cur or stack:
            while cur:
                stack.append(cur)
                cur = cur.left
            cur = stack.pop()
            out.append(cur.value)
            cur = cur.right
        return out


if __name__ == "__main__":
    bst = BinarySearchTree()
    for v in (50, 30, 70, 20, 40, 60, 80, 35):
        bst.insert(v)
    assert bst.inorder() == [20, 30, 35, 40, 50, 60, 70, 80]
    assert bst.contains(60) and not bst.contains(65)
    assert bst.min() == 20 and bst.max() == 80
    assert bst.floor(65) == 60 and bst.ceiling(36) == 40 and bst.floor(1) is None
    assert bst.kth_smallest(3) == 35
    bst.delete(20)  # leaf
    bst.delete(40)  # one child
    bst.delete(50)  # two children
    assert bst.inorder() == [30, 35, 60, 70, 80]
    assert bst.kth_smallest(3) == 60

    skewed = BinarySearchTree()
    for i in range(1, 101):
        skewed.insert(i)
    assert skewed.height() == 99  # degenerate: sorted input
    print("all checks passed")
