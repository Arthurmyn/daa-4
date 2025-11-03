package graph.dagsp;

import graph.util.Graph;
import graph.util.Metrics;
import graph.topo.TopoSort;

import java.util.Arrays;

public final class DagLP {

    public static final double NEG_INF = -1e300;

    public record Result(double[] dist, int[] parent) {}

    /**
     * @param dag  acyclic directed graph
     * @param s    source vertex
     * @param m    metrics collector
     * @return     Result containing distances and parents
     */
    public static Result compute(Graph dag, int s, Metrics m) {
        int[] order = TopoSort.kahn(dag, m);
        double[] d = new double[dag.n];
        int[] p = new int[dag.n];
        Arrays.fill(d, NEG_INF);
        Arrays.fill(p, -1);
        d[s] = 0;

        for (int u : order) {
            if (d[u] <= NEG_INF) continue;
            for (var e : dag.adj.get(u)) {
                m.tick("dagsp.long.relax.attempts");
                double nd = d[u] + e.w();
                if (nd > d[e.to()]) {
                    d[e.to()] = nd;
                    p[e.to()] = u;
                    m.tick("dagsp.long.relax.success");
                }
            }
        }
        return new Result(d, p);
    }
    public static int[] reconstruct(int t, int[] parent) {
        java.util.ArrayList<Integer> path = new java.util.ArrayList<>();
        for (int v = t; v != -1; v = parent[v]) path.add(v);
        java.util.Collections.reverse(path);
        return path.stream().mapToInt(i -> i).toArray();
    }
}
