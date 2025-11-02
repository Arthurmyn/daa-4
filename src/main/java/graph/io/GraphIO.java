package graph.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.nio.file.*;
import java.util.*;

public final class GraphIO {

    public static final class Edge { public int u, v; public double w; }

    public static final class GraphDTO {
        public boolean directed;
        public int n;
        public List<Edge> edges;
        public Integer source;
        public String weight_model;
    }

    private static final ObjectMapper MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static GraphDTO load(Path p) throws Exception {
        return MAPPER.readValue(Files.readString(p), GraphDTO.class);
    }

    // запись всего отчета
    public static void writeOutput(Path out, Map<String,Object> root) throws Exception {
        Files.createDirectories(out.getParent());
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(out.toFile(), root);
    }
}
