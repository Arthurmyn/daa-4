package graph;
import graph.util.*; import graph.scc.*; import graph.topo.*; import graph.dagsp.*;
import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
public class SimpleTests {
    @Test void tinySCC(){
        Graph g=new Graph(3); g.add(0,1,1); g.add(1,2,1); g.add(2,0,1);
        var r=new TarjanSCC(g,new graph.util.SimpleMetrics()).run();
        assertEquals(1, r.compCount);
    }
    @Test void topoOrder(){
        Graph g=new Graph(3); g.add(0,1,1); g.add(1,2,1);
        int[] ord=TopoSort.kahn(g,new graph.util.SimpleMetrics());
        assertArrayEquals(new int[]{0,1,2}, ord);
    }
    @Test void dagShortest(){
        Graph g=new Graph(4); g.add(0,1,2); g.add(0,2,1); g.add(2,1,1); g.add(1,3,3); g.add(2,3,4);
        var r=DagSP.singleSourceShortest(g,0,new graph.util.SimpleMetrics());
        assertEquals(5.0, r.dist()[3], 1e-9);
    }
}
