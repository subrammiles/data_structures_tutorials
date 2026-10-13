// Package queue: First In, First Out.
//
// Removing from the front of a slice (s = s[1:]) is O(1) but leaks the backing array;
// a circular buffer reuses fixed storage: front index + size, wrap with % capacity.
// Deque adds O(1) operations at both ends. QueueWithTwoStacks is amortised O(1).
package queue

type CircularQueue struct {
	data        []int
	front, size int
}

func NewCircularQueue(capacity int) *CircularQueue {
	return &CircularQueue{data: make([]int, capacity)}
}

func (q *CircularQueue) Enqueue(x int) bool {
	if q.size == len(q.data) {
		return false
	}
	q.data[(q.front+q.size)%len(q.data)] = x
	q.size++
	return true
}

func (q *CircularQueue) Dequeue() (int, bool) {
	if q.size == 0 {
		return 0, false
	}
	x := q.data[q.front]
	q.front = (q.front + 1) % len(q.data)
	q.size--
	return x, true
}

func (q *CircularQueue) IsEmpty() bool { return q.size == 0 }

type CircularDeque struct {
	data        []int
	front, size int
}

func NewCircularDeque(capacity int) *CircularDeque {
	return &CircularDeque{data: make([]int, capacity)}
}

func (d *CircularDeque) AddFirst(x int) bool {
	if d.size == len(d.data) {
		return false
	}
	d.front = (d.front - 1 + len(d.data)) % len(d.data) // Go % can be negative
	d.data[d.front] = x
	d.size++
	return true
}

func (d *CircularDeque) AddLast(x int) bool {
	if d.size == len(d.data) {
		return false
	}
	d.data[(d.front+d.size)%len(d.data)] = x
	d.size++
	return true
}

func (d *CircularDeque) RemoveFirst() (int, bool) {
	if d.size == 0 {
		return 0, false
	}
	x := d.data[d.front]
	d.front = (d.front + 1) % len(d.data)
	d.size--
	return x, true
}

func (d *CircularDeque) RemoveLast() (int, bool) {
	if d.size == 0 {
		return 0, false
	}
	d.size--
	return d.data[(d.front+d.size)%len(d.data)], true
}

type QueueWithTwoStacks struct {
	in, out []int
}

func (q *QueueWithTwoStacks) Enqueue(x int) { q.in = append(q.in, x) }

func (q *QueueWithTwoStacks) Dequeue() (int, bool) {
	if len(q.out) == 0 {
		for len(q.in) > 0 { // reverse once; each element moves at most once
			q.out = append(q.out, q.in[len(q.in)-1])
			q.in = q.in[:len(q.in)-1]
		}
	}
	if len(q.out) == 0 {
		return 0, false
	}
	x := q.out[len(q.out)-1]
	q.out = q.out[:len(q.out)-1]
	return x, true
}
