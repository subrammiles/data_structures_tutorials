/*
 * Skip list: sorted linked list with express lanes; each node is promoted with probability 1/2.
 * Expected O(log n) search/insert/delete. A seeded PRNG keeps the demo deterministic.
 */
const assert = require('node:assert/strict');

const MAX_LEVEL = 16;

function mulberry32(seed) {
  return () => {
    seed = (seed + 0x6d2b79f5) | 0;
    let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

class SkipList {
  constructor(seed = 1) {
    this.head = { value: -Infinity, next: new Array(MAX_LEVEL).fill(null) };
    this.levels = 1;
    this.random = mulberry32(seed);
  }

  #randomLevel() {
    let level = 1;
    while (level < MAX_LEVEL && this.random() < 0.5) level++;
    return level;
  }

  #predecessors(value) {
    const update = new Array(MAX_LEVEL).fill(this.head);
    let n = this.head;
    for (let lvl = this.levels - 1; lvl >= 0; lvl--) {
      while (n.next[lvl] && n.next[lvl].value < value) n = n.next[lvl];
      update[lvl] = n;
    }
    return update;
  }

  contains(value) {
    const n = this.#predecessors(value)[0].next[0];
    return n !== null && n.value === value;
  }

  insert(value) {
    const update = this.#predecessors(value);
    if (update[0].next[0]?.value === value) return;
    const lvl = this.#randomLevel();
    this.levels = Math.max(this.levels, lvl);
    const node = { value, next: new Array(lvl).fill(null) };
    for (let i = 0; i < lvl; i++) {
      node.next[i] = update[i].next[i];
      update[i].next[i] = node;
    }
  }

  delete(value) {
    const update = this.#predecessors(value);
    const target = update[0].next[0];
    if (!target || target.value !== value) return false;
    for (let i = 0; i < target.next.length; i++) update[i].next[i] = target.next[i];
    while (this.levels > 1 && !this.head.next[this.levels - 1]) this.levels--;
    return true;
  }

  toArray() {
    const out = [];
    for (let n = this.head.next[0]; n; n = n.next[0]) out.push(n.value);
    return out;
  }
}

const s = new SkipList(42);
[30, 10, 50, 20, 40, 5].forEach((v) => s.insert(v));
assert.deepEqual(s.toArray(), [5, 10, 20, 30, 40, 50]);
assert.ok(s.contains(40));
assert.equal(s.contains(45), false);
assert.ok(s.delete(30));
assert.deepEqual(s.toArray(), [5, 10, 20, 40, 50]);

const big = new SkipList(7);
for (let i = 0; i < 50_000; i++) big.insert(i * 2);
assert.ok(big.levels >= 10 && big.levels <= MAX_LEVEL, `levels ${big.levels}`);
assert.ok(big.contains(12_346) && !big.contains(12_347));
console.log('all checks passed');
