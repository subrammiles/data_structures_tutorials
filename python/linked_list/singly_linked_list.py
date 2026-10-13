"""Singly linked list: head -> [10] -> [20] -> [30] -> None

    access by index  O(n)       insert/remove at head  O(1)
    append with tail O(1)       search                 O(n)

Interview essentials: reverse in place, middle with slow/fast pointers, Floyd cycle detection.
"""


class Node:
    def __init__(self, value, next=None):
        self.value = value
        self.next = next


class SinglyLinkedList:
    def __init__(self):
        self.head = self.tail = None
        self.size = 0

    def add_first(self, value):
        self.head = Node(value, self.head)
        if self.tail is None:
            self.tail = self.head
        self.size += 1

    def add_last(self, value):
        node = Node(value)
        if self.tail is None:
            self.head = self.tail = node
        else:
            self.tail.next = node
            self.tail = node
        self.size += 1

    def insert_at(self, index, value):
        if not 0 <= index <= self.size:
            raise IndexError(index)
        if index == 0:
            return self.add_first(value)
        if index == self.size:
            return self.add_last(value)
        prev = self.head
        for _ in range(index - 1):
            prev = prev.next
        prev.next = Node(value, prev.next)
        self.size += 1

    def remove(self, value):
        """Remove the first node holding value. A dummy node avoids a special case for the head."""
        dummy = Node(None, self.head)
        prev = dummy
        while prev.next and prev.next.value != value:
            prev = prev.next
        if prev.next is None:
            return False
        if prev.next is self.tail:
            self.tail = None if prev is dummy else prev
        prev.next = prev.next.next
        self.head = dummy.next
        self.size -= 1
        return True

    def contains(self, value):
        node = self.head
        while node:
            if node.value == value:
                return True
            node = node.next
        return False

    def reverse(self):
        prev, current = None, self.head
        self.tail = self.head
        while current:
            current.next, prev, current = prev, current, current.next  # turn the arrow around
        self.head = prev

    def middle(self):
        slow = fast = self.head
        while fast and fast.next:
            slow, fast = slow.next, fast.next.next
        return slow.value

    def to_list(self):
        out, node = [], self.head
        while node:
            out.append(node.value)
            node = node.next
        return out


def has_cycle(head):
    """Floyd: fast moves 2 steps, slow 1. In a loop fast eventually laps slow."""
    slow = fast = head
    while fast and fast.next:
        slow, fast = slow.next, fast.next.next
        if slow is fast:
            return True
    return False


if __name__ == "__main__":
    lst = SinglyLinkedList()
    lst.add_last(20)
    lst.add_last(30)
    lst.add_first(10)
    lst.insert_at(3, 40)
    assert lst.to_list() == [10, 20, 30, 40]
    assert lst.middle() == 30
    lst.reverse()
    assert lst.to_list() == [40, 30, 20, 10]
    assert lst.remove(10)
    lst.add_last(5)
    assert lst.to_list() == [40, 30, 20, 5]  # tail pointer still correct
    assert not lst.remove(99)
    assert lst.size == 4

    a, b, c = Node(1), Node(2), Node(3)
    a.next, b.next = b, c
    assert not has_cycle(a)
    c.next = a
    assert has_cycle(a)
    print("all checks passed")
