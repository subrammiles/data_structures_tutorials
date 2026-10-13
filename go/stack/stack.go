// Package stack: Last In, First Out. Push/Pop/Peek are O(1).
//
// In Go a slice is the natural stack: append to push, s[:len(s)-1] to pop.
// Stack[T] is generic; MinStack answers Min() in O(1) by storing the running minimum.
package stack

type Stack[T any] struct {
	items []T
}

func (s *Stack[T]) Push(x T) { s.items = append(s.items, x) }

func (s *Stack[T]) Pop() (T, bool) {
	var zero T
	if len(s.items) == 0 {
		return zero, false
	}
	x := s.items[len(s.items)-1]
	s.items = s.items[:len(s.items)-1]
	return x, true
}

func (s *Stack[T]) Peek() (T, bool) {
	var zero T
	if len(s.items) == 0 {
		return zero, false
	}
	return s.items[len(s.items)-1], true
}

func (s *Stack[T]) IsEmpty() bool { return len(s.items) == 0 }

// LinkedStack pushes and pops at the head of a linked list.
type LinkedStack struct {
	top *node
}

type node struct {
	value int
	next  *node
}

func (s *LinkedStack) Push(x int) { s.top = &node{x, s.top} }

func (s *LinkedStack) Pop() (int, bool) {
	if s.top == nil {
		return 0, false
	}
	v := s.top.value
	s.top = s.top.next
	return v, true
}

type MinStack struct {
	values, mins []int
}

func (s *MinStack) Push(x int) {
	s.values = append(s.values, x)
	if len(s.mins) == 0 || x < s.mins[len(s.mins)-1] {
		s.mins = append(s.mins, x)
	} else {
		s.mins = append(s.mins, s.mins[len(s.mins)-1])
	}
}

func (s *MinStack) Pop() int {
	x := s.values[len(s.values)-1]
	s.values, s.mins = s.values[:len(s.values)-1], s.mins[:len(s.mins)-1]
	return x
}

func (s *MinStack) Min() int { return s.mins[len(s.mins)-1] }

// IsBalanced is the classic stack application: matching brackets.
func IsBalanced(text string) bool {
	pairs := map[rune]rune{')': '(', ']': '[', '}': '{'}
	var st Stack[rune]
	for _, ch := range text {
		switch ch {
		case '(', '[', '{':
			st.Push(ch)
		case ')', ']', '}':
			open, ok := st.Pop()
			if !ok || open != pairs[ch] {
				return false
			}
		}
	}
	return st.IsEmpty()
}
