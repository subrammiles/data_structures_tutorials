// Package trie: prefix tree. Insert/Search/StartsWith are O(L) for a word of length L.
// prefixCount per node makes Delete and CountWordsWithPrefix simple.
package trie

import "sort"

type node struct {
	children    map[rune]*node
	isEnd       bool
	prefixCount int
}

func newNode() *node { return &node{children: map[rune]*node{}} }

type Trie struct {
	root *node
}

func New() *Trie { return &Trie{root: newNode()} }

func (t *Trie) Insert(word string) {
	if t.Search(word) {
		return
	}
	n := t.root
	for _, ch := range word {
		child, ok := n.children[ch]
		if !ok {
			child = newNode()
			n.children[ch] = child
		}
		n = child
		n.prefixCount++
	}
	n.isEnd = true
}

func (t *Trie) walk(s string) *node {
	n := t.root
	for _, ch := range s {
		if n = n.children[ch]; n == nil {
			return nil
		}
	}
	return n
}

func (t *Trie) Search(word string) bool {
	n := t.walk(word)
	return n != nil && n.isEnd
}

func (t *Trie) StartsWith(prefix string) bool { return t.walk(prefix) != nil }

func (t *Trie) CountWordsWithPrefix(prefix string) int {
	if n := t.walk(prefix); n != nil {
		return n.prefixCount
	}
	return 0
}

func (t *Trie) Delete(word string) bool {
	if !t.Search(word) {
		return false
	}
	n := t.root
	for _, ch := range word {
		child := n.children[ch]
		child.prefixCount--
		if child.prefixCount == 0 { // nobody else uses this branch
			delete(n.children, ch)
			return true
		}
		n = child
	}
	n.isEnd = false
	return true
}

func (t *Trie) WordsWithPrefix(prefix string) []string {
	var out []string
	var collect func(*node, string)
	collect = func(n *node, path string) {
		if n.isEnd {
			out = append(out, path)
		}
		keys := make([]rune, 0, len(n.children))
		for ch := range n.children {
			keys = append(keys, ch)
		}
		sort.Slice(keys, func(i, j int) bool { return keys[i] < keys[j] })
		for _, ch := range keys {
			collect(n.children[ch], path+string(ch))
		}
	}
	if start := t.walk(prefix); start != nil {
		collect(start, prefix)
	}
	return out
}
