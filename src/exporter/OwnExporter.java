/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgraph.JGraphAdapter.MyEdge;
import org.jgrapht.Graph;

/**
 *
 * @author lennaertn
 */
// Exporter, der einen Graphen textuell darstellt, hier wird die .graph Datei erstellt
public class OwnExporter {

    Graph g;
    String filename;

    public OwnExporter(Graph g, String filename) {
        this.g = g;
        this.filename = filename;
    }

    public boolean exportGraph() {
        String fileContent;
        boolean directed = this.g.getType().isDirected();

        fileContent = this.writeGraphType(directed);
        
        fileContent += this.writeNodes();
         try {
            writeToFile(fileContent);
            
        } catch (IOException ex) {

            
        }
       
        this.writeEdges(directed);
       
      
        return true;
    }

    private String writeGraphType(boolean directed) {
        String graphtype;
        if (directed) {
            graphtype = "DirectedGraph;" + System.lineSeparator();
                   
        } else {
            graphtype = "UndirectedGraph;" + System.lineSeparator();
                   
        }
        return graphtype;
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
        String edges = "";
        String arrow = this.getArrowType(directed);

        Set<MyEdge> edgeSet = this.g.edgeSet();
        
        Iterator<MyEdge> itr = edgeSet.iterator();
        
        for(int i=0; i<edgeSet.size();i++){
             MyEdge e = itr.next();
            String source = this.g.getEdgeSource(e).toString();
            String target = this.g.getEdgeTarget(e).toString();
            String weight = Integer.toString((int) this.g.getEdgeWeight(e));

            edges = source + arrow + target + " [weight: " + weight + "],"
                    + System.lineSeparator();
            try {
                this.appendToFile(edges);
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

        BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename,true));
       
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
}
