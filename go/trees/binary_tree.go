// Package trees: binary tree traversals, a binary search tree and an AVL tree.
//
//	     1
//	   /   \
//	  2     3          preorder 1 2 4 5 3 6    inorder 4 2 5 1 3 6
//	 / \     \         postorder 4 5 2 6 3 1   level order [1] [2 3] [4 5 6]
//	4   5     6
package trees

type Node struct {
	Value       int
	Left, Right *Node
}

func Preorder(n *Node) []int {
	if n == nil {
		return nil
	}
	return append(append([]int{n.Value}, Preorder(n.Left)...), Preorder(n.Right)...)
}

func Inorder(n *Node) []int {
	if n == nil {
		return nil
	}
	return append(append(Inorder(n.Left), n.Value), Inorder(n.Right)...)
}

func Postorder(n *Node) []int {
	if n == nil {
		return nil
	}
	return append(append(Postorder(n.Left), Postorder(n.Right)...), n.Value)
}

func LevelOrder(root *Node) [][]int {
	var levels [][]int
	queue := []*Node{}
	if root != nil {
		queue = append(queue, root)
	}
	for len(queue) > 0 {
		var level []int
		var next []*Node
		for _, n := range queue {
			level = append(level, n.Value)
			if n.Left != nil {
				next = append(next, n.Left)
			}
			if n.Right != nil {
				next = append(next, n.Right)
			}
		}
		levels = append(levels, level)
		queue = next
	}
	return levels
}

func InorderIterative(root *Node) []int {
	var out []int
	var stack []*Node
	cur := root
	for cur != nil || len(stack) > 0 {
		for cur != nil { // go as far left as possible
			stack = append(stack, cur)
			cur = cur.Left
		}
		cur = stack[len(stack)-1]
		stack = stack[:len(stack)-1]
		out = append(out, cur.Value)
		cur = cur.Right
	}
	return out
}

func PreorderIterative(root *Node) []int {
	var out []int
	stack := []*Node{}
	if root != nil {
		stack = append(stack, root)
	}
	for len(stack) > 0 {
		n := stack[len(stack)-1]
		stack = stack[:len(stack)-1]
		out = append(out, n.Value)
		if n.Right != nil { // right first so left is processed first
			stack = append(stack, n.Right)
		}
		if n.Left != nil {
			stack = append(stack, n.Left)
		}
	}
	return out
}

func Height(n *Node) int {
	if n == nil {
		return -1
	}
	return 1 + max(Height(n.Left), Height(n.Right))
}

func CountLeaves(n *Node) int {
	switch {
	case n == nil:
		return 0
	case n.Left == nil && n.Right == nil:
		return 1
	default:
		return CountLeaves(n.Left) + CountLeaves(n.Right)
	}
}

// IsBalanced checks |h(left) - h(right)| <= 1 everywhere in a single O(n) pass.
func IsBalanced(root *Node) bool {
	const unbalanced = -2
	var checked func(*Node) int
	checked = func(n *Node) int {
		if n == nil {
			return -1
		}
		l, r := checked(n.Left), checked(n.Right)
		if l == unbalanced || r == unbalanced || l-r > 1 || r-l > 1 {
			return unbalanced
		}
		return 1 + max(l, r)
	}
	return checked(root) != unbalanced
}

func Mirror(n *Node) *Node {
	if n == nil {
		return nil
	}
	return &Node{n.Value, Mirror(n.Right), Mirror(n.Left)}
}
