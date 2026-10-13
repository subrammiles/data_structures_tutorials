package advanced

import (
	"crypto/sha256"
	"encoding/binary"
	"math"
)

// BloomFilter: false positives possible, false negatives impossible.
// m = -n ln p / (ln 2)^2 bits, k = (m / n) ln 2 hash functions (via double hashing).
type BloomFilter struct {
	bits []uint64
	M, K int
}

func NewBloomFilter(expectedItems int, falsePositiveRate float64) *BloomFilter {
	m := int(math.Ceil(-float64(expectedItems) * math.Log(falsePositiveRate) / (math.Ln2 * math.Ln2)))
	k := max(1, int(math.Round(float64(m)/float64(expectedItems)*math.Ln2)))
	return &BloomFilter{bits: make([]uint64, (m+63)/64), M: m, K: k}
}

func (b *BloomFilter) indexes(item string) []int {
	sum := sha256.Sum256([]byte(item))
	h1 := binary.LittleEndian.Uint64(sum[0:8])
	h2 := binary.LittleEndian.Uint64(sum[8:16]) | 1
	out := make([]int, b.K)
	for i := range out {
		out[i] = int((h1 + uint64(i)*h2) % uint64(b.M))
	}
	return out
}

func (b *BloomFilter) Add(item string) {
	for _, i := range b.indexes(item) {
		b.bits[i/64] |= 1 << (i % 64)
	}
}

func (b *BloomFilter) MightContain(item string) bool {
	for _, i := range b.indexes(item) {
		if b.bits[i/64]&(1<<(i%64)) == 0 {
			return false
		}
	}
	return true
}
