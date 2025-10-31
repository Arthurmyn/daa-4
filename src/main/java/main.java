import graph.io.GraphIO;
import graph.util.*;
import graph.scc.TarjanSCC;
import graph.topo.TopoSort;
import graph.dagsp.DagSP;
import java.nio.file.Path;

public class main {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: scc|topo|dagsp <file> [source]");
            return;
        }

        var dto = GraphIO.load(Path.of(args[1]));
        System.out.println("Loaded graph: " + dto.n + " nodes, " + dto.edges.size() + " edges");

        Graph g = new Graph(dto.n);
        dto.edges.forEach(e -> g.add(e.u, e.v, e.w));

        var met = new SimpleMetrics();

        switch (args[0]) {
            case "scc" -> {
                var res = new TarjanSCC(g, met).run();
                System.out.println("SCC count = " + res.compCount);
                int[] comp = res.comp;
                int[] sz = new int[res.compCount];
                for (int v = 0; v < g.n; v++) sz[comp[v]]++;
                for (int c = 0; c < sz.length; c++)
                    System.out.println("comp " + c + " size = " + sz[c]);
                Graph dag = TarjanSCC.condense(g, res);
                System.out.println("Condensation DAG nodes = " + dag.n);
            }
            case "topo" -> {
                var res = new TarjanSCC(g, met).run();
                Graph dag = TarjanSCC.condense(g, res);
                int[] order = TopoSort.kahn(dag, met);
                System.out.print("Topo order of components: ");
                for (int x : order) System.out.print(x + " ");
                System.out.println();
            }
            case "dagsp" -> {
                var res = new TarjanSCC(g, met).run();
                Graph dag = TarjanSCC.condense(g, res);
                int s = args.length >= 3 ? Integer.parseInt(args[2]) : 0;
                var sp = DagSP.singleSourceShortest(dag, s, met);
                var lp = DagSP.longestPath(dag, s, met);
                System.out.println("Shortest dist from " + s + ":");
                for (int i = 0; i < dag.n; i++)
                    System.out.println(i + " " + sp.dist()[i]);

                int t = 0;
                for (int i = 0; i < dag.n; i++)
                    if (lp.dist()[i] < lp.dist()[t]) t = i;

                int[] path = DagSP.reconstructPath(t, lp.parent());
                System.out.print("Critical path (components): ");
                for (int v : path) System.out.print(v + " ");
                System.out.println();
                System.out.println("Critical length = " + (-lp.dist()[t]));
            }
            default -> { }
        }

        System.out.println("METRICS " + met);
    }
}
