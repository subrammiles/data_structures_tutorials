"""Core weighted / directed graph algorithms. Edges are (from, to, weight).

Topological sort (Kahn)  DAG only                    O(V + E)
Dijkstra                 non-negative weights        O(E log V)
Bellman-Ford             negative weights, detects negative cycles   O(V * E)
Prim                     minimum spanning tree       O(E log V)
Floyd-Warshall           all pairs                   O(V^3)
(Kruskal lives in disjoint_set/union_find.py.)
"""
import heapq
from collections import deque

INF = float("inf")


def adjacency(n, edges, directed=True):
    adj = [[] for _ in range(n)]
    for e in edges:
        u, v, w = e if len(e) == 3 else (*e, 1)
        adj[u].append((v, w))
        if not directed:
            adj[v].append((u, w))
    return adj


def topological_sort(n, edges):
    """Returns None if there is a cycle."""
    adj = adjacency(n, edges)
    indegree = [0] * n
    for _, v, *rest in edges:
        indegree[v] += 1
    queue = deque(i for i in range(n) if indegree[i] == 0)
    order = []
    while queue:
        u = queue.popleft()
        order.append(u)
        for v, _ in adj[u]:
            indegree[v] -= 1
            if indegree[v] == 0:
                queue.append(v)
    return order if len(order) == n else None


def dijkstra(n, edges, source):
    adj = adjacency(n, edges)
    dist = [INF] * n
    dist[source] = 0
    heap = [(0, source)]
    while heap:
        d, u = heapq.heappop(heap)
        if d > dist[u]:
            continue  # outdated entry
        for v, w in adj[u]:
            if d + w < dist[v]:
                dist[v] = d + w
                heapq.heappush(heap, (dist[v], v))
    return dist


def bellman_ford(n, edges, source):
    """Returns None if a negative cycle is reachable."""
    dist = [INF] * n
    dist[source] = 0
    for _ in range(n - 1):  # a shortest path has at most n-1 edges
        for u, v, w in edges:
            if dist[u] + w < dist[v]:
                dist[v] = dist[u] + w
    if any(dist[u] + w < dist[v] for u, v, w in edges):
        return None
    return dist


def prim_mst_weight(n, edges):
    adj = adjacency(n, edges, directed=False)
    in_tree, heap, total = [False] * n, [(0, 0)], 0
    while heap:
        w, u = heapq.heappop(heap)
        if in_tree[u]:
            continue
        in_tree[u] = True
        total += w
        for v, wv in adj[u]:
            if not in_tree[v]:
                heapq.heappush(heap, (wv, v))
    return total if all(in_tree) else -1


def floyd_warshall(n, edges):
    d = [[0 if i == j else INF for j in range(n)] for i in range(n)]
    for u, v, w in edges:
        d[u][v] = min(d[u][v], w)
    for k in range(n):  # allow k as an intermediate stop
        for i in range(n):
            for j in range(n):
                if d[i][k] + d[k][j] < d[i][j]:
                    d[i][j] = d[i][k] + d[k][j]
    return d


if __name__ == "__main__":
    # Getting dressed: 0 underwear, 1 trousers, 2 shoes, 3 socks, 4 shirt, 5 belt
    dressing = [(0, 1), (1, 2), (3, 2), (1, 5), (4, 5)]
    assert topological_sort(6, dressing) == [0, 3, 4, 1, 2, 5]
    assert topological_sort(2, [(0, 1), (1, 0)]) is None

    roads = [(0, 1, 4), (0, 2, 1), (2, 1, 2), (1, 3, 1), (2, 3, 5)]
    assert dijkstra(4, roads, 0) == [0, 3, 1, 4]
    assert bellman_ford(4, roads, 0) == [0, 3, 1, 4]
    assert bellman_ford(3, [(0, 1, 4), (0, 2, 5), (2, 1, -3)], 0) == [0, 2, 5]
    assert bellman_ford(2, [(0, 1, 1), (1, 0, -2)], 0) is None

    cables = [(0, 1, 2), (0, 3, 6), (1, 2, 3), (1, 3, 8), (1, 4, 5), (2, 4, 7), (3, 4, 9)]
    assert prim_mst_weight(5, cables) == 16
    assert floyd_warshall(4, roads)[0][3] == 4
    print("all checks passed")
