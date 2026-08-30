// 1. ler o grafo todo
// 2. representar em forward star ou reverse star
// 3. Apresentar os seguintes itens APENAS PARA O VÉRTICE INFORMADO
//     3.1 grau de saída
//     3.2 grau de entrada
//     3.3 conjunto de sucessores
//     3.4 conjunto de predecessores

// n -> vértices (1 a n)
// m -> arestas  (lista, uma por linha)
    

#include <iostream>
#include <fstream>
#include <vector>
#include <string>

using namespace std;

class Graph { 
public:
    int n, m;
    vector<vector<int>> adj;      // Sucessores
    vector<vector<int>> adj_in;   // Predecessores

    void load(string arqName) {
        ifstream file(arqName);
        if (!file.is_open()) {
            cout << "Erro ao abrir: " << arqName << endl;
            return;
        }

        file >> n >> m;
        adj.assign(n + 1, vector<int>());
        adj_in.assign(n + 1, vector<int>());

        int u, v;
        for (int i = 0; i < m; ++i) {
            file >> u >> v;
            adj[u].push_back(v);
            adj_in[v].push_back(u);
        }
        file.close();
    }

    void printVertexInfo(int v) {
        if (v < 1 || v > n) {
            cout << "Vertice invalido." << endl;
            return;
        }
        
        cout << "Grau de saida: " << adj[v].size() << "\nSucessores: ";
        for (int x : adj[v]) cout << x << " ";
        
        cout << "\nGrau de entrada: " << adj_in[v].size() << "\nPredecessores: ";
        for (int x : adj_in[v]) cout << x << " ";
        cout << endl;
    }
};

void readArq(int arq) {
    string arqName = (arq == 1) ? "graph-test-100.txt" : "graph-test-50000.txt";
    cout << "Reading file: " << arqName << "\n\n";

    Graph g;
    g.load(arqName);

    int vertex;
    cout << "Digite o vertice: ";
    cin >> vertex;
    cout << endl;

    g.printVertexInfo(vertex);
}

int main() {
    cout << "Choose your file:\n1- graph-test-100.txt\n2- graph-test-50000.txt\nOpcao: ";
    int arq = 0;

    while (arq != 1 && arq != 2) {
        cin >> arq;
    }

    readArq(arq);

    return 0;
}