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
import jgraph.JGraphAdapter.MyEdge;

/**
 *
 * @author lennaertn
 */
// Klasse die den minimalen Spannbaum eines Graphen textuell exportiert
public class KruskalExporter {
     String filename;

    public KruskalExporter(String filename) {
        this.filename = filename;
    }
     
     public void exportKruskaltoFile(Set<MyEdge> set) throws IOException {
         
        String filecontent;
        
        filecontent = "Total Weight: " + this.totalWeight(set)  + System.lineSeparator();
        
         Iterator itr = set.iterator();
         
         while(itr.hasNext()){
             
             MyEdge e = (MyEdge) itr.next();
             filecontent += e.getSource() + "--" + e.getTarget()+ " [" + e.toString() + "]"+ System.lineSeparator();
         }

        BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename));
        writer.write(filecontent);
        writer.close();
        
    }
     
     private int totalWeight(Set<MyEdge> set){
         Iterator itr = set.iterator();
         int count = 0;
         while(itr.hasNext()){
             int tmp =0;
             MyEdge e = (MyEdge) itr.next();
             tmp = Integer.parseInt(e.toString());
             count += tmp;
         }
         return count;
     }
     
}
