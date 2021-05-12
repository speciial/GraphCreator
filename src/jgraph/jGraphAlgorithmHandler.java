/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm.MaximumFlow;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.NegativeCycleDetectedException;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

/**
 *
 * @author lennaertn
 */
//Klasse, die Algorithmen ausführt
public class jGraphAlgorithmHandler {
    //bellmanFord Algorithmus
    public ShortestPathAlgorithm.SingleSourcePaths bellmanFord(Graph aGraph, String startvertex) {
        BellmanFordShortestPath bfsp = new BellmanFordShortestPath(aGraph);
        try {
            ShortestPathAlgorithm.SingleSourcePaths paths = bfsp.getPaths(startvertex);
            return paths;
        } catch (NegativeCycleDetectedException e) {
            return null;
        }

    }
    //Minimaler Spannbaum nach Kruskal
    public Set<JGraphAdapter.MyEdge> kruskalMinSpan(Graph aGraph) {
        KruskalMinimumSpanningTree kruskal = new KruskalMinimumSpanningTree(aGraph);
        Set<JGraphAdapter.MyEdge> edgeSet = kruskal.getSpanningTree().getEdges();
        return edgeSet;
    }
    //kürzester Weg nach Dijkstra
    public GraphPath dijkstraPath(Pair pair, Graph aGraph) {
        DijkstraShortestPath dijkstra = new DijkstraShortestPath(aGraph);

        GraphPath path = dijkstra.getPath(pair.getKey().toString(), pair.getValue().toString());
        return path;
    }
    //tiefensuche
    public List<String> tiefenSuche(Graph aGraph, String startvertex) {
        DepthFirstIterator itr = new DepthFirstIterator(aGraph, startvertex);
        List<String> vertexList = new ArrayList<>();
        while (itr.hasNext()) {
            vertexList.add(itr.next().toString());

        }
        return vertexList;
    }
    //breitensuche
    public List<String> breitenSuche(Graph aGraph, String startvertex) {
        BreadthFirstIterator itr = new BreadthFirstIterator(aGraph);

        List<String> vertexList = new ArrayList<>();
        while (itr.hasNext()) {
            vertexList.add(itr.next().toString());

        }
        return vertexList;
    }
    // Max-Flow nach Edmonds Karp
    public Pair<MaximumFlow, Integer> maxFlowEdmondsKarp(Graph g, String quelle, String senke) {
        if (quelle.equals(senke)) {
            this.erorrNetzwerkQuelleGleichSenke();
            return null;
        } else {
            EdmondsKarpMFImpl maxflow = new EdmondsKarpMFImpl(g);
            double totalflow = maxflow.calculateMaximumFlow(quelle, senke);
            int totflow = (int) totalflow;
            MaximumFlow mf = maxflow.getMaximumFlow(quelle, senke);
            return new Pair(mf, totflow);
        }

    }
    // Fehlermeldung, falls Quelle = Senke
    public void erorrNetzwerkQuelleGleichSenke() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Quelle entspricht Senke. Die Datei wurde nicht exportiert."
                + System.lineSeparator() + "Die restlichen Dateien werden erstellt.", ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            alert.close();
        }
    }

}
