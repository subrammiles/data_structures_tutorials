// Package heap: binary min-heap stored in a slice.
//
//	parent(i) = (i-1)/2, children 2i+1 and 2i+2
//	Peek O(1) | Push/Pop O(log n) | NewMinHeap (heapify) O(n) | HeapSort O(n log n)
//
// The standard library equivalent is container/heap (you implement heap.Interface).
package heap

type MinHeap struct {
	a []int
}

// NewMinHeap heapifies in O(n) by sifting down every non-leaf, bottom-up.
func NewMinHeap(values ...int) *MinHeap {
	h := &MinHeap{a: append([]int(nil), values...)}
	for i := len(h.a)/2 - 1; i >= 0; i-- {
		h.siftDown(i)
	}
	return h
}

func (h *MinHeap) Len() int { return len(h.a) }

func (h *MinHeap) Push(x int) {
	h.a = append(h.a, x)
	for i := len(h.a) - 1; i > 0 && h.a[i] < h.a[(i-1)/2]; i = (i - 1) / 2 {
		h.a[i], h.a[(i-1)/2] = h.a[(i-1)/2], h.a[i]
	}
}

func (h *MinHeap) Peek() (int, bool) {
	if len(h.a) == 0 {
		return 0, false
	}
	return h.a[0], true
}

func (h *MinHeap) Pop() (int, bool) {
	if len(h.a) == 0 {
		return 0, false
	}
	top := h.a[0]
	last := len(h.a) - 1
	h.a[0] = h.a[last]
	h.a = h.a[:last]
	h.siftDown(0)
	return top, true
}

func (h *MinHeap) siftDown(i int) {
	n := len(h.a)
	for {
		l, r, smallest := 2*i+1, 2*i+2, i
		if l < n && h.a[l] < h.a[smallest] {
			smallest = l
		}
		if r < n && h.a[r] < h.a[smallest] {
			smallest = r
		}
		if smallest == i {
			return
		}
		h.a[i], h.a[smallest] = h.a[smallest], h.a[i]
		i = smallest
	}
}

func (h *MinHeap) IsValid() bool {
	for i := 1; i < len(h.a); i++ {
		if h.a[i] < h.a[(i-1)/2] {
			return false
		}
	}
	return true
}

// HeapSort sorts ascending in place using a max-heap.
func HeapSort(a []int) {
	siftDown := func(i, n int) {
		for {
			l, r, largest := 2*i+1, 2*i+2, i
			if l < n && a[l] > a[largest] {
				largest = l
			}
			if r < n && a[r] > a[largest] {
				largest = r
			}
			if largest == i {
				return
			}
			a[i], a[largest] = a[largest], a[i]
			i = largest
		}
	}
	for i := len(a)/2 - 1; i >= 0; i-- {
		siftDown(i, len(a))
	}
	for end := len(a) - 1; end > 0; end-- {
		a[0], a[end] = a[end], a[0]
		siftDown(0, end)
	}
}
