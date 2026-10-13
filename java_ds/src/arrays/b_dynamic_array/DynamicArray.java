package arrays.b_dynamic_array;

import common.Check;

/*
 * Dynamic array (how ArrayList / JS arrays / Python lists grow).
 *
 * Problem with a plain array: its size is fixed when you create it.
 * Idea: keep a bigger backing array and a separate "size". When it is full,
 * allocate an array twice as large and copy everything across.
 *
 * Operation          | Time
 * -------------------|-------------------------------------------------
 * get(i) / set(i)    | O(1)   - index arithmetic: address = base + i * elementSize
 * add (at the end)   | O(1) amortised - copying happens rarely (1, 2, 4, 8, ...)
 * insert(i) / remove | O(n)   - elements after i must shift
 * search             | O(n)   - unless the array is sorted (then binary search O(log n))
 *
 * Why amortised O(1)? Adding n items copies 1 + 2 + 4 + ... + n < 2n elements in total,
 * so the average cost per add is constant. Growing by +1 each time would be O(n^2) total.
 */
public class DynamicArray {

    private int[] data;
    private int size;

    public DynamicArray() {
        data = new int[2];
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    public int get(int index) {
        checkIndex(index);
        return data[index];
    }

    public void set(int index, int value) {
        checkIndex(index);
        data[index] = value;
    }

    public void add(int value) {
        if (size == data.length) resize(data.length * 2);
        data[size++] = value;
    }

    public void insert(int index, int value) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("Index: " + index);
        if (size == data.length) resize(data.length * 2);
        for (int i = size; i > index; i--) data[i] = data[i - 1]; // shift right
        data[index] = value;
        size++;
    }

    public int removeAt(int index) {
        checkIndex(index);
        int removed = data[index];
        for (int i = index; i < size - 1; i++) data[i] = data[i + 1]; // shift left
        size--;
        // Shrink when only a quarter is used (not half - avoids resizing back and forth).
        if (size > 0 && size == data.length / 4) resize(data.length / 2);
        return removed;
    }

    public int indexOf(int value) {
        for (int i = 0; i < size; i++) if (data[i] == value) return i;
        return -1;
    }

    private void resize(int newCapacity) {
        int[] bigger = new int[newCapacity];
        for (int i = 0; i < size; i++) bigger[i] = data[i];
        data = bigger;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    public int[] toArray() {
        int[] out = new int[size];
        System.arraycopy(data, 0, out, 0, size);
        return out;
    }

    public static void main(String[] args) {
        DynamicArray list = new DynamicArray();
        for (int i = 1; i <= 5; i++) list.add(i * 10);
        Check.eq("after 5 adds", list.toArray(), new int[]{10, 20, 30, 40, 50});
        Check.eq("capacity doubled 2 -> 4 -> 8", list.capacity(), 8);
        list.insert(1, 15);
        Check.eq("insert at 1", list.toArray(), new int[]{10, 15, 20, 30, 40, 50});
        Check.eq("removeAt 0", list.removeAt(0), 10);
        Check.eq("indexOf 40", list.indexOf(40), 3);
        Check.throwsException("get out of range", () -> list.get(99));
        while (list.size() > 1) list.removeAt(0);
        Check.eq("shrunk capacity", list.capacity(), 2);
    }
}
