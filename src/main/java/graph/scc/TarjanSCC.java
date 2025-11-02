package graph.scc;
import graph.util.Graph; import graph.util.Metrics;
import java.util.*;
public final class TarjanSCC {
    public static final class Result {
        public final int[] comp; public final int compCount;
        public Result(int[] c,int cc){ comp=c; compCount=cc; }
    }
    private final Graph g; private final Metrics met;
    private int time=0, compCnt=0;
    private final int[] disc, low, comp; private final boolean[] inSt;
    private final Deque<Integer> st = new ArrayDeque<>();
    public TarjanSCC(Graph g, Metrics m){ this.g=g; this.met=m;
        disc=new int[g.n]; low=new int[g.n]; comp=new int[g.n]; inSt=new boolean[g.n];
        Arrays.fill(disc,-1); Arrays.fill(comp,-1);
    }
    public Result run(){
        long t0=System.nanoTime();
        for(int v=0; v<g.n; v++) if(disc[v]==-1) dfs(v);
        met.inc("scc.time.ns", System.nanoTime()-t0);
        return new Result(comp, compCnt);
    }
    private void dfs(int u){
        met.tick("scc.dfs.calls");
        disc[u]=low[u]=++time; st.push(u); inSt[u]=true;
        for(var e: g.adj.get(u)){
            met.tick("scc.edges");
            int v=e.to();
            if(disc[v]==-1){ dfs(v); low[u]=Math.min(low[u], low[v]); }
            else if(inSt[v]) low[u]=Math.min(low[u], disc[v]);
        }
        if(low[u]==disc[u]){
            while(true){
                int v=st.pop(); inSt[v]=false; comp[v]=compCnt;
                if(v==u) break;
            }
            compCnt++;
        }
    }
    public static Graph condense(Graph g, Result r){
        Graph dag = new Graph(r.compCount);
        Set<Long> seen = new HashSet<>();
        for(int u=0; u<g.n; u++){
            int cu=r.comp[u];
            for(var e: g.adj.get(u)){
                int cv=r.comp[e.to()];
                if(cu!=cv){
                    long key = (((long)cu)<<32) | (cv & 0xffffffffL);
                    if(seen.add(key)) dag.add(cu, cv, e.w());
                }
            }
        }
        return dag;
    }
}
