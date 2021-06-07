package exporter;

import jgraph.JGraphAdapter.MyEdge;
import org.jgrapht.Graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ADExerciseExporter {

    private Graph graph;
    private String fileName;

    public ADExerciseExporter(Graph graph, String fileName) {
        this.graph = graph;
        this.fileName = fileName;
    }

    public boolean exportGraph() {
        boolean couldWriteFile = true;
        StringBuilder builder = new StringBuilder();

        // number of vertices
        builder.append(graph.vertexSet().size()).append(System.lineSeparator());
        // number of edges
        builder.append(graph.edgeSet().size()).append(System.lineSeparator());

        // edges
        for (MyEdge edge : (Iterable<MyEdge>) graph.edgeSet()) {
            builder.append(graph.getEdgeSource(edge).toString()).append(" ");
            builder.append(graph.getEdgeTarget(edge).toString()).append(" ");
            builder.append(graph.getEdgeWeight(edge)).append(System.lineSeparator());
        }

        // write out to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName))) {
            writer.write(builder.toString());
        } catch (IOException e) {
            couldWriteFile = false;
            e.printStackTrace();
        }

        return couldWriteFile;
    }

}
