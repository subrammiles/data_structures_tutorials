package trie

import (
	"reflect"
	"testing"
)

func TestTrie(t *testing.T) {
	tr := New()
	for _, w := range []string{"car", "cat", "cart", "dog"} {
		tr.Insert(w)
	}
	if !tr.Search("car") || tr.Search("ca") || !tr.StartsWith("ca") {
		t.Fatal("search / startsWith")
	}
	if tr.CountWordsWithPrefix("ca") != 3 {
		t.Fatal("prefix count")
	}
	if got := tr.WordsWithPrefix("car"); !reflect.DeepEqual(got, []string{"car", "cart"}) {
		t.Fatalf("autocomplete %v", got)
	}
	if !tr.Delete("car") || tr.Search("car") || !tr.Search("cart") {
		t.Fatal("delete car")
	}
	tr.Delete("dog")
	if tr.StartsWith("d") || tr.CountWordsWithPrefix("c") != 2 {
		t.Fatal("delete dog")
	}
}
