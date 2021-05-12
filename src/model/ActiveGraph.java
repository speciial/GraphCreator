/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import org.jgrapht.Graph;

/**
 *
 * @author lennaertn
 */
//hilfsklasse, die den aktuellen graph, sowie die zugehörigen Repräsentationsformen des Graphen speichert
public class ActiveGraph {
    private GridPaneWithAdjacencList gpAdjacenc;
    private GridPaneWithMatrixList gpMatrix;
    private Graph graph;

    public GridPaneWithAdjacencList getGpAdjacenc() {
        return gpAdjacenc;
    }

    public void setGpAdjacenc(GridPaneWithAdjacencList gpAdjacenc) {
        this.gpAdjacenc = gpAdjacenc;
    }

    public GridPaneWithMatrixList getGpMatrix() {
        return gpMatrix;
    }

    public void setGpMatrix(GridPaneWithMatrixList gpMatrix) {
        this.gpMatrix = gpMatrix;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

   

    
}
