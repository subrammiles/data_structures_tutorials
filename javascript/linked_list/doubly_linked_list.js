/*
 * Doubly linked list with sentinel head/tail nodes: null <- [A] <-> [B] <-> [C] -> null
 * O(1) removal of a node you hold + backwards traversal (the basis of an LRU cache).
 */
const assert = require('node:assert/strict');

class Node {
  constructor(value = null) {
    this.value = value;
    this.prev = this.next = null;
  }
}

class DoublyLinkedList {
  constructor() {
    this.head = new Node();
    this.tail = new Node();
    this.head.next = this.tail;
    this.tail.prev = this.head;
    this.size = 0;
  }

  #insertBetween(value, before, after) {
    const node = new Node(value);
    node.prev = before;
    node.next = after;
    before.next = after.prev = node;
    this.size++;
    return node;
  }

  addFirst(value) {
    return this.#insertBetween(value, this.head, this.head.next);
  }

  addLast(value) {
    return this.#insertBetween(value, this.tail.prev, this.tail);
  }

  removeNode(node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
    this.size--;
  }

  removeFirst() {
    if (this.size === 0) throw new Error('list is empty');
    const node = this.head.next;
    this.removeNode(node);
    return node.value;
  }

  removeLast() {
    if (this.size === 0) throw new Error('list is empty');
    const node = this.tail.prev;
    this.removeNode(node);
    return node.value;
  }

  forward() {
    const out = [];
    for (let n = this.head.next; n !== this.tail; n = n.next) out.push(n.value);
    return out;
  }

  backward() {
    const out = [];
    for (let n = this.tail.prev; n !== this.head; n = n.prev) out.push(n.value);
    return out;
  }
}

const list = new DoublyLinkedList();
list.addLast(2);
const three = list.addLast(3);
list.addFirst(1);
list.addLast(4);
assert.deepEqual(list.forward(), [1, 2, 3, 4]);
assert.deepEqual(list.backward(), [4, 3, 2, 1]);
list.removeNode(three);
assert.deepEqual(list.forward(), [1, 2, 4]);
assert.equal(list.removeFirst(), 1);
assert.equal(list.removeLast(), 4);
assert.deepEqual(list.forward(), [2]);
list.removeFirst();
assert.throws(() => list.removeFirst());
console.log('all checks passed');
