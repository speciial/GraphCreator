/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 *
 * @author lennaertn
 */
//Hilfsklasse um ein Gridpane mit einer Matrix zu verwalten
public class GridPaneWithMatrixList {
    GridPane g;
    List<List<TextField>> MatrixList;

    public GridPane getG() {
        return g;
    }

    public void setG(GridPane g) {
        this.g = g;
    }

    public List<List<TextField>> getMatrixList() {
        return MatrixList;
    }

    public void setMatrixList(List<List<TextField>> MatrixList) {
        this.MatrixList = MatrixList;
    }
    
}
