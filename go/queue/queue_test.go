package queue

import (
	"reflect"
	"testing"
)

func TestCircularQueue(t *testing.T) {
	q := NewCircularQueue(5)
	for i := 1; i <= 5; i++ {
		q.Enqueue(i)
	}
	if q.Enqueue(99) {
		t.Fatal("queue should be full")
	}
	q.Dequeue()
	q.Dequeue()
	q.Enqueue(6)
	q.Enqueue(7) // wraps around
	var order []int
	for !q.IsEmpty() {
		x, _ := q.Dequeue()
		order = append(order, x)
	}
	if !reflect.DeepEqual(order, []int{3, 4, 5, 6, 7}) {
		t.Fatalf("order %v", order)
	}
}

func TestDequeAndTwoStacks(t *testing.T) {
	d := NewCircularDeque(3)
	d.AddLast(2)
	d.AddFirst(1)
	d.AddLast(3)
	if d.AddFirst(0) {
		t.Fatal("deque should be full")
	}
	if x, _ := d.RemoveLast(); x != 3 {
		t.Fatal("RemoveLast")
	}
	if x, _ := d.RemoveFirst(); x != 1 {
		t.Fatal("RemoveFirst")
	}

	var s QueueWithTwoStacks
	s.Enqueue(1)
	s.Enqueue(2)
	a, _ := s.Dequeue()
	s.Enqueue(3)
	b, _ := s.Dequeue()
	c, _ := s.Dequeue()
	if a != 1 || b != 2 || c != 3 {
		t.Fatalf("FIFO order broken: %d %d %d", a, b, c)
	}
}
