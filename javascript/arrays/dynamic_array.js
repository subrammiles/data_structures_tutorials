/*
 * Dynamic array - how growable arrays work under the hood.
 * JS arrays already grow automatically; here we use a fixed-length typed array as "raw memory"
 * to see what the engine does for us.
 *
 *   get/set O(1) | push O(1) amortised (double when full) | insert/removeAt O(n) (shifting)
 */
const assert = require('node:assert/strict');

class DynamicArray {
  constructor() {
    this.data = new Int32Array(2);
    this.size = 0;
  }

  get capacity() {
    return this.data.length;
  }

  get(index) {
    this.#check(index);
    return this.data[index];
  }

  set(index, value) {
    this.#check(index);
    this.data[index] = value;
  }

  push(value) {
    if (this.size === this.data.length) this.#resize(this.data.length * 2);
    this.data[this.size++] = value;
  }

  insert(index, value) {
    if (index < 0 || index > this.size) throw new RangeError(`index ${index}`);
    if (this.size === this.data.length) this.#resize(this.data.length * 2);
    for (let i = this.size; i > index; i--) this.data[i] = this.data[i - 1]; // shift right
    this.data[index] = value;
    this.size++;
  }

  removeAt(index) {
    this.#check(index);
    const removed = this.data[index];
    for (let i = index; i < this.size - 1; i++) this.data[i] = this.data[i + 1]; // shift left
    this.size--;
    // shrink at 1/4 full (not 1/2) to avoid resizing back and forth
    if (this.size > 0 && this.size === this.data.length / 4) this.#resize(this.data.length / 2);
    return removed;
  }

  indexOf(value) {
    for (let i = 0; i < this.size; i++) if (this.data[i] === value) return i;
    return -1;
  }

  toArray() {
    return Array.from(this.data.subarray(0, this.size));
  }

  #resize(capacity) {
    const bigger = new Int32Array(capacity);
    for (let i = 0; i < this.size; i++) bigger[i] = this.data[i];
    this.data = bigger;
  }

  #check(index) {
    if (index < 0 || index >= this.size) throw new RangeError(`index ${index}, size ${this.size}`);
  }
}

const arr = new DynamicArray();
for (let i = 1; i <= 5; i++) arr.push(i * 10);
assert.deepEqual(arr.toArray(), [10, 20, 30, 40, 50]);
assert.equal(arr.capacity, 8); // 2 -> 4 -> 8
arr.insert(1, 15);
assert.deepEqual(arr.toArray(), [10, 15, 20, 30, 40, 50]);
assert.equal(arr.removeAt(0), 10);
assert.equal(arr.indexOf(40), 3);
assert.throws(() => arr.get(99), RangeError);
while (arr.size > 1) arr.removeAt(0);
assert.equal(arr.capacity, 2);
console.log('all checks passed');
