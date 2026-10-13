// Package arrays shows why arrays exist and how a dynamic array grows.
//
// A Go slice IS a dynamic array: a pointer, a length and a capacity. append doubles the
// capacity (roughly) when it runs out. DynamicArray re-implements that by hand.
//
//	Get/Set O(1) | Append O(1) amortised | Insert/RemoveAt O(n) (shifting)
package arrays

import "fmt"

// AverageWithoutArray needs one variable per student - it does not scale.
func AverageWithoutArray() float64 {
	s1, s2, s3, s4, s5, s6, s7, s8, s9, s10 := 45.0, 67.0, 64.0, 67.0, 56.0, 79.0, 80.0, 76.0, 67.0, 45.0
	return (s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 + s9 + s10) / 10
}

// AverageWithArray handles any number of students with one loop.
func AverageWithArray(marks []float64) float64 {
	total := 0.0
	for _, m := range marks {
		total += m
	}
	return total / float64(len(marks))
}

type DynamicArray struct {
	data []int // used only up to cap: we manage the length ourselves
	size int
}

func NewDynamicArray() *DynamicArray {
	return &DynamicArray{data: make([]int, 2)}
}

func (a *DynamicArray) Len() int      { return a.size }
func (a *DynamicArray) Capacity() int { return len(a.data) }

func (a *DynamicArray) Get(i int) int {
	a.check(i)
	return a.data[i]
}

func (a *DynamicArray) Set(i, v int) {
	a.check(i)
	a.data[i] = v
}

func (a *DynamicArray) Append(v int) {
	if a.size == len(a.data) {
		a.resize(2 * len(a.data))
	}
	a.data[a.size] = v
	a.size++
}

func (a *DynamicArray) Insert(i, v int) {
	if i < 0 || i > a.size {
		panic(fmt.Sprintf("index %d out of range", i))
	}
	if a.size == len(a.data) {
		a.resize(2 * len(a.data))
	}
	for j := a.size; j > i; j-- { // shift right
		a.data[j] = a.data[j-1]
	}
	a.data[i] = v
	a.size++
}

func (a *DynamicArray) RemoveAt(i int) int {
	a.check(i)
	removed := a.data[i]
	for j := i; j < a.size-1; j++ { // shift left
		a.data[j] = a.data[j+1]
	}
	a.size--
	if a.size > 0 && a.size == len(a.data)/4 { // shrink at 1/4, not 1/2
		a.resize(len(a.data) / 2)
	}
	return removed
}

func (a *DynamicArray) IndexOf(v int) int {
	for i := 0; i < a.size; i++ {
		if a.data[i] == v {
			return i
		}
	}
	return -1
}

func (a *DynamicArray) Slice() []int {
	return append([]int(nil), a.data[:a.size]...)
}

func (a *DynamicArray) resize(capacity int) {
	bigger := make([]int, capacity)
	copy(bigger, a.data[:a.size])
	a.data = bigger
}

func (a *DynamicArray) check(i int) {
	if i < 0 || i >= a.size {
		panic(fmt.Sprintf("index %d out of range [0,%d)", i, a.size))
	}
}
