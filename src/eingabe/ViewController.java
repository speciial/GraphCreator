/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eingabe;

import ausgabe.AusgabeVC;
import com.mxgraph.swing.mxGraphComponent;
import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import model.ActiveGraph;
import model.DataBean;
import model.GridPaneWithAdjacencList;
import model.GridPaneWithMatrixList;
import model.mxGraphWithInfo;
import model.mxGraphWithPath;
import org.jgrapht.Graph;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.ImportException;

/**
 *
 * @author lennaertn
 */
//funktioniert in der Anwendung als View, sowie als Controller der Oberfläche hier 
//werden alle Eingaben, die durch den Nutzer eintreten verarbeitet
public class ViewController implements Initializable {

    //datenhaltung der Anwendung
    private DataBean bean;
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane matrix;
    @FXML
    private GridPane grid;
    @FXML
    private AnchorPane graphGridPane;
    @FXML
    private TextField anzahlKnoten;
    @FXML
    private GridPane right;
    @FXML
    private ChoiceBox editOption;
    @FXML
    private RadioButton ungerichtet;
    @FXML
    private RadioButton gerichtet;
    @FXML
    private Menu newGraph;
    @FXML
    private Button editGraphButton;
    @FXML
    private Button closeButton;
    @FXML
    private MenuItem menuItemSave;
    @FXML
    private MenuItem menuItemSaveToADEx;
    @FXML
    private MenuItem exportProject;
    @FXML
    private MenuItem openGraph;
    @FXML
    private MenuItem menuItemNewGraph;
    @FXML
    private Button dijkstra;
    @FXML
    private Button maxFlowButton;

    private List<List<TextField>> matrixList = new ArrayList<>();

    private SwingNode frameNode;
    private JPanel graphpanel;
    private mxGraphComponent mxGraphInput;
    private mxGraphComponent mxGraphOutput;
    private boolean directed;
    private EingabeVC evc;
    private AusgabeVC avc;
    private DialogEingabeVC devc;
    private DialogGraphSolutionVC dgsvc;
    private ExportDialog expdialog;
    private boolean activeProject;

    //funktion für folgeprojekt um Datei zu öffnen und Graph zu laden
    @FXML
    private void openFileButton(ActionEvent event) throws FileNotFoundException, IOException, ImportException {

        mxGraphWithInfo cont = this.avc.importGraphFromOwnFormat();
        if (cont != null) {
            this.resetUserWorkSpace();
            this.mxGraphInput = cont.getMxGraph();
            this.setUpGraph();
            int number = cont.getNumberofNodes();
            List<List<TextField>> matrix = cont.getMatrix();
            ActiveGraph aGraph = new ActiveGraph();
            aGraph.setGpAdjacenc(new GridPaneWithAdjacencList());
            aGraph.setGpMatrix(new GridPaneWithMatrixList());
            aGraph.setGraph(this.avc.getjGraph().getActiveGraph());
            GridPaneWithMatrixList gp = this.devc.generateMatrixListRandom(number, matrix, cont.isDirected());
            aGraph.setGpMatrix(gp);
            aGraph.setGpAdjacenc(this.updateListFromMatrix(number, matrix, cont.isDirected()));
            this.bean.setAgraph(aGraph);
            this.bean.setGraphComponent(this.mxGraphInput);
            this.setupUserWorkSpace(new DialogInputs(cont.isDirected(), number));
            setMenuItems();

        }

    }

    // einblenden der verfügbaren menüoptionen
    private void setMenuItems() {
        this.newGraph.setDisable(true);
        this.openGraph.setDisable(true);
        this.menuItemSave.setDisable(false);
        this.menuItemSaveToADEx.setDisable(false);
        this.exportProject.setDisable(false);
        this.activeProject = true;
    }

    //funktion für das exportieren des projekts
    @FXML
    public void exportProject() throws IOException {
        int nodecount = Integer.parseInt(this.anzahlKnoten.getText());
        boolean undirected = this.bean.getAgraph().getGraph().getType().isUndirected();
        ExportInputs inputs = this.expdialog.createExportDialog(nodecount, undirected);
        if (inputs != null) {
            inputs.setNodecount(nodecount);
            this.avc.exportGraphproject(inputs, this.bean);
        }

    }

    //funktion für ein neues graphprojekt ohne visualisierung
    @FXML
    private void newGraphWOVisualisation() throws IOException {
        DialogInputs dInput = this.devc.newGraphWOVisualisation();

        if (dInput != null) {
            if (dInput.isRandom()) {
                int number = dInput.getNumberofnodes();

                ExportInputsWOVis expInputs = this.expdialog.createExportDialogWOVis(number, !dInput.isDirection());

                if (expInputs != null) {
                    mxGraphWithInfo cont = this.avc.getjGraph().randomGraph(dInput.isDirection(), dInput, false);

                    this.avc.exportGraphWOVis(cont.getMatrix(), expInputs, dInput.isDirection(), this.bean);
                }

            }
        }
    }

    //graph im .graph format speichern
    @FXML
    public void saveGraph() throws ExportException {
        this.avc.exportGraphToOwnFormat();
    }

    @FXML
    public void saveGraphToADEx() throws ExportException {
        this.avc.exportGraphToADEx();
    }

    //visualisierung der Lösung eines Graphen mit dem Dijkstra algorithmus
    @FXML
    public void showDijkstra() {

        if (!this.anzahlKnoten.getText().isEmpty()) {
            int count = Integer.parseInt(this.anzahlKnoten.getText());
            String header = "Geben sie die Knoten ein zwischen denen der kürzeste Weg berechnet werden soll.";
            String title = "Finden des kürzesten Weges mit dem Dijkstra-Algorithmus";
            Pair<String, String> nodes = this.devc.createSolveDialog(count, header, title, false);

            if (nodes != null) {
                String from = nodes.getKey();
                String to = nodes.getValue();

                if (from.equals(to)) {
                    this.avc.errorsourceTargeSame();
                } else {
                    mxGraphWithPath output = this.avc.solveDijkstra(this.bean, nodes, this.mxGraphInput, directed);

                    if (output == null) {
                        this.avc.errorNoSolution();
                    } else {
                        this.mxGraphOutput = output.getMxGraph();
                        this.dgsvc.createSolveDialogDijkstra(this.mxGraphOutput, nodes, output.getGraphPath());
                        this.avc.getjGraph().getjGraphVisualisationHandler().colorBackGraph(this.mxGraphOutput);
                    }
                }

            }
        }

    }

    // Visualisierung des Graphen als Netzwerk und Lösung durch den Edmonds-Karp Algorithmus
    @FXML
    public void showNetzwerk() {
        int count = Integer.parseInt(this.anzahlKnoten.getText());
        String header = "Geben sie Quelle und Senke des Netzwerkgraphen an.";
        String title = "Netzwerkgraphen anzeigen";
        Pair<String, String> nodes = this.devc.createSolveDialog(count, header, title, true);
        if (nodes != null) {
            String from = nodes.getKey();
            String to = nodes.getValue();
            if (from.equals(to)) {
                this.avc.errorQuelleGleichSenke();

            } else if (this.checkIfNegativeEdges()) {
                this.avc.erorrNetzwerkNegativeKanten();
            } else {
                Pair<mxGraphComponent, Integer> pair = this.avc.getjGraph().maxFlowEdmondKarp(nodes.getKey(), nodes.getValue(), this.mxGraphInput, this.directed);
                this.mxGraphOutput = pair.getKey();
                this.dgsvc.createSolveDialogNetwork(mxGraphOutput, nodes, pair.getValue());
                this.avc.getjGraph().getjGraphVisualisationHandler().resetGraphNetwork(this.mxGraphOutput);
                this.avc.getjGraph().getjGraphVisualisationHandler().colorBackGraph(this.mxGraphOutput);
            }
        }

    }

    //swing Kompomenten müssen nach der erstellung der JavaFx komponenten initalisiert werden, daher hilfsfunktion
    private void createSwingContent(final SwingNode swingNode, JPanel panel) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                swingNode.setContent(panel);

            }
        });
    }

    //prüfen auf negative Kanten im Graphen, bezüglich der Algorithmen
    private boolean checkIfNegativeEdges() {
        List<List<TextField>> matrix = this.bean.getAgraph().getGpMatrix().getMatrixList();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.size(); j++) {
                int tmp = Integer.parseInt(matrix.get(i).get(j).getText());
                if (tmp < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    //funktion die bei erzeugung der Klasse aufgerufen wird und die Grundkomponenten initalisiert
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.evc = new EingabeVC();
        this.avc = new AusgabeVC();
        this.devc = new DialogEingabeVC();
        this.dgsvc = new DialogGraphSolutionVC();
        this.expdialog = new ExportDialog();
        this.devc.setStage(this);
        this.frameNode = new SwingNode();

        this.graphpanel = new JPanel();
        this.graphpanel.setSize(800, 100);
        this.activeProject = false;
        this.menuItemSave.setDisable(true);
        this.exportProject.setDisable(true);
        // swing content in javaFX einbetten 
        // Wichtig ist, dass javafx komponenten SwingNodes übergeben bekommen können, dann einfach ein Panel erstellen und in einen Node einbetten 
        this.createSwingContent(frameNode, graphpanel);

        this.mxGraphInput = null;
        this.mxGraphOutput = null;
        this.right.setVisible(false);

    }

    //funktion die bei Klicken des Nutzers auf erstellen eines Neuen Graphen aufgerufen wird
    @FXML
    private void newGraph(ActionEvent event) throws IOException {

        DialogInputs dInput = this.devc.createNewGraphDialog();

        if (dInput != null) {
            this.directed = dInput.isDirection();

            //prüfen ob der neue Graph zufällig generiert werden soll
            if (dInput.isRandom()) {
                createRandomGraph(dInput);

            }
            setupUserWorkSpace(dInput);
            this.setMenuItems();

        }

    }
//aufbauen des Arbeitsbereichs für den Nutzer

    private void setupUserWorkSpace(DialogInputs dInput) {

        ToggleGroup group = new ToggleGroup();
        this.gerichtet.setToggleGroup(group);
        this.gerichtet.setUserData("Directed");
        this.ungerichtet.setUserData("Undirected");
        this.ungerichtet.setToggleGroup(group);
        setListenerToCloseButton();

        this.anzahlKnoten.setText(Integer.toString(dInput.getNumberofnodes()));
        if (dInput.isDirection()) {
            this.gerichtet.setSelected(true);

        } else {
            this.ungerichtet.setSelected(true);

        }
        this.anzahlKnoten.setDisable(true);
        this.ungerichtet.setDisable(true);
        this.ungerichtet.setDisable(true);
        this.editOption.getItems().addAll("Matrix", "Liste");
        this.editOption.getSelectionModel().select("Matrix");
        this.editGraphButton.setOnMouseClicked(event2 -> {

            handleGridPanes(dInput.isDirection());

        });

        this.right.setVisible(true);
    }

    //Generierung eines neuen zufälligen Graphen
    private void createRandomGraph(DialogInputs dInput) throws IOException {

        mxGraphWithInfo cont = this.avc.getjGraph().randomGraph(dInput.isDirection(), dInput, true);

        if (cont != null) {
            this.resetUserWorkSpace();
            this.mxGraphInput = cont.getMxGraph();

            this.setUpGraph();
            int number = cont.getNumberofNodes();
            List<List<TextField>> matrix = cont.getMatrix();
            ActiveGraph aGraph = new ActiveGraph();
            aGraph.setGpAdjacenc(new GridPaneWithAdjacencList());
            aGraph.setGpMatrix(new GridPaneWithMatrixList());
            aGraph.setGraph(this.avc.getjGraph().getActiveGraph());
            GridPaneWithMatrixList gp = this.devc.generateMatrixListRandom(number, matrix, cont.isDirected());
            aGraph.setGpMatrix(gp);
            aGraph.setGpAdjacenc(this.updateListFromMatrix(number, matrix, cont.isDirected()));
            this.bean.setAgraph(aGraph);
            this.bean.setGraphComponent(this.mxGraphInput);

        }
    }

    //Button zum schließen eines Graphen
    private void setListenerToCloseButton() {
        Image imageAdd = new Image("/icons/close.png", 15, 15, false, false);
        this.closeButton.setGraphic(new ImageView(imageAdd));
        this.closeButton.setOnMouseClicked(eventClose -> {
            if (this.devc.sureToClose()) {

                resetUserWorkSpace();
            }

        });
    }

    //Funktion zum zurücksetzen der Arbeitsfläche
    private void resetUserWorkSpace() {
        this.anzahlKnoten.clear();
        this.bean.setAgraph(null);
        this.mxGraphInput = null;
        this.editOption.getItems().clear();
        if (this.bp.getLeft() != null) {
            this.bp.getLeft().setVisible(false);
        }
        this.bean.setAgraph(null);
        this.devc.setTextfieldList(new ArrayList<>());
        this.right.setVisible(false);

        this.newGraph.setDisable(false);
        this.openGraph.setDisable(false);
        this.menuItemSave.setDisable(true);
        this.exportProject.setDisable(true);

        this.activeProject = false;

    }

    //funktion die das Zusammenspiel der Adjazenzliste und der Matrix behandelt, weiterhin steuert sie die Visualisierung
    private void handleGridPanes(boolean directed) throws NumberFormatException {
        ActiveGraph g = new ActiveGraph();
        g.setGpAdjacenc(new GridPaneWithAdjacencList());
        g.setGpMatrix(new GridPaneWithMatrixList());
        String representation = this.editOption.getSelectionModel().getSelectedItem().toString();
        int number = Integer.parseInt(this.anzahlKnoten.getText());

        //unterscheidung ob durch liste oder matrix bearbeitet werden soll
        if (representation.equals("Liste")) {
            GridPaneWithAdjacencList tmp;
            GridPaneWithAdjacencList tmp2;
            List<List<String>> oldMatrix;
            List<List<TextField>> newMatrix;

            //falls noch kein Graph vorhanden ist und der Nutzer als erstes über die Liste bearbeitet
            if (this.bean.getAgraph() == null) {
                tmp = this.devc.editGraphviaList(number, directed);
                if (tmp != null) {
                    g.setGpAdjacenc(tmp);
                    g.setGpMatrix(this.updateMatrixFromList(number, tmp.getAdjacencList(), directed));
                    this.bean.setAgraph(g);
                    try {

                        this.mxGraphInput = this.avc.displayGraphfromList(this.bean.getAgraph().getGpAdjacenc().getAdjacencList(), bean, directed);
                        this.bean.setGraphComponent(this.mxGraphInput);
                    } catch (IOException ex) {
                        Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    setUpGraph();
                }
            } // falls Nutzer vorhanden Graphen über Liste bearbeitet
            else {

                tmp2 = this.bean.getAgraph().getGpAdjacenc();
                oldMatrix = this.textfieldToString(this.bean.getAgraph().getGpMatrix().getMatrixList());
                tmp = this.devc.updateGraphviaList(number, tmp2.getG());
                if (tmp != null) {
                    g.setGpAdjacenc(tmp);
                    g.setGpMatrix(this.updateMatrixFromList(number, tmp.getAdjacencList(), directed));
                    this.bean.setAgraph(g);
                    newMatrix = this.bean.getAgraph().getGpMatrix().getMatrixList();
                    this.updateGraph(oldMatrix, newMatrix, directed);
                }

            }

        } //bearbeiten des Graphens über Matrix 
        else if (representation.equals("Matrix")) {

            GridPaneWithMatrixList tmp = new GridPaneWithMatrixList();
            List<List<String>> oldMatrix;
            //falls kein Graph vorhanden ist und erster Aufruf über Matrix erfolgt
            if (this.bean.getAgraph() == null) {
                tmp = this.devc.editGraphviaMatrix(number, directed);

                if (tmp != null) {

                    g.setGpMatrix(tmp);
                    g.setGpAdjacenc(this.updateListFromMatrix(number, tmp.getMatrixList(), directed));
                    g.setGraph(this.avc.getjGraph().getActiveGraph());
                    this.bean.setAgraph(g);

                    try {

                        List<List<TextField>> matrixlist = this.bean.getAgraph().getGpMatrix().getMatrixList();
                        //   System.out.println(matrixList.size());

                        if (matrixlist != null) {
                            this.mxGraphInput = this.avc.displayGraphfromMatrix(matrixlist, bean, directed);
                            this.bean.setGraphComponent(this.mxGraphInput);
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(ViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    setUpGraph();
                }

            } //falls Graph vorhanden ist und Graph über Matrix bearbeitet werden soll
            else {

                oldMatrix = this.textfieldToString(this.bean.getAgraph().getGpMatrix().getMatrixList());
                tmp = this.devc.updateGraphviaMatrix(number, this.bean.getAgraph().getGpMatrix());
                if (tmp != null) {
                    g.setGraph(this.updateGraph(oldMatrix, tmp.getMatrixList(), directed));
                    g.setGpMatrix(tmp);
                    g.setGpAdjacenc(this.updateListFromMatrix(number, tmp.getMatrixList(), directed));
                    this.bean.setAgraph(g);
                }

            }

        }
    }

    //hilfsfunktion um Graphen erstmalig anzuzeigen
    private void setUpGraph() {
        this.graphpanel.removeAll();
        this.graphGridPane.getChildren().clear();
        this.graphpanel.setLayout(new BorderLayout());
        this.graphpanel.add(this.mxGraphInput, BorderLayout.CENTER);
        this.frameNode.setContent(graphpanel);

        AnchorPane.setTopAnchor(this.frameNode, 0.0);
        AnchorPane.setRightAnchor(this.frameNode, 0.0);
        AnchorPane.setLeftAnchor(this.frameNode, 0.0);
        AnchorPane.setBottomAnchor(this.frameNode, 0.0);
        this.graphGridPane.getChildren().setAll(this.frameNode);
        this.bp.setLeft(this.graphGridPane);
        this.bp.getLeft().setVisible(true);
    }

    //Änderungen in der Matrix im Gegensatz zum letzten Aufruf finden und anhand der Änderungen die Visualisierung anpassen
    public Graph updateGraph(List<List<String>> oldMatrix, List<List<TextField>> newMatrix, boolean directed) {

        List<List<String>> edgesToInsert = new ArrayList<>();
        List<List<String>> edgesToRemove = new ArrayList<>();
        List<List<String>> newMatrixString = this.textfieldToString(newMatrix);

        for (int i = 0; i < oldMatrix.size(); i++) {
            edgesToInsert.add(new ArrayList<>());
            edgesToRemove.add(new ArrayList<>());
            for (int j = 0; j < oldMatrix.size(); j++) {
                edgesToInsert.get(i).add("");
                edgesToRemove.get(i).add("");
                String oldVal = oldMatrix.get(i).get(j);
                String newVal = newMatrix.get(i).get(j).getText();

                if (newVal.startsWith("0") && newVal.length() > 1) {
                    newVal = newVal.substring(1);
                }

                //wenn alter und neuer wert unterschiedlich sind und nicht 0 quasi updaten einer kante
                if (!oldVal.equals(newVal) && !newVal.equals("0")) {
                    edgesToInsert.get(i).set(j, newVal);
                    edgesToRemove.get(i).set(j, "2");

                }
                //wenn kante entfernt werden soll 
                if (!oldVal.equals(newVal) && newVal.equals("0")) {
                    edgesToRemove.get(i).set(j, "1");
                }
            }
        }

        this.avc.updateGraph(edgesToInsert, edgesToRemove, this.mxGraphInput, directed);
        return this.avc.updateJGraphTGraph(edgesToInsert, edgesToRemove, newMatrixString, directed);
    }

    //updaten der Matrix bei Änderungen in der Liste
    public GridPaneWithMatrixList updateMatrixFromList(int number, ArrayList<ArrayList<Pair>> adjacencList, boolean directed) {

        GridPaneWithMatrixList tmp = this.devc.generateMatrixList(number, directed);

        List<List<TextField>> tmplist = tmp.getMatrixList();

        for (int i = 0; i < adjacencList.size(); i++) {

            for (Pair p : adjacencList.get(i)) {
                int key = Integer.parseInt((String) p.getKey());

                tmplist.get(i).get(key).setText((String) p.getValue());
                if (!directed) {
                    tmplist.get(key).get(i).setText((String) p.getValue());
                }

            }
        }
        tmp.setMatrixList(tmplist);
        return tmp;
    }

    //updaten der Liste bei Änderungen in der Matrix
    public GridPaneWithAdjacencList updateListFromMatrix(int number, List<List<TextField>> matrixList, boolean directed) {
        GridPaneWithAdjacencList tmp = new GridPaneWithAdjacencList();
        tmp.setG(this.devc.generateAdjacencyList(number, directed));
        ArrayList<ArrayList<Pair>> tmplist = new ArrayList<>();

        for (int i = 0; i < matrixList.size(); i++) {
            tmplist.add(new ArrayList<>());
            for (int j = 0; j < matrixList.get(i).size(); j++) {
                if (Integer.parseInt(matrixList.get(i).get(j).getText()) > 0) {
                    tmplist.get(i).add(new Pair(Integer.toString(j), matrixList.get(i).get(j).getText()));

                }

            }
        }

        tmp.setAdjacencList(tmplist);
        tmp.setG(this.devc.generateAdjacencyListFromNewList(number, tmp.getAdjacencList()));
        return tmp;

    }

    //hilfsfunktion um textfelder in Strings zu konvertieren
    public List<List<String>> textfieldToString(List<List<TextField>> oldList) {

        List<List<String>> newList = new ArrayList<>();

        for (int i = 0; i < oldList.size(); i++) {
            newList.add(new ArrayList<>());
            for (int j = 0; j < oldList.size(); j++) {

                newList.get(i).add(oldList.get(i).get(j).getText());

            }

        }

        return newList;
    }

    public void setStage(DataBean bean) {
        this.bean = bean;
    }

    public DataBean getBean() {
        return bean;
    }

    public void setBean(DataBean bean) {
        this.bean = bean;
    }
}
