**Implementation N.03 - Shortest Path with the Fewest Edges**

This project finds the shortest path between two vertices of a directed graph with positive weights. When more than one shortest path exists, it returns the one with the fewest edges.

**How it works**

* The graph is stored in forward star (arrays `pointer`, `to`, `weight`).
* Dijkstra's algorithm compares the pair (cost, edges) instead of only the cost. A vertex is updated when a cheaper path is found, or when a path with the same cost and fewer edges is found.
* Complexity: O((n + m) log n).

**Features**

The program receives:
* The name of the file containing the graph.
* The origin and destination vertices.

It shows:
* The length (cost) of the shortest path.
* The number of edges of the path.
* The vertices of the path, in order.

**Input Format**

The first line holds the number of vertices (n) and edges (m). The following m lines hold `origin destination weight`, with vertices numbered from 1 to n. A line without weight counts as weight 1, so the files from N.01 also work.

`exemplo.txt` has two paths of cost 4 from 1 to 5 (`1 -> 3 -> 4 -> 5` and `1 -> 2 -> 5`); the program picks the one with 2 edges.

**Running**

```
javac ShortestPath.java Benchmark.java
java ShortestPath exemplo.txt 1 5     # or just: java ShortestPath (asks in the terminal)
java Benchmark                        # generates graphs/ and runs the tests
```

**Tests**

`Benchmark` generates two types of graphs, in four sizes each (fixed seeds, so they are always the same):
* Random: n vertices and 5n random edges (n = 1,000 to 1,000,000).
* Grid: side x side vertices, each one connected to its 4 neighbors (n = 1,024 to 1,000,000).

Weights are random integers from 1 to 10. For each graph it runs 20 random queries, compares the result with plain Dijkstra (no tie-break) and checks it against a Bellman-Ford that counts rounds. The results are in `relatorio.pdf`.
