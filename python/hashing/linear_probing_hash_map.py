"""Hash map with open addressing (linear probing).

All entries live directly in one array. On a collision try the next slot (i+1, i+2, ... wrapping).
Deleting needs a TOMBSTONE ("deleted - keep probing past me") or later keys become unreachable.
Keep the load factor <= ~0.5 - probe sequences grow quickly as the table fills.
CPython's dict uses open addressing (with a smarter probe sequence).
"""

_TOMBSTONE = object()


class LinearProbingHashMap:
    def __init__(self):
        self._keys = [None] * 8
        self._values = [None] * 8
        self._size = self._tombstones = 0

    def _hash(self, key, capacity):
        return hash(key) % capacity

    def put(self, key, value):
        if (self._size + self._tombstones + 1) * 2 > len(self._keys):
            self._resize()
        i = self._hash(key, len(self._keys))
        first_tombstone = None
        while self._keys[i] is not None:
            if self._keys[i] is _TOMBSTONE:
                if first_tombstone is None:
                    first_tombstone = i
            elif self._keys[i] == key:
                self._values[i] = value
                return
            i = (i + 1) % len(self._keys)
        if first_tombstone is not None:  # reuse a deleted slot
            i = first_tombstone
            self._tombstones -= 1
        self._keys[i], self._values[i] = key, value
        self._size += 1

    def get(self, key):
        i = self._find(key)
        return None if i is None else self._values[i]

    def remove(self, key):
        i = self._find(key)
        if i is None:
            return False
        self._keys[i], self._values[i] = _TOMBSTONE, None
        self._size -= 1
        self._tombstones += 1
        return True

    def _find(self, key):
        i = self._hash(key, len(self._keys))
        while self._keys[i] is not None:  # stop only at a truly empty slot
            if self._keys[i] is not _TOMBSTONE and self._keys[i] == key:
                return i
            i = (i + 1) % len(self._keys)
        return None

    def _resize(self):
        old = list(zip(self._keys, self._values))
        self._keys = [None] * (len(old) * 2)
        self._values = [None] * (len(old) * 2)
        self._size = self._tombstones = 0  # rehashing drops tombstones
        for k, v in old:
            if k is not None and k is not _TOMBSTONE:
                self.put(k, v)

    def __len__(self):
        return self._size


class _SameHash:
    """Keys that always collide, to demonstrate probing."""

    def __init__(self, name):
        self.name = name

    def __hash__(self):
        return 42

    def __eq__(self, other):
        return isinstance(other, _SameHash) and other.name == self.name


if __name__ == "__main__":
    m = LinearProbingHashMap()
    a, b = _SameHash("a"), _SameHash("b")
    m.put(a, 1)
    m.put(b, 2)
    assert m.get(a) == 1 and m.get(b) == 2
    assert m.remove(a)
    assert m.get(b) == 2  # still found past the tombstone
    m.put(a, 10)
    assert m.get(a) == 10
    for i in range(100):
        m.put(f"key{i}", i)
    assert m.get("key77") == 77
    assert len(m) == 102
    print("all checks passed")
