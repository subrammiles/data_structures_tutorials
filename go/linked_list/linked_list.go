// Package linkedlist implements singly and doubly linked lists.
//
//	Singly: head -> [10] -> [20] -> nil. Add/remove at head O(1), index access O(n).
//	Doubly: sentinel head/tail nodes; removing a node you hold is O(1) (LRU caches use this).
//
// Interview essentials: Reverse, Middle (slow/fast pointers), HasCycle (Floyd).
package linkedlist

type Node struct {
	Value int
	Next  *Node
}

type SinglyLinkedList struct {
	head, tail *Node
	size       int
}

func (l *SinglyLinkedList) AddFirst(v int) {
	l.head = &Node{Value: v, Next: l.head}
	if l.tail == nil {
		l.tail = l.head
	}
	l.size++
}

func (l *SinglyLinkedList) AddLast(v int) {
	n := &Node{Value: v}
	if l.tail == nil {
		l.head, l.tail = n, n
	} else {
		l.tail.Next = n
		l.tail = n
	}
	l.size++
}

func (l *SinglyLinkedList) InsertAt(i, v int) {
	switch {
	case i < 0 || i > l.size:
		panic("index out of range")
	case i == 0:
		l.AddFirst(v)
	case i == l.size:
		l.AddLast(v)
	default:
		prev := l.head
		for k := 0; k < i-1; k++ {
			prev = prev.Next
		}
		prev.Next = &Node{Value: v, Next: prev.Next}
		l.size++
	}
}

// Remove deletes the first node with value v. The dummy node avoids a special case for head.
func (l *SinglyLinkedList) Remove(v int) bool {
	dummy := &Node{Next: l.head}
	prev := dummy
	for prev.Next != nil && prev.Next.Value != v {
		prev = prev.Next
	}
	if prev.Next == nil {
		return false
	}
	if prev.Next == l.tail {
		if prev == dummy {
			l.tail = nil
		} else {
			l.tail = prev
		}
	}
	prev.Next = prev.Next.Next
	l.head = dummy.Next
	l.size--
	return true
}

// Reverse turns every arrow around in place: O(n) time, O(1) space.
func (l *SinglyLinkedList) Reverse() {
	var prev *Node
	cur := l.head
	l.tail = l.head
	for cur != nil {
		next := cur.Next
		cur.Next = prev
		prev, cur = cur, next
	}
	l.head = prev
}

// Middle uses slow (1 step) and fast (2 steps) pointers.
func (l *SinglyLinkedList) Middle() int {
	slow, fast := l.head, l.head
	for fast != nil && fast.Next != nil {
		slow, fast = slow.Next, fast.Next.Next
	}
	return slow.Value
}

func (l *SinglyLinkedList) Len() int { return l.size }

func (l *SinglyLinkedList) Values() []int {
	var out []int
	for n := l.head; n != nil; n = n.Next {
		out = append(out, n.Value)
	}
	return out
}

// HasCycle is Floyd's tortoise and hare.
func HasCycle(head *Node) bool {
	slow, fast := head, head
	for fast != nil && fast.Next != nil {
		slow, fast = slow.Next, fast.Next.Next
		if slow == fast {
			return true
		}
	}
	return false
}

type DNode struct {
	Value      int
	prev, next *DNode
}

type DoublyLinkedList struct {
	head, tail *DNode // sentinels, never hold data
	size       int
}

func NewDoublyLinkedList() *DoublyLinkedList {
	l := &DoublyLinkedList{head: &DNode{}, tail: &DNode{}}
	l.head.next, l.tail.prev = l.tail, l.head
	return l
}

func (l *DoublyLinkedList) insertBetween(v int, before, after *DNode) *DNode {
	n := &DNode{Value: v, prev: before, next: after}
	before.next, after.prev = n, n
	l.size++
	return n
}

func (l *DoublyLinkedList) AddFirst(v int) *DNode { return l.insertBetween(v, l.head, l.head.next) }
func (l *DoublyLinkedList) AddLast(v int) *DNode  { return l.insertBetween(v, l.tail.prev, l.tail) }

func (l *DoublyLinkedList) RemoveNode(n *DNode) {
	n.prev.next, n.next.prev = n.next, n.prev
	l.size--
}

func (l *DoublyLinkedList) RemoveFirst() (int, bool) {
	if l.size == 0 {
		return 0, false
	}
	n := l.head.next
	l.RemoveNode(n)
	return n.Value, true
}

func (l *DoublyLinkedList) RemoveLast() (int, bool) {
	if l.size == 0 {
		return 0, false
	}
	n := l.tail.prev
	l.RemoveNode(n)
	return n.Value, true
}

func (l *DoublyLinkedList) Forward() []int {
	var out []int
	for n := l.head.next; n != l.tail; n = n.next {
		out = append(out, n.Value)
	}
	return out
}

func (l *DoublyLinkedList) Backward() []int {
	var out []int
	for n := l.tail.prev; n != l.head; n = n.prev {
		out = append(out, n.Value)
	}
	return out
}
