package linked_list;

import java.util.ArrayList;
import java.util.List;

import common.Check;

/*
 * Singly linked list: each node stores a value and a reference to the next node.
 *
 *   head -> [10] -> [20] -> [30] -> null
 *
 * Array vs linked list:
 *   access by index : array O(1)      | list O(n) (must walk from head)
 *   insert at front : array O(n) shift| list O(1)
 *   insert at end   : array O(1)*     | list O(1) if we keep a tail pointer
 *   memory          : array contiguous, cache friendly | list has a pointer per node, scattered
 *
 * Interview essentials shown here: reverse, find middle (slow/fast pointers), detect cycle.
 */
public class SinglyLinkedList {

    static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head, tail;
    private int size;

    public void addFirst(int value) {
        Node node = new Node(value);
        node.next = head;
        head = node;
        if (tail == null) tail = node;
        size++;
    }

    public void addLast(int value) {
        Node node = new Node(value);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public void insertAt(int index, int value) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("Index: " + index);
        if (index == 0) {
            addFirst(value);
            return;
        }
        if (index == size) {
            addLast(value);
            return;
        }
        Node prev = head;
        for (int i = 0; i < index - 1; i++) prev = prev.next;
        Node node = new Node(value);
        node.next = prev.next;
        prev.next = node;
        size++;
    }

    /* Removes the first node holding value. Returns false if not found. */
    public boolean remove(int value) {
        Node dummy = new Node(0); // a dummy node before head avoids a special case for removing head
        dummy.next = head;
        Node prev = dummy;
        while (prev.next != null && prev.next.value != value) prev = prev.next;
        if (prev.next == null) return false;
        if (prev.next == tail) tail = prev == dummy ? null : prev;
        prev.next = prev.next.next;
        head = dummy.next;
        size--;
        return true;
    }

    public boolean contains(int value) {
        for (Node n = head; n != null; n = n.next) if (n.value == value) return true;
        return false;
    }

    /* Reverse in place: turn every arrow around. O(n) time, O(1) space. */
    public void reverse() {
        Node prev = null, current = head;
        tail = head;
        while (current != null) {
            Node next = current.next; // 1. remember the rest of the list
            current.next = prev;      // 2. turn the arrow around
            prev = current;           // 3. move both pointers forward
            current = next;
        }
        head = prev;
    }

    /* Slow moves 1 step, fast moves 2: when fast reaches the end, slow is in the middle. */
    public int middle() {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow.value;
    }

    /* Floyd's cycle detection: if there is a loop, fast eventually laps slow. */
    static boolean hasCycle(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public List<Integer> toList() {
        List<Integer> out = new ArrayList<>();
        for (Node n = head; n != null; n = n.next) out.add(n.value);
        return out;
    }

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();
        list.addLast(20);
        list.addLast(30);
        list.addFirst(10);
        list.insertAt(3, 40);
        Check.eq("build", list.toList(), List.of(10, 20, 30, 40));
        Check.eq("middle", list.middle(), 30);
        list.reverse();
        Check.eq("reverse", list.toList(), List.of(40, 30, 20, 10));
        Check.isTrue("remove tail", list.remove(10));
        list.addLast(5);
        Check.eq("tail still correct", list.toList(), List.of(40, 30, 20, 5));
        Check.eq("remove missing", list.remove(99), false);
        Check.eq("size", list.size(), 4);

        Node a = new Node(1), b = new Node(2), c = new Node(3);
        a.next = b;
        b.next = c;
        Check.eq("no cycle", hasCycle(a), false);
        c.next = a;
        Check.isTrue("cycle", hasCycle(a));
    }
}
