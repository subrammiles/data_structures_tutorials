package trees

// AVL keeps |height(left) - height(right)| <= 1 at every node using rotations,
// so every operation is O(log n) even for sorted input.
//
//	LL -> rotate right   RR -> rotate left
//	LR -> rotate child left, then right   RL -> rotate child right, then left
type AVL struct {
	root      *avlNode
	Rotations int
}

type avlNode struct {
	value, height int
	left, right   *avlNode
}

func ht(n *avlNode) int {
	if n == nil {
		return -1
	}
	return n.height
}

func update(n *avlNode)      { n.height = 1 + max(ht(n.left), ht(n.right)) }
func balance(n *avlNode) int { return ht(n.left) - ht(n.right) }

func (t *AVL) rotateRight(y *avlNode) *avlNode {
	x := y.left
	y.left, x.right = x.right, y
	update(y)
	update(x)
	t.Rotations++
	return x
}

func (t *AVL) rotateLeft(x *avlNode) *avlNode {
	y := x.right
	x.right, y.left = y.left, x
	update(x)
	update(y)
	t.Rotations++
	return y
}

func (t *AVL) rebalance(n *avlNode) *avlNode {
	update(n)
	switch b := balance(n); {
	case b > 1:
		if balance(n.left) < 0 {
			n.left = t.rotateLeft(n.left) // Left-Right
		}
		return t.rotateRight(n)
	case b < -1:
		if balance(n.right) > 0 {
			n.right = t.rotateRight(n.right) // Right-Left
		}
		return t.rotateLeft(n)
	}
	return n
}

func (t *AVL) Insert(v int) {
	var ins func(*avlNode) *avlNode
	ins = func(n *avlNode) *avlNode {
		if n == nil {
			return &avlNode{value: v}
		}
		if v < n.value {
			n.left = ins(n.left)
		} else if v > n.value {
			n.right = ins(n.right)
		} else {
			return n
		}
		return t.rebalance(n)
	}
	t.root = ins(t.root)
}

func (t *AVL) Delete(v int) {
	var del func(*avlNode, int) *avlNode
	del = func(n *avlNode, v int) *avlNode {
		if n == nil {
			return nil
		}
		switch {
		case v < n.value:
			n.left = del(n.left, v)
		case v > n.value:
			n.right = del(n.right, v)
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
			n.right = del(n.right, s.value)
		}
		return t.rebalance(n)
	}
	t.root = del(t.root, v)
}

func (t *AVL) Height() int { return ht(t.root) }

func (t *AVL) IsBalanced() bool {
	var check func(*avlNode) bool
	check = func(n *avlNode) bool {
		return n == nil || (balance(n) >= -1 && balance(n) <= 1 && check(n.left) && check(n.right))
	}
	return check(t.root)
}

func (t *AVL) RootValue() int { return t.root.value }
