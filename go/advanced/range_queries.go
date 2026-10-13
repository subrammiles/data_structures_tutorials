// Package advanced: range-query structures and probabilistic / system data structures.
//
//	SegmentTree  range min + point update   O(log n)
//	FenwickTree  prefix sum + point update  O(log n)
//	SparseTable  range min, static data     O(1) query after O(n log n) build
//	SkipList     sorted set                 expected O(log n)
//	BloomFilter  "probably contains"        O(k), false positives only
package advanced

import "math"

type SegmentTree struct {
	tree []int
	n    int
}

func NewSegmentTree(values []int) *SegmentTree {
	st := &SegmentTree{tree: make([]int, 4*len(values)), n: len(values)}
	st.build(1, 0, st.n-1, values)
	return st
}

func (s *SegmentTree) build(node, l, r int, v []int) {
	if l == r {
		s.tree[node] = v[l]
		return
	}
	mid := (l + r) / 2
	s.build(2*node, l, mid, v)
	s.build(2*node+1, mid+1, r, v)
	s.tree[node] = min(s.tree[2*node], s.tree[2*node+1])
}

func (s *SegmentTree) Update(index, value int) { s.update(1, 0, s.n-1, index, value) }

func (s *SegmentTree) update(node, l, r, index, value int) {
	if l == r {
		s.tree[node] = value
		return
	}
	mid := (l + r) / 2
	if index <= mid {
		s.update(2*node, l, mid, index, value)
	} else {
		s.update(2*node+1, mid+1, r, index, value)
	}
	s.tree[node] = min(s.tree[2*node], s.tree[2*node+1])
}

func (s *SegmentTree) RangeMin(ql, qr int) int { return s.query(1, 0, s.n-1, ql, qr) }

func (s *SegmentTree) query(node, l, r, ql, qr int) int {
	if qr < l || r < ql {
		return math.MaxInt // no overlap
	}
	if ql <= l && r <= qr {
		return s.tree[node] // full overlap
	}
	mid := (l + r) / 2
	return min(s.query(2*node, l, mid, ql, qr), s.query(2*node+1, mid+1, r, ql, qr))
}

// FenwickTree is 1-based; i & -i is the lowest set bit of i.
type FenwickTree struct{ tree []int }

func NewFenwickTree(n int) *FenwickTree { return &FenwickTree{tree: make([]int, n+1)} }

func (f *FenwickTree) Add(i, delta int) {
	for ; i < len(f.tree); i += i & -i {
		f.tree[i] += delta
	}
}

func (f *FenwickTree) PrefixSum(i int) int {
	s := 0
	for ; i > 0; i -= i & -i {
		s += f.tree[i]
	}
	return s
}

func (f *FenwickTree) RangeSum(l, r int) int { return f.PrefixSum(r) - f.PrefixSum(l-1) }

// SparseTable: table[k][i] = min of a[i .. i + 2^k - 1].
type SparseTable struct {
	table [][]int
	log   []int
}

func NewSparseTable(a []int) *SparseTable {
	n := len(a)
	st := &SparseTable{log: make([]int, n+1)}
	for i := 2; i <= n; i++ {
		st.log[i] = st.log[i/2] + 1
	}
	st.table = [][]int{append([]int(nil), a...)}
	for k := 1; 1<<k <= n; k++ {
		prev, half := st.table[k-1], 1<<(k-1)
		row := make([]int, n-(1<<k)+1)
		for i := range row {
			row[i] = min(prev[i], prev[i+half])
		}
		st.table = append(st.table, row)
	}
	return st
}

func (s *SparseTable) RangeMin(l, r int) int {
	k := s.log[r-l+1]
	return min(s.table[k][l], s.table[k][r-(1<<k)+1]) // two overlapping blocks
}
