# data_structures_tutorials

Data structures built from scratch, step by step, in **Java, Python, JavaScript and Go**.
Each lesson explains the idea in its header comment (often with an ASCII diagram) and checks
itself, so running it tells you whether everything works.

Companion repo for algorithms and interview problems: **algorithams** (`interview/` track).

## Layout

Every language has the same topic folders:

```
java/src/<topic>/      Java 11, IntelliJ module (java/java_ds.iml); folder = package
python/<topic>/        standalone scripts, run with python3
javascript/<topic>/    standalone scripts, run with node
go/<topic>/            one Go package per topic, run with go test
```

## Learning path

| Step | Topic folder | Lessons | Key ideas |
|---|---|---|---|
| 1 | `arrays` | need for an array, dynamic array | O(1) indexing, amortised doubling |
| 2 | `linked_list` | singly, doubly | reverse, slow/fast pointers, sentinels |
| 3 | `stack` | array / linked / min stack, balanced brackets | LIFO |
| 4 | `queue` | circular queue, deque, queue from two stacks | FIFO, wrap-around |
| 5 | `hashing` | direct address table, chaining, linear probing | collisions, load factor, rehashing, tombstones |
| 6 | `trees` | binary tree, BST, AVL | traversals, 3 delete cases, rotations |
| 7 | `heap` | min-heap, heap sort | array layout, sift up/down, O(n) heapify |
| 8 | `trie` | trie | prefix counts, delete, autocomplete |
| 9 | `graph` | representation + BFS/DFS, algorithms | topo sort, Dijkstra, Bellman-Ford, Prim, Floyd-Warshall |
| 10 | `disjoint_set` | union-find | path compression, union by size, Kruskal |
| 11 | `advanced` | segment tree, Fenwick tree, sparse table, skip list, Bloom filter | range queries, probabilistic structures |

The same lesson has a matching name in every language:

| Java | Python | JavaScript | Go |
|---|---|---|---|
| `java/src/trees/BinarySearchTree.java` | `python/trees/binary_search_tree.py` | `javascript/trees/binary_search_tree.js` | `go/trees/bst.go` + `trees_test.go` |

JavaScript also has the original step-by-step hashing lessons
(`javascript/hashing/hash_step1_simple_hash.js` … `hash_step5_load_factor.js`) and
`linked_list/linking_nodes/` (linking nodes by hand), which come before the full implementations.

## Complexity cheat sheet (average case)

| Structure | Access | Search | Insert | Delete | Notes |
|---|---|---|---|---|---|
| Array / dynamic array | O(1) | O(n) | O(1)* end, O(n) middle | O(n) | *amortised |
| Linked list | O(n) | O(n) | O(1) at known node | O(1) at known node (doubly) | |
| Stack / queue / deque | – | – | O(1) | O(1) | |
| Hash map | – | O(1) | O(1) | O(1) | O(n) worst case |
| BST (unbalanced) | – | O(h) | O(h) | O(h) | h = n when skewed |
| AVL / red-black | – | O(log n) | O(log n) | O(log n) | Java TreeMap |
| Binary heap | min O(1) | O(n) | O(log n) | pop O(log n) | heapify O(n) |
| Trie | – | O(L) | O(L) | O(L) | L = word length |
| Union-find | – | find ≈ O(1) | union ≈ O(1) | – | inverse Ackermann |
| Segment tree | – | query O(log n) | update O(log n) | – | |
| Fenwick tree | – | prefix O(log n) | update O(log n) | – | sums / invertible ops |
| Sparse table | – | query O(1) | – | – | static data only |
| Skip list | – | O(log n) | O(log n) | O(log n) | expected |
| Bloom filter | – | O(k) | O(k) | ✗ | false positives only |

## Running

```bash
# Java
cd java && javac -d /tmp/ds $(find src -name '*.java') && java -cp /tmp/ds trees.BinarySearchTree

# Python / JavaScript
python3 python/trees/binary_search_tree.py
node javascript/trees/binary_search_tree.js

# Go
cd go && go test ./trees/        # or ./... for everything
```

Check every lesson in every language:

```bash
scripts/check_all.sh              # or: scripts/check_all.sh python go
```
