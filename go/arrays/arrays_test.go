package arrays

import (
	"reflect"
	"testing"
)

func TestAverages(t *testing.T) {
	marks := []float64{45, 67, 64, 67, 56, 79, 80, 76, 67, 45}
	if got := AverageWithArray(marks); got != AverageWithoutArray() {
		t.Fatalf("averages differ: %v vs %v", got, AverageWithoutArray())
	}
}

func TestDynamicArray(t *testing.T) {
	a := NewDynamicArray()
	for i := 1; i <= 5; i++ {
		a.Append(i * 10)
	}
	if got := a.Slice(); !reflect.DeepEqual(got, []int{10, 20, 30, 40, 50}) {
		t.Fatalf("append: %v", got)
	}
	if a.Capacity() != 8 {
		t.Fatalf("capacity should double 2->4->8, got %d", a.Capacity())
	}
	a.Insert(1, 15)
	if got := a.Slice(); !reflect.DeepEqual(got, []int{10, 15, 20, 30, 40, 50}) {
		t.Fatalf("insert: %v", got)
	}
	if a.RemoveAt(0) != 10 || a.IndexOf(40) != 3 {
		t.Fatal("removeAt / indexOf")
	}
	func() {
		defer func() {
			if recover() == nil {
				t.Fatal("expected panic for out-of-range Get")
			}
		}()
		a.Get(99)
	}()
	for a.Len() > 1 {
		a.RemoveAt(0)
	}
	if a.Capacity() != 2 {
		t.Fatalf("should shrink, capacity %d", a.Capacity())
	}
}
