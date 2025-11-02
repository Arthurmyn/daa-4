import graph.io.GraphIO;
import graph.io.MetricsCsvWriter;
import graph.util.*;
import graph.scc.TarjanSCC;
import graph.topo.TopoSort;
import graph.dagsp.DagSP;

import java.nio.file.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        List<Path> files = Files.list(Path.of("data"))
                .filter(p -> p.toString().endsWith(".json"))
                .sorted()
                .toList();

        MetricsCsvWriter csv = new MetricsCsvWriter(Path.of("data/metrics.csv"));
        csv.writeHeader();

        List<Map<String,Object>> results = new ArrayList<>();

        for (Path file : files) {
            var dto = GraphIO.load(file);

            if (dto.edges == null || dto.n == 0) {
                System.out.println("skip: " + file.getFileName());
                continue;
            }

            Graph g = new Graph(dto.n);
            dto.edges.forEach(e -> g.add(e.u, e.v, e.w));
            var met = new SimpleMetrics();

            long t1 = System.nanoTime();
            var sccRes = new TarjanSCC(g, met).run();
            long tarjanNs = System.nanoTime() - t1;

            Graph dag = TarjanSCC.condense(g, sccRes);

            t1 = System.nanoTime();
            int[] topo = TopoSort.kahn(dag, met);
            long topoNs = System.nanoTime() - t1;

            int src = dto.source != null ? dto.source : 0;

            t1 = System.nanoTime();
            var sp = DagSP.singleSourceShortest(dag, src, met);
            long dagShortNs = System.nanoTime() - t1;

            t1 = System.nanoTime();
            var lp = DagSP.longestPath(dag, src, met);
            long dagLongNs = System.nanoTime() - t1;

            Map<String,Object> item = new LinkedHashMap<>();
            item.put("file", file.getFileName().toString());
            item.put("vertices", dto.n);
            item.put("edges", dto.edges.size());
            item.put("weight_model", dto.weight_model != null ? dto.weight_model : "edge");

            int[] comp = sccRes.comp;
            int compCount = sccRes.compCount;
            List<List<Integer>> buckets = new ArrayList<>();
            for (int i = 0; i < compCount; i++) buckets.add(new ArrayList<>());
            for (int v = 0; v < g.n; v++) buckets.get(comp[v]).add(v);
            List<Map<String,Object>> sccList = new ArrayList<>();
            for (int i = 0; i < compCount; i++) {
                Map<String,Object> sc = new LinkedHashMap<>();
                sc.put("id", i);
                sc.put("size", buckets.get(i).size());
                sc.put("vertices", buckets.get(i));
                sccList.add(sc);
            }
            item.put("scc", sccList);
            item.put("componentTopo", Arrays.stream(topo).boxed().toList());

            Map<String,Object> shortest = new LinkedHashMap<>();
            shortest.put("sourceComp", src);
            shortest.put("dist", Arrays.stream(sp.dist()).boxed().toList());
            item.put("shortest", shortest);

            int end = 0;
            for (int i = 0; i < dag.n; i++) if (lp.dist()[i] < lp.dist()[end]) end = i;
            int[] path = DagSP.reconstructPath(end, lp.parent());
            Map<String,Object> crit = new LinkedHashMap<>();
            crit.put("length", -lp.dist()[end]);
            crit.put("path", Arrays.stream(path).boxed().toList());
            item.put("criticalPath", crit);

            item.put("Tarjan_SCC_count", compCount);
            item.put("Tarjan_time_ns", tarjanNs);
            item.put("Kahn_time_ns", topoNs);
            item.put("DAGSP_short_time_ns", dagShortNs);
            item.put("DAGSP_long_time_ns", dagLongNs);
            item.put("DAGSP_short_relax_ops", met.get("dagsp.relax.attempts"));
            item.put("DAGSP_long_relax_ops", met.get("dagsp.relax.success"));

            results.add(item);

            csv.append(
                    file.getFileName().toString(),
                    dto.n,
                    dto.edges.size(),
                    compCount,
                    topo.length,
                    met.get("dagsp.relax.attempts"),
                    met.get("dagsp.relax.success"),
                    tarjanNs,
                    topoNs,
                    dagShortNs,
                    dagLongNs
            );

            System.out.println("done: " + file.getFileName());
        }

        Map<String,Object> root = new LinkedHashMap<>();
        root.put("results", results);
        GraphIO.writeOutput(Path.of("data/output.json"), root);
    }
}
