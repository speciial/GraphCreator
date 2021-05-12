/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 *
 * @author lennaertn
 */
//Hilfsklasse um ein Gridpane in Verbindung mit der Adjazenzliste zu verwalten
public class GridPaneWithAdjacencList {
    GridPane g;
    ArrayList<ArrayList<Pair>> adjacencList;

    public GridPane getG() {
        return g;
    }

    public void setG(GridPane g) {
        this.g = g;
    }

    public ArrayList<ArrayList<Pair>> getAdjacencList() {
        return adjacencList;
    }

    public void setAdjacencList(ArrayList<ArrayList<Pair>> adjacencList) {
        this.adjacencList = adjacencList;
    }
}
