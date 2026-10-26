// Testes da implementacao N.03
// 1. gera os grafos de teste na pasta graphs/ (se ainda nao existirem)
//      aleatorio: n vertices e 5n arestas sorteadas
//      grade:     lado x lado, cada vertice liga nos vizinhos de cima, baixo, esquerda e direita
//      pesos inteiros sorteados de 1 a 10 (sementes fixas, entao os grafos sao sempre os mesmos)
// 2. para cada grafo, sorteia 20 pares (origem, destino) e mede
//      eficacia:   custo e arestas do caminho, comparando com o Dijkstra comum (sem desempate)
//                  e conferindo com um Bellman-Ford por rodadas (so nos grafos menores)
//      eficiencia: tempo de leitura e tempo medio por consulta
// 3. imprime as tabelas e salva tudo em resultados.csv

// Uso: java Benchmark

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Benchmark {
    static final int QUERIES = 20;
    static final int MAX_WEIGHT = 10;

    public static void main(String[] args) throws IOException {
        new File("graphs").mkdirs();
        BufferedWriter csv = new BufferedWriter(new FileWriter("resultados.csv"));
        csv.write("tipo,n,m,alcancaveis,custo_medio,arestas_desempate,arestas_comum,"
            + "consultas_melhoradas,verificadas,leitura_ms,consulta_desempate_ms,consulta_comum_ms\n");

        int[] sizes = {1000, 10000, 100000, 1000000};
        for (String type : new String[] {"aleatorio", "grade"}) {
            System.out.println("\nGrafos do tipo " + type + " (" + QUERIES + " consultas por grafo)");
            System.out.printf("%9s %9s %7s %8s %8s %8s %6s %6s %10s %10s %10s%n", "n", "m", "alcanc.",
                "custo", "arestas", "comum", "melh.", "verif.", "leitura", "desempate", "comum");

            for (int size : sizes) {
                String file;
                if (type.equals("aleatorio")) {
                    file = "graphs/aleatorio-" + size + ".txt";
                    if (!new File(file).exists()) randomGraph(file, size);
                } else {
                    int side = (int) Math.round(Math.sqrt(size));
                    file = "graphs/grade-" + side + "x" + side + ".txt";
                    if (!new File(file).exists()) gridGraph(file, side);
                }
                run(type, file, csv);
            }
        }
        csv.close();
    }

    static void randomGraph(String file, int n) throws IOException {
        Random r = new Random(n);
        int m = 5 * n;
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(n + " " + m + "\n");
        for (int i = 0; i < m; i++) {
            int u = r.nextInt(n) + 1;
            int v = r.nextInt(n) + 1;
            while (v == u) v = r.nextInt(n) + 1;
            out.write(u + " " + v + " " + (r.nextInt(MAX_WEIGHT) + 1) + "\n");
        }
        out.close();
    }

    static void gridGraph(String file, int side) throws IOException {
        Random r = new Random(side);
        int n = side * side;
        int m = 4 * side * (side - 1);
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(n + " " + m + "\n");
        for (int row = 0; row < side; row++) {
            for (int col = 0; col < side; col++) {
                for (int d = 0; d < 4; d++) {
                    int nRow = row + dRow[d];
                    int nCol = col + dCol[d];
                    if (nRow < 0 || nRow >= side || nCol < 0 || nCol >= side) continue;
                    int u = row * side + col + 1;
                    int v = nRow * side + nCol + 1;
                    out.write(u + " " + v + " " + (r.nextInt(MAX_WEIGHT) + 1) + "\n");
                }
            }
        }
        out.close();
    }

    static void run(String type, String file, BufferedWriter csv) throws IOException {
        ShortestPath g = new ShortestPath();
        long t0 = System.nanoTime();
        g.load(file);
        double loadMs = (System.nanoTime() - t0) / 1e6;

        boolean verify = g.n <= 200000;   // Bellman-Ford e O(n * m), so da para usar nos menores
        Random r = new Random(42);

        // aquece a JVM antes de medir
        for (int i = 0; i < 3; i++) g.dijkstra(r.nextInt(g.n) + 1, r.nextInt(g.n) + 1, true);

        int reachable = 0, improved = 0, verified = 0;
        long sumCost = 0, sumEdges = 0, sumPlainEdges = 0;
        long timeTie = 0, timePlain = 0;

        for (int q = 0; q < QUERIES; q++) {
            int s = r.nextInt(g.n) + 1;
            int t = r.nextInt(g.n) + 1;

            long a = System.nanoTime();
            g.dijkstra(s, t, false);
            timePlain += System.nanoTime() - a;
            long plainCost = g.dist[t];
            int plainEdges = g.edges[t];

            a = System.nanoTime();
            g.dijkstra(s, t, true);
            timeTie += System.nanoTime() - a;

            if (verify) {
                long[] bf = bellmanFord(g, s, t);
                boolean ok = bf[0] == g.dist[t] && plainCost == g.dist[t]
                    && (g.dist[t] == ShortestPath.INF || bf[1] == g.edges[t]);
                if (ok) verified++;
            }

            if (g.dist[t] == ShortestPath.INF) continue;
            reachable++;
            sumCost += g.dist[t];
            sumEdges += g.edges[t];
            sumPlainEdges += plainEdges;
            if (g.edges[t] < plainEdges) improved++;
        }

        double avgCost = (double) sumCost / reachable;
        double avgEdges = (double) sumEdges / reachable;
        double avgPlainEdges = (double) sumPlainEdges / reachable;
        double tieMs = timeTie / 1e6 / QUERIES;
        double plainMs = timePlain / 1e6 / QUERIES;
        String verifiedText = verify ? verified + "/" + QUERIES : "-";

        System.out.printf("%9d %9d %4d/%-2d %8.1f %8.2f %8.2f %6d %6s %8.1fms %8.3fms %8.3fms%n",
            g.n, g.m, reachable, QUERIES, avgCost, avgEdges, avgPlainEdges, improved, verifiedText,
            loadMs, tieMs, plainMs);
        csv.write(String.format("%s,%d,%d,%d,%.1f,%.2f,%.2f,%d,%s,%.1f,%.3f,%.3f%n", type, g.n, g.m,
            reachable, avgCost, avgEdges, avgPlainEdges, improved, verifiedText, loadMs, tieMs, plainMs));
    }

    // Conferencia independente: na rodada k, cur[v] e o menor custo ate v usando no maximo k arestas.
    // A primeira rodada em que o destino atinge o custo final e o menor numero de arestas possivel.
    // Devolve {custo, arestas}.
    static long[] bellmanFord(ShortestPath g, int s, int t) {
        long[] prev = new long[g.n + 1];
        for (int v = 1; v <= g.n; v++) prev[v] = ShortestPath.INF;
        prev[s] = 0;
        long bestCost = s == t ? 0 : ShortestPath.INF;
        long bestEdges = 0;

        for (int k = 1; k < g.n; k++) {
            long[] cur = prev.clone();
            boolean changed = false;
            for (int u = 1; u <= g.n; u++) {
                if (prev[u] == ShortestPath.INF) continue;
                for (int e = g.pointer[u]; e < g.pointer[u + 1]; e++) {
                    long d = prev[u] + g.weight[e];
                    if (d < cur[g.to[e]]) {
                        cur[g.to[e]] = d;
                        changed = true;
                    }
                }
            }
            if (!changed) break;
            if (cur[t] < bestCost) {
                bestCost = cur[t];
                bestEdges = k;
            }
            prev = cur;
        }
        return new long[] {bestCost, bestEdges};
    }
}
