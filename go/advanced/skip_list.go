package advanced

import "math/rand"

const maxLevel = 16

type skipNode struct {
	value int
	next  []*skipNode
}

// SkipList is a sorted linked list with express lanes; each node is promoted with p = 1/2.
type SkipList struct {
	head   *skipNode
	Levels int
	rng    *rand.Rand
}

func NewSkipList(seed int64) *SkipList {
	return &SkipList{head: &skipNode{next: make([]*skipNode, maxLevel)}, Levels: 1, rng: rand.New(rand.NewSource(seed))}
}

func (s *SkipList) randomLevel() int {
	level := 1
	for level < maxLevel && s.rng.Intn(2) == 0 {
		level++
	}
	return level
}

func (s *SkipList) predecessors(value int) []*skipNode {
	update := make([]*skipNode, maxLevel)
	for i := range update {
		update[i] = s.head
	}
	n := s.head
	for lvl := s.Levels - 1; lvl >= 0; lvl-- {
		for n.next[lvl] != nil && n.next[lvl].value < value {
			n = n.next[lvl]
		}
		update[lvl] = n
	}
	return update
}

func (s *SkipList) Contains(value int) bool {
	n := s.predecessors(value)[0].next[0]
	return n != nil && n.value == value
}

func (s *SkipList) Insert(value int) {
	update := s.predecessors(value)
	if n := update[0].next[0]; n != nil && n.value == value {
		return
	}
	lvl := s.randomLevel()
	s.Levels = max(s.Levels, lvl)
	node := &skipNode{value: value, next: make([]*skipNode, lvl)}
	for i := 0; i < lvl; i++ {
		node.next[i] = update[i].next[i]
		update[i].next[i] = node
	}
}

func (s *SkipList) Delete(value int) bool {
	update := s.predecessors(value)
	target := update[0].next[0]
	if target == nil || target.value != value {
		return false
	}
	for i := range target.next {
		update[i].next[i] = target.next[i]
	}
	for s.Levels > 1 && s.head.next[s.Levels-1] == nil {
		s.Levels--
	}
	return true
}

func (s *SkipList) Values() []int {
	var out []int
	for n := s.head.next[0]; n != nil; n = n.next[0] {
		out = append(out, n.value)
	}
	return out
}
