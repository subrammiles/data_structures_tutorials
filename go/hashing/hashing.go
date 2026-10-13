// Package hashing builds a key-value store step by step:
//
//  1. DirectAddressTable - the key IS the array index. O(1) but keys must be small integers.
//  2. ChainingHashMap    - index = hash(key) % capacity; collisions go into a per-bucket chain.
//  3. LinearProbingHashMap - collisions probe the next slot; deletes leave tombstones.
//
// Both hash maps keep their load factor low by doubling and rehashing.
// Go's built-in map is a hash table (buckets of 8 entries + overflow chains).
package hashing

import "hash/fnv"

type Student struct {
	RollNumber int
	Name       string
	Marks      int
}

type DirectAddressTable struct {
	slots []*Student
}

func NewDirectAddressTable(maxRollNumber int) *DirectAddressTable {
	return &DirectAddressTable{slots: make([]*Student, maxRollNumber+1)}
}

func (t *DirectAddressTable) Insert(s *Student)        { t.slots[s.RollNumber] = s }
func (t *DirectAddressTable) Search(roll int) *Student { return t.slots[roll] }
func (t *DirectAddressTable) Delete(roll int)          { t.slots[roll] = nil }

func hashString(key string) uint64 {
	h := fnv.New64a()
	h.Write([]byte(key))
	return h.Sum64()
}

type entry struct {
	key   string
	value int
	next  *entry
}

type ChainingHashMap struct {
	buckets []*entry
	size    int
}

const maxLoadFactor = 0.75

func NewChainingHashMap() *ChainingHashMap {
	return &ChainingHashMap{buckets: make([]*entry, 4)}
}

func (m *ChainingHashMap) index(key string, capacity int) int {
	return int(hashString(key) % uint64(capacity))
}

// Put inserts or updates; it returns the previous value and whether there was one.
func (m *ChainingHashMap) Put(key string, value int) (int, bool) {
	i := m.index(key, len(m.buckets))
	for e := m.buckets[i]; e != nil; e = e.next {
		if e.key == key {
			old := e.value
			e.value = value
			return old, true
		}
	}
	m.buckets[i] = &entry{key, value, m.buckets[i]}
	m.size++
	if float64(m.size)/float64(len(m.buckets)) > maxLoadFactor {
		m.resize()
	}
	return 0, false
}

func (m *ChainingHashMap) Get(key string) (int, bool) {
	for e := m.buckets[m.index(key, len(m.buckets))]; e != nil; e = e.next {
		if e.key == key {
			return e.value, true
		}
	}
	return 0, false
}

func (m *ChainingHashMap) Remove(key string) (int, bool) {
	i := m.index(key, len(m.buckets))
	var prev *entry
	for e := m.buckets[i]; e != nil; prev, e = e, e.next {
		if e.key == key {
			if prev == nil {
				m.buckets[i] = e.next
			} else {
				prev.next = e.next
			}
			m.size--
			return e.value, true
		}
	}
	return 0, false
}

func (m *ChainingHashMap) resize() {
	old := m.buckets
	m.buckets = make([]*entry, 2*len(old))
	for _, head := range old {
		for e := head; e != nil; e = e.next {
			i := m.index(e.key, len(m.buckets)) // index changes with capacity
			m.buckets[i] = &entry{e.key, e.value, m.buckets[i]}
		}
	}
}

func (m *ChainingHashMap) Len() int      { return m.size }
func (m *ChainingHashMap) Capacity() int { return len(m.buckets) }

func (m *ChainingHashMap) LongestChain() int {
	longest := 0
	for _, head := range m.buckets {
		n := 0
		for e := head; e != nil; e = e.next {
			n++
		}
		longest = max(longest, n)
	}
	return longest
}

type slotState byte

const (
	empty slotState = iota
	used
	tombstone // deleted: keep probing past me, but I may be reused
)

type LinearProbingHashMap struct {
	keys             []string
	values           []int
	state            []slotState
	size, tombstones int
	hash             func(string) uint64
}

func NewLinearProbingHashMap(hash func(string) uint64) *LinearProbingHashMap {
	if hash == nil {
		hash = hashString
	}
	m := &LinearProbingHashMap{hash: hash}
	m.alloc(8)
	return m
}

func (m *LinearProbingHashMap) alloc(capacity int) {
	m.keys, m.values, m.state = make([]string, capacity), make([]int, capacity), make([]slotState, capacity)
}

func (m *LinearProbingHashMap) Put(key string, value int) {
	if (m.size+m.tombstones+1)*2 > len(m.keys) { // keep load factor <= 0.5
		m.resize()
	}
	n := len(m.keys)
	i, firstTombstone := int(m.hash(key)%uint64(n)), -1
	for m.state[i] != empty {
		if m.state[i] == tombstone {
			if firstTombstone == -1 {
				firstTombstone = i
			}
		} else if m.keys[i] == key {
			m.values[i] = value
			return
		}
		i = (i + 1) % n
	}
	if firstTombstone != -1 {
		i = firstTombstone
		m.tombstones--
	}
	m.keys[i], m.values[i], m.state[i] = key, value, used
	m.size++
}

func (m *LinearProbingHashMap) find(key string) int {
	n := len(m.keys)
	for i := int(m.hash(key) % uint64(n)); m.state[i] != empty; i = (i + 1) % n {
		if m.state[i] == used && m.keys[i] == key {
			return i
		}
	}
	return -1
}

func (m *LinearProbingHashMap) Get(key string) (int, bool) {
	if i := m.find(key); i != -1 {
		return m.values[i], true
	}
	return 0, false
}

func (m *LinearProbingHashMap) Remove(key string) bool {
	i := m.find(key)
	if i == -1 {
		return false
	}
	m.state[i] = tombstone
	m.size--
	m.tombstones++
	return true
}

func (m *LinearProbingHashMap) resize() {
	keys, values, state := m.keys, m.values, m.state
	m.alloc(2 * len(keys))
	m.size, m.tombstones = 0, 0 // rehashing drops tombstones
	for i := range keys {
		if state[i] == used {
			m.Put(keys[i], values[i])
		}
	}
}

func (m *LinearProbingHashMap) Len() int { return m.size }
