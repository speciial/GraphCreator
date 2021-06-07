/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ausgabe;

import com.mxgraph.swing.mxGraphComponent;
import eingabe.DialogInputs;
import eingabe.ExportInputs;
import eingabe.ExportInputsWOVis;
import eingabe.ViewController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import jgraph.JGraphAdapter;
import model.DataBean;
import model.mxGraphWithInfo;
import model.mxGraphWithPath;
import org.jgrapht.Graph;

/**
 *
 * @author lennaertn
 */
//die Klasse dient dem Austausch zwischen der Klasse der JGraphKomponenten und den eintretenden Ereignissen der View
public class AusgabeVC extends ViewController {

    private final JGraphAdapter jGraph = new JGraphAdapter();

    //erstes Anzeigen eines Graphen über die Matrix
    public mxGraphComponent displayGraphfromMatrix(List<List<TextField>> matrixList, DataBean bean, boolean directed) throws IOException {
        mxGraphComponent comp = this.jGraph.displayGraphFromMatrix(matrixList, bean, directed);
        return comp;
    }

    //erstes Anzeigen eines Graphen über die Liste
    public mxGraphComponent displayGraphfromList(ArrayList<ArrayList<Pair>> adjacencList, DataBean bean, boolean directed) throws IOException {
        mxGraphComponent comp = this.jGraph.displayGraphFromList(adjacencList, bean, directed);
        return comp;
    }

    //bei Änderungen der Adjazenzliste oder der Matrix muss der Graph geupdatet werden
    public void updateGraph(List<List<String>> EdgesToInsert, List<List<String>> EdgesToRemove, mxGraphComponent mxGraph, boolean directed) {
        this.jGraph.updateGraph(EdgesToInsert, EdgesToRemove, mxGraph, directed);
    }

    public Graph updateJGraphTGraph(List<List<String>> EdgesToInsert, List<List<String>> EdgesToRemove, List<List<String>> matrixList, boolean directed) {
        return this.jGraph.updateJGraphTgraph(EdgesToRemove, EdgesToRemove, matrixList, directed);
    }

    

    //Testfunktion um den kürzesten Weg innerhalb eines Graphen anzuzeigen
    public mxGraphWithPath solveDijkstra(DataBean bean, Pair pair, mxGraphComponent mxGraph, boolean directed) {
        mxGraphWithPath comp = this.jGraph.shortestPathDijkstra(bean, pair, mxGraph, directed);
        return comp;
    }

    public JGraphAdapter getjGraph() {
        return jGraph;
    }

    public void exportGraphToOwnFormat() {
        this.jGraph.saveGraph();
    }

    public void exportGraphToADEx() {
        this.jGraph.saveGraphToADEx();
    }

    public mxGraphWithInfo importGraphFromOwnFormat() throws IOException {
        return this.jGraph.importGraphFromOwnFormat();
    }

    public void errorNoSolution() {
        Alert alert = new Alert(AlertType.ERROR, "Kein kürzester Weg zwischen Knoten  oder negative Kantengewichte im Graphen vorhanden.", ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

    public void errorsourceTargeSame() {
        Alert alert = new Alert(AlertType.ERROR, "Startknoten entspricht Zielknoten", ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }
    public void errorQuelleGleichSenke() {
        Alert alert = new Alert(AlertType.ERROR, "Quelle entspricht Senke.", ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

    public void errorNoVertexInGraph() {
        Alert alert = new Alert(AlertType.ERROR, "Ein Zielknoten der hinzugefügt werden sollte befindet sich nicht im Graphen", ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }
    
    public void erorrNetzwerkNegativeKanten() {
        Alert alert = new Alert(AlertType.ERROR, "Ein Netzwerkgraph kann nur positive Kapazitäten enthalten.", ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

    public void exportGraphproject(ExportInputs inputs, DataBean bean) throws IOException {
        this.jGraph.exportGraphProject(inputs, bean);
    }

    public void exportGraphWOVis(List<List<TextField>> randomMatrix, ExportInputsWOVis dInputs, boolean directed, DataBean bean) {
        this.jGraph.exportGraphWOVis(randomMatrix, dInputs, directed, bean);
    }

}
