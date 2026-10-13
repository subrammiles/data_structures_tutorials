package advanced;

import common.Check;

/*
 * Segment tree: answers RANGE queries (min, max, sum, gcd...) on an array that also changes.
 *
 * Each node stores the answer for a range; children split the range in half.
 *
 *   array: [5, 2, 6, 3]           [0..3] min 2
 *                                /            \
 *                         [0..1] min 2     [2..3] min 3
 *                          /     \           /     \
 *                       [0]5   [1]2       [2]6    [3]3
 *
 *   build O(n), point update O(log n), range query O(log n), memory 4n.
 *
 * Compare: a plain array gives O(1) update but O(n) query; a prefix-sum array gives O(1) sum
 * query but O(n) update. The segment tree balances both. For range UPDATES add lazy propagation.
 */
public class SegmentTree {

    private final int[] tree;
    private final int n;

    public SegmentTree(int[] values) {
        n = values.length;
        tree = new int[4 * n];
        build(1, 0, n - 1, values);
    }

    private void build(int node, int l, int r, int[] values) {
        if (l == r) {
            tree[node] = values[l];
            return;
        }
        int mid = (l + r) / 2;
        build(2 * node, l, mid, values);
        build(2 * node + 1, mid + 1, r, values);
        tree[node] = Math.min(tree[2 * node], tree[2 * node + 1]);
    }

    public void update(int index, int value) {
        update(1, 0, n - 1, index, value);
    }

    private void update(int node, int l, int r, int index, int value) {
        if (l == r) {
            tree[node] = value;
            return;
        }
        int mid = (l + r) / 2;
        if (index <= mid) update(2 * node, l, mid, index, value);
        else update(2 * node + 1, mid + 1, r, index, value);
        tree[node] = Math.min(tree[2 * node], tree[2 * node + 1]);
    }

    /* Minimum of values[ql..qr], inclusive. */
    public int rangeMin(int ql, int qr) {
        return query(1, 0, n - 1, ql, qr);
    }

    private int query(int node, int l, int r, int ql, int qr) {
        if (qr < l || r < ql) return Integer.MAX_VALUE; // no overlap
        if (ql <= l && r <= qr) return tree[node];      // full overlap
        int mid = (l + r) / 2;                          // partial overlap: ask both children
        return Math.min(query(2 * node, l, mid, ql, qr), query(2 * node + 1, mid + 1, r, ql, qr));
    }

    public static void main(String[] args) {
        SegmentTree st = new SegmentTree(new int[]{5, 2, 6, 3, 7, 1, 4});
        Check.eq("min all", st.rangeMin(0, 6), 1);
        Check.eq("min 0..3", st.rangeMin(0, 3), 2);
        Check.eq("min 2..4", st.rangeMin(2, 4), 3);
        st.update(3, 0);
        Check.eq("after update min 2..4", st.rangeMin(2, 4), 0);
        Check.eq("single element", st.rangeMin(6, 6), 4);
    }
}
