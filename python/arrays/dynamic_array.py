"""Dynamic array - how Python's list (and Java's ArrayList) grows.

A fixed block of memory plus a separate `size`. When it is full, allocate a block twice as
large and copy everything over.

    get/set by index  O(1)
    append            O(1) amortised (copies happen at sizes 1, 2, 4, 8 ... total < 2n)
    insert/remove     O(n) - elements after the index shift
    search            O(n)

Python's list over-allocates by ~12.5% instead of 2x; the idea is identical.
"""


class DynamicArray:
    def __init__(self):
        self._capacity = 2
        self._size = 0
        self._data = [None] * self._capacity  # pretend this is raw fixed-size memory

    def __len__(self):
        return self._size

    @property
    def capacity(self):
        return self._capacity

    def get(self, index):
        self._check(index)
        return self._data[index]

    def set(self, index, value):
        self._check(index)
        self._data[index] = value

    def append(self, value):
        if self._size == self._capacity:
            self._resize(self._capacity * 2)
        self._data[self._size] = value
        self._size += 1

    def insert(self, index, value):
        if not 0 <= index <= self._size:
            raise IndexError(index)
        if self._size == self._capacity:
            self._resize(self._capacity * 2)
        for i in range(self._size, index, -1):  # shift right
            self._data[i] = self._data[i - 1]
        self._data[index] = value
        self._size += 1

    def remove_at(self, index):
        self._check(index)
        removed = self._data[index]
        for i in range(index, self._size - 1):  # shift left
            self._data[i] = self._data[i + 1]
        self._size -= 1
        # shrink at 1/4 full (not 1/2) so we don't resize back and forth
        if 0 < self._size == self._capacity // 4:
            self._resize(self._capacity // 2)
        return removed

    def index_of(self, value):
        for i in range(self._size):
            if self._data[i] == value:
                return i
        return -1

    def to_list(self):
        return [self._data[i] for i in range(self._size)]

    def _resize(self, new_capacity):
        new_data = [None] * new_capacity
        for i in range(self._size):
            new_data[i] = self._data[i]
        self._data, self._capacity = new_data, new_capacity

    def _check(self, index):
        if not 0 <= index < self._size:
            raise IndexError(f"index {index}, size {self._size}")


if __name__ == "__main__":
    arr = DynamicArray()
    for i in range(1, 6):
        arr.append(i * 10)
    assert arr.to_list() == [10, 20, 30, 40, 50]
    assert arr.capacity == 8  # 2 -> 4 -> 8
    arr.insert(1, 15)
    assert arr.to_list() == [10, 15, 20, 30, 40, 50]
    assert arr.remove_at(0) == 10
    assert arr.index_of(40) == 3
    try:
        arr.get(99)
        raise AssertionError("expected IndexError")
    except IndexError:
        pass
    while len(arr) > 1:
        arr.remove_at(0)
    assert arr.capacity == 2
    print("all checks passed")
