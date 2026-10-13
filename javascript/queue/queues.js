/*
 * Queue = First In, First Out.
 * array.shift() is O(n) (it re-indexes everything). A circular buffer with a front index and
 * modulo wrap-around gives O(1) enqueue/dequeue. Deque: O(1) at both ends.
 * Queue from two stacks: amortised O(1).
 */
const assert = require('node:assert/strict');

class CircularQueue {
  constructor(capacity) {
    this.data = new Array(capacity);
    this.front = 0;
    this.size = 0;
  }

  enqueue(x) {
    if (this.size === this.data.length) return false;
    this.data[(this.front + this.size) % this.data.length] = x;
    this.size++;
    return true;
  }

  dequeue() {
    if (this.size === 0) throw new Error('queue is empty');
    const x = this.data[this.front];
    this.front = (this.front + 1) % this.data.length;
    this.size--;
    return x;
  }

  isEmpty() {
    return this.size === 0;
  }
}

class CircularDeque {
  constructor(capacity) {
    this.data = new Array(capacity);
    this.front = 0;
    this.size = 0;
  }

  addFirst(x) {
    if (this.size === this.data.length) return false;
    this.front = (this.front - 1 + this.data.length) % this.data.length; // JS % can be negative
    this.data[this.front] = x;
    this.size++;
    return true;
  }

  addLast(x) {
    if (this.size === this.data.length) return false;
    this.data[(this.front + this.size) % this.data.length] = x;
    this.size++;
    return true;
  }

  removeFirst() {
    if (this.size === 0) throw new Error('deque is empty');
    const x = this.data[this.front];
    this.front = (this.front + 1) % this.data.length;
    this.size--;
    return x;
  }

  removeLast() {
    if (this.size === 0) throw new Error('deque is empty');
    this.size--;
    return this.data[(this.front + this.size) % this.data.length];
  }
}

class QueueWithTwoStacks {
  #in = [];
  #out = [];

  enqueue(x) {
    this.#in.push(x);
  }

  dequeue() {
    if (this.#out.length === 0) while (this.#in.length) this.#out.push(this.#in.pop());
    if (this.#out.length === 0) throw new Error('queue is empty');
    return this.#out.pop();
  }
}

const q = new CircularQueue(5);
for (let i = 1; i <= 5; i++) q.enqueue(i);
assert.equal(q.enqueue(99), false);
assert.equal(q.dequeue(), 1);
assert.equal(q.dequeue(), 2);
q.enqueue(6);
q.enqueue(7); // wraps around
const order = [];
while (!q.isEmpty()) order.push(q.dequeue());
assert.deepEqual(order, [3, 4, 5, 6, 7]);

const d = new CircularDeque(3);
d.addLast(2);
d.addFirst(1);
d.addLast(3);
assert.equal(d.addFirst(0), false);
assert.equal(d.removeLast(), 3);
assert.equal(d.removeFirst(), 1);

const s = new QueueWithTwoStacks();
s.enqueue(1);
s.enqueue(2);
assert.equal(s.dequeue(), 1);
s.enqueue(3);
assert.equal(s.dequeue(), 2);
assert.equal(s.dequeue(), 3);
console.log('all checks passed');
