/*
 * Bloom filter: false positives possible, false negatives impossible. m bits, k hashes.
 * m = -n ln p / (ln 2)^2, k = (m / n) ln 2.
 */
const assert = require('node:assert/strict');
const { createHash } = require('node:crypto');

class BloomFilter {
  constructor(expectedItems, falsePositiveRate) {
    this.m = Math.ceil((-expectedItems * Math.log(falsePositiveRate)) / Math.log(2) ** 2);
    this.k = Math.max(1, Math.round((this.m / expectedItems) * Math.log(2)));
    this.bits = new Uint8Array(Math.ceil(this.m / 8));
  }

  #indexes(item) {
    const digest = createHash('sha256').update(item).digest();
    const h1 = digest.readUInt32LE(0);
    const h2 = digest.readUInt32LE(4) | 1;
    return Array.from({ length: this.k }, (_, i) => (h1 + i * h2) % this.m); // double hashing
  }

  add(item) {
    for (const i of this.#indexes(item)) this.bits[i >> 3] |= 1 << (i & 7);
  }

  mightContain(item) {
    return this.#indexes(item).every((i) => this.bits[i >> 3] & (1 << (i & 7)));
  }
}

const bf = new BloomFilter(10_000, 0.01);
console.log(`bits m = ${bf.m}, hash functions k = ${bf.k}`);
for (let i = 0; i < 10_000; i++) bf.add(`user-${i}`);
for (let i = 0; i < 10_000; i++) assert.ok(bf.mightContain(`user-${i}`)); // no false negatives
let falsePositives = 0;
for (let i = 0; i < 50_000; i++) if (bf.mightContain(`other-${i}`)) falsePositives++;
const rate = falsePositives / 50_000;
console.log(`false positive rate: ${rate.toFixed(4)}`);
assert.ok(rate < 0.02);
console.log('all checks passed');
