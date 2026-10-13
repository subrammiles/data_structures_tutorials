package queue.a_basics;

import common.Check;

/*
 * Queue = First In, First Out (FIFO). Think of people waiting in line.
 *
 * Naive array queue: dequeue shifts every element left -> O(n). Bad.
 * Circular array queue: keep front index + size; wrap indices with % capacity.
 *   enqueue/dequeue O(1), no shifting, no wasted space at the front.
 *
 *   capacity 5, after enqueue 1..5, dequeue twice, enqueue 6, 7:
 *   index:  0  1  2  3  4
 *   data:  [6][7][3][4][5]      front = 2, rear = (front + size - 1) % 5 = 1
 *
 * Deque (double-ended queue): add/remove at both ends in O(1). Same circular trick,
 * front moves backwards with (front - 1 + capacity) % capacity.
 *
 * Where queues appear: BFS, task schedulers, printer jobs, message brokers, rate limiting.
 * Java: use ArrayDeque for both stacks and queues.
 */
public class Queues {

    static class CircularQueue {
        private final int[] data;
        private int front, size;

        CircularQueue(int capacity) {
            data = new int[capacity];
        }

        boolean enqueue(int x) {
            if (size == data.length) return false; // full
            data[(front + size) % data.length] = x;
            size++;
            return true;
        }

        int dequeue() {
            if (size == 0) throw new IllegalStateException("Queue is empty");
            int x = data[front];
            front = (front + 1) % data.length;
            size--;
            return x;
        }

        int peek() {
            if (size == 0) throw new IllegalStateException("Queue is empty");
            return data[front];
        }

        boolean isEmpty() {
            return size == 0;
        }
    }

    static class CircularDeque {
        private final int[] data;
        private int front, size;

        CircularDeque(int capacity) {
            data = new int[capacity];
        }

        boolean addFirst(int x) {
            if (size == data.length) return false;
            front = (front - 1 + data.length) % data.length;
            data[front] = x;
            size++;
            return true;
        }

        boolean addLast(int x) {
            if (size == data.length) return false;
            data[(front + size) % data.length] = x;
            size++;
            return true;
        }

        int removeFirst() {
            if (size == 0) throw new IllegalStateException("Deque is empty");
            int x = data[front];
            front = (front + 1) % data.length;
            size--;
            return x;
        }

        int removeLast() {
            if (size == 0) throw new IllegalStateException("Deque is empty");
            int x = data[(front + size - 1) % data.length];
            size--;
            return x;
        }
    }

    /* Queue built from two stacks: amortised O(1) per operation (a common interview question). */
    static class QueueWithTwoStacks {
        private final java.util.ArrayDeque<Integer> in = new java.util.ArrayDeque<>();
        private final java.util.ArrayDeque<Integer> out = new java.util.ArrayDeque<>();

        void enqueue(int x) {
            in.push(x);
        }

        int dequeue() {
            if (out.isEmpty()) while (!in.isEmpty()) out.push(in.pop()); // reverse once
            if (out.isEmpty()) throw new IllegalStateException("Queue is empty");
            return out.pop();
        }
    }

    public static void main(String[] args) {
        CircularQueue q = new CircularQueue(5);
        for (int i = 1; i <= 5; i++) q.enqueue(i);
        Check.eq("full", q.enqueue(99), false);
        Check.eq("dequeue", q.dequeue(), 1);
        Check.eq("dequeue", q.dequeue(), 2);
        q.enqueue(6);
        q.enqueue(7); // wraps around to index 0 and 1
        StringBuilder order = new StringBuilder();
        while (!q.isEmpty()) order.append(q.dequeue());
        Check.eq("wrap-around order", order.toString(), "34567");

        CircularDeque d = new CircularDeque(3);
        d.addLast(2);
        d.addFirst(1);
        d.addLast(3);
        Check.eq("deque full", d.addFirst(0), false);
        Check.eq("removeLast", d.removeLast(), 3);
        Check.eq("removeFirst", d.removeFirst(), 1);

        QueueWithTwoStacks s = new QueueWithTwoStacks();
        s.enqueue(1);
        s.enqueue(2);
        Check.eq("two stacks 1", s.dequeue(), 1);
        s.enqueue(3);
        Check.eq("two stacks 2", s.dequeue(), 2);
        Check.eq("two stacks 3", s.dequeue(), 3);
    }
}
