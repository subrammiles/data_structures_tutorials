package heap

import (
	"reflect"
	"testing"
)

func TestMinHeap(t *testing.T) {
	h := NewMinHeap()
	for _, x := range []int{7, 3, 9, 1, 4, 2, 8, 6, 5} {
		h.Push(x)
	}
	if !h.IsValid() {
		t.Fatal("heap property")
	}
	var out []int
	for h.Len() > 0 {
		x, _ := h.Pop()
		out = append(out, x)
	}
	if !reflect.DeepEqual(out, []int{1, 2, 3, 4, 5, 6, 7, 8, 9}) {
		t.Fatalf("pops %v", out)
	}
	built := NewMinHeap(5, 9, 3, 1, 8)
	if top, _ := built.Peek(); !built.IsValid() || top != 1 {
		t.Fatal("heapify")
	}
	if _, ok := NewMinHeap().Pop(); ok {
		t.Fatal("pop on empty")
	}
	a := []int{5, 2, 9, 1, 5, 6}
	HeapSort(a)
	if !reflect.DeepEqual(a, []int{1, 2, 5, 5, 6, 9}) {
		t.Fatalf("heap sort %v", a)
	}
}
