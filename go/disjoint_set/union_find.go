// Package disjointset: union-find with path compression and union by size,
// ~O(1) amortised per operation. KruskalMSTWeight shows its most famous use.
package disjointset

import "sort"

type UnionFind struct {
	parent, size []int
	Groups       int
}

func New(n int) *UnionFind {
	uf := &UnionFind{parent: make([]int, n), size: make([]int, n), Groups: n}
	for i := range uf.parent {
		uf.parent[i], uf.size[i] = i, 1
	}
	return uf
}

func (u *UnionFind) Find(x int) int {
	for u.parent[x] != x {
		u.parent[x] = u.parent[u.parent[x]] // path halving
		x = u.parent[x]
	}
	return x
}

func (u *UnionFind) Union(a, b int) bool {
	ra, rb := u.Find(a), u.Find(b)
	if ra == rb {
		return false
	}
	if u.size[ra] < u.size[rb] {
		ra, rb = rb, ra
	}
	u.parent[rb] = ra
	u.size[ra] += u.size[rb]
	u.Groups--
	return true
}

func (u *UnionFind) Connected(a, b int) bool { return u.Find(a) == u.Find(b) }
func (u *UnionFind) GroupSize(x int) int     { return u.size[u.Find(x)] }

// KruskalMSTWeight sorts edges ([u, v, w]) by weight and keeps those joining different groups.
func KruskalMSTWeight(n int, edges [][3]int) int {
	sorted := append([][3]int(nil), edges...)
	sort.Slice(sorted, func(i, j int) bool { return sorted[i][2] < sorted[j][2] })
	uf := New(n)
	total, used := 0, 0
	for _, e := range sorted {
		if uf.Union(e[0], e[1]) {
			total += e[2]
			used++
		}
	}
	if used != n-1 {
		return -1
	}
	return total
}
