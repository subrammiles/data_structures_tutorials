"""Binary min-heap stored in a list. Every parent <= its children; the minimum is at index 0.

    parent(i) = (i - 1) // 2     left(i) = 2i + 1     right(i) = 2i + 2

peek O(1) | push O(log n): append, sift up | pop O(log n): move last to root, sift down
heapify O(n): sift down every non-leaf, bottom-up | heap sort O(n log n), in place
Python's standard library version: the heapq module (functions over a plain list).
"""


class MinHeap:
    def __init__(self, values=None):
        self._a = list(values or [])
        for i in range(len(self._a) // 2 - 1, -1, -1):  # O(n) heapify
            self._sift_down(i)

    def __len__(self):
        return len(self._a)

    def push(self, x):
        self._a.append(x)
        self._sift_up(len(self._a) - 1)

    def peek(self):
        if not self._a:
            raise IndexError("heap is empty")
        return self._a[0]

    def pop(self):
        smallest = self.peek()
        last = self._a.pop()
        if self._a:
            self._a[0] = last
            self._sift_down(0)
        return smallest

    def _sift_up(self, i):
        a = self._a
        while i > 0 and a[i] < a[(i - 1) // 2]:
            a[i], a[(i - 1) // 2] = a[(i - 1) // 2], a[i]
            i = (i - 1) // 2

    def _sift_down(self, i):
        a, n = self._a, len(self._a)
        while True:
            left, right, smallest = 2 * i + 1, 2 * i + 2, i
            if left < n and a[left] < a[smallest]:
                smallest = left
            if right < n and a[right] < a[smallest]:
                smallest = right
            if smallest == i:
                return
            a[i], a[smallest] = a[smallest], a[i]
            i = smallest

    def is_valid(self):
        return all(self._a[i] >= self._a[(i - 1) // 2] for i in range(1, len(self._a)))


def heap_sort(a):
    """In-place ascending sort with a MAX-heap."""
    def sift_down(i, n):
        while True:
            l, r, largest = 2 * i + 1, 2 * i + 2, i
            if l < n and a[l] > a[largest]:
                largest = l
            if r < n and a[r] > a[largest]:
                largest = r
            if largest == i:
                return
            a[i], a[largest] = a[largest], a[i]
            i = largest

    for i in range(len(a) // 2 - 1, -1, -1):
        sift_down(i, len(a))
    for end in range(len(a) - 1, 0, -1):
        a[0], a[end] = a[end], a[0]  # largest to its final place
        sift_down(0, end)


if __name__ == "__main__":
    h = MinHeap()
    for x in (7, 3, 9, 1, 4, 2, 8, 6, 5):
        h.push(x)
    assert h.is_valid() and h.peek() == 1
    assert [h.pop() for _ in range(len(h))] == list(range(1, 10))

    built = MinHeap([5, 9, 3, 1, 8])
    assert built.is_valid() and built.peek() == 1

    data = [5, 2, 9, 1, 5, 6]
    heap_sort(data)
    assert data == [1, 2, 5, 5, 6, 9]
    try:
        MinHeap().pop()
        raise AssertionError("expected IndexError")
    except IndexError:
        pass
    print("all checks passed")
