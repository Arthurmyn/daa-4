package graph.util;
import java.util.concurrent.ConcurrentHashMap;
public final class SimpleMetrics implements Metrics {
    private final ConcurrentHashMap<String,Long> m = new ConcurrentHashMap<>();
    public void inc(String k,long by){ m.merge(k,by,Long::sum); }
    public long get(String k){ return m.getOrDefault(k,0L); }
    @Override public String toString(){ return m.toString(); }
}
