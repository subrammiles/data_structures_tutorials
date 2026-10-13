/*
 * Segment tree: range minimum + point update, both O(log n). Memory 4n.
 */
const assert = require('node:assert/strict');

class SegmentTree {
  constructor(values) {
    this.n = values.length;
    this.tree = new Array(4 * this.n).fill(Infinity);
    this.#build(1, 0, this.n - 1, values);
  }

  #build(node, l, r, values) {
    if (l === r) {
      this.tree[node] = values[l];
      return;
    }
    const mid = (l + r) >> 1;
    this.#build(2 * node, l, mid, values);
    this.#build(2 * node + 1, mid + 1, r, values);
    this.tree[node] = Math.min(this.tree[2 * node], this.tree[2 * node + 1]);
  }

  update(index, value, node = 1, l = 0, r = this.n - 1) {
    if (l === r) {
      this.tree[node] = value;
      return;
    }
    const mid = (l + r) >> 1;
    if (index <= mid) this.update(index, value, 2 * node, l, mid);
    else this.update(index, value, 2 * node + 1, mid + 1, r);
    this.tree[node] = Math.min(this.tree[2 * node], this.tree[2 * node + 1]);
  }

  rangeMin(ql, qr, node = 1, l = 0, r = this.n - 1) {
    if (qr < l || r < ql) return Infinity; // no overlap
    if (ql <= l && r <= qr) return this.tree[node]; // full overlap
    const mid = (l + r) >> 1;
    return Math.min(this.rangeMin(ql, qr, 2 * node, l, mid), this.rangeMin(ql, qr, 2 * node + 1, mid + 1, r));
  }
}

const st = new SegmentTree([5, 2, 6, 3, 7, 1, 4]);
assert.equal(st.rangeMin(0, 6), 1);
assert.equal(st.rangeMin(0, 3), 2);
assert.equal(st.rangeMin(2, 4), 3);
st.update(3, 0);
assert.equal(st.rangeMin(2, 4), 0);
assert.equal(st.rangeMin(6, 6), 4);
console.log('all checks passed');
