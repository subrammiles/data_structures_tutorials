package trees

// BST: left < node < right. Operations are O(h); sorted input makes h = n (see AVL).
// Delete: leaf -> remove, one child -> replace, two children -> copy successor and delete it.
// Subtree sizes make KthSmallest O(h).
type BST struct {
	root *bstNode
}

type bstNode struct {
	value, size int
	left, right *bstNode
}

func size(n *bstNode) int {
	if n == nil {
		return 0
	}
	return n.size
}

func (t *BST) Insert(v int) { t.root = bstInsert(t.root, v) }

func bstInsert(n *bstNode, v int) *bstNode {
	if n == nil {
		return &bstNode{value: v, size: 1}
	}
	if v < n.value {
		n.left = bstInsert(n.left, v)
	} else if v > n.value {
		n.right = bstInsert(n.right, v)
	}
	n.size = 1 + size(n.left) + size(n.right)
	return n
}

func (t *BST) Contains(v int) bool {
	for n := t.root; n != nil; {
		if v == n.value {
			return true
		}
		if v < n.value {
			n = n.left
		} else {
			n = n.right
		}
	}
	return false
}

func (t *BST) Delete(v int) { t.root = bstDelete(t.root, v) }

func bstDelete(n *bstNode, v int) *bstNode {
	if n == nil {
		return nil
	}
	switch {
	case v < n.value:
		n.left = bstDelete(n.left, v)
	case v > n.value:
		n.right = bstDelete(n.right, v)
	default:
		if n.left == nil {
			return n.right
		}
		if n.right == nil {
			return n.left
		}
		s := n.right
		for s.left != nil {
			s = s.left
		}
		n.value = s.value
		n.right = bstDelete(n.right, s.value)
	}
	n.size = 1 + size(n.left) + size(n.right)
	return n
}

// Floor returns the largest value <= x.
func (t *BST) Floor(x int) (int, bool) {
	best, found := 0, false
	for n := t.root; n != nil; {
		if n.value == x {
			return x, true
		}
		if n.value < x {
			best, found = n.value, true
			n = n.right
		} else {
			n = n.left
		}
	}
	return best, found
}

// Ceiling returns the smallest value >= x.
func (t *BST) Ceiling(x int) (int, bool) {
	best, found := 0, false
	for n := t.root; n != nil; {
		if n.value == x {
			return x, true
		}
		if n.value > x {
			best, found = n.value, true
			n = n.left
		} else {
			n = n.right
		}
	}
	return best, found
}

func (t *BST) KthSmallest(k int) int {
	for n := t.root; n != nil; {
		left := size(n.left)
		switch {
		case k == left+1:
			return n.value
		case k <= left:
			n = n.left
		default:
			k -= left + 1
			n = n.right
		}
	}
	panic("k out of range")
}

func (t *BST) Height() int {
	var h func(*bstNode) int
	h = func(n *bstNode) int {
		if n == nil {
			return -1
		}
		return 1 + max(h(n.left), h(n.right))
	}
	return h(t.root)
}

func (t *BST) Inorder() []int {
	var out []int
	var walk func(*bstNode)
	walk = func(n *bstNode) {
		if n != nil {
			walk(n.left)
			out = append(out, n.value)
			walk(n.right)
		}
	}
	walk(t.root)
	return out
}
