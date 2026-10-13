/*
 * Union-find with path compression + union by size: ~O(1) amortised per operation.
 * Kruskal's MST: sort edges, keep an edge if it joins two different groups.
 */
const assert = require('node:assert/strict');

class UnionFind {
  constructor(n) {
    this.parent = Array.from({ length: n }, (_, i) => i);
    this.size = new Array(n).fill(1);
    this.groups = n;
  }

  find(x) {
    while (this.parent[x] !== x) {
      this.parent[x] = this.parent[this.parent[x]]; // path halving
      x = this.parent[x];
    }
    return x;
  }

  union(a, b) {
    let ra = this.find(a);
    let rb = this.find(b);
    if (ra === rb) return false;
    if (this.size[ra] < this.size[rb]) [ra, rb] = [rb, ra];
    this.parent[rb] = ra;
    this.size[ra] += this.size[rb];
    this.groups--;
    return true;
  }

  connected(a, b) {
    return this.find(a) === this.find(b);
  }

  groupSize(x) {
    return this.size[this.find(x)];
  }
}

function kruskalMstWeight(n, edges) {
  const uf = new UnionFind(n);
  let total = 0;
  let used = 0;
  for (const [u, v, w] of [...edges].sort((a, b) => a[2] - b[2])) {
    if (uf.union(u, v)) {
      total += w;
      used++;
    }
  }
  return used === n - 1 ? total : -1;
}

const uf = new UnionFind(6);
uf.union(0, 1);
uf.union(1, 2);
uf.union(3, 4);
assert.ok(uf.connected(0, 2));
assert.equal(uf.connected(0, 3), false);
assert.equal(uf.groups, 3);
assert.equal(uf.groupSize(0), 3);
assert.equal(uf.union(0, 2), false);
const cables = [[0, 1, 2], [0, 3, 6], [1, 2, 3], [1, 3, 8], [1, 4, 5], [2, 4, 7], [3, 4, 9]];
assert.equal(kruskalMstWeight(5, cables), 16);
console.log('all checks passed');
