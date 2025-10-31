package graph.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class GraphIO {
    public static final class Edge { public int u; public int v; public double w; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class GraphDTO {
        public boolean directed;
        public int n;
        public List<Edge> edges;
        public Integer source;          // optional
        public String weight_model;     // optional
        public int nodes;
    }

    private static final ObjectMapper MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static GraphDTO load(Path p) throws Exception {
        return MAPPER.readValue(Files.readString(p), GraphDTO.class);
    }
}
