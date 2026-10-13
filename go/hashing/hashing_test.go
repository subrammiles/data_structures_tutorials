package hashing

import (
	"strconv"
	"testing"
)

func TestDirectAddressTable(t *testing.T) {
	dat := NewDirectAddressTable(5)
	dat.Insert(&Student{1, "Subbu", 78})
	dat.Insert(&Student{2, "Ravi", 95})
	if dat.Search(2).Name != "Ravi" {
		t.Fatal("search")
	}
	dat.Delete(2)
	if dat.Search(2) != nil || dat.Search(4) != nil {
		t.Fatal("delete / empty slot")
	}
}

func TestChainingHashMap(t *testing.T) {
	m := NewChainingHashMap()
	m.Put("cat", 2)
	m.Put("tiger", 7)
	m.Put("lion", 1)
	if old, ok := m.Put("cat", 3); !ok || old != 2 {
		t.Fatal("update should return old value")
	}
	if v, _ := m.Get("cat"); v != 3 {
		t.Fatal("get after update")
	}
	if _, ok := m.Get("ox"); ok {
		t.Fatal("missing key")
	}
	if v, ok := m.Remove("tiger"); !ok || v != 7 || m.Len() != 2 {
		t.Fatal("remove")
	}

	big := NewChainingHashMap()
	for i := 0; i < 1000; i++ {
		big.Put(strconv.Itoa(i), i*i)
	}
	if v, _ := big.Get("999"); v != 998001 {
		t.Fatal("get after resizes")
	}
	if float64(big.Len()) > 0.75*float64(big.Capacity()) || big.LongestChain() > 5 {
		t.Fatalf("load factor / chain length: %d/%d, chain %d", big.Len(), big.Capacity(), big.LongestChain())
	}
}

func TestLinearProbingHashMap(t *testing.T) {
	collide := NewLinearProbingHashMap(func(string) uint64 { return 42 }) // every key collides
	collide.Put("a", 1)
	collide.Put("b", 2)
	if !collide.Remove("a") {
		t.Fatal("remove")
	}
	if v, ok := collide.Get("b"); !ok || v != 2 {
		t.Fatal("must find b past the tombstone")
	}
	collide.Put("a", 10)
	if v, _ := collide.Get("a"); v != 10 {
		t.Fatal("reinsert")
	}

	m := NewLinearProbingHashMap(nil)
	for i := 0; i < 100; i++ {
		m.Put("key"+strconv.Itoa(i), i)
	}
	if v, _ := m.Get("key77"); v != 77 || m.Len() != 100 {
		t.Fatal("after resizes")
	}
}
