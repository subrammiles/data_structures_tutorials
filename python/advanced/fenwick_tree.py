"""Fenwick tree (Binary Indexed Tree): prefix sums + point updates in O(log n).

i & -i is the lowest set bit of i = how many elements tree[i] covers.
prefix_sum(7) = tree[7] + tree[6] + tree[4]    (7 -> 6 -> 4 -> 0, drop the low bit)
add(5, x) touches tree[5], tree[6], tree[8]      (5 -> 6 -> 8, add the low bit)
"""


class FenwickTree:
    def __init__(self, n):
        self.tree = [0] * (n + 1)  # 1-based

    def add(self, i, delta):
        while i < len(self.tree):
            self.tree[i] += delta
            i += i & -i

    def prefix_sum(self, i):
        s = 0
        while i > 0:
            s += self.tree[i]
            i -= i & -i
        return s

    def range_sum(self, l, r):
        return self.prefix_sum(r) - self.prefix_sum(l - 1)


if __name__ == "__main__":
    a = [3, 2, -1, 6, 5, 4, -3, 3]
    ft = FenwickTree(len(a))
    for i, v in enumerate(a, start=1):
        ft.add(i, v)
    assert ft.prefix_sum(7) == 16
    assert ft.range_sum(2, 5) == 12
    ft.add(3, 10)
    assert ft.range_sum(2, 5) == 22
    assert ft.prefix_sum(8) == 29
    print("all checks passed")
