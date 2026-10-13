// Package graph: adjacency-list graphs, traversals and classic algorithms.
//
//	BFS / DFS / components / cycle / bipartite   O(V + E)
//	Topological sort (Kahn)                       O(V + E), DAG only
//	Dijkstra (container/heap)                     O(E log V), non-negative weights
//	Bellman-Ford                                  O(V * E), negative weights, detects negative cycles
//	Prim MST                                      O(E log V)
//	Floyd-Warshall                                O(V^3), all pairs
package graph

type Graph struct {
	Adj      [][]int
	directed bool
}

func New(vertices int, directed bool) *Graph {
	return &Graph{Adj: make([][]int, vertices), directed: directed}
}

func (g *Graph) AddEdge(u, v int) {
	g.Adj[u] = append(g.Adj[u], v)
	if !g.directed {
		g.Adj[v] = append(g.Adj[v], u)
	}
}

func (g *Graph) BFS(start int) []int {
	visited := map[int]bool{start: true} // mark when enqueued
	queue := []int{start}
	for head := 0; head < len(queue); head++ {
		for _, v := range g.Adj[queue[head]] {
			if !visited[v] {
				visited[v] = true
				queue = append(queue, v)
			}
		}
	}
	return queue // the queue itself is the visit order
}

func (g *Graph) DFS(start int) []int {
	return g.dfs(start, map[int]bool{})
}

func (g *Graph) dfs(u int, visited map[int]bool) []int {
	visited[u] = true
	order := []int{u}
	for _, v := range g.Adj[u] {
		if !visited[v] {
			order = append(order, g.dfs(v, visited)...)
		}
	}
	return order
}

// ShortestPath returns the path with the fewest edges, or nil.
func (g *Graph) ShortestPath(s, t int) []int {
	parent := map[int]int{s: -1}
	queue := []int{s}
	for head := 0; head < len(queue) && queue[head] != t; head++ {
		for _, v := range g.Adj[queue[head]] {
			if _, seen := parent[v]; !seen {
				parent[v] = queue[head]
				queue = append(queue, v)
			}
		}
	}
	if _, ok := parent[t]; !ok {
		return nil
	}
	var path []int
	for v := t; v != -1; v = parent[v] {
		path = append([]int{v}, path...)
	}
	return path
}

func (g *Graph) ConnectedComponents() int {
	visited, count := map[int]bool{}, 0
	for u := range g.Adj {
		if !visited[u] {
			count++
			g.dfs(u, visited)
		}
	}
	return count
}

func (g *Graph) HasCycleUndirected() bool {
	visited := map[int]bool{}
	var visit func(u, parent int) bool
	visit = func(u, parent int) bool {
		visited[u] = true
		for _, v := range g.Adj[u] {
			if !visited[v] {
				if visit(v, u) {
					return true
				}
			} else if v != parent {
				return true
			}
		}
		return false
	}
	for u := range g.Adj {
		if !visited[u] && visit(u, -1) {
			return true
		}
	}
	return false
}

func (g *Graph) IsBipartite() bool {
	color := make([]int, len(g.Adj))
	for s := range g.Adj {
		if color[s] != 0 {
			continue
		}
		color[s] = 1
		queue := []int{s}
		for head := 0; head < len(queue); head++ {
			u := queue[head]
			for _, v := range g.Adj[u] {
				if color[v] == 0 {
					color[v] = -color[u]
					queue = append(queue, v)
				} else if color[v] == color[u] {
					return false
				}
			}
		}
	}
	return true
}
