package graph.a_representation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import common.Check;

/*
 * Graph = vertices (nodes) + edges (connections). Directed or undirected, weighted or not.
 *
 * Representations for V vertices and E edges:
 *                     | adjacency matrix | adjacency list
 *   memory            | O(V^2)           | O(V + E)
 *   is (u,v) an edge? | O(1)             | O(degree(u))
 *   iterate neighbours| O(V)             | O(degree(u))
 * Most real graphs are SPARSE (E much less than V^2), so adjacency lists are the default.
 *
 * Traversals, both O(V + E):
 *   BFS (queue)  - explores level by level; gives SHORTEST PATHS in unweighted graphs.
 *   DFS (stack / recursion) - goes deep first; used for cycles, components, topological sort.
 */
public class Graph {

    private final List<List<Integer>> adj = new ArrayList<>();
    private final boolean directed;

    public Graph(int vertices, boolean directed) {
        this.directed = directed;
        for (int i = 0; i < vertices; i++) adj.add(new ArrayList<>());
    }

    public void addEdge(int u, int v) {
        adj.get(u).add(v);
        if (!directed) adj.get(v).add(u);
    }

    public int vertices() {
        return adj.size();
    }

    public List<Integer> neighbours(int u) {
        return adj.get(u);
    }

    public int[][] toMatrix() {
        int n = adj.size();
        int[][] m = new int[n][n];
        for (int u = 0; u < n; u++) for (int v : adj.get(u)) m[u][v] = 1;
        return m;
    }

    public List<Integer> bfs(int start) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[vertices()];
        Deque<Integer> queue = new ArrayDeque<>();
        queue.offer(start);
        visited[start] = true; // mark when ENQUEUED, not when dequeued, to avoid duplicates
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.offer(v);
                }
            }
        }
        return order;
    }

    public List<Integer> dfs(int start) {
        List<Integer> order = new ArrayList<>();
        dfs(start, new boolean[vertices()], order);
        return order;
    }

    private void dfs(int u, boolean[] visited, List<Integer> order) {
        visited[u] = true;
        order.add(u);
        for (int v : adj.get(u)) if (!visited[v]) dfs(v, visited, order);
    }

    /* Shortest path (fewest edges) from s to t using BFS parent pointers. */
    public List<Integer> shortestPath(int s, int t) {
        int[] parent = new int[vertices()];
        Arrays.fill(parent, -2); // -2 = unvisited
        parent[s] = -1;
        Deque<Integer> queue = new ArrayDeque<>();
        queue.offer(s);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (u == t) break;
            for (int v : adj.get(u)) {
                if (parent[v] == -2) {
                    parent[v] = u;
                    queue.offer(v);
                }
            }
        }
        if (parent[t] == -2) return Collections.emptyList();
        List<Integer> path = new ArrayList<>();
        for (int v = t; v != -1; v = parent[v]) path.add(v);
        Collections.reverse(path);
        return path;
    }

    public int connectedComponents() {
        boolean[] visited = new boolean[vertices()];
        int count = 0;
        for (int u = 0; u < vertices(); u++) {
            if (!visited[u]) {
                count++;
                dfs(u, visited, new ArrayList<>());
            }
        }
        return count;
    }

    /* Undirected cycle: reaching an already-visited vertex that is not our parent. */
    public boolean hasCycleUndirected() {
        boolean[] visited = new boolean[vertices()];
        for (int u = 0; u < vertices(); u++) if (!visited[u] && cycleFrom(u, -1, visited)) return true;
        return false;
    }

    private boolean cycleFrom(int u, int parent, boolean[] visited) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                if (cycleFrom(v, u, visited)) return true;
            } else if (v != parent) {
                return true;
            }
        }
        return false;
    }

    /* A graph is bipartite if it can be 2-coloured: BFS and alternate colours. */
    public boolean isBipartite() {
        int[] color = new int[vertices()];
        for (int s = 0; s < vertices(); s++) {
            if (color[s] != 0) continue;
            color[s] = 1;
            Deque<Integer> q = new ArrayDeque<>();
            q.offer(s);
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v : adj.get(u)) {
                    if (color[v] == 0) {
                        color[v] = -color[u];
                        q.offer(v);
                    } else if (color[v] == color[u]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        //   0 --- 1 --- 3
        //   |     |
        //   2 --- 4     5 --- 6
        Graph g = new Graph(7, false);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(2, 4);
        g.addEdge(5, 6);
        Check.eq("bfs", g.bfs(0), List.of(0, 1, 2, 3, 4));
        Check.eq("dfs", g.dfs(0), List.of(0, 1, 3, 4, 2));
        Check.eq("shortest 0->4", g.shortestPath(0, 4), List.of(0, 1, 4));
        Check.eq("no path", g.shortestPath(0, 6), List.of());
        Check.eq("components", g.connectedComponents(), 2);
        Check.isTrue("cycle 0-1-4-2", g.hasCycleUndirected());
        Check.isTrue("even cycle is bipartite", g.isBipartite());
        g.addEdge(0, 4); // creates triangle 0-1-4
        Check.eq("odd cycle not bipartite", g.isBipartite(), false);
        Check.eq("matrix of graph without edges", new Graph(2, true).toMatrix()[0], new int[]{0, 0});
    }
}
