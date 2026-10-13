/*
 * Binary search tree: left < node < right, inorder is sorted. Operations O(h).
 * Delete: leaf -> remove; one child -> replace; two children -> copy inorder successor, delete it.
 * Subtree sizes make kthSmallest O(h). Sorted inserts make h = n -> see avl_tree.js.
 */
const assert = require('node:assert/strict');

const size = (n) => (n ? n.size : 0);

class BinarySearchTree {
  root = null;

  insert(value) {
    const ins = (n) => {
      if (!n) return { value, left: null, right: null, size: 1 };
      if (value < n.value) n.left = ins(n.left);
      else if (value > n.value) n.right = ins(n.right);
      n.size = 1 + size(n.left) + size(n.right);
      return n;
    };
    this.root = ins(this.root);
  }

  contains(value) {
    let n = this.root;
    while (n) {
      if (value === n.value) return true;
      n = value < n.value ? n.left : n.right;
    }
    return false;
  }

  delete(value) {
    const del = (n, v) => {
      if (!n) return null;
      if (v < n.value) n.left = del(n.left, v);
      else if (v > n.value) n.right = del(n.right, v);
      else {
        if (!n.left) return n.right;
        if (!n.right) return n.left;
        let s = n.right;
        while (s.left) s = s.left;
        n.value = s.value;
        n.right = del(n.right, s.value);
      }
      n.size = 1 + size(n.left) + size(n.right);
      return n;
    };
    this.root = del(this.root, value);
  }

  min() {
    let n = this.root;
    while (n.left) n = n.left;
    return n.value;
  }

  max() {
    let n = this.root;
    while (n.right) n = n.right;
    return n.value;
  }

  floor(x) {
    let best = null;
    for (let n = this.root; n; ) {
      if (n.value === x) return x;
      if (n.value < x) {
        best = n.value;
        n = n.right;
      } else n = n.left;
    }
    return best;
  }

  ceiling(x) {
    let best = null;
    for (let n = this.root; n; ) {
      if (n.value === x) return x;
      if (n.value > x) {
        best = n.value;
        n = n.left;
      } else n = n.right;
    }
    return best;
  }

  kthSmallest(k) {
    let n = this.root;
    while (n) {
      const left = size(n.left);
      if (k === left + 1) return n.value;
      if (k <= left) n = n.left;
      else {
        k -= left + 1;
        n = n.right;
      }
    }
    throw new RangeError('k out of range');
  }

  height() {
    const h = (n) => (n ? 1 + Math.max(h(n.left), h(n.right)) : -1);
    return h(this.root);
  }

  inorder() {
    const out = [];
    const stack = [];
    let cur = this.root;
    while (cur || stack.length) {
      while (cur) {
        stack.push(cur);
        cur = cur.left;
      }
      cur = stack.pop();
      out.push(cur.value);
      cur = cur.right;
    }
    return out;
  }
}

const bst = new BinarySearchTree();
[50, 30, 70, 20, 40, 60, 80, 35].forEach((v) => bst.insert(v));
assert.deepEqual(bst.inorder(), [20, 30, 35, 40, 50, 60, 70, 80]);
assert.ok(bst.contains(60));
assert.equal(bst.contains(65), false);
assert.equal(bst.min(), 20);
assert.equal(bst.max(), 80);
assert.equal(bst.floor(65), 60);
assert.equal(bst.ceiling(36), 40);
assert.equal(bst.floor(1), null);
assert.equal(bst.kthSmallest(3), 35);
bst.delete(20);
bst.delete(40);
bst.delete(50);
assert.deepEqual(bst.inorder(), [30, 35, 60, 70, 80]);
assert.equal(bst.kthSmallest(3), 60);

const skewed = new BinarySearchTree();
for (let i = 1; i <= 100; i++) skewed.insert(i);
assert.equal(skewed.height(), 99);
console.log('all checks passed');
