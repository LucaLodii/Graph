**Implementation N.01 - Graph Representation**

This project implements the reading and in-memory representation of a directed graph from a text file. The data structure was chosen to support large-scale graphs (potentially having hundreds of thousands of vertices and tens of millions of edges).

**Features**

The program receives two inputs from the user:
* The name of the file containing the directed graph data.
* The number of one of the vertices described in the file.

For the specified vertex, the implementation calculates and returns:
* Out-degree
* In-degree
* Set of successors
* Set of predecessors

**Input Format and Tests**

The text file must contain on the first line the number of vertices (n) and the number of edges (m). The following m lines must contain the representation of the edges, indicating the source and destination vertices separated by a space.

Tests can be performed using the following files provided in the specification:
* graph-test-100.txt
* graph-test-50000.txt