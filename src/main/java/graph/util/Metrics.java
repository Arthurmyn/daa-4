package graph.util;
public interface Metrics {
    void inc(String key,long by);
    long get(String key);
    default void tick(String key){ inc(key,1); }
}
