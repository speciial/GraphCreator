/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;

/**
 *
 * @author lennaertn
 */
// Exporter, der Lösung der kürzestenen Wege im Graphen mit dem Bellman-Ford Algorithmus textuell darstellt
public class BellmanFordExporter {

    String filename;
    String startvertex;

    public BellmanFordExporter(String filename, String startvertex) {
        this.filename = filename;
        this.startvertex = startvertex;
    }

    public void exportBellmanFordToFile(SingleSourcePaths paths, int number) throws IOException {

        String filecontent;
        filecontent = "Startvertex " + this.startvertex +";"+ System.lineSeparator();
        int startv = Integer.parseInt(this.startvertex);
        for (int i = 0; i < number; i++) {
            GraphPath path = paths.getPath(Integer.toString(i));
            
            if (path != null && i != startv) {
                
                filecontent += this.startvertex + "->" + Integer.toString(i) + ": ";
                List<String> list = path.getVertexList();
               
                for (String s : list) {
                    filecontent += s + " ";
                }
                filecontent += "[weight: " + (int) path.getWeight() + "],";
                filecontent += System.lineSeparator();
            }
        }
        filecontent +=";";

        BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename));
        writer.write(filecontent);
        writer.close();
        
    }
}
