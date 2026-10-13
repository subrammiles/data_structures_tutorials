/*
 * Sparse table: O(1) range minimum on a static array after O(n log n) build.
 */
const assert = require('node:assert/strict');

class SparseTable {
  constructor(a) {
    const n = a.length;
    this.log = new Array(n + 1).fill(0);
    for (let i = 2; i <= n; i++) this.log[i] = this.log[i >> 1] + 1;
    this.table = [a.slice()];
    for (let k = 1; 1 << k <= n; k++) {
      const prev = this.table[k - 1];
      const half = 1 << (k - 1);
      this.table.push(Array.from({ length: n - (1 << k) + 1 }, (_, i) => Math.min(prev[i], prev[i + half])));
    }
  }

  rangeMin(l, r) {
    const k = this.log[r - l + 1];
    return Math.min(this.table[k][l], this.table[k][r - (1 << k) + 1]);
  }
}

const st = new SparseTable([7, 2, 3, 0, 5, 10, 3, 12, 18]);
assert.equal(st.rangeMin(0, 2), 2);
assert.equal(st.rangeMin(4, 7), 3);
assert.equal(st.rangeMin(0, 8), 0);
assert.equal(st.rangeMin(7, 8), 12);
console.log('all checks passed');
