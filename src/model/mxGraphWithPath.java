/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.GraphPath;

/**
 *
 * @author lennaertn
 */
public class mxGraphWithPath {
    private mxGraphComponent mxGraph;
    private GraphPath graphPath;

    public mxGraphWithPath(mxGraphComponent mxGraph, GraphPath graphPath) {
        this.mxGraph = mxGraph;
        this.graphPath = graphPath;
    }

    public mxGraphComponent getMxGraph() {
        return mxGraph;
    }

    public void setMxGraph(mxGraphComponent mxGraph) {
        this.mxGraph = mxGraph;
    }

    public GraphPath getGraphPath() {
        return graphPath;
    }

    public void setGraphPath(GraphPath graphPath) {
        this.graphPath = graphPath;
    }
    
    
}
