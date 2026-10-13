# data_structures_tutorials

Data structures built from scratch, step by step, in Java (`java_ds/src`) and JavaScript (`java_script_ds`).
Each Java file explains the idea in its header comment and checks itself: run `main()` and you get
`PASS …` lines, or an error if something is broken.

Companion repo for problem solving / interview patterns: `algorithams` → `java/src/interview`.

## Learning path (Java)

| Step | Topic | File | Key ideas |
|---|---|---|---|
| 1 | Arrays | [NeedForAnArray](java_ds/src/arrays/a_basics/NeedForAnArray.java), [DynamicArray](java_ds/src/arrays/b_dynamic_array/DynamicArray.java) | index O(1), amortised doubling |
| 2 | Linked lists | [SinglyLinkedList](java_ds/src/linked_list/a_basics/SinglyLinkedList.java), [DoublyLinkedList](java_ds/src/linked_list/a_basics/DoublyLinkedList.java) | reverse, slow/fast pointers, sentinels |
| 3 | Stacks | [Stacks](java_ds/src/stack/a_basics/Stacks.java) | array vs linked, min-stack, brackets |
| 4 | Queues | [Queues](java_ds/src/queue/a_basics/Queues.java) | circular buffer, deque, queue from two stacks |
| 5 | Hashing | [DAT](java_ds/src/hashing/a_basics/dat/DAT.java), [ChainingHashMap](java_ds/src/hashing/b_separate_chaining/ChainingHashMap.java), [LinearProbingHashMap](java_ds/src/hashing/c_open_addressing/LinearProbingHashMap.java) | collisions, load factor, rehashing, tombstones |
| 6 | Binary trees | [BinaryTree](java_ds/src/trees/a_binary_tree/BinaryTree.java) | 4 traversals (recursive + iterative), height, balance |
| 7 | BST | [BinarySearchTree](java_ds/src/trees/b_bst/BinarySearchTree.java) | 3 delete cases, floor/ceiling, kth smallest |
| 8 | Balanced trees | [AVLTree](java_ds/src/trees/c_avl/AVLTree.java) | rotations LL / RR / LR / RL |
| 9 | Heaps | [MinHeap](java_ds/src/heap/a_binary_heap/MinHeap.java) | array layout, sift up/down, O(n) heapify, heap sort |
| 10 | Tries | [Trie](java_ds/src/trie/a_basics/Trie.java) | prefix counts, delete, autocomplete |
| 11 | Graphs | [Graph](java_ds/src/graph/a_representation/Graph.java), [GraphAlgorithms](java_ds/src/graph/b_algorithms/GraphAlgorithms.java) | BFS/DFS, cycles, bipartite, topo sort, Dijkstra, Bellman-Ford, Prim, Floyd-Warshall |
| 12 | Disjoint set | [UnionFind](java_ds/src/disjoint_set/a_basics/UnionFind.java) | path compression, union by size, Kruskal |
| 13 | Range queries | [SegmentTree](java_ds/src/advanced/a_segment_tree/SegmentTree.java), [FenwickTree](java_ds/src/advanced/b_fenwick_tree/FenwickTree.java), [SparseTable](java_ds/src/advanced/c_sparse_table/SparseTable.java) | when to pick which |
| 14 | Probabilistic / system | [SkipList](java_ds/src/advanced/d_skip_list/SkipList.java), [BloomFilter](java_ds/src/advanced/e_bloom_filter/BloomFilter.java) | used inside Redis, RocksDB, Cassandra |

## Complexity cheat sheet (average case)

| Structure | Access | Search | Insert | Delete | Notes |
|---|---|---|---|---|---|
| Array / dynamic array | O(1) | O(n) | O(1)* end, O(n) middle | O(n) | *amortised |
| Linked list | O(n) | O(n) | O(1) at known node | O(1) at known node (doubly) | |
| Stack / queue / deque | – | – | O(1) | O(1) | |
| Hash map | – | O(1) | O(1) | O(1) | O(n) worst case |
| BST (unbalanced) | – | O(h) | O(h) | O(h) | h = n when skewed |
| AVL / red-black | – | O(log n) | O(log n) | O(log n) | Java TreeMap |
| Binary heap | min O(1) | O(n) | O(log n) | poll O(log n) | heapify O(n) |
| Trie | – | O(L) | O(L) | O(L) | L = word length |
| Union-find | – | find ≈ O(1) | union ≈ O(1) | – | inverse Ackermann |
| Segment tree | – | query O(log n) | update O(log n) | – | |
| Fenwick tree | – | prefix O(log n) | update O(log n) | – | sums / invertible ops |
| Sparse table | – | query O(1) | – | – | static data only |
| Skip list | – | O(log n) | O(log n) | O(log n) | expected |
| Bloom filter | – | O(k) | O(k) | ✗ | false positives only |

## Running
Open `java_ds` in IntelliJ and run any file, or from the command line:
```
cd java_ds
javac -d /tmp/ds $(find src -name '*.java')
java -cp /tmp/ds trees.b_bst.BinarySearchTree
```
