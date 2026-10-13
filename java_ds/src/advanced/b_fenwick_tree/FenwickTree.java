package advanced.b_fenwick_tree;

import common.Check;

/*
 * Fenwick tree (Binary Indexed Tree): prefix sums + point updates in O(log n), using one array
 * of size n + 1 and two tiny loops. Simpler and lighter than a segment tree when you only need
 * sums (or any invertible operation like XOR).
 *
 * The trick is i & -i = the lowest set bit of i = how many elements tree[i] covers.
 *   tree[4]  (100) covers 4 elements: a[1..4]
 *   tree[6]  (110) covers 2 elements: a[5..6]
 *   tree[7]  (111) covers 1 element : a[7]
 *   prefixSum(7) = tree[7] + tree[6] + tree[4]   (7 -> 6 -> 4 -> 0, removing the low bit each step)
 *   add(5, x) touches tree[5], tree[6], tree[8]  (5 -> 6 -> 8, adding the low bit each step)
 */
public class FenwickTree {

    private final long[] tree;

    public FenwickTree(int n) {
        tree = new long[n + 1]; // index 0 unused: the bit tricks need 1-based indices
    }

    /* a[i] += delta, i is 1-based. */
    public void add(int i, long delta) {
        for (; i < tree.length; i += i & -i) tree[i] += delta;
    }

    /* a[1] + ... + a[i] */
    public long prefixSum(int i) {
        long sum = 0;
        for (; i > 0; i -= i & -i) sum += tree[i];
        return sum;
    }

    public long rangeSum(int l, int r) {
        return prefixSum(r) - prefixSum(l - 1);
    }

    public static void main(String[] args) {
        int[] a = {3, 2, -1, 6, 5, 4, -3, 3};
        FenwickTree ft = new FenwickTree(a.length);
        for (int i = 0; i < a.length; i++) ft.add(i + 1, a[i]);
        Check.eq("prefix 7", ft.prefixSum(7), 16L);
        Check.eq("range 2..5", ft.rangeSum(2, 5), 12L);
        ft.add(3, 10); // a[3] becomes 9
        Check.eq("range 2..5 after add", ft.rangeSum(2, 5), 22L);
        Check.eq("total", ft.prefixSum(8), 29L);
    }
}
