package advanced.d_skip_list;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import common.Check;

/*
 * Skip list: a sorted linked list with "express lanes".
 *
 *   level 2:  head ----------------------> 30 ----------------> null
 *   level 1:  head --------> 10 ---------> 30 -------> 50 ----> null
 *   level 0:  head -> 5 ---> 10 -> 20 ---> 30 -> 40 -> 50 ----> null
 *
 * Each inserted node is promoted to the next level with probability 1/2 (coin flips), so
 * level k holds about n / 2^k nodes. Search starts at the top level and drops down when the
 * next node would overshoot. Expected O(log n) search/insert/delete, no rotations needed.
 *
 * Why it matters: simpler than balanced trees and easy to make concurrent
 * (Java's ConcurrentSkipListMap, Redis sorted sets, LevelDB/RocksDB memtables).
 */
public class SkipList {

    private static final int MAX_LEVEL = 16;

    private static class Node {
        final int value;
        final Node[] next;

        Node(int value, int levels) {
            this.value = value;
            this.next = new Node[levels];
        }
    }

    private final Node head = new Node(Integer.MIN_VALUE, MAX_LEVEL);
    private final Random random;
    private int levels = 1;

    public SkipList(long seed) {
        random = new Random(seed);
    }

    private int randomLevel() {
        int level = 1;
        while (level < MAX_LEVEL && random.nextBoolean()) level++;
        return level;
    }

    public boolean contains(int value) {
        Node n = head;
        for (int lvl = levels - 1; lvl >= 0; lvl--) {
            while (n.next[lvl] != null && n.next[lvl].value < value) n = n.next[lvl];
        }
        n = n.next[0];
        return n != null && n.value == value;
    }

    public void insert(int value) {
        Node[] update = new Node[MAX_LEVEL]; // last node before 'value' on each level
        Node n = head;
        for (int lvl = levels - 1; lvl >= 0; lvl--) {
            while (n.next[lvl] != null && n.next[lvl].value < value) n = n.next[lvl];
            update[lvl] = n;
        }
        if (n.next[0] != null && n.next[0].value == value) return; // no duplicates
        int lvl = randomLevel();
        if (lvl > levels) {
            for (int i = levels; i < lvl; i++) update[i] = head;
            levels = lvl;
        }
        Node node = new Node(value, lvl);
        for (int i = 0; i < lvl; i++) {
            node.next[i] = update[i].next[i];
            update[i].next[i] = node;
        }
    }

    public boolean delete(int value) {
        Node[] update = new Node[MAX_LEVEL];
        Node n = head;
        for (int lvl = levels - 1; lvl >= 0; lvl--) {
            while (n.next[lvl] != null && n.next[lvl].value < value) n = n.next[lvl];
            update[lvl] = n;
        }
        Node target = n.next[0];
        if (target == null || target.value != value) return false;
        for (int i = 0; i < target.next.length; i++) update[i].next[i] = target.next[i];
        while (levels > 1 && head.next[levels - 1] == null) levels--;
        return true;
    }

    public List<Integer> toList() {
        List<Integer> out = new ArrayList<>();
        for (Node n = head.next[0]; n != null; n = n.next[0]) out.add(n.value);
        return out;
    }

    public static void main(String[] args) {
        SkipList list = new SkipList(42);
        for (int v : new int[]{30, 10, 50, 20, 40, 5}) list.insert(v);
        Check.eq("sorted", list.toList(), List.of(5, 10, 20, 30, 40, 50));
        Check.isTrue("contains 40", list.contains(40));
        Check.eq("contains 45", list.contains(45), false);
        Check.isTrue("delete 30", list.delete(30));
        Check.eq("after delete", list.toList(), List.of(5, 10, 20, 40, 50));

        SkipList big = new SkipList(7);
        for (int i = 0; i < 100_000; i++) big.insert(i * 2);
        Check.isTrue("levels ~ log2(n) (" + big.levels + ")", big.levels <= MAX_LEVEL && big.levels >= 10);
        Check.isTrue("big contains", big.contains(123_456) && !big.contains(123_457));
    }
}
