/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgraph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;
import jgraph.JGraphAdapter.MyEdge;
import model.DataBean;
import model.mxGraphWithPath;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm.MaximumFlow;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 *
 * @author lennaertn
 */
//Klasse die für das Visualisierung der Graphen zuständig ist
public class jGraphVisualisationHandler {

    //hilfsfunktion mittels der ein JgraphT Graph über einen Adapter visualisiert wird
    mxGraphComponent visualizeGraph(Graph g, boolean directed, JGraphAdapter jGraphAdapter, int number) {
        int sizeOfGeometry = this.sizeOfNodes(number);
        jGraphAdapter.graphAdapter = new JGraphXAdapter<>(g);
        mxGraphComponent graphComponent = new mxGraphComponent(jGraphAdapter.graphAdapter);
        jGraphAdapter.graphAdapter.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);

        mxGraphModel graphModel = (mxGraphModel) graphComponent.getGraph().getModel();
        graphComponent.getGraph().setCellsResizable(false);
        graphComponent.setConnectable(false);
        //wichtig für die exception drag & drop in progress
        graphComponent.setDragEnabled(false);

        graphModel.addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener() {

            @Override
            public void invoke(Object sender, mxEventObject evt) {
                evt.consume();

                new mxParallelEdgeLayout(jGraphAdapter.graphAdapter).execute(jGraphAdapter.graphAdapter.getDefaultParent());
                jGraphAdapter.graphAdapter.refresh();

            }

        });

        graphComponent.getGraph().setAllowDanglingEdges(false);
        // keine pfeile bei ungerichteten Graphen
        if (!directed) {
            jGraphAdapter.graphAdapter.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);

        }

        jGraphAdapter.graphAdapter.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
        jGraphAdapter.graphAdapter.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);

        //visualisierungs Eigenschaften bestimmen
        jGraphAdapter.graphAdapter.getModel().beginUpdate();
        try {
            jGraphAdapter.graphAdapter.clearSelection();
            Collection<Object> cells = graphModel.getCells().values();

            for (Object c : cells) {
                mxCell cell = (mxCell) c;
                mxGeometry geometry = cell.getGeometry();

                if (cell.isVertex()) {
                    geometry.setWidth(sizeOfGeometry);
                    geometry.setHeight(sizeOfGeometry);
                    cell.setConnectable(false);
                    graphModel.setStyle(cell, "editable=0");

                }
                if (cell.isEdge()) {
                    cell.setConnectable(false);

                }

            }
        } finally {
            jGraphAdapter.graphAdapter.getModel().endUpdate();
        }

        mxCircleLayout circle = new mxCircleLayout(jGraphAdapter.graphAdapter);
        circle.setRadius(330);
        circle.execute(jGraphAdapter.graphAdapter.getDefaultParent());

        new mxParallelEdgeLayout(jGraphAdapter.graphAdapter).execute(jGraphAdapter.graphAdapter.getDefaultParent());

        return graphComponent;
    }

    //Graph nach der Visualisierung eines Algorithmus zurückfärben
    public void colorBackGraph(mxGraphComponent mxGraph) {
        mxGraph graph = new mxGraph(mxGraph.getGraph().getModel());
        Object[] cells = graph.getChildCells(graph.getDefaultParent());
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#6482B9", cells);
        graph.refresh();
    }

    //funktion um den kürzesten Weg innerhalb eines Graphen zu visualisieren 
    public mxGraphWithPath shortestPathDijkstra(DataBean bean, Pair pair, mxGraphComponent mxGraph, boolean directed, JGraphAdapter jGraphAdapter, Graph jgraph) {
        ListenableGraph<String, JGraphAdapter.MyEdge> g = new DefaultListenableGraph<>(new DefaultUndirectedWeightedGraph<>(JGraphAdapter.MyEdge.class));
        JGraphXAdapter<String, JGraphAdapter.MyEdge> graphAdapter = new JGraphXAdapter(g);
        try {
            GraphPath path = jGraphAdapter.getjGraphAlgoHandler().dijkstraPath(pair, jgraph);

            if (path != null) {
                List<String> shortestPath = path.getVertexList();
                graphAdapter.setStylesheet(jGraphAdapter.graphAdapter.getStylesheet());
                graphAdapter.addCells(jGraphAdapter.graphAdapter.cloneCells(jGraphAdapter.graphAdapter.getChildCells(jGraphAdapter.graphAdapter.getDefaultParent())));
                mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
                graphComponent.getGraph().setCellsResizable(false);
                graphComponent.setConnectable(false);
                //wichtig für die exception drag & drop in progress
                graphComponent.setDragEnabled(false);
                graphComponent.getGraph().setAllowDanglingEdges(false);
                mxGraph graph = new mxGraph(mxGraph.getGraph().getModel());
                graphAdapter.setModel(graph.getModel());
                Object[] cellsEdges = graph.getChildCells(graph.getDefaultParent(), false, true);
                Object[] cellstoColor = new Object[shortestPath.size()];
                for (int j = 0; j < shortestPath.size() - 1; j++) {
                    for (int i = 0; i < cellsEdges.length; i++) {
                        mxCell c = (mxCell) cellsEdges[i];
                        String source = c.getSource().getValue().toString();
                        String target = c.getTarget().getValue().toString();
                        if (directed) {
                            if (source.equals(shortestPath.get(j)) && target.equals(shortestPath.get(j + 1))) {
                                cellstoColor[j] = c;
                            }
                        } else {
                            if ((source.equals(shortestPath.get(j + 1)) && target.equals(shortestPath.get(j))) || (source.equals(shortestPath.get(j)) && target.equals(shortestPath.get(j + 1)))) {
                                cellstoColor[j] = c;
                            }
                        }
                    }
                }
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", cellstoColor);
                graph.refresh();
                graph.repaint();
                mxGraphWithPath output = new mxGraphWithPath(graphComponent, path);
                return output;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    //zeigt den maximalen Fluss in einem Graphen an
    public mxGraphComponent netzWerkGraph(JGraphAdapter jGraphAdapter, boolean directed, Pair<MaximumFlow, Integer> mf, mxGraphComponent mxGraph) {
        ListenableGraph<String, JGraphAdapter.MyEdge> g = new DefaultListenableGraph<>(new DefaultUndirectedWeightedGraph<>(JGraphAdapter.MyEdge.class));
        JGraphXAdapter<String, JGraphAdapter.MyEdge> graphAdapter = new JGraphXAdapter(g);

        if (mf.getKey() != null) {
            graphAdapter.setStylesheet(jGraphAdapter.graphAdapter.getStylesheet());
            graphAdapter.addCells(jGraphAdapter.graphAdapter.cloneCells(jGraphAdapter.graphAdapter.getChildCells(jGraphAdapter.graphAdapter.getDefaultParent())));
            mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
            graphComponent.setConnectable(false);
            //wichtig für die exception drag & drop in progress
            graphComponent.setDragEnabled(false);
            graphComponent.getGraph().setAllowDanglingEdges(false);
            graphComponent.getGraph().setCellsResizable(false);
            mxGraph graph = new mxGraph(mxGraph.getGraph().getModel());

            graphAdapter.setModel(graph.getModel());

            Map mp = mf.getKey().getFlowMap();
            // value= flow
            // key = kantengewicht
            List<NetzwerkKante> edges = new ArrayList<>();

            Iterator it = mp.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                NetzwerkKante e = new NetzwerkKante();
                MyEdge key = (MyEdge) pair.getKey();
                double dflow = (double) pair.getValue();
                int flow = (int) dflow;
                e.setsrc(key.getSource());
                e.settrg(key.getTarget());
                e.setFlow(Integer.toString(flow));
                e.setWeightCustom(pair.getKey().toString());
                edges.add(e);

            }

            Object[] cellsEdges = graph.getChildCells(graph.getDefaultParent(), false, true);

            Object[] cellstoColor = new Object[cellsEdges.length];
            graph.getModel().beginUpdate();
            try {
                for (int i = 0; i < cellsEdges.length; i++) {
                    mxCell edge = (mxCell) cellsEdges[i];
                    for (NetzwerkKante nedge : edges) {
                        if (directed) {
                            if (edge.getSource().getValue().equals(nedge.getsrc()) && edge.getTarget().getValue().equals(nedge.gettrg())) {
                                graphAdapter.getModel().setValue(edge, nedge.toString());
                                if (Integer.parseInt(nedge.getFlow()) > 0) {

                                    cellstoColor[i] = edge;
                                }
                            }

                        } else if (!directed) {
                            if ((edge.getSource().getValue().equals(nedge.getsrc()) && edge.getTarget().getValue().equals(nedge.gettrg()))
                                    || (edge.getSource().getValue().equals(nedge.gettrg()) && edge.getTarget().getValue().equals(nedge.getsrc()))) {
                                graphAdapter.getModel().setValue(edge, nedge.toString());
                                if (Integer.parseInt(nedge.getFlow()) > 0) {

                                    cellstoColor[i] = edge;
                                }
                            }

                        }
                    }
                }

                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", cellstoColor);
            } finally {
                graph.getModel().endUpdate();

            }

            return graphComponent;

        } else {
            return null;
        }
    }

    //Kanten nachdem Netzwerk angezeigt wurde wieder zurückändern 
    public void resetGraphNetwork(mxGraphComponent mxGraph) {
        mxGraph graph = new mxGraph(mxGraph.getGraph().getModel());
        Object[] edges = graph.getChildCells(graph.getDefaultParent(), false, true);

        graph.getModel().beginUpdate();

        try {
            for (int i = 0; i < edges.length; i++) {
                mxCell edge = (mxCell) edges[i];
                String val = edge.getValue().toString();
                String[] parts = val.split("/");
                if (parts.length > 1) {
                    graph.getModel().setValue(edge, parts[1]);
                }

            }
        } finally {
            graph.getModel().endUpdate();

            graph.refresh();
        }

    }
    //Für das erstellen der png datei des minimalen Spannbaums
    public mxGraphComponent minSpannTree(DataBean bean, mxGraphComponent mxGraph, JGraphAdapter jGraphAdapter) {
        ListenableGraph<String, MyEdge> g = new DefaultListenableGraph<>(new DefaultUndirectedWeightedGraph<>(MyEdge.class
        ));
        JGraphXAdapter<String, MyEdge> graphAdapter = new JGraphXAdapter(g);
        Set<MyEdge> vertexSet = jGraphAdapter.getjGraphAlgoHandler().kruskalMinSpan(bean.getAgraph().getGraph());

        if (vertexSet != null) {
            int size = vertexSet.size();
            graphAdapter.setStylesheet(jGraphAdapter.graphAdapter.getStylesheet());
            graphAdapter.addCells(jGraphAdapter.graphAdapter.cloneCells(jGraphAdapter.graphAdapter.getChildCells(jGraphAdapter.graphAdapter.getDefaultParent())));
            mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
            graphComponent.setConnectable(false);
            //wichtig für die exception drag & drop in progress
            graphComponent.setDragEnabled(false);
            graphComponent.getGraph().setCellsResizable(false);
            graphComponent.getGraph().setAllowDanglingEdges(false);
            mxGraph graph = new mxGraph(mxGraph.getGraph().getModel());
            graphAdapter.setModel(graph.getModel());
            Object[] cellsEdges = graph.getChildCells(graph.getDefaultParent(), false, true);
            Object[] cellstoColor = new Object[size];
            int count = 0;
            Iterator itr = vertexSet.iterator();
            while (itr.hasNext()) {
                MyEdge e = (MyEdge) itr.next();

                for (int i = 0; i < cellsEdges.length; i++) {
                    mxCell c = (mxCell) cellsEdges[i];
                    String source = c.getSource().getValue().toString();
                    String target = c.getTarget().getValue().toString();
                    if (source.equals(e.getSource()) && target.equals(e.getTarget()) || source.equals(e.getTarget()) && target.equals(e.getSource())) {
                        cellstoColor[count] = c;
                    }
                }
                count++;
            }

            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "red", cellstoColor);
            graph.refresh();
            return graphComponent;
        } else {
            return null;
        }
    }
    // Die Größe der Knoten in der Visualisierung des Graphen verändert sich je nach Menge der Knoten
    private int sizeOfNodes(int number) {
        if (number <= 10) {
            return 45;
        } else if (number > 10 && number <= 15) {
            return 35;
        } else {
            return 25;

        }
    }
    //statische Klasse um Netzwerkgraphen zu halten
    public static class NetzwerkKante extends DefaultWeightedEdge {

        private String flow = "";
        private String weight = "";
        private String src = "";
        private String trg = "";

        public NetzwerkKante() {

        }

        public String getsrc() {
            return this.src;
        }

        public String gettrg() {
            return this.trg;
        }

        public void setsrc(String src) {
            this.src = src;
        }

        public void settrg(String trg) {
            this.trg = trg;
        }

        public String getFlow() {
            return this.flow;
        }

        public void setFlow(String flow) {
            this.flow = flow;
        }

        public String getWeightCustom() {
            return this.weight;
        }

        public void setWeightCustom(String weight) {
            this.weight = weight;
        }

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

            return this.flow + "/" + this.weight;
        }

    }

}
