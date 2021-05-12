/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import jgraph.JGraphAdapter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

/**
 *
 * @author lennaertn
 */
// Klasse, die das einlesen der .graph dateien ermöglicht
public class OwnImporter {

    private File file;
    private Graph g;

    public OwnImporter(File file) {
        this.file = file;
    }

    public Graph importGraph() throws IOException {
        String input = this.readFile();
        if (input == null) {
            this.cantImportAlert();
            return null;
        }
        String[] parts = this.splitFile(input);

        this.g = this.createGraphType(parts[0]);
        this.addNodes(parts[1]);
        this.addEdges(parts[2]);

        return this.g;

    }

    private String readFile() throws IOException {
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String input = "";
            String str;

            int i = 0;
            int j =0;
            while ((str = br.readLine()) != null) {
                input += str;
                if(str.contains(";")){
                     j++;
                }
                i++;
                if (i > 30 && j ==1) {
                    
                    return null;
                }
            }
            return input;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OwnImporter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private String[] splitFile(String input) {
        String[] parts = input.split(";");
        return parts;
    }

    private Graph createGraphType(String graphtype) {
        Graph g;
        if (graphtype.contains("Directed")) {
            g = new DefaultListenableGraph<>(new DefaultDirectedWeightedGraph<>(JGraphAdapter.MyEdge.class));
        } else {
            g = new DefaultListenableGraph<>(new DefaultUndirectedWeightedGraph<>(JGraphAdapter.MyEdge.class));
        }
        return g;
    }

    private void addNodes(String inputNodes) {
        String[] nodes = inputNodes.split(",");

        for (String node : nodes) {
            this.g.addVertex(node);
        }
    }

    private void addEdges(String inputEdges) {
        String[] edges = inputEdges.split(",");
        for (String edge : edges) {

            String[] splitEdge = splitEdgeString(edge);
            this.g.addEdge(splitEdge[0], splitEdge[1]);
            int weight = Integer.parseInt(splitEdge[2]);
            this.g.setEdgeWeight(splitEdge[0], splitEdge[1], weight);
        }
    }

    private String[] splitEdgeString(String edge) {
        String[] edges = new String[3];
        String[] spaces = edge.split(" ");
        edges[0] = spaces[0];
        edges[1] = spaces[2];
        String weight = spaces[4];
        weight = weight.replaceFirst("]", "");
        edges[2] = weight;
        return edges;
    }

    private void cantImportAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Die Anzahl der Knoten ist zu groß (>25) um ihn darzustellen.", ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }
}
