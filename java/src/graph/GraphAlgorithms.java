package graph.b_algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;

import common.Check;

/*
 * Core graph algorithms on weighted / directed graphs. Edges are int[]{from, to, weight}.
 *
 * Topological sort (DAG only): order vertices so every edge goes forward. Kahn's BFS, O(V + E).
 * Dijkstra: shortest paths, NON-NEGATIVE weights. Min-heap, O(E log V).
 * Bellman-Ford: handles negative weights, detects negative cycles. O(V * E).
 * Prim: minimum spanning tree, grows one tree with a heap. O(E log V).
 * Kruskal: MST by sorting edges + union-find - see disjoint_set/a_basics/UnionFind.java.
 * Floyd-Warshall: all pairs shortest paths, O(V^3), fine for V up to a few hundred.
 */
public class GraphAlgorithms {

    static final int INF = Integer.MAX_VALUE / 2;

    static List<List<int[]>> adjacency(int n, int[][] edges, boolean directed) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e.length > 2 ? e[2] : 1});
            if (!directed) adj.get(e[1]).add(new int[]{e[0], e.length > 2 ? e[2] : 1});
        }
        return adj;
    }

    /* Returns null if the graph has a cycle. */
    static List<Integer> topologicalSort(int n, int[][] edges) {
        List<List<int[]>> adj = adjacency(n, edges, true);
        int[] indegree = new int[n];
        for (int[] e : edges) indegree[e[1]]++;
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indegree[i] == 0) queue.offer(i);
        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (int[] e : adj.get(u)) if (--indegree[e[0]] == 0) queue.offer(e[0]);
        }
        return order.size() == n ? order : null;
    }

    static int[] dijkstra(int n, int[][] edges, int source) {
        List<List<int[]>> adj = adjacency(n, edges, true);
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[source] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.offer(new int[]{source, 0});
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int u = top[0];
            if (top[1] > dist[u]) continue; // outdated entry: a shorter path was already found
            for (int[] e : adj.get(u)) {
                int v = e[0], nd = dist[u] + e[1];
                if (nd < dist[v]) {
                    dist[v] = nd;
                    pq.offer(new int[]{v, nd});
                }
            }
        }
        return dist;
    }

    /* Returns null if a negative cycle is reachable. */
    static int[] bellmanFord(int n, int[][] edges, int source) {
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[source] = 0;
        for (int round = 0; round < n - 1; round++) { // a shortest path has at most n-1 edges
            for (int[] e : edges) {
                if (dist[e[0]] != INF && dist[e[0]] + e[2] < dist[e[1]]) dist[e[1]] = dist[e[0]] + e[2];
            }
        }
        for (int[] e : edges) { // one more improvement means a negative cycle
            if (dist[e[0]] != INF && dist[e[0]] + e[2] < dist[e[1]]) return null;
        }
        return dist;
    }

    static int primMstWeight(int n, int[][] edges) {
        List<List<int[]>> adj = adjacency(n, edges, false);
        boolean[] inTree = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.offer(new int[]{0, 0});
        int total = 0, added = 0;
        while (!pq.isEmpty() && added < n) {
            int[] top = pq.poll();
            if (inTree[top[0]]) continue;
            inTree[top[0]] = true;
            total += top[1];
            added++;
            for (int[] e : adj.get(top[0])) if (!inTree[e[0]]) pq.offer(new int[]{e[0], e[1]});
        }
        return added == n ? total : -1; // -1: graph not connected
    }

    static int[][] floydWarshall(int n, int[][] edges) {
        int[][] d = new int[n][n];
        for (int[] row : d) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) d[i][i] = 0;
        for (int[] e : edges) d[e[0]][e[1]] = Math.min(d[e[0]][e[1]], e[2]);
        for (int k = 0; k < n; k++)          // allow vertex k as an intermediate stop
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (d[i][k] + d[k][j] < d[i][j]) d[i][j] = d[i][k] + d[k][j];
        return d;
    }

    public static void main(String[] args) {
        // Getting dressed: 0 underwear, 1 trousers, 2 shoes, 3 socks, 4 shirt, 5 belt
        int[][] dressing = {{0, 1}, {1, 2}, {3, 2}, {1, 5}, {4, 5}};
        Check.eq("topological sort", topologicalSort(6, dressing), List.of(0, 3, 4, 1, 2, 5));
        Check.eq("topo cycle", topologicalSort(2, new int[][]{{0, 1}, {1, 0}}), null);

        int[][] roads = {{0, 1, 4}, {0, 2, 1}, {2, 1, 2}, {1, 3, 1}, {2, 3, 5}};
        Check.eq("dijkstra", dijkstra(4, roads, 0), new int[]{0, 3, 1, 4});
        Check.eq("bellman-ford", bellmanFord(4, roads, 0), new int[]{0, 3, 1, 4});
        Check.eq("bellman-ford negative edge", bellmanFord(3, new int[][]{{0, 1, 4}, {0, 2, 5}, {2, 1, -3}}, 0),
                new int[]{0, 2, 5});
        Check.eq("negative cycle", bellmanFord(2, new int[][]{{0, 1, 1}, {1, 0, -2}}, 0), null);

        int[][] cables = {{0, 1, 2}, {0, 3, 6}, {1, 2, 3}, {1, 3, 8}, {1, 4, 5}, {2, 4, 7}, {3, 4, 9}};
        Check.eq("prim mst", primMstWeight(5, cables), 16);
        Check.eq("floyd 0->3", floydWarshall(4, roads)[0][3], 4);
    }
}
