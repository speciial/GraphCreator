/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.mxgraph.swing.mxGraphComponent;
import javafx.stage.Stage;


/**
 *
 * @author lennaertn
 */
//Datenmodell der Anwendung, hier werden alle Komponenten gehalten und verwaltet
public class DataBean {

    private Stage primaryStage = null;
    private Stage modalStage = null;
    private ActiveGraph agraph = null;
    private mxGraphComponent graphComponent = null;
    public DataBean(Stage stage) {
        primaryStage = stage;
    }

    public DataBean() {

    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getModalStage() {
        return modalStage;
    }

    public void setModalStage(Stage modalStage) {
        this.modalStage = modalStage;
    }

    public ActiveGraph getAgraph() {
        return agraph;
    }

    public void setAgraph(ActiveGraph agraph) {
        this.agraph = agraph;
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void setGraphComponent(mxGraphComponent graphComponent) {
        this.graphComponent = graphComponent;
    }
    
}
