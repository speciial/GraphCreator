/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import jgraph.JGraphAdapter;

import jgraph.jGraphVisualisationHandler.NetzwerkKante;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm.MaximumFlow;

/**
 *
 * @author lennaertn
 */
// Klasse die den  Graphen  als Netzwerk exportiert und textuell exportiert
public class NetzwerkGraphExporter {

    String filename;
    String quelle;
    String senke;

    Graph g;
    Pair<MaximumFlow, Integer> mf;

    public NetzwerkGraphExporter(String filename, String quelle, String senke, Graph g, Pair<MaximumFlow, Integer> mf) {
        this.filename = filename;
        this.quelle = quelle;
        this.senke = senke;

        this.g = g;
        this.mf = mf;
    }

    public boolean exportGraph() {
        String fileContent;
        boolean directed = this.g.getType().isDirected();

        fileContent = this.writeGraphType(directed);
        fileContent += this.writeQuelleSenke();
        fileContent += this.writeTotalFlow();
        fileContent += this.writeNodes();
        try {
            writeToFile(fileContent);

        } catch (IOException ex) {

        }

        this.writeEdges(directed);

        return true;
    }

    private String writeTotalFlow() {
        String totalFlow = "Total Flow: ";
        totalFlow += Integer.toString(this.mf.getValue()) + ";" + System.lineSeparator();
        return totalFlow;
    }

    private String writeGraphType(boolean directed) {
        String graphtype;
        if (directed) {
            graphtype = "DirectedNetworkGraph;" + System.lineSeparator();

        } else {
            graphtype = "UndirectedNetworkGraph;" + System.lineSeparator();

        }
        return graphtype;
    }

    private String writeQuelleSenke() {
        String cont = "Quelle: " + this.quelle + ";" + System.lineSeparator()
                + "Senke: " + this.senke + ";" + System.lineSeparator();

        return cont;
    }

    private String writeNodes() {
        String nodes = "";
        Set<String> vertexSet = this.g.vertexSet();
        Iterator<String> itr = vertexSet.iterator();
        while (itr.hasNext()) {
            nodes += itr.next() + "," + System.lineSeparator();
        }
        nodes += ";" + System.lineSeparator();
        return nodes;
    }

    private void writeEdges(boolean directed) {
        String content = "";
        String arrow = this.getArrowType(directed);

        Map mp = this.mf.getKey().getFlowMap();
        // value= flow
        // key = kantengewicht
        List<NetzwerkKante> edges = new ArrayList<>();
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            NetzwerkKante e = new NetzwerkKante();
            JGraphAdapter.MyEdge key = (JGraphAdapter.MyEdge) pair.getKey();
            double dflow = (double) pair.getValue();
            int flow = (int) dflow;
            e.setsrc(key.getSource());
            e.settrg(key.getTarget());
            e.setFlow(Integer.toString(flow));
            e.setWeightCustom(pair.getKey().toString());
            edges.add(e);

        }

        for (NetzwerkKante e : edges) {
            String tmp = "";
            tmp += e.getsrc() + arrow + e.gettrg() + " [flow: " + e.getFlow() + "/" + e.getWeightCustom() + "]," + System.lineSeparator();
            try {
                this.appendToFile(tmp);
            } catch (IOException ex) {
                Logger.getLogger(OwnExporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            this.appendToFile(";");
        } catch (IOException ex) {
            Logger.getLogger(OwnExporter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeToFile(String fileContent) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename));

        writer.write(fileContent);
        writer.close();

    }

    private void appendToFile(String fileContent) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename, true));

        writer.append(fileContent);
        writer.close();

    }

    private String getArrowType(boolean directed) {
        String arrow;
        if (directed) {
            arrow = " -> ";
        } else {
            arrow = " -- ";
        }
        return arrow;
    }

    public Graph getG() {
        return g;
    }

    public void setG(Graph g) {
        this.g = g;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getQuelle() {
        return quelle;
    }

    public void setQuelle(String quelle) {
        this.quelle = quelle;
    }

    public String getSenke() {
        return senke;
    }

    public void setSenke(String senke) {
        this.senke = senke;
    }

}
