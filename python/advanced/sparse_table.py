"""Sparse table: O(1) range-minimum queries on a static array after O(n log n) preprocessing.

table[k][i] = min of the 2^k elements starting at i.
Query [l, r]: two overlapping blocks of size 2^k cover the range (fine for min/max/gcd, not sum).
"""


class SparseTable:
    def __init__(self, a):
        n = len(a)
        self.log = [0] * (n + 1)
        for i in range(2, n + 1):
            self.log[i] = self.log[i // 2] + 1
        self.table = [list(a)]
        k = 1
        while (1 << k) <= n:
            prev, half = self.table[-1], 1 << (k - 1)
            self.table.append([min(prev[i], prev[i + half]) for i in range(n - (1 << k) + 1)])
            k += 1

    def range_min(self, l, r):
        k = self.log[r - l + 1]
        return min(self.table[k][l], self.table[k][r - (1 << k) + 1])


if __name__ == "__main__":
    st = SparseTable([7, 2, 3, 0, 5, 10, 3, 12, 18])
    assert st.range_min(0, 2) == 2
    assert st.range_min(4, 7) == 3
    assert st.range_min(0, 8) == 0
    assert st.range_min(7, 8) == 12
    print("all checks passed")
