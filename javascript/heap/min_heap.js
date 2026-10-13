/*
 * Binary min-heap in an array: parent(i) = (i-1)>>1, children 2i+1 and 2i+2.
 * peek O(1) | push/pop O(log n) | heapify O(n) | heap sort O(n log n) in place.
 * JS has no built-in priority queue - this class is what you write in interviews.
 */
const assert = require('node:assert/strict');

class MinHeap {
  constructor(values = [], compare = (a, b) => a - b) {
    this.a = [...values];
    this.compare = compare;
    for (let i = (this.a.length >> 1) - 1; i >= 0; i--) this.#siftDown(i); // O(n) heapify
  }

  get size() {
    return this.a.length;
  }

  push(x) {
    this.a.push(x);
    this.#siftUp(this.a.length - 1);
  }

  peek() {
    if (!this.a.length) throw new Error('heap is empty');
    return this.a[0];
  }

  pop() {
    const top = this.peek();
    const last = this.a.pop();
    if (this.a.length) {
      this.a[0] = last;
      this.#siftDown(0);
    }
    return top;
  }

  #less(i, j) {
    return this.compare(this.a[i], this.a[j]) < 0;
  }

  #swap(i, j) {
    [this.a[i], this.a[j]] = [this.a[j], this.a[i]];
  }

  #siftUp(i) {
    while (i > 0 && this.#less(i, (i - 1) >> 1)) {
      this.#swap(i, (i - 1) >> 1);
      i = (i - 1) >> 1;
    }
  }

  #siftDown(i) {
    for (;;) {
      const l = 2 * i + 1;
      const r = l + 1;
      let smallest = i;
      if (l < this.a.length && this.#less(l, smallest)) smallest = l;
      if (r < this.a.length && this.#less(r, smallest)) smallest = r;
      if (smallest === i) return;
      this.#swap(i, smallest);
      i = smallest;
    }
  }

  isValid() {
    return this.a.every((x, i) => i === 0 || this.compare(this.a[(i - 1) >> 1], x) <= 0);
  }
}

function heapSort(a) {
  const siftDown = (i, n) => {
    for (;;) {
      const l = 2 * i + 1;
      const r = l + 1;
      let largest = i;
      if (l < n && a[l] > a[largest]) largest = l;
      if (r < n && a[r] > a[largest]) largest = r;
      if (largest === i) return;
      [a[i], a[largest]] = [a[largest], a[i]];
      i = largest;
    }
  };
  for (let i = (a.length >> 1) - 1; i >= 0; i--) siftDown(i, a.length);
  for (let end = a.length - 1; end > 0; end--) {
    [a[0], a[end]] = [a[end], a[0]];
    siftDown(0, end);
  }
  return a;
}

module.exports = { MinHeap };

if (require.main === module) {
  const heap = new MinHeap();
  [7, 3, 9, 1, 4, 2, 8, 6, 5].forEach((x) => heap.push(x));
  assert.ok(heap.isValid());
  assert.equal(heap.peek(), 1);
  const out = [];
  while (heap.size) out.push(heap.pop());
  assert.deepEqual(out, [1, 2, 3, 4, 5, 6, 7, 8, 9]);

  const built = new MinHeap([5, 9, 3, 1, 8]);
  assert.ok(built.isValid());
  assert.equal(built.peek(), 1);
  assert.deepEqual(heapSort([5, 2, 9, 1, 5, 6]), [1, 2, 5, 5, 6, 9]);
  assert.throws(() => new MinHeap().pop());
  console.log('all checks passed');
}
