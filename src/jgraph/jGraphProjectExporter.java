/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgraph;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import eingabe.ExportInputs;
import eingabe.ExportInputsWOVis;
import exporter.BellmanFordExporter;
import exporter.KruskalExporter;
import exporter.NetzwerkGraphExporter;
import exporter.OwnExporter;
import exporter.TiefenundBreitensucheExporter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import model.DataBean;
import org.controlsfx.dialog.ProgressDialog;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm.MaximumFlow;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.NegativeCycleDetectedException;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

/**
 *
 * @author lennaertn
 */
// Klasse die das Handeln des Exports bereitstellt
public class jGraphProjectExporter {
    //Graph ohne Visualisierung exportieren
    void exportGraphWOVis(Graph g, ExportInputsWOVis input, jGraphAlgorithmHandler algorithm, DataBean bean, List<List<TextField>> matrix) {

        Task copyWorker = createWorker(g, input, algorithm, bean, this);

        ProgressDialog dialog = new ProgressDialog(copyWorker);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setGraphic(null);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.initStyle(StageStyle.UTILITY);

        new Thread(copyWorker).start();
        dialog.showAndWait();
        String errormsg = "";
        boolean error = false;
        if (input.getNetzwerk().getKey()) {
            if (this.checkIfNegativeEdges(matrix)) {
                errormsg += "Netzwerkgraph konnte nicht erstellt werden, da der Graph negative Kapazitäten beinhaltet " + System.lineSeparator();
                error = true;

            }
        }

        if (input.getBellmanFord().getKey()) {
            if (!this.checkIfGraphisUsableforBFord(g) || algorithm.bellmanFord(g, input.getBellmanFord().getValue()) == null) {
                errormsg += "Bellman-Ford-Algorithmus hat einen negativen Kreislauf im Graphen entdeckt. " + System.lineSeparator()
                        + "Daher wurde die Datei für den Algorithmus nicht exportiert." + System.lineSeparator();
                error = true;
            }
        }
        if (error) {
            errormsg += "Die restlichen Projektdatein wurden erfolgreich erstellt.";
            this.errorExport(errormsg);
        } else {
            exportfinished();
        }
        
    }
    //Fehlermeldung, z.b negative Kanten oder Kreislauf
    private void errorExport(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }
    //erfolgreich exportiert Dialog
    private void exportfinished() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Projektorder wurde erfolgreich erstellt", ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }
    //Exportfunktion für Graphen mit visualisierung
    public void exportGraphProject(ExportInputs input, DataBean bean, JGraphAdapter jGraphAdapter, jGraphAlgorithmHandler algorithm) throws IOException {

        String path = input.getPath();
        File file = new File(path + "/" + input.getDirname());
        file.mkdir();
        Graph g = bean.getAgraph().getGraph();
        String abspath = file.getAbsolutePath() + "/";
        boolean directed = g.getType().isDirected();
        boolean error = false;
        String errormsg = "";
        if (input.isPicture()) {
            this.saveGraphImageToProject(bean.getGraphComponent().getGraph(), abspath, input.getDirname());
        }
        if (input.isDotFormat()) {
            try {
                this.exportGraphDOT(abspath, input.getDirname(), g);
            } catch (ExportException ex) {
                Logger.getLogger(JGraphAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (input.isGraphFormat()) {
            this.exportGraphToOwnFormatProject(abspath, input.getDirname(), bean.getAgraph().getGraph());
        }
        if (input.getBellmanFord().getKey()) {
            if (this.checkIfGraphisUsableforBFord(g)) {
                String startvertex = input.getBellmanFord().getValue();
                try {
                    ShortestPathAlgorithm.SingleSourcePaths bellmanFordPath = algorithm.bellmanFord(g, startvertex);
                    if (bellmanFordPath == null) {
                        error = true;
                        errormsg += "Bellman-Ford-Algorithmus hat einen negativen Kreislauf im Graphen entdeckt. " + System.lineSeparator()
                                + "Daher wurde die Datei für den Algorithmus nicht exportiert." + System.lineSeparator();
                    } else {
                        this.shortestPathsBellmanFord(startvertex, input.getNodecount(), abspath, bellmanFordPath);
                    }

                } catch (NegativeCycleDetectedException e) {

                }
            } else {
                error = true;
                errormsg += "Graph ist ungerichtet und enthällt negative Kantengewichte." + System.lineSeparator()
                        + "Daher kann der Bellman-Ford-Algorithmus nicht auf den Graphen angewendet werden. " + System.lineSeparator();
            }

        }
        if (input.isKruskal()) {

            Set<JGraphAdapter.MyEdge> edgeSet = algorithm.kruskalMinSpan(g);
            File fileKruskal = new File(abspath + "Minimaler Spannbaum");
            fileKruskal.mkdir();
            String abspathKruskal = fileKruskal.getAbsolutePath() + "/";

            this.minimumSpanningTreeKruskal(abspathKruskal, input.getDirname(), edgeSet);
            mxGraphComponent gComp = jGraphAdapter.getjGraphVisualisationHandler().minSpannTree(bean, bean.getGraphComponent(), jGraphAdapter);
            this.saveGraphImageToProject(gComp.getGraph(), abspathKruskal, "MinimalerSpannbaum");
            jGraphAdapter.getjGraphVisualisationHandler().colorBackGraph(gComp);
        }
        if (input.getTiefensuche().getKey()) {
            String startvertex = input.getTiefensuche().getValue();
            List<String> vertexList = algorithm.tiefenSuche(g, startvertex);
            this.tiefenundBreitenSuche(vertexList, abspath, "tiefensuche");

        }
        if (input.getBreitensuche().getKey()) {
            String startvertex = input.getBreitensuche().getValue();
            List<String> vertexList = algorithm.breitenSuche(g, startvertex);
            this.tiefenundBreitenSuche(vertexList, abspath, "breitensuche");
        }
        if (input.getNetzwerk().getKey()) {

            String quelle = input.getNetzwerk().getValue().getKey();
            String senke = input.getNetzwerk().getValue().getValue();

            if (!this.checkIfNegativeEdges(bean.getAgraph().getGpMatrix().getMatrixList())) {
                Pair<MaximumFlow, Integer> mf = algorithm.maxFlowEdmondsKarp(g, quelle, senke);
                if (mf != null) {
                    File fileNetzwerk = new File(abspath + "Netzwerkgraph");
                    fileNetzwerk.mkdir();
                    String abspathNetzwerk = fileNetzwerk.getAbsolutePath() + "/";
                    mxGraphComponent mxGraph = jGraphAdapter.getjGraphVisualisationHandler().netzWerkGraph(jGraphAdapter, directed, mf, bean.getGraphComponent());
                    this.netzwerkGraph(mf, abspathNetzwerk, "Netzwerkgraph", quelle, senke, g);
                    this.saveGraphImageToProject(mxGraph.getGraph(), abspathNetzwerk, "Netzwerk");
                    jGraphAdapter.getjGraphVisualisationHandler().resetGraphNetwork(mxGraph);
                    jGraphAdapter.getjGraphVisualisationHandler().colorBackGraph(mxGraph);
                }

            } else {
                error = true;
                errormsg += "Netzwerkgraph konnte nicht erstellt werden, da der Graph negative Kapazitäten beinhaltet " + System.lineSeparator();
            }
        }
        if (error) {
            errormsg += "Die restlichen Projektdateien wurden erfolgreich erstellt.";
            this.errorExport(errormsg);
        } else {
            this.exportfinished();
        }

    }
    //NetzwerkGraphDatei erstellen
    public void netzwerkGraph(Pair<MaximumFlow, Integer> mf, String path, String name, String quelle, String senke, Graph g) {
        String suffix = ".txt";
        File ownFile = new File(path + name + suffix);
        NetzwerkGraphExporter netzExporter = new NetzwerkGraphExporter(ownFile.toString(), quelle, senke, g, mf);
        netzExporter.exportGraph();
    }
    //Negative Kanten im Graphen prüfen
    private boolean checkIfNegativeEdges(List<List<TextField>> matrix) {

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
    //tiefen und Breitensuche exportieren
    public void tiefenundBreitenSuche(List<String> vertexList, String path, String name) {
        String suffix = ".txt";

        TiefenundBreitensucheExporter tsucheExporter = new TiefenundBreitensucheExporter(path + name + suffix);
        try {
            tsucheExporter.exportTiefensucheToFile(vertexList);
        } catch (IOException ex) {
            Logger.getLogger(jGraphProjectExporter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    //.graph Datei erzeugen
    public void exportGraphToOwnFormatProject(String path, String name, Graph g) {
        File ownFile;
        String suffix = ".graph";
        ownFile = new File(path + name + suffix);
        OwnExporter exporter = new OwnExporter(g, ownFile.toString());
        exporter.exportGraph();
    }
    //Bellman-Ford datei erzeugen
    public void shortestPathsBellmanFord(String startvertex, int number, String path, ShortestPathAlgorithm.SingleSourcePaths paths) {

        String suffix = ".txt";
        BellmanFordExporter bfordExporter = new BellmanFordExporter(path + "bellmanFord" + suffix, startvertex);
        try {
            bfordExporter.exportBellmanFordToFile(paths, number);
        } catch (IOException ex) {
            Logger.getLogger(JGraphAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    //minimalen Spannbaum nach Kruskal erzeugen ( textfile) 
    public void minimumSpanningTreeKruskal(String path, String name, Set<JGraphAdapter.MyEdge> edgeSet) {

       
        String suffix = ".txt";
        KruskalExporter kexp = new KruskalExporter(path + "MinimalerSpannbaum" + suffix);
        try {
            kexp.exportKruskaltoFile(edgeSet);

        } catch (IOException ex) {
            Logger.getLogger(JGraphAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    // DOT format erzeugen
    public void exportGraphDOT(String path, String name, Graph g) throws ExportException {

        ComponentNameProvider<String> vertexIdProvider = new ComponentNameProvider<String>() {
            @Override
            public String getName(String s) {
                int number = Integer.parseInt(s);
                String result = Integer.toString(number);
                return result;
            }
        };

        ComponentNameProvider<JGraphAdapter.MyEdge> edgeProvider = new ComponentNameProvider<JGraphAdapter.MyEdge>() {

            @Override
            public String getName(JGraphAdapter.MyEdge t) {
                return t.toString();
            }

        };

        GraphExporter<String, JGraphAdapter.MyEdge> gexp = new DOTExporter<>(vertexIdProvider, null, edgeProvider);
        File dotFile;
        String suffix = ".dot";
        dotFile = new File(path + name + suffix);
        gexp.exportGraph(g, dotFile);
    }
    //Bild des Graphen erzeugen
    public boolean saveGraphImageToProject(mxGraph g, String path, String name) throws IOException {
        File imgFile;
        String suffix = ".png";
        imgFile = new File(path + name + suffix);
        imgFile.createNewFile();

        BufferedImage image = mxCellRenderer.createBufferedImage(g, null, 2, Color.white, true, null);
        return ImageIO.write(image, "PNG", imgFile);

    }
    //überprüfung ob BellmanFord negative Kanten in einem ungerichteten Graphen hat -> immer negativer Kreislauf
    private boolean checkIfGraphisUsableforBFord(Graph g) {
        if (g.getType().isUndirected()) {
            Set<JGraphAdapter.MyEdge> edgeSet = g.edgeSet();
            Iterator itr = edgeSet.iterator();
            while (itr.hasNext()) {
                JGraphAdapter.MyEdge e = (JGraphAdapter.MyEdge) itr.next();
                int val = Integer.parseInt(e.toString());
                if (val < 0) {
                    return false;
                }
            }
        }
        return true;
    }
    // Fortschrittsanzeige während des exportierens
    public Task createWorker(Graph g, ExportInputsWOVis input, jGraphAlgorithmHandler algorithm, DataBean bean, jGraphProjectExporter exporter) {
        return new Task() {
            @Override
            protected Object call() {
                String path = input.getPath();
                File file = new File(path + "/" + input.getDirname());
                file.mkdir();
                String abspath = file.getAbsolutePath() + "/";

                List<String> listofTasks = new ArrayList<>();

                if (input.isDotFormat()) {
                    listofTasks.add("DOT");
                }
                if (input.isGraphFormat()) {
                    listofTasks.add("Graph");
                }
                if (input.getBellmanFord().getKey()) {
                    listofTasks.add("Bford");
                }
                if (input.isKruskal()) {
                    listofTasks.add("Kruskal");
                }
                if (input.getTiefensuche().getKey()) {
                    listofTasks.add("Tiefensuche");
                }
                if (input.getBreitensuche().getKey()) {
                    listofTasks.add("Breitensuche");
                }
                if (input.getNetzwerk().getKey()) {
                    listofTasks.add("Netzwerk");
                }
                int numberofTasks = listofTasks.size();

                if (input.isDotFormat()) {
                    try {
                        updateMessage("Erstelle .DOT-Format...");
                        exporter.exportGraphDOT(abspath, input.getDirname(), g);
                        this.updateProgress(listofTasks.indexOf("DOT") + 1, numberofTasks);

                    } catch (ExportException ex) {
                        Logger.getLogger(jGraphProjectExporter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (input.isGraphFormat()) {
                    updateMessage("Erstelle .Graph-Format...");
                    exporter.exportGraphToOwnFormatProject(abspath, input.getDirname(), g);
                    this.updateProgress(listofTasks.indexOf("Graph") + 1, numberofTasks);

                }

                if (input.getBellmanFord().getKey()) {
                    if (exporter.checkIfGraphisUsableforBFord(g)) {
                        String startvertex = input.getBellmanFord().getValue();
                        updateMessage("Berechne Bellman-Ford-Algorithmus...");
                        ShortestPathAlgorithm.SingleSourcePaths bellmanFordPath = algorithm.bellmanFord(g, startvertex);
                        if (bellmanFordPath != null) {
                            exporter.shortestPathsBellmanFord(startvertex, input.getNodecount(), abspath, bellmanFordPath);
                            this.updateProgress(listofTasks.indexOf("Bford") + 1, numberofTasks);
                        }

                    }

                }

                if (input.isKruskal()) {
                    updateMessage("Berechne minimalen Spannbaum...");
                    Set<JGraphAdapter.MyEdge> edgeSet = algorithm.kruskalMinSpan(g);
                    File fileKruskal = new File(abspath + "Minimaler Spannbaum");
                    fileKruskal.mkdir();
                    String abspathKruskal = fileKruskal.getAbsolutePath() + "/";
                    exporter.minimumSpanningTreeKruskal(abspathKruskal, input.getDirname(), edgeSet);
                    this.updateProgress(listofTasks.indexOf("Kruskal") + 1, numberofTasks);

                }

                if (input.getTiefensuche().getKey()) {
                    updateMessage("Berechne Tiefensuche...");
                    String startvertex = input.getTiefensuche().getValue();
                    List<String> vertexList = algorithm.tiefenSuche(g, startvertex);
                    exporter.tiefenundBreitenSuche(vertexList, abspath, "tiefensuche");
                    this.updateProgress(listofTasks.indexOf("Tiefensuche") + 1, numberofTasks);
                }
                if (input.getBreitensuche().getKey()) {
                    updateMessage("Berechne Breitensuche...");
                    String startvertex = input.getBreitensuche().getValue();
                    List<String> vertexList = algorithm.breitenSuche(g, startvertex);
                    exporter.tiefenundBreitenSuche(vertexList, abspath, "breitensuche");
                    this.updateProgress(listofTasks.indexOf("Breitensuche") + 1, numberofTasks);
                }

                if (input.getNetzwerk().getKey()) {
                    updateMessage("Erstelle Netzwerkgraphen...");
                    String quelle = input.getNetzwerk().getValue().getKey();
                    String senke = input.getNetzwerk().getValue().getValue();

                    Pair<MaximumFlow, Integer> mf = algorithm.maxFlowEdmondsKarp(g, quelle, senke);
                    if (mf != null) {
                        File fileNetzwerk = new File(abspath + "Netzwerkgraph");
                        fileNetzwerk.mkdir();
                        String abspathNetzwerk = fileNetzwerk.getAbsolutePath() + "/";
                        exporter.netzwerkGraph(mf, abspathNetzwerk, "Netzwerkgraph", quelle, senke, g);
                        this.updateProgress(listofTasks.indexOf("Netzwerk") + 1, numberofTasks);
                    }

                }

                return true;
            }
        };
    }
}
