"""Graph representation and traversals.

                 | adjacency matrix | adjacency list
memory           | O(V^2)           | O(V + E)
edge (u,v)?      | O(1)             | O(degree(u))
neighbours of u  | O(V)             | O(degree(u))
Most real graphs are sparse, so adjacency lists are the default.

BFS (queue): level by level, shortest paths in unweighted graphs. DFS (stack/recursion): goes deep;
cycles, components, topological sort. Both O(V + E).
"""
from collections import deque


class Graph:
    def __init__(self, vertices, directed=False):
        self.adj = [[] for _ in range(vertices)]
        self.directed = directed

    def add_edge(self, u, v):
        self.adj[u].append(v)
        if not self.directed:
            self.adj[v].append(u)

    def to_matrix(self):
        n = len(self.adj)
        m = [[0] * n for _ in range(n)]
        for u in range(n):
            for v in self.adj[u]:
                m[u][v] = 1
        return m

    def bfs(self, start):
        order, visited, queue = [], {start}, deque([start])  # mark when ENQUEUED
        while queue:
            u = queue.popleft()
            order.append(u)
            for v in self.adj[u]:
                if v not in visited:
                    visited.add(v)
                    queue.append(v)
        return order

    def dfs(self, start, visited=None):
        visited = visited if visited is not None else set()
        visited.add(start)
        order = [start]
        for v in self.adj[start]:
            if v not in visited:
                order += self.dfs(v, visited)
        return order

    def shortest_path(self, s, t):
        """Fewest edges from s to t via BFS parent pointers."""
        parent, queue = {s: None}, deque([s])
        while queue:
            u = queue.popleft()
            if u == t:
                break
            for v in self.adj[u]:
                if v not in parent:
                    parent[v] = u
                    queue.append(v)
        if t not in parent:
            return []
        path = []
        while t is not None:
            path.append(t)
            t = parent[t]
        return path[::-1]

    def connected_components(self):
        visited, count = set(), 0
        for u in range(len(self.adj)):
            if u not in visited:
                count += 1
                self.dfs(u, visited)
        return count

    def has_cycle_undirected(self):
        visited = set()

        def visit(u, parent):
            visited.add(u)
            for v in self.adj[u]:
                if v not in visited:
                    if visit(v, u):
                        return True
                elif v != parent:
                    return True
            return False

        return any(u not in visited and visit(u, -1) for u in range(len(self.adj)))

    def is_bipartite(self):
        color = [0] * len(self.adj)
        for s in range(len(self.adj)):
            if color[s]:
                continue
            color[s], queue = 1, deque([s])
            while queue:
                u = queue.popleft()
                for v in self.adj[u]:
                    if color[v] == 0:
                        color[v] = -color[u]
                        queue.append(v)
                    elif color[v] == color[u]:
                        return False
        return True


if __name__ == "__main__":
    #   0 --- 1 --- 3
    #   |     |
    #   2 --- 4     5 --- 6
    g = Graph(7)
    for u, v in ((0, 1), (0, 2), (1, 3), (1, 4), (2, 4), (5, 6)):
        g.add_edge(u, v)
    assert g.bfs(0) == [0, 1, 2, 3, 4]
    assert g.dfs(0) == [0, 1, 3, 4, 2]
    assert g.shortest_path(0, 4) == [0, 1, 4]
    assert g.shortest_path(0, 6) == []
    assert g.connected_components() == 2
    assert g.has_cycle_undirected()
    assert g.is_bipartite()
    g.add_edge(0, 4)  # triangle 0-1-4
    assert not g.is_bipartite()
    assert Graph(2, directed=True).to_matrix() == [[0, 0], [0, 0]]
    print("all checks passed")
