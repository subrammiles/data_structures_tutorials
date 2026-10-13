"""Hash map with separate chaining.

index = hash(key) % capacity. Different keys can land in the same bucket (a collision), so each
bucket holds a small list ("chain") of (key, value) pairs.
Keep load factor = size / capacity <= 0.75 by doubling the bucket array and REHASHING every entry
(indices change when capacity changes). Average O(1) get/put/remove; worst case O(n).
"""


class ChainingHashMap:
    MAX_LOAD_FACTOR = 0.75

    def __init__(self):
        self._buckets = [[] for _ in range(4)]
        self._size = 0

    def _index(self, key, capacity):
        return hash(key) % capacity

    def put(self, key, value):
        bucket = self._buckets[self._index(key, len(self._buckets))]
        for i, (k, v) in enumerate(bucket):
            if k == key:
                bucket[i] = (key, value)  # update
                return v
        bucket.append((key, value))
        self._size += 1
        if self._size / len(self._buckets) > self.MAX_LOAD_FACTOR:
            self._resize()
        return None

    def get(self, key):
        for k, v in self._buckets[self._index(key, len(self._buckets))]:
            if k == key:
                return v
        return None

    def remove(self, key):
        bucket = self._buckets[self._index(key, len(self._buckets))]
        for i, (k, v) in enumerate(bucket):
            if k == key:
                del bucket[i]
                self._size -= 1
                return v
        return None

    def _resize(self):
        old = self._buckets
        self._buckets = [[] for _ in range(len(old) * 2)]
        for bucket in old:
            for k, v in bucket:
                self._buckets[self._index(k, len(self._buckets))].append((k, v))

    def __len__(self):
        return self._size

    @property
    def capacity(self):
        return len(self._buckets)

    def longest_chain(self):
        return max(len(b) for b in self._buckets)


if __name__ == "__main__":
    m = ChainingHashMap()
    m.put("cat", 2)
    m.put("tiger", 7)
    m.put("lion", 1)
    assert m.get("cat") == 2
    assert m.put("cat", 3) == 2 and m.get("cat") == 3
    assert m.get("ox") is None
    assert m.remove("tiger") == 7 and m.get("tiger") is None
    assert len(m) == 2

    big = ChainingHashMap()
    for i in range(1000):
        big.put(i, i * i)
    assert big.get(999) == 998001
    assert len(big) <= 0.75 * big.capacity
    assert big.longest_chain() <= 3
    print("all checks passed")
