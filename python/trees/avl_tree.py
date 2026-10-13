"""AVL tree: a self-balancing BST. For every node |height(left) - height(right)| <= 1,
so height stays ~1.44 log2(n) and every operation is O(log n).

Fix the first unbalanced node on the way back up with rotations:
  Left-Left -> rotate right         Right-Right -> rotate left
  Left-Right -> rotate child left, then node right
  Right-Left -> rotate child right, then node left

  rotate_right(y):     y              x
                      / \            / \
                     x   C   ==>    A   y
                    / \                / \
                   A   B              B   C
"""


class Node:
    def __init__(self, value):
        self.value = value
        self.left = self.right = None
        self.height = 0


def _h(n):
    return n.height if n else -1


def _update(n):
    n.height = 1 + max(_h(n.left), _h(n.right))


def _balance(n):
    return _h(n.left) - _h(n.right)


class AVLTree:
    def __init__(self):
        self.root = None
        self.rotations = 0  # only for the tutorial output

    def _rotate_right(self, y):
        x = y.left
        y.left, x.right = x.right, y
        _update(y)
        _update(x)
        self.rotations += 1
        return x

    def _rotate_left(self, x):
        y = x.right
        x.right, y.left = y.left, x
        _update(x)
        _update(y)
        self.rotations += 1
        return y

    def _rebalance(self, n):
        _update(n)
        b = _balance(n)
        if b > 1:  # left heavy
            if _balance(n.left) < 0:
                n.left = self._rotate_left(n.left)  # Left-Right
            return self._rotate_right(n)
        if b < -1:  # right heavy
            if _balance(n.right) > 0:
                n.right = self._rotate_right(n.right)  # Right-Left
            return self._rotate_left(n)
        return n

    def insert(self, value):
        def _insert(n):
            if n is None:
                return Node(value)
            if value < n.value:
                n.left = _insert(n.left)
            elif value > n.value:
                n.right = _insert(n.right)
            else:
                return n
            return self._rebalance(n)
        self.root = _insert(self.root)

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
                s = n.right
                while s.left:
                    s = s.left
                n.value = s.value
                n.right = _delete(n.right, s.value)
            return self._rebalance(n)
        self.root = _delete(self.root, value)

    def height(self):
        return _h(self.root)

    def is_balanced(self):
        def check(n):
            return n is None or (abs(_balance(n)) <= 1 and check(n.left) and check(n.right))
        return check(self.root)

    def inorder(self):
        def walk(n):
            return walk(n.left) + [n.value] + walk(n.right) if n else []
        return walk(self.root)


if __name__ == "__main__":
    t = AVLTree()
    for v in (30, 20, 10):  # Left-Left
        t.insert(v)
    assert t.root.value == 20 and t.rotations == 1

    lr = AVLTree()
    for v in (30, 10, 20):  # Left-Right
        lr.insert(v)
    assert lr.root.value == 20 and lr.rotations == 2

    big = AVLTree()
    for i in range(1, 1001):  # sorted input; a plain BST would have height 999
        big.insert(i)
    assert big.height() <= 14 and big.is_balanced()
    for i in range(1, 1001, 2):
        big.delete(i)
    assert big.is_balanced()
    assert big.inorder()[:3] == [2, 4, 6]
    print("all checks passed")
