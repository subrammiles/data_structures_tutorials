package hashing.c_open_addressing;

import common.Check;

/*
 * Hash map with OPEN ADDRESSING (linear probing).
 *
 * Instead of chains, all entries live directly in the array. On a collision, try the next
 * slot (i + 1, i + 2, ... wrapping around) until an empty one is found.
 *
 * Deletion is the tricky part: simply emptying a slot would break the probe sequence of keys
 * placed after it. Use a TOMBSTONE marker: "deleted, keep probing past me, but you may reuse me".
 *
 * Chaining vs open addressing:
 *   - Open addressing: no extra node objects, cache friendly, but performance collapses as the
 *     load factor approaches 1 -> keep it <= ~0.5-0.7. Clustering: runs of filled slots grow.
 *   - Chaining: tolerates high load factors, simpler deletes, extra memory per entry.
 * Other probing schemes: quadratic probing (i + 1^2, i + 2^2, ...), double hashing (step = hash2(key)).
 * Python's dict and Java's IdentityHashMap use open addressing.
 */
public class LinearProbingHashMap {

    private static final String TOMBSTONE = new String("<deleted>");
    private String[] keys;
    private int[] values;
    private int size, tombstones;

    public LinearProbingHashMap() {
        keys = new String[8];
        values = new int[8];
    }

    private int hash(String key, int capacity) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    public void put(String key, int value) {
        if ((size + tombstones + 1) * 2 > keys.length) resize(); // keep load factor <= 0.5
        int i = hash(key, keys.length), firstTombstone = -1;
        while (keys[i] != null) {
            if (keys[i] == TOMBSTONE) {
                if (firstTombstone == -1) firstTombstone = i;
            } else if (keys[i].equals(key)) {
                values[i] = value; // update existing key
                return;
            }
            i = (i + 1) % keys.length;
        }
        if (firstTombstone != -1) { // reuse a deleted slot
            i = firstTombstone;
            tombstones--;
        }
        keys[i] = key;
        values[i] = value;
        size++;
    }

    public Integer get(String key) {
        int i = find(key);
        return i == -1 ? null : values[i];
    }

    public boolean remove(String key) {
        int i = find(key);
        if (i == -1) return false;
        keys[i] = TOMBSTONE;
        size--;
        tombstones++;
        return true;
    }

    private int find(String key) {
        int i = hash(key, keys.length);
        while (keys[i] != null) { // stop only at a truly empty slot
            if (keys[i] != TOMBSTONE && keys[i].equals(key)) return i;
            i = (i + 1) % keys.length;
        }
        return -1;
    }

    private void resize() {
        String[] oldKeys = keys;
        int[] oldValues = values;
        keys = new String[oldKeys.length * 2];
        values = new int[oldKeys.length * 2];
        size = 0;
        tombstones = 0; // rehashing drops all tombstones
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null && oldKeys[i] != TOMBSTONE) put(oldKeys[i], oldValues[i]);
        }
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        LinearProbingHashMap map = new LinearProbingHashMap();
        // "Aa" and "BB" have the same String.hashCode() -> guaranteed collision
        map.put("Aa", 1);
        map.put("BB", 2);
        Check.eq("collision 1", map.get("Aa"), 1);
        Check.eq("collision 2", map.get("BB"), 2);
        Check.isTrue("remove Aa", map.remove("Aa"));
        Check.eq("BB still found past tombstone", map.get("BB"), 2);
        map.put("Aa", 10);
        Check.eq("reinsert reuses tombstone", map.get("Aa"), 10);
        for (int i = 0; i < 100; i++) map.put("key" + i, i);
        Check.eq("after resizes", map.get("key77"), 77);
        Check.eq("size", map.size(), 102);
    }
}
