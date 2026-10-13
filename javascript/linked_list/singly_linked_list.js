/*
 * Singly linked list: head -> [10] -> [20] -> [30] -> null
 *   index access O(n) | add/remove at head O(1) | append with tail pointer O(1)
 * Interview essentials: reverse in place, middle (slow/fast pointers), Floyd cycle detection.
 */
const assert = require('node:assert/strict');

class Node {
  constructor(value, next = null) {
    this.value = value;
    this.next = next;
  }
}

class SinglyLinkedList {
  constructor() {
    this.head = this.tail = null;
    this.size = 0;
  }

  addFirst(value) {
    this.head = new Node(value, this.head);
    if (!this.tail) this.tail = this.head;
    this.size++;
  }

  addLast(value) {
    const node = new Node(value);
    if (!this.tail) this.head = this.tail = node;
    else this.tail = this.tail.next = node;
    this.size++;
  }

  insertAt(index, value) {
    if (index < 0 || index > this.size) throw new RangeError(`index ${index}`);
    if (index === 0) return this.addFirst(value);
    if (index === this.size) return this.addLast(value);
    let prev = this.head;
    for (let i = 0; i < index - 1; i++) prev = prev.next;
    prev.next = new Node(value, prev.next);
    this.size++;
  }

  remove(value) {
    const dummy = new Node(null, this.head); // avoids a special case for removing the head
    let prev = dummy;
    while (prev.next && prev.next.value !== value) prev = prev.next;
    if (!prev.next) return false;
    if (prev.next === this.tail) this.tail = prev === dummy ? null : prev;
    prev.next = prev.next.next;
    this.head = dummy.next;
    this.size--;
    return true;
  }

  reverse() {
    let prev = null;
    let current = this.head;
    this.tail = this.head;
    while (current) {
      const next = current.next; // remember the rest
      current.next = prev; // turn the arrow around
      prev = current;
      current = next;
    }
    this.head = prev;
  }

  middle() {
    let slow = this.head;
    let fast = this.head;
    while (fast && fast.next) {
      slow = slow.next;
      fast = fast.next.next;
    }
    return slow.value;
  }

  toArray() {
    const out = [];
    for (let n = this.head; n; n = n.next) out.push(n.value);
    return out;
  }
}

function hasCycle(head) {
  let slow = head;
  let fast = head;
  while (fast && fast.next) {
    slow = slow.next;
    fast = fast.next.next;
    if (slow === fast) return true;
  }
  return false;
}

const list = new SinglyLinkedList();
list.addLast(20);
list.addLast(30);
list.addFirst(10);
list.insertAt(3, 40);
assert.deepEqual(list.toArray(), [10, 20, 30, 40]);
assert.equal(list.middle(), 30);
list.reverse();
assert.deepEqual(list.toArray(), [40, 30, 20, 10]);
assert.ok(list.remove(10));
list.addLast(5);
assert.deepEqual(list.toArray(), [40, 30, 20, 5]); // tail still correct
assert.equal(list.remove(99), false);
assert.equal(list.size, 4);

const a = new Node(1);
const b = new Node(2);
const c = new Node(3);
a.next = b;
b.next = c;
assert.equal(hasCycle(a), false);
c.next = a;
assert.ok(hasCycle(a));
console.log('all checks passed');
