package advanced.e_bloom_filter;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import common.Check;

/*
 * Bloom filter: a tiny probabilistic set.
 *   mightContain(x) == false  -> x was DEFINITELY never added
 *   mightContain(x) == true   -> x was PROBABLY added (false positives possible, no false negatives)
 *
 * Structure: a bit array of m bits and k hash functions. add(x) sets k bits; a query checks them.
 * You cannot delete (use a counting Bloom filter for that).
 *
 * Sizing for n items and a target false-positive rate p:
 *   m = -n * ln(p) / (ln 2)^2        k = (m / n) * ln 2
 *   e.g. 1 million items at 1% -> ~1.2 MB and 7 hashes, versus tens of MB for a HashSet<String>.
 *
 * Uses: databases skip disk reads for missing keys (Cassandra, RocksDB), web crawlers
 * ("already visited?"), CDN caches, spell checkers.
 */
public class BloomFilter {

    private final BitSet bits;
    private final int m, k;

    public BloomFilter(int expectedItems, double falsePositiveRate) {
        m = (int) Math.ceil(-expectedItems * Math.log(falsePositiveRate) / (Math.log(2) * Math.log(2)));
        k = Math.max(1, (int) Math.round((double) m / expectedItems * Math.log(2)));
        bits = new BitSet(m);
    }

    /* Double hashing: k hash functions from two base hashes, h_i = h1 + i * h2. */
    private int index(String item, int i) {
        byte[] bytes = item.getBytes(StandardCharsets.UTF_8);
        int h1 = fnv1a(bytes), h2 = item.hashCode() | 1; // odd step
        return Math.floorMod(h1 + i * h2, m);
    }

    private static int fnv1a(byte[] data) {
        int hash = 0x811c9dc5;
        for (byte b : data) {
            hash ^= b;
            hash *= 0x01000193;
        }
        return hash;
    }

    public void add(String item) {
        for (int i = 0; i < k; i++) bits.set(index(item, i));
    }

    public boolean mightContain(String item) {
        for (int i = 0; i < k; i++) if (!bits.get(index(item, i))) return false;
        return true;
    }

    public static void main(String[] args) {
        BloomFilter filter = new BloomFilter(10_000, 0.01);
        System.out.println("bits m = " + filter.m + ", hash functions k = " + filter.k);
        for (int i = 0; i < 10_000; i++) filter.add("user-" + i);

        boolean noFalseNegatives = true;
        for (int i = 0; i < 10_000; i++) noFalseNegatives &= filter.mightContain("user-" + i);
        Check.isTrue("no false negatives", noFalseNegatives);

        int falsePositives = 0;
        for (int i = 0; i < 100_000; i++) if (filter.mightContain("other-" + i)) falsePositives++;
        double rate = falsePositives / 100_000.0;
        Check.isTrue("false positive rate near 1% (" + rate + ")", rate < 0.02);
    }
}
