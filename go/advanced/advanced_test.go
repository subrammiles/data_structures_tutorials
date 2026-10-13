package advanced

import (
	"fmt"
	"reflect"
	"testing"
)

func TestSegmentTree(t *testing.T) {
	st := NewSegmentTree([]int{5, 2, 6, 3, 7, 1, 4})
	if st.RangeMin(0, 6) != 1 || st.RangeMin(0, 3) != 2 || st.RangeMin(2, 4) != 3 {
		t.Fatal("range min")
	}
	st.Update(3, 0)
	if st.RangeMin(2, 4) != 0 || st.RangeMin(6, 6) != 4 {
		t.Fatal("after update")
	}
}

func TestFenwickTree(t *testing.T) {
	ft := NewFenwickTree(8)
	for i, v := range []int{3, 2, -1, 6, 5, 4, -3, 3} {
		ft.Add(i+1, v)
	}
	if ft.PrefixSum(7) != 16 || ft.RangeSum(2, 5) != 12 {
		t.Fatal("sums")
	}
	ft.Add(3, 10)
	if ft.RangeSum(2, 5) != 22 || ft.PrefixSum(8) != 29 {
		t.Fatal("after add")
	}
}

func TestSparseTable(t *testing.T) {
	st := NewSparseTable([]int{7, 2, 3, 0, 5, 10, 3, 12, 18})
	for _, c := range [][3]int{{0, 2, 2}, {4, 7, 3}, {0, 8, 0}, {7, 8, 12}} {
		if got := st.RangeMin(c[0], c[1]); got != c[2] {
			t.Fatalf("min(%d..%d) = %d, want %d", c[0], c[1], got, c[2])
		}
	}
}

func TestSkipList(t *testing.T) {
	s := NewSkipList(42)
	for _, v := range []int{30, 10, 50, 20, 40, 5} {
		s.Insert(v)
	}
	if !reflect.DeepEqual(s.Values(), []int{5, 10, 20, 30, 40, 50}) || !s.Contains(40) || s.Contains(45) {
		t.Fatalf("values %v", s.Values())
	}
	if !s.Delete(30) || !reflect.DeepEqual(s.Values(), []int{5, 10, 20, 40, 50}) {
		t.Fatal("delete")
	}
	big := NewSkipList(7)
	for i := 0; i < 50000; i++ {
		big.Insert(i * 2)
	}
	if big.Levels < 10 || !big.Contains(12346) || big.Contains(12347) {
		t.Fatalf("levels %d", big.Levels)
	}
}

func TestBloomFilter(t *testing.T) {
	bf := NewBloomFilter(10000, 0.01)
	for i := 0; i < 10000; i++ {
		bf.Add(fmt.Sprintf("user-%d", i))
	}
	for i := 0; i < 10000; i++ {
		if !bf.MightContain(fmt.Sprintf("user-%d", i)) {
			t.Fatal("false negative")
		}
	}
	fp := 0
	for i := 0; i < 50000; i++ {
		if bf.MightContain(fmt.Sprintf("other-%d", i)) {
			fp++
		}
	}
	if rate := float64(fp) / 50000; rate > 0.02 {
		t.Fatalf("false positive rate %.4f", rate)
	}
}
