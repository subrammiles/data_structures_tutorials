/*
 * Hash map with separate chaining (continues the hash_step1..5 lessons in this folder).
 * index = hash(key) % capacity; each bucket is a small array of [key, value] pairs.
 * Keep load factor <= 0.75 by doubling and REHASHING. Average O(1); worst case O(n).
 * Real JS: use the built-in Map.
 */
const assert = require('node:assert/strict');

function hashString(key) {
  // FNV-1a over the string's characters
  let h = 0x811c9dc5;
  for (const ch of String(key)) {
    h ^= ch.codePointAt(0);
    h = Math.imul(h, 0x01000193);
  }
  return h >>> 0; // unsigned 32-bit
}

class ChainingHashMap {
  static MAX_LOAD_FACTOR = 0.75;

  constructor() {
    this.buckets = Array.from({ length: 4 }, () => []);
    this.size = 0;
  }

  #bucket(key) {
    return this.buckets[hashString(key) % this.buckets.length];
  }

  set(key, value) {
    const bucket = this.#bucket(key);
    const entry = bucket.find(([k]) => k === key);
    if (entry) {
      const old = entry[1];
      entry[1] = value;
      return old;
    }
    bucket.push([key, value]);
    this.size++;
    if (this.size / this.buckets.length > ChainingHashMap.MAX_LOAD_FACTOR) this.#resize();
    return undefined;
  }

  get(key) {
    const entry = this.#bucket(key).find(([k]) => k === key);
    return entry ? entry[1] : undefined;
  }

  delete(key) {
    const bucket = this.#bucket(key);
    const i = bucket.findIndex(([k]) => k === key);
    if (i === -1) return undefined;
    const [, value] = bucket.splice(i, 1)[0];
    this.size--;
    return value;
  }

  #resize() {
    const old = this.buckets;
    this.buckets = Array.from({ length: old.length * 2 }, () => []);
    for (const bucket of old) for (const [k, v] of bucket) this.#bucket(k).push([k, v]);
  }

  longestChain() {
    return Math.max(...this.buckets.map((b) => b.length));
  }
}

const map = new ChainingHashMap();
map.set('cat', 2);
map.set('tiger', 7);
map.set('lion', 1);
assert.equal(map.get('cat'), 2);
assert.equal(map.set('cat', 3), 2);
assert.equal(map.get('cat'), 3);
assert.equal(map.get('ox'), undefined);
assert.equal(map.delete('tiger'), 7);
assert.equal(map.get('tiger'), undefined);
assert.equal(map.size, 2);

const big = new ChainingHashMap();
for (let i = 0; i < 1000; i++) big.set(i, i * i);
assert.equal(big.get(999), 998001);
assert.ok(big.size <= 0.75 * big.buckets.length);
assert.ok(big.longestChain() <= 5, `longest chain ${big.longestChain()}`);
console.log('all checks passed');
