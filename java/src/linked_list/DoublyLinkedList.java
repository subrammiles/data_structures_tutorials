package linked_list.a_basics;

import java.util.ArrayList;
import java.util.List;

import common.Check;

/*
 * Doubly linked list: every node knows both its next and previous node.
 *
 *   null <- [A] <-> [B] <-> [C] -> null
 *
 * Extra pointer per node buys:
 *   - O(1) removal of a node you already hold (no need to find its predecessor)
 *   - walking backwards
 * This is why LRU caches and java.util.LinkedList use doubly linked lists.
 *
 * Trick used here: two SENTINEL nodes (head and tail) that never hold data.
 * Every real node always has a non-null prev and next, so no null checks are needed.
 */
public class DoublyLinkedList {

    static class Node {
        int value;
        Node prev, next;

        Node(int value) {
            this.value = value;
        }
    }

    private final Node head = new Node(0), tail = new Node(0);
    private int size;

    public DoublyLinkedList() {
        head.next = tail;
        tail.prev = head;
    }

    private Node insertBetween(int value, Node before, Node after) {
        Node node = new Node(value);
        node.prev = before;
        node.next = after;
        before.next = node;
        after.prev = node;
        size++;
        return node;
    }

    public Node addFirst(int value) {
        return insertBetween(value, head, head.next);
    }

    public Node addLast(int value) {
        return insertBetween(value, tail.prev, tail);
    }

    /* O(1): the node knows its neighbours. */
    public void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        size--;
    }

    public int removeFirst() {
        if (size == 0) throw new IllegalStateException("List is empty");
        Node first = head.next;
        removeNode(first);
        return first.value;
    }

    public int removeLast() {
        if (size == 0) throw new IllegalStateException("List is empty");
        Node last = tail.prev;
        removeNode(last);
        return last.value;
    }

    public List<Integer> forward() {
        List<Integer> out = new ArrayList<>();
        for (Node n = head.next; n != tail; n = n.next) out.add(n.value);
        return out;
    }

    public List<Integer> backward() {
        List<Integer> out = new ArrayList<>();
        for (Node n = tail.prev; n != head; n = n.prev) out.add(n.value);
        return out;
    }

    public static void main(String[] args) {
        DoublyLinkedList list = new DoublyLinkedList();
        list.addLast(2);
        Node three = list.addLast(3);
        list.addFirst(1);
        list.addLast(4);
        Check.eq("forward", list.forward(), List.of(1, 2, 3, 4));
        Check.eq("backward", list.backward(), List.of(4, 3, 2, 1));
        list.removeNode(three);
        Check.eq("remove held node O(1)", list.forward(), List.of(1, 2, 4));
        Check.eq("removeFirst", list.removeFirst(), 1);
        Check.eq("removeLast", list.removeLast(), 4);
        Check.eq("left", list.forward(), List.of(2));
        list.removeFirst();
        Check.throwsException("remove from empty", list::removeFirst);
    }
}
