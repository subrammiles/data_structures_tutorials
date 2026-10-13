/*
 * AVL tree: self-balancing BST, |height(left) - height(right)| <= 1 at every node -> O(log n).
 * Rotations: LL -> right, RR -> left, LR -> left(child) then right, RL -> right(child) then left.
 */
const assert = require('node:assert/strict');

const h = (n) => (n ? n.height : -1);
const update = (n) => {
  n.height = 1 + Math.max(h(n.left), h(n.right));
};
const balance = (n) => h(n.left) - h(n.right);

class AVLTree {
  root = null;
  rotations = 0;

  #rotateRight(y) {
    const x = y.left;
    y.left = x.right;
    x.right = y;
    update(y);
    update(x);
    this.rotations++;
    return x;
  }

  #rotateLeft(x) {
    const y = x.right;
    x.right = y.left;
    y.left = x;
    update(x);
    update(y);
    this.rotations++;
    return y;
  }

  #rebalance(n) {
    update(n);
    const b = balance(n);
    if (b > 1) {
      if (balance(n.left) < 0) n.left = this.#rotateLeft(n.left); // Left-Right
      return this.#rotateRight(n);
    }
    if (b < -1) {
      if (balance(n.right) > 0) n.right = this.#rotateRight(n.right); // Right-Left
      return this.#rotateLeft(n);
    }
    return n;
  }

  insert(value) {
    const ins = (n) => {
      if (!n) return { value, left: null, right: null, height: 0 };
      if (value < n.value) n.left = ins(n.left);
      else if (value > n.value) n.right = ins(n.right);
      else return n;
      return this.#rebalance(n);
    };
    this.root = ins(this.root);
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
      return this.#rebalance(n);
    };
    this.root = del(this.root, value);
  }

  height() {
    return h(this.root);
  }

  isBalanced(n = this.root) {
    return !n || (Math.abs(balance(n)) <= 1 && this.isBalanced(n.left) && this.isBalanced(n.right));
  }

  inorder(n = this.root) {
    return n ? [...this.inorder(n.left), n.value, ...this.inorder(n.right)] : [];
  }
}

const t = new AVLTree();
[30, 20, 10].forEach((v) => t.insert(v)); // Left-Left
assert.equal(t.root.value, 20);
assert.equal(t.rotations, 1);

const lr = new AVLTree();
[30, 10, 20].forEach((v) => lr.insert(v)); // Left-Right
assert.equal(lr.root.value, 20);
assert.equal(lr.rotations, 2);

const big = new AVLTree();
for (let i = 1; i <= 1000; i++) big.insert(i);
assert.ok(big.height() <= 14);
assert.ok(big.isBalanced());
for (let i = 1; i <= 1000; i += 2) big.delete(i);
assert.ok(big.isBalanced());
assert.deepEqual(big.inorder().slice(0, 3), [2, 4, 6]);
console.log('all checks passed');
