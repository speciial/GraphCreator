/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgraph;

import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import eingabe.DialogInputs;
import eingabe.ExportInputs;
import eingabe.ExportInputsWOVis;
import exporter.OwnExporter;
import importer.OwnImporter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import javax.swing.JApplet;
import model.DataBean;
import model.mxGraphWithInfo;
import model.mxGraphWithPath;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm.MaximumFlow;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 *
 * @author lennaertn
 */
//klasse die das erstellen von Graphen, sowie das Visualisieren handelt
public class JGraphAdapter extends JApplet {

    public JGraphXAdapter<String, MyEdge> graphAdapter;
    private Graph activeGraph;
    //Algorithmen-Klasse
    private final jGraphAlgorithmHandler jGraphAlgoHandler;
    //DateiExporter-Klasse
    private final jGraphProjectExporter jGraphProjectExp;
    //Visualisierung der Graphen
    private final jGraphVisualisationHandler jGraphVisualisationHandler;

    public JGraphAdapter() {
        this.jGraphAlgoHandler = new jGraphAlgorithmHandler();
        this.jGraphProjectExp = new jGraphProjectExporter();
        this.jGraphVisualisationHandler = new jGraphVisualisationHandler();
    }

    public JGraphXAdapter<String, MyEdge> getGraphAdapter() {
        return graphAdapter;
    }

    public jGraphAlgorithmHandler getjGraphAlgoHandler() {
        return jGraphAlgoHandler;
    }

    public jGraphProjectExporter getjGraphProjectExp() {
        return jGraphProjectExp;
    }

    public jGraphVisualisationHandler getjGraphVisualisationHandler() {
        return jGraphVisualisationHandler;
    }

    //konvertiert Matrix des Nutzers in einen Graphen 
    public mxGraphComponent displayGraphFromMatrix(List<List<TextField>> matrixList, DataBean bean, boolean directed) throws IOException {
        ListenableGraph<String, MyEdge> g = newGraphFromMatrix(directed, matrixList);
        int numberofNodes = matrixList.size();
        mxGraphComponent graphComponent = jGraphVisualisationHandler.visualizeGraph(g, directed, this, numberofNodes);
        graphComponent.setConnectable(false);
        //wichtig für die exception drag & drop in progress
        graphComponent.setDragEnabled(false);
        graphComponent.getGraph().setAllowDanglingEdges(false);
        bean.getAgraph().setGraph(g);
        this.activeGraph = g;
        return graphComponent;
    }

    //erzeugt einen neuen Graphen, falls der Nutzer leeren Graphen erzeugt (nicht zufällig)
    private ListenableGraph<String, MyEdge> newGraphFromMatrix(boolean directed, List<List<TextField>> matrixList) throws NumberFormatException {
        ListenableGraph<String, MyEdge> g;
        if (!directed) {
            g = new DefaultListenableGraph<>(new DefaultUndirectedWeightedGraph<>(MyEdge.class));
        } else {
            g = new DefaultListenableGraph<>(new DefaultDirectedWeightedGraph<>(MyEdge.class));
        }
        //vertices im graphen hinzufuegen
        List<String> vertexList = new ArrayList<>();
        for (int i = 0; i < matrixList.size(); i++) {
            vertexList.add(Integer.toString(i));
            g.addVertex(Integer.toString(i));

        }

        //kanten hinzufuegen
        for (int i = 0; i < matrixList.size(); i++) {
            for (int j = 0; j < matrixList.get(i).size(); j++) {
                int value = Integer.parseInt(matrixList.get(j).get(i).getText());

                if (value != 0) {

                    if (!directed) {
                        if (j >= i) {

                            g.addEdge(vertexList.get(j), vertexList.get(i));

                            g.setEdgeWeight(vertexList.get(j), vertexList.get(i), value);

                        }
                    } else {
                        g.addEdge(vertexList.get(j), vertexList.get(i));

                        g.setEdgeWeight(vertexList.get(j), vertexList.get(i), value);

                    }
                }
            }
        }
        return g;
    }

    // zufälligen Graphen generieren
    public mxGraphWithInfo randomGraph(boolean directed, DialogInputs dInput, boolean vizualize) {
        ListenableGraph<String, MyEdge> g = null;
        Random r = new Random();
        int nodeCount = dInput.getNumberofnodes();

        g = this.erdosRenyiRandomGraph(g, directed, dInput);

        ConnectivityInspector inspector = new ConnectivityInspector(g);
        // überprüfen ob es isolierte Knoten gibt, falls ja zufällige Knoten verbinden
        if (!inspector.isConnected()) {

            List<Set<String>> connectedSets = inspector.connectedSets();
            for (int i = 0; i < connectedSets.size() - 1; i++) {
                Set<String> tmp = connectedSets.get(i);
                Set<String> tmp2 = connectedSets.get(i + 1);

                int setsize = tmp.size();
                int setsize2 = tmp2.size();
                int numberOfSetEntry = new Random().nextInt(setsize);
                int numberOfSetEntry2 = new Random().nextInt(setsize2);
                int j = 0;
                String firstVertex = "";
                String secondVertex = "";
                for (String e : tmp) {
                    if (numberOfSetEntry == j) {
                        firstVertex = e;
                    }
                    j++;
                }
                j = 0;
                for (String e : tmp2) {

                    if (numberOfSetEntry2 == j) {
                        secondVertex = e;
                    }
                    j++;
                }
                int number = r.nextInt((dInput.getMax() - dInput.getMin()) + 1) + dInput.getMin();
                while (number == 0) {
                    number = r.nextInt((dInput.getMax() - dInput.getMin()) + 1) + dInput.getMin();
                }

                g.addEdge(firstVertex, secondVertex);
                g.setEdgeWeight(firstVertex, secondVertex, number);

            }

        }

        Set<MyEdge> edgeset = g.edgeSet();
        Iterator<MyEdge> itr = edgeset.iterator();
        while (itr.hasNext()) {
            int number = r.nextInt((dInput.getMax() - dInput.getMin()) + 1) + dInput.getMin();
            while (number == 0) {
                number = r.nextInt((dInput.getMax() - dInput.getMin()) + 1) + dInput.getMin();
            }
            g.setEdgeWeight(itr.next(), number);

        }

        this.activeGraph = g;
        boolean direction = this.activeGraph.getType().isDirected();
        mxGraphComponent mxGraph = null;
        if (vizualize) {
            mxGraph = jGraphVisualisationHandler.visualizeGraph(g, direction, this, nodeCount);

        }

        List<List<TextField>> matrix = this.matrixFromGraph(g, direction, nodeCount);
        mxGraphWithInfo cont = new mxGraphWithInfo(mxGraph, direction, nodeCount, matrix);

        return cont;
    }

    // generierung eines zufälligen Graphen mit dem Erdös-Renyi modell
    public ListenableGraph<String, MyEdge> erdosRenyiRandomGraph(ListenableGraph<String, MyEdge> g, boolean directed, DialogInputs dInput) {
        Random r = new Random();
        double edgeNumber = this.getRandomEdgeNumber(dInput.getDichte(), directed, dInput.getNumberofnodes());

        GnmRandomGraphGenerator generator = new GnmRandomGraphGenerator(dInput.getNumberofnodes(), (int) edgeNumber);

        Supplier<String> vSupplier = new Supplier<String>() {
            private int id = 0;

            @Override
            public String get() {
                return Integer.toString(id++);
            }
        };

        if (!directed) {
            g = new DefaultListenableGraph<>(new DefaultUndirectedWeightedGraph<>(vSupplier, MyEdgeSupplier.createDefaultWeightedEdgeSupplier()));
        } else {
            g = new DefaultListenableGraph<>(new DefaultDirectedWeightedGraph<>(vSupplier, MyEdgeSupplier.createDefaultWeightedEdgeSupplier()));
        }

        generator.generateGraph(g);

        return g;
    }

    //Anzahl der Kanten für einen zufälligen Graphen bestimmen
    private double getRandomEdgeNumber(int dichte, boolean directed, int nodes) {

        if (!directed) {
            if (dichte == 25) {
                return (nodes * (nodes - 1)) * 0.125;
            }
            if (dichte == 50) {
                return (nodes * (nodes - 1)) * 0.25;
            }
            if (dichte == 75) {
                return (nodes * (nodes - 1)) * 0.375;
            }
            if (dichte == 100) {
                return (nodes * (nodes - 1)) * 0.5;
            }
        } else {
            if (dichte == 25) {
                return (nodes * (nodes - 1)) * 0.25;
            }
            if (dichte == 50) {
                return (nodes * (nodes - 1)) * 0.5;
            }
            if (dichte == 75) {
                return (nodes * (nodes - 1)) * 0.75;
            }
            if (dichte == 100) {
                return nodes * (nodes - 1);
            }
        }
        return 0;
    }

    //konviert Adjazenzliste des Nutzers in einen Graphen
    public mxGraphComponent displayGraphFromList(ArrayList<ArrayList<Pair>> adjacencList, DataBean bean, boolean directed) {
        ListenableGraph<String, MyEdge> g;
        if (!directed) {
            g = new DefaultListenableGraph<>(new DefaultUndirectedWeightedGraph<>(MyEdge.class));
        } else {
            g = new DefaultListenableGraph<>(new DefaultDirectedWeightedGraph<>(MyEdge.class));
        }

        List<String> VertexList = new ArrayList<>();

        for (int i = 0; i < adjacencList.size(); i++) {
            VertexList.add(Integer.toString(i));
            g.addVertex(VertexList.get(i));
        }

        for (int i = 0; i < adjacencList.size(); i++) {
            for (int j = 0; j < adjacencList.get(i).size(); j++) {
                Pair tmp = adjacencList.get(i).get(j);
                int weight = Integer.parseInt(tmp.getValue().toString());
                g.addEdge(Integer.toString(i), tmp.getKey().toString());
                g.setEdgeWeight(Integer.toString(i), tmp.getKey().toString(), weight);

            }
        }
        int numberofNodes = adjacencList.size();
        mxGraphComponent graphComponent = jGraphVisualisationHandler.visualizeGraph(g, directed, this, numberofNodes);

        bean.getAgraph().setGraph(g);
        this.activeGraph = g;
        return graphComponent;
    }

    //updaten der Datenstruktur eines Graphen bei Änderungen
    public Graph updateJGraphTgraph(List<List<String>> edgesToBeUpdated, List<List<String>> edgesToBeRemoved, List<List<String>> matrixList, boolean directed) {

        for (int i = 0; i < edgesToBeUpdated.size(); i++) {
            for (int j = 0; j < edgesToBeUpdated.size(); j++) {
                String val = edgesToBeUpdated.get(i).get(j);
                String valRemove = edgesToBeRemoved.get(i).get(j);
                String weight = matrixList.get(i).get(j);

                if (!val.equals((""))) {

                    //if und else vereinfachen sind nur zu testzwecken da
                    this.activeGraph.removeEdge(Integer.toString(i), Integer.toString(j));
                    this.activeGraph.addEdge(Integer.toString(i), Integer.toString(j));
                    this.activeGraph.setEdgeWeight(Integer.toString(i), Integer.toString(j), Integer.parseInt(weight));

                }
                if (valRemove.equals("1")) {

                    this.activeGraph.removeAllEdges(Integer.toString(i), Integer.toString(j));

                }
            }
        }

        return this.activeGraph;

    }

    //Funktion die bei Änderungen des Nutzers aufgerufen wird und die Visualisierung des Graphen updated
    public void updateGraph(List<List<String>> edgesToBeUpdated, List<List<String>> edgesToBeRemoved, mxGraphComponent mxGraphInput, boolean directed) {
        mxGraphModel graphModel = (mxGraphModel) mxGraphInput.getGraph().getModel();
        Object parent = mxGraphInput.getGraph().getDefaultParent();

        mxGraphInput.getGraph().getModel().beginUpdate();

        try {

            Collection<Object> cells = graphModel.getCells().values();
            //einzelnen Zellen des Graphen finden und ersetzen bzw. hinzufügen von Kanten
            if (directed) {

                for (int i = 0; i < edgesToBeUpdated.size(); i++) {
                    for (int j = 0; j < edgesToBeUpdated.size(); j++) {
                        updateGraphModel(edgesToBeUpdated, i, j, edgesToBeRemoved, cells, mxGraphInput, directed, parent);

                    }
                }
            } else {
                for (int i = 0; i < edgesToBeUpdated.size(); i++) {
                    for (int j = i; j < edgesToBeUpdated.size(); j++) {
                        updateGraphModel(edgesToBeUpdated, i, j, edgesToBeRemoved, cells, mxGraphInput, directed, parent);

                    }
                }
            }

        } finally {
            mxGraphInput.getGraph().getModel().endUpdate();
            if (directed) {
                mxGraphInput.validateGraph();
                new mxParallelEdgeLayout(mxGraphInput.getGraph()).execute(parent);
            }

        }
    }

    //updaten Kanten in der Visualisierung
    private void updateGraphModel(List<List<String>> edgesToBeUpdated, int i, int j, List<List<String>> edgesToBeRemoved, Collection<Object> cells, mxGraphComponent mxGraphInput, boolean directed, Object parent) throws NumberFormatException {
        mxCell vertex1 = new mxCell();
        mxCell vertex2 = new mxCell();
        String val = edgesToBeUpdated.get(i).get(j);
        String valRemove = edgesToBeRemoved.get(i).get(j);
        if (!val.equals("")) {

            for (Object c : cells) {
                mxCell cell = (mxCell) c;

                if (cell.isVertex()) {

                    int cellVal = Integer.parseInt(cell.getValue().toString());

                    if (cellVal == i) {
                        vertex1 = cell;

                    }
                    if (cellVal == j) {
                        vertex2 = cell;
                    }
                }
            }
            removeEdge(valRemove, cells, i, j, mxGraphInput, "2", directed);

            mxGraphInput.getGraph().insertEdge(parent, null, val, vertex1, vertex2);

        }

        removeEdge(valRemove, cells, i, j, mxGraphInput, "1", directed);
    }

    //hilfsfunktion um zu entfernende Kanten zu finden und zu entfernen
    private void removeEdge(String valRemove, Collection<Object> cells, int i, int j, mxGraphComponent mxGraphInput, String num, boolean directed) throws NumberFormatException {
        if (!valRemove.equals("")) {
            Object[] edges = null;
            Object[] unedges = null;
            for (Object c : cells) {
                mxCell cell = (mxCell) c;
                if (cell.isEdge() && !cell.isVertex()) {

                    int source = Integer.parseInt(cell.getSource().getValue().toString());
                    int target = Integer.parseInt(cell.getTarget().getValue().toString());

                    if (directed && valRemove.equals(num) && source == i && target == j) {

                        edges = mxGraphInput.getGraph().getEdgesBetween(cell.getSource(), cell.getTarget(), directed);

                        break;
                    } else if ((!directed && valRemove.equals(num) && source == i && target == j) || (!directed && valRemove.equals(num) && source == j && target == i)) {
                        edges = mxGraphInput.getGraph().getEdgesBetween(cell.getSource(), cell.getTarget(), directed);
                        break;
                    }
                }
            }
            if (edges != null) {

                mxGraphInput.getGraph().removeCells(edges);

            }

        }
    }

    //MaxFlow nach Edmonds-Karps Algorithmus
    public Pair<mxGraphComponent, Integer> maxFlowEdmondKarp(String quelle, String senke, mxGraphComponent mxGraph, boolean directed) {
        Pair<MaximumFlow, Integer> mf = this.jGraphAlgoHandler.maxFlowEdmondsKarp(this.activeGraph, quelle, senke);
        mxGraphComponent graphComponent = this.jGraphVisualisationHandler.netzWerkGraph(this, directed, mf, mxGraph);

        return new Pair(graphComponent, mf.getValue());
    }

    //Projekt Exportieren
    public void exportGraphProject(ExportInputs inputs, DataBean bean) throws IOException {

        this.jGraphProjectExp.exportGraphProject(inputs, bean, this, this.jGraphAlgoHandler);
    }

    //Lösung eines Graphen durch den Dijkstra Algorithmus
    public mxGraphWithPath shortestPathDijkstra(DataBean bean, Pair pair, mxGraphComponent mxGraph, boolean directed) {
        return this.jGraphVisualisationHandler.shortestPathDijkstra(bean, pair, mxGraph, directed, this, this.activeGraph);

    }

    //Graphen ohne Visualisierung exportieren
    public void exportGraphWOVis(List<List<TextField>> randomMatrix, ExportInputsWOVis expInputs, boolean directed, DataBean bean) {
        ListenableGraph<String, MyEdge> g = newGraphFromMatrix(directed, randomMatrix);
        this.activeGraph = g;

        this.jGraphProjectExp.exportGraphWOVis(this.activeGraph, expInputs, this.getjGraphAlgoHandler(), bean, randomMatrix);

    }

    //Eigene Kante, zeigt das Kantengewicht an der Kante an
    public static class MyEdge extends DefaultWeightedEdge {

        @Override
        public String getSource() {
            return super.getSource().toString();
        }

        @Override
        public String getTarget() {
            return super.getTarget().toString();

        }

        @Override
        public String toString() {
            int i = (int) getWeight();
            return String.valueOf(i);
        }
    }

    //graph in eigenem Dateiformat speichern
    public void saveGraph() {
        FileChooser fileChooser = new FileChooser();
        File ownFile = fileChooser.showSaveDialog(null);
        String suffix = ".graph";
        if (ownFile != null) {
            if (!ownFile.toString().contains(suffix)) {
                ownFile = new File(ownFile.toString() + suffix);
            }
            OwnExporter exporter = new OwnExporter(this.activeGraph, ownFile.toString());
            //fehler muss noch zurückgegeben werden 
            exporter.exportGraph();
            this.graphFormatsaved(ownFile.getName());

        }

    }

    //Dialogfenster erfolgsfall des speichern
    private void graphFormatsaved(String filename) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, filename + " wurde erfolgreich  erstellt.", ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

    // Fenster zum öffnen eines Graphen
    public mxGraphWithInfo importGraphFromOwnFormat() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(".graph Datei wählen", "*.graph"));
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            OwnImporter importer = new OwnImporter(selectedFile);
            Graph g = importer.importGraph();
            if (g != null) {
                int nodeCount = g.vertexSet().size();
                this.activeGraph = g;
                boolean direction = this.activeGraph.getType().isDirected();
                mxGraphComponent mxGraph = jGraphVisualisationHandler.visualizeGraph(g, direction, this, nodeCount);
                List<List<TextField>> matrix = this.matrixFromGraph(g, direction, nodeCount);
                mxGraphWithInfo cont = new mxGraphWithInfo(mxGraph, direction, nodeCount, matrix);

                return cont;
            }

        }
        return null;

    }

    // aus einem zufälligen Graphen Adjazenzmatrix erstellen
    public List<List<TextField>> matrixFromGraph(Graph g, boolean directed, int nodes) {
        List<List<TextField>> matrix = new ArrayList<>();

        Set<Object> edgeset = g.edgeSet();
        Iterator<Object> itr = edgeset.iterator();

        for (int i = 0; i < nodes; i++) {
            matrix.add(new ArrayList<>());
            for (int j = 0; j < nodes; j++) {
                matrix.get(i).add(new TextField());
                matrix.get(i).get(j).setText("0");
            }
        }

        while (itr.hasNext()) {
            MyEdge e = (MyEdge) itr.next();
            String src = (String) g.getEdgeSource(e);
            String tar = (String) g.getEdgeTarget(e);
            int weight = (int) g.getEdgeWeight(e);

            matrix.get(Integer.parseInt(src)).get(Integer.parseInt(tar)).setText(Integer.toString(weight));
            if (!directed) {
                matrix.get(Integer.parseInt(tar)).get(Integer.parseInt(src)).setText(Integer.toString(weight));
            }

        }

        return matrix;
    }

    public Graph getActiveGraph() {
        return activeGraph;
    }

}
