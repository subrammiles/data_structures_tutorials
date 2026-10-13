/*
 * Stack = Last In, First Out. push / pop / peek O(1).
 * ArrayStack (array push/pop), LinkedStack (head of a linked list), MinStack (O(1) getMin).
 * In everyday JS just use an array's push() and pop().
 */
const assert = require('node:assert/strict');

class ArrayStack {
  #items = [];

  push(x) {
    this.#items.push(x);
  }

  pop() {
    if (this.isEmpty()) throw new Error('stack underflow');
    return this.#items.pop();
  }

  peek() {
    if (this.isEmpty()) throw new Error('stack is empty');
    return this.#items[this.#items.length - 1];
  }

  isEmpty() {
    return this.#items.length === 0;
  }
}

class LinkedStack {
  #top = null;

  push(x) {
    this.#top = { value: x, next: this.#top };
  }

  pop() {
    if (!this.#top) throw new Error('stack underflow');
    const { value } = this.#top;
    this.#top = this.#top.next;
    return value;
  }
}

class MinStack {
  #items = []; // [value, minSoFar]

  push(x) {
    const min = this.#items.length ? Math.min(x, this.#items[this.#items.length - 1][1]) : x;
    this.#items.push([x, min]);
  }

  pop() {
    return this.#items.pop()[0];
  }

  getMin() {
    return this.#items[this.#items.length - 1][1];
  }
}

function isBalanced(s) {
  const pairs = { ')': '(', ']': '[', '}': '{' };
  const stack = new ArrayStack();
  for (const ch of s) {
    if ('([{'.includes(ch)) stack.push(ch);
    else if (ch in pairs && (stack.isEmpty() || stack.pop() !== pairs[ch])) return false;
  }
  return stack.isEmpty();
}

const a = new ArrayStack();
for (let i = 1; i <= 6; i++) a.push(i);
assert.equal(a.pop(), 6);
assert.equal(a.peek(), 5);

const l = new LinkedStack();
l.push(1);
l.push(2);
assert.equal(l.pop(), 2);
assert.equal(l.pop(), 1);
assert.throws(() => l.pop());

const m = new MinStack();
[5, 2, 7].forEach((x) => m.push(x));
assert.equal(m.getMin(), 2);
m.pop();
m.pop();
assert.equal(m.getMin(), 5);

assert.ok(isBalanced('{[()()]}'));
assert.equal(isBalanced('([)]'), false);
assert.equal(isBalanced('(('), false);
console.log('all checks passed');
