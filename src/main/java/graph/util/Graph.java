package graph.util;
import java.util.*;
public final class Graph {
    public record E(int to, double w) {}
    public final int n;
    public final List<List<E>> adj;
    public Graph(int n){ this.n=n; this.adj=new ArrayList<>(n); for(int i=0;i<n;i++) adj.add(new ArrayList<>()); }
    public void add(int u,int v,double w){ adj.get(u).add(new E(v,w)); }
}
