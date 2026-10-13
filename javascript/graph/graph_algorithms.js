/*
 * Topological sort (Kahn), Dijkstra, Bellman-Ford, Prim, Floyd-Warshall. Edges: [from, to, weight].
 * Uses the MinHeap from ../heap/min_heap.js (JS has no built-in priority queue).
 */
const assert = require('node:assert/strict');
const { MinHeap } = require('../heap/min_heap');

function adjacency(n, edges, directed = true) {
  const adj = Array.from({ length: n }, () => []);
  for (const [u, v, w = 1] of edges) {
    adj[u].push([v, w]);
    if (!directed) adj[v].push([u, w]);
  }
  return adj;
}

function topologicalSort(n, edges) {
  const adj = adjacency(n, edges);
  const indegree = new Array(n).fill(0);
  edges.forEach(([, v]) => indegree[v]++);
  const queue = [];
  indegree.forEach((d, i) => d === 0 && queue.push(i));
  for (let head = 0; head < queue.length; head++) {
    for (const [v] of adj[queue[head]]) if (--indegree[v] === 0) queue.push(v);
  }
  return queue.length === n ? queue : null; // null: cycle
}

function dijkstra(n, edges, source) {
  const adj = adjacency(n, edges);
  const dist = new Array(n).fill(Infinity);
  dist[source] = 0;
  const heap = new MinHeap([[0, source]], (a, b) => a[0] - b[0]);
  while (heap.size) {
    const [d, u] = heap.pop();
    if (d > dist[u]) continue; // outdated entry
    for (const [v, w] of adj[u]) {
      if (d + w < dist[v]) {
        dist[v] = d + w;
        heap.push([dist[v], v]);
      }
    }
  }
  return dist;
}

function bellmanFord(n, edges, source) {
  const dist = new Array(n).fill(Infinity);
  dist[source] = 0;
  for (let round = 0; round < n - 1; round++) {
    for (const [u, v, w] of edges) if (dist[u] + w < dist[v]) dist[v] = dist[u] + w;
  }
  return edges.some(([u, v, w]) => dist[u] + w < dist[v]) ? null : dist; // null: negative cycle
}

function primMstWeight(n, edges) {
  const adj = adjacency(n, edges, false);
  const inTree = new Array(n).fill(false);
  const heap = new MinHeap([[0, 0]], (a, b) => a[0] - b[0]);
  let total = 0;
  while (heap.size) {
    const [w, u] = heap.pop();
    if (inTree[u]) continue;
    inTree[u] = true;
    total += w;
    for (const [v, wv] of adj[u]) if (!inTree[v]) heap.push([wv, v]);
  }
  return inTree.every(Boolean) ? total : -1;
}

function floydWarshall(n, edges) {
  const d = Array.from({ length: n }, (_, i) => Array.from({ length: n }, (_, j) => (i === j ? 0 : Infinity)));
  for (const [u, v, w] of edges) d[u][v] = Math.min(d[u][v], w);
  for (let k = 0; k < n; k++)
    for (let i = 0; i < n; i++)
      for (let j = 0; j < n; j++) if (d[i][k] + d[k][j] < d[i][j]) d[i][j] = d[i][k] + d[k][j];
  return d;
}

const dressing = [[0, 1], [1, 2], [3, 2], [1, 5], [4, 5]];
assert.deepEqual(topologicalSort(6, dressing), [0, 3, 4, 1, 2, 5]);
assert.equal(topologicalSort(2, [[0, 1], [1, 0]]), null);

const roads = [[0, 1, 4], [0, 2, 1], [2, 1, 2], [1, 3, 1], [2, 3, 5]];
assert.deepEqual(dijkstra(4, roads, 0), [0, 3, 1, 4]);
assert.deepEqual(bellmanFord(4, roads, 0), [0, 3, 1, 4]);
assert.deepEqual(bellmanFord(3, [[0, 1, 4], [0, 2, 5], [2, 1, -3]], 0), [0, 2, 5]);
assert.equal(bellmanFord(2, [[0, 1, 1], [1, 0, -2]], 0), null);

const cables = [[0, 1, 2], [0, 3, 6], [1, 2, 3], [1, 3, 8], [1, 4, 5], [2, 4, 7], [3, 4, 9]];
assert.equal(primMstWeight(5, cables), 16);
assert.equal(floydWarshall(4, roads)[0][3], 4);
console.log('all checks passed');
