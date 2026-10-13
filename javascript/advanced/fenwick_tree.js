/*
 * Fenwick tree (BIT): prefix sums and point updates in O(log n). i & -i = lowest set bit.
 */
const assert = require('node:assert/strict');

class FenwickTree {
  constructor(n) {
    this.tree = new Array(n + 1).fill(0); // 1-based
  }

  add(i, delta) {
    for (; i < this.tree.length; i += i & -i) this.tree[i] += delta;
  }

  prefixSum(i) {
    let s = 0;
    for (; i > 0; i -= i & -i) s += this.tree[i];
    return s;
  }

  rangeSum(l, r) {
    return this.prefixSum(r) - this.prefixSum(l - 1);
  }
}

const a = [3, 2, -1, 6, 5, 4, -3, 3];
const ft = new FenwickTree(a.length);
a.forEach((v, i) => ft.add(i + 1, v));
assert.equal(ft.prefixSum(7), 16);
assert.equal(ft.rangeSum(2, 5), 12);
ft.add(3, 10);
assert.equal(ft.rangeSum(2, 5), 22);
assert.equal(ft.prefixSum(8), 29);
console.log('all checks passed');
