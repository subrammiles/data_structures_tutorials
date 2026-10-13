/*
 * Binary tree traversals (all O(n)).
 *            1
 *          /   \
 *         2     3
 *        / \     \
 *       4   5     6
 * preorder 1 2 4 5 3 6 | inorder 4 2 5 1 3 6 | postorder 4 5 2 6 3 1 | level order 1 2 3 4 5 6
 */
const assert = require('node:assert/strict');

class Node {
  constructor(value, left = null, right = null) {
    this.value = value;
    this.left = left;
    this.right = right;
  }
}

const preorder = (n) => (n ? [n.value, ...preorder(n.left), ...preorder(n.right)] : []);
const inorder = (n) => (n ? [...inorder(n.left), n.value, ...inorder(n.right)] : []);
const postorder = (n) => (n ? [...postorder(n.left), ...postorder(n.right), n.value] : []);

function levelOrder(root) {
  const levels = [];
  let queue = root ? [root] : [];
  while (queue.length) {
    levels.push(queue.map((n) => n.value));
    queue = queue.flatMap((n) => [n.left, n.right].filter(Boolean)); // next level
  }
  return levels;
}

function preorderIterative(root) {
  const out = [];
  const stack = root ? [root] : [];
  while (stack.length) {
    const n = stack.pop();
    out.push(n.value);
    if (n.right) stack.push(n.right); // right first so left is processed first
    if (n.left) stack.push(n.left);
  }
  return out;
}

function inorderIterative(root) {
  const out = [];
  const stack = [];
  let cur = root;
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

function postorderIterative(root) {
  // reverse of (root, right, left)
  const out = [];
  const stack = root ? [root] : [];
  while (stack.length) {
    const n = stack.pop();
    out.push(n.value);
    if (n.left) stack.push(n.left);
    if (n.right) stack.push(n.right);
  }
  return out.reverse();
}

const height = (n) => (n ? 1 + Math.max(height(n.left), height(n.right)) : -1);
const countNodes = (n) => (n ? 1 + countNodes(n.left) + countNodes(n.right) : 0);
const countLeaves = (n) => (!n ? 0 : !n.left && !n.right ? 1 : countLeaves(n.left) + countLeaves(n.right));

function isBalanced(root) {
  const UNBALANCED = -2;
  const checked = (n) => {
    if (!n) return -1;
    const l = checked(n.left);
    const r = checked(n.right);
    if (l === UNBALANCED || r === UNBALANCED || Math.abs(l - r) > 1) return UNBALANCED;
    return 1 + Math.max(l, r);
  };
  return checked(root) !== UNBALANCED;
}

const mirror = (n) => (n ? new Node(n.value, mirror(n.right), mirror(n.left)) : null);

const root = new Node(1, new Node(2, new Node(4), new Node(5)), new Node(3, null, new Node(6)));
assert.deepEqual(preorder(root), [1, 2, 4, 5, 3, 6]);
assert.deepEqual(inorder(root), [4, 2, 5, 1, 3, 6]);
assert.deepEqual(postorder(root), [4, 5, 2, 6, 3, 1]);
assert.deepEqual(levelOrder(root), [[1], [2, 3], [4, 5, 6]]);
assert.deepEqual(preorderIterative(root), preorder(root));
assert.deepEqual(inorderIterative(root), inorder(root));
assert.deepEqual(postorderIterative(root), postorder(root));
assert.equal(height(root), 2);
assert.equal(countNodes(root), 6);
assert.equal(countLeaves(root), 3);
assert.ok(isBalanced(root));
assert.equal(isBalanced(new Node(1, new Node(2, new Node(3)))), false);
assert.deepEqual(inorder(mirror(root)), [6, 3, 1, 5, 2, 4]);
console.log('all checks passed');
