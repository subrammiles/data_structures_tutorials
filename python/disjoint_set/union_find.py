"""Disjoint Set Union (union-find).

find(x) -> group representative, union(a, b) -> merge groups.
Path compression + union by size give O(alpha(n)) amortised per operation (effectively constant).
Uses: Kruskal's MST, connected components, cycle detection in undirected graphs.
"""


class UnionFind:
    def __init__(self, n):
        self.parent = list(range(n))  # everyone starts as their own group
        self.size = [1] * n
        self.groups = n

    def find(self, x):
        while self.parent[x] != x:
            self.parent[x] = self.parent[self.parent[x]]  # path halving
            x = self.parent[x]
        return x

    def union(self, a, b):
        ra, rb = self.find(a), self.find(b)
        if ra == rb:
            return False
        if self.size[ra] < self.size[rb]:
            ra, rb = rb, ra
        self.parent[rb] = ra  # attach smaller under larger
        self.size[ra] += self.size[rb]
        self.groups -= 1
        return True

    def connected(self, a, b):
        return self.find(a) == self.find(b)

    def group_size(self, x):
        return self.size[self.find(x)]


def kruskal_mst_weight(n, edges):
    """Sort edges by weight; take an edge if it joins two different groups. O(E log E)."""
    uf, total, used = UnionFind(n), 0, 0
    for u, v, w in sorted(edges, key=lambda e: e[2]):
        if uf.union(u, v):
            total += w
            used += 1
    return total if used == n - 1 else -1


if __name__ == "__main__":
    uf = UnionFind(6)
    uf.union(0, 1)
    uf.union(1, 2)
    uf.union(3, 4)
    assert uf.connected(0, 2) and not uf.connected(0, 3)
    assert uf.groups == 3 and uf.group_size(0) == 3
    assert not uf.union(0, 2)  # would form a cycle
    cables = [(0, 1, 2), (0, 3, 6), (1, 2, 3), (1, 3, 8), (1, 4, 5), (2, 4, 7), (3, 4, 9)]
    assert kruskal_mst_weight(5, cables) == 16
    print("all checks passed")
