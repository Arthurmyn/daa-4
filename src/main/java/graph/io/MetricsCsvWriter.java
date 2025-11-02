package graph.io;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public final class MetricsCsvWriter {

    private final Path path;
    private boolean headerWritten = false;

    public MetricsCsvWriter(Path path) {
        this.path = path;
    }

    public void writeHeader() throws IOException {
        if (headerWritten) return;
        Files.createDirectories(path.getParent());
        try (var w = Files.newBufferedWriter(path)) {
            w.write("file,n,edges,sccCount,topoLen,shortRelax,longRelax,tarjanNs,topoNs,dagShortNs,dagLongNs\n");
        }
        headerWritten = true;
    }

    public void append(String file,
                       int n,
                       int edges,
                       int sccCount,
                       int topoLen,
                       long shortRelax,
                       long longRelax,
                       long tarjanNs,
                       long topoNs,
                       long dagShortNs,
                       long dagLongNs) throws IOException {
        Files.writeString(
                path,
                String.format("%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d%n",
                        file, n, edges, sccCount, topoLen,
                        shortRelax, longRelax,
                        tarjanNs, topoNs, dagShortNs, dagLongNs),
                StandardOpenOption.APPEND);
    }
}
