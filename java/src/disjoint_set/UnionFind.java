package disjoint_set;

import java.util.Arrays;

import common.Check;

/*
 * Disjoint Set Union (Union-Find): keeps track of elements split into groups.
 *   find(x)     -> representative (root) of x's group
 *   union(a, b) -> merge the two groups
 *   connected(a, b) -> find(a) == find(b)
 *
 * Each group is a tree stored in parent[]. Two optimisations make it nearly O(1):
 *   1. Union by rank/size: attach the smaller tree under the larger one (keeps trees shallow).
 *   2. Path compression: during find, point every visited node directly at the root.
 * Together: O(alpha(n)) amortised per operation, alpha = inverse Ackermann (< 5 for any real n).
 *
 * Uses: Kruskal's MST, connected components, cycle detection in undirected graphs,
 * network connectivity, image segmentation, accounts merge.
 */
public class UnionFind {

    private final int[] parent, size;
    private int groups;

    public UnionFind(int n) {
        parent = new int[n];
        size = new int[n];
        groups = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i; // every element starts as its own group
            size[i] = 1;
        }
    }

    public int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // path compression
        return parent[x];
    }

    public boolean union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) return false; // already in the same group
        if (size[ra] < size[rb]) { // union by size
            int t = ra;
            ra = rb;
            rb = t;
        }
        parent[rb] = ra;
        size[ra] += size[rb];
        groups--;
        return true;
    }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    public int groups() {
        return groups;
    }

    public int groupSize(int x) {
        return size[find(x)];
    }

    /* Kruskal: sort edges by weight, add an edge if it joins two different groups. O(E log E). */
    static int kruskalMstWeight(int n, int[][] edges) {
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, (x, y) -> Integer.compare(x[2], y[2]));
        UnionFind uf = new UnionFind(n);
        int total = 0, used = 0;
        for (int[] e : sorted) {
            if (uf.union(e[0], e[1])) {
                total += e[2];
                if (++used == n - 1) break;
            }
        }
        return used == n - 1 ? total : -1;
    }

    public static void main(String[] args) {
        UnionFind uf = new UnionFind(6);
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(3, 4);
        Check.isTrue("0~2", uf.connected(0, 2));
        Check.eq("0~3", uf.connected(0, 3), false);
        Check.eq("groups", uf.groups(), 3); // {0,1,2} {3,4} {5}
        Check.eq("size of 0's group", uf.groupSize(0), 3);
        Check.eq("union same group", uf.union(0, 2), false); // this edge would form a cycle

        int[][] cables = {{0, 1, 2}, {0, 3, 6}, {1, 2, 3}, {1, 3, 8}, {1, 4, 5}, {2, 4, 7}, {3, 4, 9}};
        Check.eq("kruskal mst (same as prim)", kruskalMstWeight(5, cables), 16);
    }
}
