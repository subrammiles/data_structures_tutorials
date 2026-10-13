package hashing.b_separate_chaining;

import common.Check;

/*
 * Hash map with SEPARATE CHAINING - the next step after the Direct Address Table (hashing/a_basics/dat).
 *
 * DAT problem: keys must be small integers, and the array must be as big as the largest key.
 * Hash map idea: index = hash(key) % capacity. Many keys can land on the same index (a COLLISION),
 * so each bucket holds a small linked list ("chain") of entries.
 *
 *   bucket 0: null
 *   bucket 1: ["cat"=2] -> ["dog"=1] -> null     <- collision handled by the chain
 *   bucket 2: ["ox"=8] -> null
 *
 * Load factor = size / capacity. Keep it below ~0.75 by doubling capacity and REHASHING every
 * entry (its index changes because capacity changed). Then chains stay short and
 * get/put/remove are O(1) on average, O(n) worst case (all keys in one bucket).
 *
 * Java's HashMap does this, and since Java 8 turns a long chain (>= 8) into a red-black tree
 * so the worst case becomes O(log n).
 */
public class ChainingHashMap<K, V> {

    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private static final double MAX_LOAD_FACTOR = 0.75;
    private Entry<K, V>[] buckets;
    private int size;

    public ChainingHashMap() {
        buckets = newArray(4);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Entry<K, V>[] newArray(int capacity) {
        return (Entry<K, V>[]) new Entry[capacity];
    }

    private int indexFor(Object key, int capacity) {
        int h = key == null ? 0 : key.hashCode();
        h ^= (h >>> 16); // mix high bits into low bits (same trick as java.util.HashMap)
        return (h & 0x7fffffff) % capacity; // clear the sign bit so the index is never negative
    }

    public V put(K key, V value) {
        int i = indexFor(key, buckets.length);
        for (Entry<K, V> e = buckets[i]; e != null; e = e.next) {
            if (java.util.Objects.equals(e.key, key)) { // key exists: update
                V old = e.value;
                e.value = value;
                return old;
            }
        }
        buckets[i] = new Entry<>(key, value, buckets[i]); // add at the head of the chain
        size++;
        if ((double) size / buckets.length > MAX_LOAD_FACTOR) resize();
        return null;
    }

    public V get(K key) {
        for (Entry<K, V> e = buckets[indexFor(key, buckets.length)]; e != null; e = e.next) {
            if (java.util.Objects.equals(e.key, key)) return e.value;
        }
        return null;
    }

    public V remove(K key) {
        int i = indexFor(key, buckets.length);
        Entry<K, V> prev = null;
        for (Entry<K, V> e = buckets[i]; e != null; prev = e, e = e.next) {
            if (java.util.Objects.equals(e.key, key)) {
                if (prev == null) buckets[i] = e.next;
                else prev.next = e.next;
                size--;
                return e.value;
            }
        }
        return null;
    }

    private void resize() {
        Entry<K, V>[] old = buckets;
        buckets = newArray(old.length * 2);
        for (Entry<K, V> head : old) {
            for (Entry<K, V> e = head; e != null; e = e.next) {
                int i = indexFor(e.key, buckets.length); // index must be recomputed
                buckets[i] = new Entry<>(e.key, e.value, buckets[i]);
            }
        }
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return buckets.length;
    }

    public int longestChain() {
        int longest = 0;
        for (Entry<K, V> head : buckets) {
            int len = 0;
            for (Entry<K, V> e = head; e != null; e = e.next) len++;
            longest = Math.max(longest, len);
        }
        return longest;
    }

    public static void main(String[] args) {
        ChainingHashMap<String, Integer> map = new ChainingHashMap<>();
        map.put("cat", 2);
        map.put("tiger", 7);
        map.put("lion", 1);
        Check.eq("get cat", map.get("cat"), 2);
        Check.eq("update returns old", map.put("cat", 3), 2);
        Check.eq("updated", map.get("cat"), 3);
        Check.eq("missing", map.get("ox"), null);
        Check.eq("remove", map.remove("tiger"), 7);
        Check.eq("removed", map.get("tiger"), null);
        Check.eq("size", map.size(), 2);

        ChainingHashMap<Integer, Integer> big = new ChainingHashMap<>();
        for (int i = 0; i < 1000; i++) big.put(i, i * i);
        Check.eq("get after many resizes", big.get(999), 998001);
        Check.isTrue("load factor kept <= 0.75", big.size() <= 0.75 * big.capacity());
        Check.isTrue("chains stay short (" + big.longestChain() + ")", big.longestChain() <= 3);
    }
}
