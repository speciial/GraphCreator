/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.mxgraph.swing.mxGraphComponent;
import java.util.List;
import javafx.scene.control.TextField;

/**
 *
 * @author lennaertn
 */
public class mxGraphWithInfo {
    private mxGraphComponent mxGraph;
    private boolean directed;
    private int numberofNodes;
    private List<List<TextField>> matrix;

    public mxGraphWithInfo(mxGraphComponent mxGraph, boolean directed, int numberofNodes, List<List<TextField>> matrix) {
        this.mxGraph = mxGraph;
        this.directed = directed;
        this.numberofNodes = numberofNodes;
        this.matrix = matrix;
    }

    public mxGraphComponent getMxGraph() {
        return mxGraph;
    }

    public void setMxGraph(mxGraphComponent mxGraph) {
        this.mxGraph = mxGraph;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public int getNumberofNodes() {
        return numberofNodes;
    }

    public void setNumberofNodes(int numberofNodes) {
        this.numberofNodes = numberofNodes;
    }

    public List<List<TextField>> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<List<TextField>> matrix) {
        this.matrix = matrix;
    }

    
    
    
}
