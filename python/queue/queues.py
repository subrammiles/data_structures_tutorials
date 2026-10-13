"""Queue = First In, First Out.

A naive list queue (list.pop(0)) shifts every element: O(n). A circular buffer keeps a
`front` index and wraps with % capacity: enqueue/dequeue O(1).
Deque: O(1) at both ends - the front index moves backwards with (front - 1) % capacity.
Queue from two stacks: amortised O(1) (classic interview question).
In real Python code use collections.deque.
"""


class CircularQueue:
    def __init__(self, capacity):
        self._data = [None] * capacity
        self._front = self._size = 0

    def enqueue(self, x):
        if self._size == len(self._data):
            return False  # full
        self._data[(self._front + self._size) % len(self._data)] = x
        self._size += 1
        return True

    def dequeue(self):
        if self._size == 0:
            raise IndexError("queue is empty")
        x = self._data[self._front]
        self._front = (self._front + 1) % len(self._data)
        self._size -= 1
        return x

    def is_empty(self):
        return self._size == 0


class CircularDeque:
    def __init__(self, capacity):
        self._data = [None] * capacity
        self._front = self._size = 0

    def add_first(self, x):
        if self._size == len(self._data):
            return False
        self._front = (self._front - 1) % len(self._data)  # Python % is never negative
        self._data[self._front] = x
        self._size += 1
        return True

    def add_last(self, x):
        if self._size == len(self._data):
            return False
        self._data[(self._front + self._size) % len(self._data)] = x
        self._size += 1
        return True

    def remove_first(self):
        if self._size == 0:
            raise IndexError("deque is empty")
        x = self._data[self._front]
        self._front = (self._front + 1) % len(self._data)
        self._size -= 1
        return x

    def remove_last(self):
        if self._size == 0:
            raise IndexError("deque is empty")
        self._size -= 1
        return self._data[(self._front + self._size) % len(self._data)]


class QueueWithTwoStacks:
    def __init__(self):
        self._in, self._out = [], []

    def enqueue(self, x):
        self._in.append(x)

    def dequeue(self):
        if not self._out:
            while self._in:  # reverse once; each element moves at most once
                self._out.append(self._in.pop())
        if not self._out:
            raise IndexError("queue is empty")
        return self._out.pop()


if __name__ == "__main__":
    q = CircularQueue(5)
    for i in range(1, 6):
        q.enqueue(i)
    assert not q.enqueue(99)
    assert q.dequeue() == 1 and q.dequeue() == 2
    q.enqueue(6)
    q.enqueue(7)  # wraps around
    order = []
    while not q.is_empty():
        order.append(q.dequeue())
    assert order == [3, 4, 5, 6, 7]

    d = CircularDeque(3)
    d.add_last(2)
    d.add_first(1)
    d.add_last(3)
    assert not d.add_first(0)
    assert d.remove_last() == 3 and d.remove_first() == 1

    s = QueueWithTwoStacks()
    s.enqueue(1)
    s.enqueue(2)
    assert s.dequeue() == 1
    s.enqueue(3)
    assert s.dequeue() == 2 and s.dequeue() == 3
    print("all checks passed")
