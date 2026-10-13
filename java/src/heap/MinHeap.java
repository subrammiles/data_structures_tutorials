package heap;

import java.util.Arrays;

import common.Check;

/*
 * Binary min-heap: a COMPLETE binary tree where every parent <= its children.
 * The smallest element is always at the root.
 *
 * Stored in a plain array - no node objects:
 *   parent(i) = (i - 1) / 2,  left(i) = 2i + 1,  right(i) = 2i + 2
 *
 *   array: [1, 3, 2, 7, 4]          1
 *                                 /   \
 *                                3     2
 *                               / \
 *                              7   4
 *
 *   peek      O(1)
 *   insert    O(log n)  add at the end, SIFT UP while smaller than the parent
 *   poll      O(log n)  move last element to the root, SIFT DOWN to the smaller child
 *   heapify   O(n)      sift down every non-leaf from the bottom up (not O(n log n)!)
 *   heap sort O(n log n), in place, not stable
 *
 * Used for priority queues, Dijkstra, top-k, median of a stream, scheduling.
 * Java: java.util.PriorityQueue (min-heap by default).
 */
public class MinHeap {

    private int[] heap;
    private int size;

    public MinHeap() {
        heap = new int[8];
    }

    /* Build a heap from an existing array in O(n). */
    public MinHeap(int[] values) {
        heap = Arrays.copyOf(values, Math.max(8, values.length));
        size = values.length;
        for (int i = size / 2 - 1; i >= 0; i--) siftDown(i);
    }

    public void insert(int x) {
        if (size == heap.length) heap = Arrays.copyOf(heap, size * 2);
        heap[size] = x;
        siftUp(size++);
    }

    public int peek() {
        if (size == 0) throw new IllegalStateException("Heap is empty");
        return heap[0];
    }

    public int poll() {
        int min = peek();
        heap[0] = heap[--size];
        siftDown(0);
        return min;
    }

    public int size() {
        return size;
    }

    private void siftUp(int i) {
        while (i > 0 && heap[i] < heap[(i - 1) / 2]) {
            swap(heap, i, (i - 1) / 2);
            i = (i - 1) / 2;
        }
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1, right = left + 1, smallest = i;
            if (left < size && heap[left] < heap[smallest]) smallest = left;
            if (right < size && heap[right] < heap[smallest]) smallest = right;
            if (smallest == i) return;
            swap(heap, i, smallest);
            i = smallest;
        }
    }

    boolean isValidHeap() {
        for (int i = 1; i < size; i++) if (heap[i] < heap[(i - 1) / 2]) return false;
        return true;
    }

    /* In-place heap sort (ascending) using a MAX-heap on the array itself. */
    static void heapSort(int[] a) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) maxSiftDown(a, i, n);
        for (int end = n - 1; end > 0; end--) {
            swap(a, 0, end); // largest goes to its final place
            maxSiftDown(a, 0, end);
        }
    }

    private static void maxSiftDown(int[] a, int i, int n) {
        while (true) {
            int l = 2 * i + 1, r = l + 1, largest = i;
            if (l < n && a[l] > a[largest]) largest = l;
            if (r < n && a[r] > a[largest]) largest = r;
            if (largest == i) return;
            swap(a, i, largest);
            i = largest;
        }
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static void main(String[] args) {
        MinHeap h = new MinHeap();
        for (int x : new int[]{7, 3, 9, 1, 4, 2, 8, 6, 5}) h.insert(x);
        Check.isTrue("heap property", h.isValidHeap());
        Check.eq("peek min", h.peek(), 1);
        StringBuilder sb = new StringBuilder();
        while (h.size() > 0) sb.append(h.poll());
        Check.eq("polls come out sorted", sb.toString(), "123456789");

        MinHeap built = new MinHeap(new int[]{5, 9, 3, 1, 8});
        Check.isTrue("heapify O(n)", built.isValidHeap());
        Check.eq("heapify min", built.peek(), 1);

        int[] a = {5, 2, 9, 1, 5, 6};
        heapSort(a);
        Check.eq("heap sort", a, new int[]{1, 2, 5, 5, 6, 9});
        Check.throwsException("poll empty", () -> new MinHeap().poll());
    }
}
