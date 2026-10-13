"""Doubly linked list: None <- [A] <-> [B] <-> [C] -> None

The extra `prev` pointer gives O(1) removal of a node you already hold and backwards
traversal - that's why LRU caches use it. Two sentinel nodes (head/tail) that never hold
data mean every real node always has neighbours, so there are no None checks.
"""


class Node:
    def __init__(self, value=None):
        self.value = value
        self.prev = self.next = None


class DoublyLinkedList:
    def __init__(self):
        self.head, self.tail = Node(), Node()
        self.head.next, self.tail.prev = self.tail, self.head
        self.size = 0

    def _insert_between(self, value, before, after):
        node = Node(value)
        node.prev, node.next = before, after
        before.next = after.prev = node
        self.size += 1
        return node

    def add_first(self, value):
        return self._insert_between(value, self.head, self.head.next)

    def add_last(self, value):
        return self._insert_between(value, self.tail.prev, self.tail)

    def remove_node(self, node):
        node.prev.next, node.next.prev = node.next, node.prev
        self.size -= 1

    def remove_first(self):
        if self.size == 0:
            raise IndexError("list is empty")
        node = self.head.next
        self.remove_node(node)
        return node.value

    def remove_last(self):
        if self.size == 0:
            raise IndexError("list is empty")
        node = self.tail.prev
        self.remove_node(node)
        return node.value

    def forward(self):
        out, n = [], self.head.next
        while n is not self.tail:
            out.append(n.value)
            n = n.next
        return out

    def backward(self):
        out, n = [], self.tail.prev
        while n is not self.head:
            out.append(n.value)
            n = n.prev
        return out


if __name__ == "__main__":
    lst = DoublyLinkedList()
    lst.add_last(2)
    three = lst.add_last(3)
    lst.add_first(1)
    lst.add_last(4)
    assert lst.forward() == [1, 2, 3, 4]
    assert lst.backward() == [4, 3, 2, 1]
    lst.remove_node(three)  # O(1)
    assert lst.forward() == [1, 2, 4]
    assert lst.remove_first() == 1
    assert lst.remove_last() == 4
    assert lst.forward() == [2]
    lst.remove_first()
    try:
        lst.remove_first()
        raise AssertionError("expected IndexError")
    except IndexError:
        pass
    print("all checks passed")
