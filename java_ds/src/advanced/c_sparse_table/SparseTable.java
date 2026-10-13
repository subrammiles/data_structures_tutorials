package advanced.c_sparse_table;

import common.Check;

/*
 * Sparse table: O(1) range minimum queries on a STATIC array (no updates), after O(n log n) build.
 *
 * table[k][i] = min of the 2^k elements starting at i.
 *   table[k][i] = min(table[k-1][i], table[k-1][i + 2^(k-1)])
 *
 * Query [l, r]: let k = floor(log2(length)). Two blocks of size 2^k, one starting at l and one
 * ending at r, cover the whole range (they may overlap - fine for min/max/gcd, NOT for sum).
 */
public class SparseTable {

    private final int[][] table;
    private final int[] log;

    public SparseTable(int[] a) {
        int n = a.length;
        log = new int[n + 1];
        for (int i = 2; i <= n; i++) log[i] = log[i / 2] + 1;
        table = new int[log[n] + 1][];
        table[0] = a.clone();
        for (int k = 1; k < table.length; k++) {
            int len = 1 << k;
            table[k] = new int[n - len + 1];
            for (int i = 0; i + len <= n; i++) {
                table[k][i] = Math.min(table[k - 1][i], table[k - 1][i + len / 2]);
            }
        }
    }

    public int rangeMin(int l, int r) {
        int k = log[r - l + 1];
        return Math.min(table[k][l], table[k][r - (1 << k) + 1]);
    }

    public static void main(String[] args) {
        SparseTable st = new SparseTable(new int[]{7, 2, 3, 0, 5, 10, 3, 12, 18});
        Check.eq("min 0..2", st.rangeMin(0, 2), 2);
        Check.eq("min 4..7", st.rangeMin(4, 7), 3);
        Check.eq("min 0..8", st.rangeMin(0, 8), 0);
        Check.eq("min 7..8", st.rangeMin(7, 8), 12);
    }
}
