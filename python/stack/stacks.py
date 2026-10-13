"""Stack = Last In, First Out. push / pop / peek are O(1).

ArrayStack  - list + append/pop at the end (Python's list is already a dynamic array).
LinkedStack - push/pop at the head of a linked list.
MinStack    - get_min() in O(1) by storing the running minimum next to each value.
Applications: call stack, undo, balanced brackets, expression evaluation, DFS.
In real Python code just use a list (or collections.deque).
"""


class ArrayStack:
    def __init__(self):
        self._data = []

    def push(self, x):
        self._data.append(x)

    def pop(self):
        if not self._data:
            raise IndexError("stack underflow")
        return self._data.pop()

    def peek(self):
        if not self._data:
            raise IndexError("stack is empty")
        return self._data[-1]

    def is_empty(self):
        return not self._data


class LinkedStack:
    class _Node:
        def __init__(self, value, next):
            self.value, self.next = value, next

    def __init__(self):
        self._top = None

    def push(self, x):
        self._top = self._Node(x, self._top)

    def pop(self):
        if self._top is None:
            raise IndexError("stack underflow")
        value, self._top = self._top.value, self._top.next
        return value

    def is_empty(self):
        return self._top is None


class MinStack:
    def __init__(self):
        self._items = []  # (value, min so far)

    def push(self, x):
        current_min = x if not self._items else min(x, self._items[-1][1])
        self._items.append((x, current_min))

    def pop(self):
        return self._items.pop()[0]

    def get_min(self):
        return self._items[-1][1]


def is_balanced(s):
    pairs = {")": "(", "]": "[", "}": "{"}
    stack = ArrayStack()
    for ch in s:
        if ch in "([{":
            stack.push(ch)
        elif ch in pairs:
            if stack.is_empty() or stack.pop() != pairs[ch]:
                return False
    return stack.is_empty()


if __name__ == "__main__":
    a = ArrayStack()
    for i in range(1, 7):
        a.push(i)
    assert a.pop() == 6 and a.peek() == 5

    l = LinkedStack()
    l.push(1)
    l.push(2)
    assert l.pop() == 2 and l.pop() == 1
    try:
        l.pop()
        raise AssertionError("expected underflow")
    except IndexError:
        pass

    m = MinStack()
    for x in (5, 2, 7):
        m.push(x)
    assert m.get_min() == 2
    m.pop()
    m.pop()
    assert m.get_min() == 5

    assert is_balanced("{[()()]}")
    assert not is_balanced("([)]")
    assert not is_balanced("((")
    print("all checks passed")
