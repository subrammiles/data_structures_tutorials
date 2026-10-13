package trees

import (
	"reflect"
	"testing"
)

func sample() *Node {
	return &Node{1, &Node{2, &Node{Value: 4}, &Node{Value: 5}}, &Node{3, nil, &Node{Value: 6}}}
}

func TestTraversals(t *testing.T) {
	root := sample()
	cases := map[string][2][]int{
		"preorder":  {Preorder(root), {1, 2, 4, 5, 3, 6}},
		"inorder":   {Inorder(root), {4, 2, 5, 1, 3, 6}},
		"postorder": {Postorder(root), {4, 5, 2, 6, 3, 1}},
		"pre iter":  {PreorderIterative(root), {1, 2, 4, 5, 3, 6}},
		"in iter":   {InorderIterative(root), {4, 2, 5, 1, 3, 6}},
		"mirror":    {Inorder(Mirror(root)), {6, 3, 1, 5, 2, 4}},
	}
	for name, c := range cases {
		if !reflect.DeepEqual(c[0], c[1]) {
			t.Errorf("%s: got %v want %v", name, c[0], c[1])
		}
	}
	if !reflect.DeepEqual(LevelOrder(root), [][]int{{1}, {2, 3}, {4, 5, 6}}) {
		t.Errorf("level order %v", LevelOrder(root))
	}
	if Height(root) != 2 || CountLeaves(root) != 3 || !IsBalanced(root) {
		t.Error("height / leaves / balanced")
	}
	if IsBalanced(&Node{1, &Node{2, &Node{Value: 3}, nil}, nil}) {
		t.Error("chain should be unbalanced")
	}
}

func TestBST(t *testing.T) {
	var bst BST
	for _, v := range []int{50, 30, 70, 20, 40, 60, 80, 35} {
		bst.Insert(v)
	}
	if !reflect.DeepEqual(bst.Inorder(), []int{20, 30, 35, 40, 50, 60, 70, 80}) {
		t.Fatalf("inorder %v", bst.Inorder())
	}
	if !bst.Contains(60) || bst.Contains(65) {
		t.Fatal("contains")
	}
	if f, _ := bst.Floor(65); f != 60 {
		t.Fatal("floor")
	}
	if c, _ := bst.Ceiling(36); c != 40 {
		t.Fatal("ceiling")
	}
	if _, ok := bst.Floor(1); ok {
		t.Fatal("floor below min")
	}
	if bst.KthSmallest(3) != 35 {
		t.Fatal("kth")
	}
	bst.Delete(20)
	bst.Delete(40)
	bst.Delete(50)
	if !reflect.DeepEqual(bst.Inorder(), []int{30, 35, 60, 70, 80}) || bst.KthSmallest(3) != 60 {
		t.Fatalf("after delete %v", bst.Inorder())
	}
	var skewed BST
	for i := 1; i <= 100; i++ {
		skewed.Insert(i)
	}
	if skewed.Height() != 99 {
		t.Fatal("sorted input should degenerate")
	}
}

func TestAVL(t *testing.T) {
	var ll AVL
	for _, v := range []int{30, 20, 10} {
		ll.Insert(v)
	}
	if ll.RootValue() != 20 || ll.Rotations != 1 {
		t.Fatal("LL case")
	}
	var lr AVL
	for _, v := range []int{30, 10, 20} {
		lr.Insert(v)
	}
	if lr.RootValue() != 20 || lr.Rotations != 2 {
		t.Fatal("LR case")
	}
	var big AVL
	for i := 1; i <= 1000; i++ {
		big.Insert(i)
	}
	if big.Height() > 14 || !big.IsBalanced() {
		t.Fatalf("height %d", big.Height())
	}
	for i := 1; i <= 1000; i += 2 {
		big.Delete(i)
	}
	if !big.IsBalanced() {
		t.Fatal("unbalanced after deletes")
	}
}
