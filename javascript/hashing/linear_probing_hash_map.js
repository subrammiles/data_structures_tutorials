/*
 * Hash map with open addressing (linear probing): entries live directly in one array; on a
 * collision try the next slot. Deletes leave a TOMBSTONE so later keys stay reachable.
 * Keep load factor <= 0.5.
 */
const assert = require('node:assert/strict');

const TOMBSTONE = Symbol('deleted');

class LinearProbingHashMap {
  constructor(hashFn = (key) => {
    let h = 0;
    for (const ch of String(key)) h = (Math.imul(h, 31) + ch.codePointAt(0)) | 0;
    return h >>> 0;
  }) {
    this.hash = hashFn;
    this.keys = new Array(8).fill(undefined);
    this.values = new Array(8);
    this.size = 0;
    this.tombstones = 0;
  }

  set(key, value) {
    if ((this.size + this.tombstones + 1) * 2 > this.keys.length) this.#resize();
    let i = this.hash(key) % this.keys.length;
    let firstTombstone = -1;
    while (this.keys[i] !== undefined) {
      if (this.keys[i] === TOMBSTONE) {
        if (firstTombstone === -1) firstTombstone = i;
      } else if (this.keys[i] === key) {
        this.values[i] = value;
        return;
      }
      i = (i + 1) % this.keys.length;
    }
    if (firstTombstone !== -1) {
      i = firstTombstone; // reuse a deleted slot
      this.tombstones--;
    }
    this.keys[i] = key;
    this.values[i] = value;
    this.size++;
  }

  get(key) {
    const i = this.#find(key);
    return i === -1 ? undefined : this.values[i];
  }

  delete(key) {
    const i = this.#find(key);
    if (i === -1) return false;
    this.keys[i] = TOMBSTONE;
    this.values[i] = undefined;
    this.size--;
    this.tombstones++;
    return true;
  }

  #find(key) {
    let i = this.hash(key) % this.keys.length;
    while (this.keys[i] !== undefined) {
      if (this.keys[i] === key) return i;
      i = (i + 1) % this.keys.length;
    }
    return -1;
  }

  #resize() {
    const oldKeys = this.keys;
    const oldValues = this.values;
    this.keys = new Array(oldKeys.length * 2).fill(undefined);
    this.values = new Array(oldKeys.length * 2);
    this.size = 0;
    this.tombstones = 0;
    oldKeys.forEach((k, i) => {
      if (k !== undefined && k !== TOMBSTONE) this.set(k, oldValues[i]);
    });
  }
}

const collide = new LinearProbingHashMap(() => 42); // every key collides
collide.set('a', 1);
collide.set('b', 2);
assert.equal(collide.get('a'), 1);
assert.equal(collide.get('b'), 2);
assert.ok(collide.delete('a'));
assert.equal(collide.get('b'), 2); // found past the tombstone
collide.set('a', 10);
assert.equal(collide.get('a'), 10);

const map = new LinearProbingHashMap();
for (let i = 0; i < 100; i++) map.set(`key${i}`, i);
assert.equal(map.get('key77'), 77);
assert.equal(map.size, 100);
console.log('all checks passed');
