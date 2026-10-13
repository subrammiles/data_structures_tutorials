"""Skip list: a sorted linked list with "express lanes".

Each node is promoted to the next level with probability 1/2, so level k holds ~n / 2^k nodes.
Search starts at the top and drops down when the next node would overshoot.
Expected O(log n) search/insert/delete; no rotations. Used by Redis sorted sets, LevelDB/RocksDB.
"""
import random

MAX_LEVEL = 16


class _Node:
    def __init__(self, value, levels):
        self.value = value
        self.next = [None] * levels


class SkipList:
    def __init__(self, seed=None):
        self.head = _Node(float("-inf"), MAX_LEVEL)
        self.levels = 1
        self.random = random.Random(seed)

    def _random_level(self):
        level = 1
        while level < MAX_LEVEL and self.random.random() < 0.5:
            level += 1
        return level

    def _predecessors(self, value):
        update, n = [self.head] * MAX_LEVEL, self.head
        for lvl in range(self.levels - 1, -1, -1):
            while n.next[lvl] and n.next[lvl].value < value:
                n = n.next[lvl]
            update[lvl] = n
        return update

    def contains(self, value):
        n = self._predecessors(value)[0].next[0]
        return n is not None and n.value == value

    def insert(self, value):
        update = self._predecessors(value)
        if update[0].next[0] and update[0].next[0].value == value:
            return  # no duplicates
        lvl = self._random_level()
        self.levels = max(self.levels, lvl)
        node = _Node(value, lvl)
        for i in range(lvl):
            node.next[i] = update[i].next[i]
            update[i].next[i] = node

    def delete(self, value):
        update = self._predecessors(value)
        target = update[0].next[0]
        if target is None or target.value != value:
            return False
        for i in range(len(target.next)):
            update[i].next[i] = target.next[i]
        while self.levels > 1 and self.head.next[self.levels - 1] is None:
            self.levels -= 1
        return True

    def to_list(self):
        out, n = [], self.head.next[0]
        while n:
            out.append(n.value)
            n = n.next[0]
        return out


if __name__ == "__main__":
    s = SkipList(seed=42)
    for v in (30, 10, 50, 20, 40, 5):
        s.insert(v)
    assert s.to_list() == [5, 10, 20, 30, 40, 50]
    assert s.contains(40) and not s.contains(45)
    assert s.delete(30) and s.to_list() == [5, 10, 20, 40, 50]

    big = SkipList(seed=7)
    for i in range(20_000):
        big.insert(i * 2)
    assert 8 <= big.levels <= MAX_LEVEL  # ~log2(n)
    assert big.contains(12_346) and not big.contains(12_347)
    print("all checks passed")
