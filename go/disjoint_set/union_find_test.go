package disjointset

import "testing"

func TestUnionFind(t *testing.T) {
	uf := New(6)
	uf.Union(0, 1)
	uf.Union(1, 2)
	uf.Union(3, 4)
	if !uf.Connected(0, 2) || uf.Connected(0, 3) || uf.Groups != 3 || uf.GroupSize(0) != 3 {
		t.Fatal("groups")
	}
	if uf.Union(0, 2) {
		t.Fatal("already connected: would form a cycle")
	}
	cables := [][3]int{{0, 1, 2}, {0, 3, 6}, {1, 2, 3}, {1, 3, 8}, {1, 4, 5}, {2, 4, 7}, {3, 4, 9}}
	if KruskalMSTWeight(5, cables) != 16 {
		t.Fatal("kruskal")
	}
}
