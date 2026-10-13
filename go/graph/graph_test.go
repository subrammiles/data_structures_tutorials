package graph

import (
	"reflect"
	"testing"
)

func TestGraph(t *testing.T) {
	//   0 --- 1 --- 3
	//   |     |
	//   2 --- 4     5 --- 6
	g := New(7, false)
	for _, e := range [][2]int{{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 4}, {5, 6}} {
		g.AddEdge(e[0], e[1])
	}
	if !reflect.DeepEqual(g.BFS(0), []int{0, 1, 2, 3, 4}) || !reflect.DeepEqual(g.DFS(0), []int{0, 1, 3, 4, 2}) {
		t.Fatalf("bfs %v dfs %v", g.BFS(0), g.DFS(0))
	}
	if !reflect.DeepEqual(g.ShortestPath(0, 4), []int{0, 1, 4}) || g.ShortestPath(0, 6) != nil {
		t.Fatal("shortest path")
	}
	if g.ConnectedComponents() != 2 || !g.HasCycleUndirected() || !g.IsBipartite() {
		t.Fatal("components / cycle / bipartite")
	}
	g.AddEdge(0, 4)
	if g.IsBipartite() {
		t.Fatal("odd cycle is not bipartite")
	}
}

func TestAlgorithms(t *testing.T) {
	dressing := []Edge{{0, 1, 1}, {1, 2, 1}, {3, 2, 1}, {1, 5, 1}, {4, 5, 1}}
	if got := TopologicalSort(6, dressing); !reflect.DeepEqual(got, []int{0, 3, 4, 1, 2, 5}) {
		t.Fatalf("topo %v", got)
	}
	if TopologicalSort(2, []Edge{{0, 1, 1}, {1, 0, 1}}) != nil {
		t.Fatal("cycle should return nil")
	}
	roads := []Edge{{0, 1, 4}, {0, 2, 1}, {2, 1, 2}, {1, 3, 1}, {2, 3, 5}}
	want := []int{0, 3, 1, 4}
	if !reflect.DeepEqual(Dijkstra(4, roads, 0), want) || !reflect.DeepEqual(BellmanFord(4, roads, 0), want) {
		t.Fatal("dijkstra / bellman-ford")
	}
	if got := BellmanFord(3, []Edge{{0, 1, 4}, {0, 2, 5}, {2, 1, -3}}, 0); !reflect.DeepEqual(got, []int{0, 2, 5}) {
		t.Fatalf("negative edge %v", got)
	}
	if BellmanFord(2, []Edge{{0, 1, 1}, {1, 0, -2}}, 0) != nil {
		t.Fatal("negative cycle")
	}
	cables := []Edge{{0, 1, 2}, {0, 3, 6}, {1, 2, 3}, {1, 3, 8}, {1, 4, 5}, {2, 4, 7}, {3, 4, 9}}
	if PrimMSTWeight(5, cables) != 16 || FloydWarshall(4, roads)[0][3] != 4 {
		t.Fatal("prim / floyd")
	}
}
