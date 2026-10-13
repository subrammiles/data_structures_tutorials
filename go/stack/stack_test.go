package stack

import "testing"

func TestStacks(t *testing.T) {
	var s Stack[int]
	for i := 1; i <= 6; i++ {
		s.Push(i)
	}
	if v, _ := s.Pop(); v != 6 {
		t.Fatal("pop")
	}
	if v, _ := s.Peek(); v != 5 {
		t.Fatal("peek")
	}

	var l LinkedStack
	l.Push(1)
	l.Push(2)
	a, _ := l.Pop()
	b, _ := l.Pop()
	if a != 2 || b != 1 {
		t.Fatal("linked stack order")
	}
	if _, ok := l.Pop(); ok {
		t.Fatal("underflow should fail")
	}

	var m MinStack
	for _, x := range []int{5, 2, 7} {
		m.Push(x)
	}
	if m.Min() != 2 {
		t.Fatal("min")
	}
	m.Pop()
	m.Pop()
	if m.Min() != 5 {
		t.Fatal("min after pops")
	}

	for in, want := range map[string]bool{"{[()()]}": true, "([)]": false, "((": false} {
		if IsBalanced(in) != want {
			t.Fatalf("IsBalanced(%q) != %v", in, want)
		}
	}
}
