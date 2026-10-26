// 1. ler o grafo direcionado e ponderado (pesos positivos)
// 2. representar em forward star
// 3. Dijkstra comparando o par (custo, arestas):
//    entre caminhos de mesmo custo, fica o que tem menos arestas
// 4. exibir o custo, o numero de arestas e os vertices do caminho

// Arquivo: primeira linha "n m", depois m linhas "origem destino peso" (vertices de 1 a n).
// Linha sem peso vale peso 1 (assim os arquivos da implementacao N.01 tambem funcionam).

// Uso: java ShortestPath <arquivo> <origem> <destino>   (sem argumentos, pergunta no terminal)

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;

public class ShortestPath {
    static final long INF = Long.MAX_VALUE;

    int n, m;
    int[] pointer;   // arestas de u ficam nas posicoes [pointer[u], pointer[u + 1])
    int[] to;        // destino de cada aresta
    long[] weight;   // peso de cada aresta

    long[] dist;     // custo do melhor caminho ate v
    int[] edges;     // numero de arestas desse caminho
    int[] parent;    // vertice anterior a v no caminho

    void load(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        String[] t = in.readLine().trim().split("\\s+");
        n = Integer.parseInt(t[0]);
        m = Integer.parseInt(t[1]);

        int[] from = new int[m];
        int[] dest = new int[m];
        long[] w = new long[m];
        pointer = new int[n + 2];

        for (int i = 0; i < m; i++) {
            t = in.readLine().trim().split("\\s+");
            from[i] = Integer.parseInt(t[0]);
            dest[i] = Integer.parseInt(t[1]);
            w[i] = t.length > 2 ? Long.parseLong(t[2]) : 1;
            pointer[from[i] + 1]++;   // conta as arestas de cada vertice
        }
        in.close();

        // soma acumulada: pointer[u] vira o inicio das arestas de u
        for (int u = 1; u <= n + 1; u++) pointer[u] += pointer[u - 1];

        to = new int[m];
        weight = new long[m];
        int[] pos = pointer.clone();
        for (int i = 0; i < m; i++) {
            int k = pos[from[i]]++;
            to[k] = dest[i];
            weight[k] = w[i];
        }
    }

    // tieBreak = true  -> desempata pelo numero de arestas (o que a tarefa pede)
    // tieBreak = false -> Dijkstra comum, usado so para comparar nos testes
    void dijkstra(int s, int target, boolean tieBreak) {
        dist = new long[n + 1];
        edges = new int[n + 1];
        parent = new int[n + 1];
        boolean[] done = new boolean[n + 1];
        for (int v = 1; v <= n; v++) dist[v] = INF;
        dist[s] = 0;

        // fila guarda {custo, arestas, vertice}, menor custo primeiro e, no empate, menos arestas
        PriorityQueue<long[]> queue = new PriorityQueue<>(
            (a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));
        queue.add(new long[] {0, 0, s});

        while (!queue.isEmpty()) {
            int u = (int) queue.poll()[2];
            if (done[u]) continue;
            done[u] = true;
            if (u == target) break;

            for (int k = pointer[u]; k < pointer[u + 1]; k++) {
                int v = to[k];
                long d = dist[u] + weight[k];
                int e = edges[u] + 1;
                if (d < dist[v] || (tieBreak && d == dist[v] && e < edges[v])) {
                    dist[v] = d;
                    edges[v] = e;
                    parent[v] = u;
                    queue.add(new long[] {d, tieBreak ? e : 0, v});
                }
            }
        }
    }

    String path(int target) {
        int[] p = new int[edges[target] + 1];
        int v = target;
        for (int i = edges[target]; i >= 0; i--) {
            p[i] = v;
            v = parent[v];
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < p.length; i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(p[i]);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        String file;
        int s, t;
        if (args.length >= 3) {
            file = args[0];
            s = Integer.parseInt(args[1]);
            t = Integer.parseInt(args[2]);
        } else {
            BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Arquivo do grafo: ");
            file = kb.readLine().trim();
            System.out.print("Vertice de origem: ");
            s = Integer.parseInt(kb.readLine().trim());
            System.out.print("Vertice de destino: ");
            t = Integer.parseInt(kb.readLine().trim());
        }

        ShortestPath g = new ShortestPath();
        long t0 = System.nanoTime();
        g.load(file);
        long t1 = System.nanoTime();

        System.out.println("\nGrafo: " + file + " (n = " + g.n + ", m = " + g.m + ")");
        if (s < 1 || s > g.n || t < 1 || t > g.n) {
            System.out.println("Vertice invalido.");
            return;
        }

        g.dijkstra(s, t, true);
        long t2 = System.nanoTime();

        System.out.println("Origem: " + s + " | Destino: " + t);
        if (g.dist[t] == INF) {
            System.out.println("Nao existe caminho de " + s + " para " + t + ".");
        } else {
            System.out.println("Comprimento do caminho minimo: " + g.dist[t]);
            System.out.println("Numero de arestas: " + g.edges[t]);
            System.out.println("Caminho: " + g.path(t));
        }
        System.out.printf("Tempo de leitura: %.2f ms | Tempo do Dijkstra: %.2f ms%n",
            (t1 - t0) / 1e6, (t2 - t1) / 1e6);
    }
}
