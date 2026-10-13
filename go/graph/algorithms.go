package graph

import (
	"container/heap"
	"math"
)

// Edge is a weighted, directed edge.
type Edge struct{ From, To, Weight int }

const Inf = math.MaxInt / 2

type arc struct{ to, w int }

func adjacency(n int, edges []Edge, directed bool) [][]arc {
	adj := make([][]arc, n)
	for _, e := range edges {
		adj[e.From] = append(adj[e.From], arc{e.To, e.Weight})
		if !directed {
			adj[e.To] = append(adj[e.To], arc{e.From, e.Weight})
		}
	}
	return adj
}

// TopologicalSort returns nil if the graph has a cycle.
func TopologicalSort(n int, edges []Edge) []int {
	adj := adjacency(n, edges, true)
	indegree := make([]int, n)
	for _, e := range edges {
		indegree[e.To]++
	}
	var queue []int
	for i, d := range indegree {
		if d == 0 {
			queue = append(queue, i)
		}
	}
	for head := 0; head < len(queue); head++ {
		for _, a := range adj[queue[head]] {
			if indegree[a.to]--; indegree[a.to] == 0 {
				queue = append(queue, a.to)
			}
		}
	}
	if len(queue) != n {
		return nil
	}
	return queue
}

// item / pq implement container/heap.Interface for (vertex, distance) pairs.
type item struct{ v, d int }
type pq []item

func (p pq) Len() int           { return len(p) }
func (p pq) Less(i, j int) bool { return p[i].d < p[j].d }
func (p pq) Swap(i, j int)      { p[i], p[j] = p[j], p[i] }
func (p *pq) Push(x any)        { *p = append(*p, x.(item)) }
func (p *pq) Pop() any          { old := *p; x := old[len(old)-1]; *p = old[:len(old)-1]; return x }

func Dijkstra(n int, edges []Edge, source int) []int {
	adj := adjacency(n, edges, true)
	dist := make([]int, n)
	for i := range dist {
		dist[i] = Inf
	}
	dist[source] = 0
	h := &pq{{source, 0}}
	for h.Len() > 0 {
		top := heap.Pop(h).(item)
		if top.d > dist[top.v] {
			continue // outdated entry
		}
		for _, a := range adj[top.v] {
			if nd := top.d + a.w; nd < dist[a.to] {
				dist[a.to] = nd
				heap.Push(h, item{a.to, nd})
			}
		}
	}
	return dist
}

// BellmanFord returns nil if a negative cycle is reachable.
func BellmanFord(n int, edges []Edge, source int) []int {
	dist := make([]int, n)
	for i := range dist {
		dist[i] = Inf
	}
	dist[source] = 0
	for round := 0; round < n-1; round++ {
		for _, e := range edges {
			if dist[e.From] != Inf && dist[e.From]+e.Weight < dist[e.To] {
				dist[e.To] = dist[e.From] + e.Weight
			}
		}
	}
	for _, e := range edges {
		if dist[e.From] != Inf && dist[e.From]+e.Weight < dist[e.To] {
			return nil
		}
	}
	return dist
}

func PrimMSTWeight(n int, edges []Edge) int {
	adj := adjacency(n, edges, false)
	inTree := make([]bool, n)
	h := &pq{{0, 0}}
	total, added := 0, 0
	for h.Len() > 0 {
		top := heap.Pop(h).(item)
		if inTree[top.v] {
			continue
		}
		inTree[top.v] = true
		total += top.d
		added++
		for _, a := range adj[top.v] {
			if !inTree[a.to] {
				heap.Push(h, item{a.to, a.w})
			}
		}
	}
	if added != n {
		return -1
	}
	return total
}

func FloydWarshall(n int, edges []Edge) [][]int {
	d := make([][]int, n)
	for i := range d {
		d[i] = make([]int, n)
		for j := range d[i] {
			if i != j {
				d[i][j] = Inf
			}
		}
	}
	for _, e := range edges {
		d[e.From][e.To] = min(d[e.From][e.To], e.Weight)
	}
	for k := 0; k < n; k++ {
		for i := 0; i < n; i++ {
			for j := 0; j < n; j++ {
				if d[i][k]+d[k][j] < d[i][j] {
					d[i][j] = d[i][k] + d[k][j]
				}
			}
		}
	}
	return d
}
