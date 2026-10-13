/*
 * Graph as an adjacency list (O(V + E) memory) with BFS, DFS, shortest path (unweighted),
 * connected components, cycle detection and bipartite check. All O(V + E).
 */
const assert = require('node:assert/strict');

class Graph {
  constructor(vertices, directed = false) {
    this.adj = Array.from({ length: vertices }, () => []);
    this.directed = directed;
  }

  addEdge(u, v) {
    this.adj[u].push(v);
    if (!this.directed) this.adj[v].push(u);
  }

  toMatrix() {
    const m = this.adj.map(() => new Array(this.adj.length).fill(0));
    this.adj.forEach((ns, u) => ns.forEach((v) => (m[u][v] = 1)));
    return m;
  }

  bfs(start) {
    const order = [];
    const visited = new Set([start]); // mark when enqueued
    const queue = [start];
    for (let head = 0; head < queue.length; head++) { // index pointer avoids O(n) shift()
      const u = queue[head];
      order.push(u);
      for (const v of this.adj[u]) {
        if (!visited.has(v)) {
          visited.add(v);
          queue.push(v);
        }
      }
    }
    return order;
  }

  dfs(start, visited = new Set()) {
    visited.add(start);
    const order = [start];
    for (const v of this.adj[start]) if (!visited.has(v)) order.push(...this.dfs(v, visited));
    return order;
  }

  shortestPath(s, t) {
    const parent = new Map([[s, null]]);
    const queue = [s];
    for (let head = 0; head < queue.length; head++) {
      const u = queue[head];
      if (u === t) break;
      for (const v of this.adj[u]) {
        if (!parent.has(v)) {
          parent.set(v, u);
          queue.push(v);
        }
      }
    }
    if (!parent.has(t)) return [];
    const path = [];
    for (let v = t; v !== null; v = parent.get(v)) path.push(v);
    return path.reverse();
  }

  connectedComponents() {
    const visited = new Set();
    let count = 0;
    for (let u = 0; u < this.adj.length; u++) {
      if (!visited.has(u)) {
        count++;
        this.dfs(u, visited);
      }
    }
    return count;
  }

  hasCycleUndirected() {
    const visited = new Set();
    const visit = (u, parent) => {
      visited.add(u);
      for (const v of this.adj[u]) {
        if (!visited.has(v)) {
          if (visit(v, u)) return true;
        } else if (v !== parent) return true;
      }
      return false;
    };
    return this.adj.some((_, u) => !visited.has(u) && visit(u, -1));
  }

  isBipartite() {
    const color = new Array(this.adj.length).fill(0);
    for (let s = 0; s < this.adj.length; s++) {
      if (color[s]) continue;
      color[s] = 1;
      const queue = [s];
      for (let head = 0; head < queue.length; head++) {
        const u = queue[head];
        for (const v of this.adj[u]) {
          if (color[v] === 0) {
            color[v] = -color[u];
            queue.push(v);
          } else if (color[v] === color[u]) return false;
        }
      }
    }
    return true;
  }
}

//   0 --- 1 --- 3
//   |     |
//   2 --- 4     5 --- 6
const g = new Graph(7);
[[0, 1], [0, 2], [1, 3], [1, 4], [2, 4], [5, 6]].forEach(([u, v]) => g.addEdge(u, v));
assert.deepEqual(g.bfs(0), [0, 1, 2, 3, 4]);
assert.deepEqual(g.dfs(0), [0, 1, 3, 4, 2]);
assert.deepEqual(g.shortestPath(0, 4), [0, 1, 4]);
assert.deepEqual(g.shortestPath(0, 6), []);
assert.equal(g.connectedComponents(), 2);
assert.ok(g.hasCycleUndirected());
assert.ok(g.isBipartite());
g.addEdge(0, 4);
assert.equal(g.isBipartite(), false);
assert.deepEqual(new Graph(2, true).toMatrix(), [[0, 0], [0, 0]]);
console.log('all checks passed');
