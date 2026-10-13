package linkedlist

import (
	"reflect"
	"testing"
)

func TestSinglyLinkedList(t *testing.T) {
	var l SinglyLinkedList
	l.AddLast(20)
	l.AddLast(30)
	l.AddFirst(10)
	l.InsertAt(3, 40)
	if !reflect.DeepEqual(l.Values(), []int{10, 20, 30, 40}) || l.Middle() != 30 {
		t.Fatalf("build: %v", l.Values())
	}
	l.Reverse()
	if !reflect.DeepEqual(l.Values(), []int{40, 30, 20, 10}) {
		t.Fatalf("reverse: %v", l.Values())
	}
	if !l.Remove(10) {
		t.Fatal("remove tail")
	}
	l.AddLast(5)
	if !reflect.DeepEqual(l.Values(), []int{40, 30, 20, 5}) || l.Remove(99) || l.Len() != 4 {
		t.Fatalf("after remove: %v", l.Values())
	}

	a, b, c := &Node{Value: 1}, &Node{Value: 2}, &Node{Value: 3}
	a.Next, b.Next = b, c
	if HasCycle(a) {
		t.Fatal("no cycle expected")
	}
	c.Next = a
	if !HasCycle(a) {
		t.Fatal("cycle expected")
	}
}

func TestDoublyLinkedList(t *testing.T) {
	l := NewDoublyLinkedList()
	l.AddLast(2)
	three := l.AddLast(3)
	l.AddFirst(1)
	l.AddLast(4)
	if !reflect.DeepEqual(l.Forward(), []int{1, 2, 3, 4}) || !reflect.DeepEqual(l.Backward(), []int{4, 3, 2, 1}) {
		t.Fatalf("forward/backward: %v %v", l.Forward(), l.Backward())
	}
	l.RemoveNode(three)
	if first, _ := l.RemoveFirst(); first != 1 {
		t.Fatal("RemoveFirst")
	}
	if last, _ := l.RemoveLast(); last != 4 {
		t.Fatal("RemoveLast")
	}
	l.RemoveFirst()
	if _, ok := l.RemoveFirst(); ok {
		t.Fatal("remove from empty should fail")
	}
}
