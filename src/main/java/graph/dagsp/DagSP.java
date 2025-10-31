package graph.dagsp;
import graph.util.Graph; import graph.util.Metrics;
import graph.topo.TopoSort;
import java.util.*;
public final class DagSP {
    public static final double INF = 1e300;
    public record SPResult(double[] dist, int[] parent) {}
    public static SPResult singleSourceShortest(Graph dag, int s, Metrics m){
        int[] ord=TopoSort.kahn(dag, m);
        double[] d=new double[dag.n]; int[] p=new int[dag.n];
        Arrays.fill(d, INF); Arrays.fill(p,-1); d[s]=0;
        for(int u: ord){
            if(d[u]>=INF) continue;
            for(var e: dag.adj.get(u)){
                m.tick("dagsp.relax.attempts");
                double nd=d[u]+e.w();
                if(nd<d[e.to()]){ d[e.to()]=nd; p[e.to()]=u; m.tick("dagsp.relax.success"); }
            }
        }
        return new SPResult(d,p);
    }
    public static SPResult longestPath(Graph dag, int s, Metrics m){
        Graph neg = dag;
        int[] ord=TopoSort.kahn(neg, m);
        double[] d=new double[neg.n]; int[] p=new int[neg.n];
        Arrays.fill(d, -INF); Arrays.fill(p,-1); d[s]=0;
        for(int u: ord){
            if(d[u]<=-INF) continue;
            for(var e: neg.adj.get(u)){
                double nd=d[u]+(-e.w());
                if(nd>d[e.to()]){ d[e.to()]=nd; p[e.to()]=u; }
            }
        }
        for(int i=0;i<d.length;i++) if(d[i]>-INF) d[i]=-d[i];
        return new SPResult(d,p);
    }
    public static int[] reconstructPath(int t, int[] parent){
        ArrayList<Integer> path=new ArrayList<>();
        for(int v=t; v!=-1; v=parent[v]) path.add(v);
        Collections.reverse(path); return path.stream().mapToInt(i->i).toArray();
    }
}
