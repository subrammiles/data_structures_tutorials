"""Bloom filter: a tiny probabilistic set.

might_contain(x) False -> definitely never added. True -> probably added (false positives only).
m bits, k hash functions; add() sets k bits, a query checks them. No deletes.
Sizing for n items, false-positive rate p:  m = -n ln p / (ln 2)^2,  k = (m / n) ln 2
Uses: skip disk reads for missing keys (Cassandra, RocksDB), "already crawled?" checks.
"""
import hashlib
import math


class BloomFilter:
    def __init__(self, expected_items, false_positive_rate):
        self.m = math.ceil(-expected_items * math.log(false_positive_rate) / math.log(2) ** 2)
        self.k = max(1, round(self.m / expected_items * math.log(2)))
        self.bits = bytearray((self.m + 7) // 8)

    def _indexes(self, item):
        digest = hashlib.sha256(item.encode()).digest()
        h1 = int.from_bytes(digest[:8], "little")
        h2 = int.from_bytes(digest[8:16], "little") | 1
        return [(h1 + i * h2) % self.m for i in range(self.k)]  # double hashing

    def add(self, item):
        for i in self._indexes(item):
            self.bits[i // 8] |= 1 << (i % 8)

    def might_contain(self, item):
        return all(self.bits[i // 8] & (1 << (i % 8)) for i in self._indexes(item))


if __name__ == "__main__":
    bf = BloomFilter(10_000, 0.01)
    print(f"bits m = {bf.m}, hash functions k = {bf.k}")
    for i in range(10_000):
        bf.add(f"user-{i}")
    assert all(bf.might_contain(f"user-{i}") for i in range(10_000))  # no false negatives
    false_positives = sum(bf.might_contain(f"other-{i}") for i in range(50_000))
    rate = false_positives / 50_000
    print(f"false positive rate: {rate:.4f}")
    assert rate < 0.02
    print("all checks passed")
