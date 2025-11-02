package graph.topo;
import graph.util.Graph; import graph.util.Metrics;
import java.util.*;
public final class TopoSort {
    public static int[] kahn(Graph dag, Metrics m){
        int n=dag.n;
        int[] indeg=new int[n];
        for(int u=0;u<n;u++)
            for(var e: dag.adj.get(u)){ indeg[e.to()]++; m.tick("topo.edges");
            }


        Deque<Integer> q=new ArrayDeque<>();
        for(int i=0;i<n;i++)
            if(indeg[i]==0) q.add(i);
        int[] order=new int[n];
        int idx=0;
        while(!q.isEmpty()){
            int u=q.remove(); order[idx++]=u; m.tick("topo.pops");
            for(var e: dag.adj.get(u))
            {
                if(--indeg[e.to()]==0) q.add(e.to()); m.tick("topo.reductions"); }
        }
        if(idx!=n)
            throw new IllegalStateException("Graph not DAG");
        return order;
    }
}
