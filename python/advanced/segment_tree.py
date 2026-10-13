"""Segment tree for range-minimum queries with point updates.

Each node stores the min of a range; children split the range in half.
build O(n), update O(log n), query O(log n), memory 4n.
Plain array: O(1) update, O(n) query. Prefix sums: O(1) query, O(n) update. This balances both.
"""


class SegmentTree:
    def __init__(self, values):
        self.n = len(values)
        self.tree = [0] * (4 * self.n)
        self._build(1, 0, self.n - 1, values)

    def _build(self, node, l, r, values):
        if l == r:
            self.tree[node] = values[l]
            return
        mid = (l + r) // 2
        self._build(2 * node, l, mid, values)
        self._build(2 * node + 1, mid + 1, r, values)
        self.tree[node] = min(self.tree[2 * node], self.tree[2 * node + 1])

    def update(self, index, value, node=1, l=0, r=None):
        r = self.n - 1 if r is None else r
        if l == r:
            self.tree[node] = value
            return
        mid = (l + r) // 2
        if index <= mid:
            self.update(index, value, 2 * node, l, mid)
        else:
            self.update(index, value, 2 * node + 1, mid + 1, r)
        self.tree[node] = min(self.tree[2 * node], self.tree[2 * node + 1])

    def range_min(self, ql, qr, node=1, l=0, r=None):
        r = self.n - 1 if r is None else r
        if qr < l or r < ql:
            return float("inf")  # no overlap
        if ql <= l and r <= qr:
            return self.tree[node]  # full overlap
        mid = (l + r) // 2
        return min(self.range_min(ql, qr, 2 * node, l, mid),
                   self.range_min(ql, qr, 2 * node + 1, mid + 1, r))


if __name__ == "__main__":
    st = SegmentTree([5, 2, 6, 3, 7, 1, 4])
    assert st.range_min(0, 6) == 1
    assert st.range_min(0, 3) == 2
    assert st.range_min(2, 4) == 3
    st.update(3, 0)
    assert st.range_min(2, 4) == 0
    assert st.range_min(6, 6) == 4
    print("all checks passed")
