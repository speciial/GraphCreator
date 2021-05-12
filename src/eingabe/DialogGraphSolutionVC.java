/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eingabe;

import com.mxgraph.swing.mxGraphComponent;
import java.util.List;
import java.util.Optional;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jgrapht.GraphPath;

/**
 *
 * @author lennaertn
 */
//klasse die das Handeln der Visualisierung der Algorithmen übernimmt, erstellt die Dialogfenster die hierfür nötig sind
public class DialogGraphSolutionVC {

    private SwingNode frameNode;
    private JPanel graphpanel;

    public DialogGraphSolutionVC() {
        this.frameNode = new SwingNode();
        this.graphpanel = new JPanel();
    }

    //funktion um Swingkomponenten in JavaFx einzubetten
    private void createSwingContent(final SwingNode swingNode, JPanel panel) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                swingNode.setContent(panel);

            }
        });
    }

    //Dialogfenster um den Dijkstra Algorithmus zu visualisieren
    public boolean createSolveDialogDijkstra(mxGraphComponent mxGraphOutput, Pair<String, String> nodes, GraphPath path) {

        this.graphpanel.removeAll();
        graphpanel.add(mxGraphOutput);
        this.createSwingContent(frameNode, graphpanel);
        String nodePath = this.buildPath(path.getVertexList());
        int tmpweight = (int) path.getWeight();
        String weight = Integer.toString(tmpweight);
        Dialog nodeDialog = new Dialog<>();

        nodeDialog.setTitle("Visualisierung der Lösung");
        nodeDialog.setHeaderText("Visualisierung des kürzesten Weges durch den Dijkstra-Algorithmus zwischen den Knoten "
                + "" + nodes.getKey() + " und " + nodes.getValue() + System.lineSeparator() + nodePath + System.lineSeparator() + "Die Gesamtkosten für diesen Weg betragen: " + weight);
        ButtonType commitButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        nodeDialog.getDialogPane().getButtonTypes().addAll(commitButtonType, ButtonType.CANCEL);

        GridPane gp = new GridPane();
        gp.setVgap(20);
        gp.setHgap(100);
        gp.add(frameNode, 1, 1);
        AnchorPane pane = new AnchorPane();

        pane.setPrefSize(900, 730);
        pane.getChildren().add(gp);

        nodeDialog.getDialogPane().setContent(pane);

        nodeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == commitButtonType) {

                return true;
            }
            return null;

        });
        Optional result = nodeDialog.showAndWait();

        if (result.isPresent()) {
            return true;
        }
        return false;

    }
    //Kürzesten Weg als String zusammen bauen
    private String buildPath(List<String> path) {
        String result = "Knotensequenz:";

        for (int i = 0; i < path.size(); i++) {
            String s = path.get(i);
            if (i != path.size() - 1) {
                result += " " + s + " -> ";
            } else {
                result += " " + s + ".";
            }

        }
        return result;
    }
    //Dialogfenster für Maxflow Algorithmus
    public boolean createSolveDialogNetwork(mxGraphComponent mxGraphOutput, Pair<String, String> nodes, int maxflow) {

        this.graphpanel.removeAll();
        graphpanel.add(mxGraphOutput);
        this.createSwingContent(frameNode, graphpanel);

        Dialog nodeDialog = new Dialog<>();

        nodeDialog.setTitle("Visualisierung der Lösung");
        nodeDialog.setHeaderText("Visualisierung des Maximalen Flusses zwischen "
                + "" + nodes.getKey() + " und " + nodes.getValue() + System.lineSeparator() + "Der maximale Fluß beträgt " + Integer.toString(maxflow));
        ButtonType commitButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        nodeDialog.getDialogPane().getButtonTypes().addAll(commitButtonType, ButtonType.CANCEL);

        AnchorPane pane = new AnchorPane();

        pane.setPrefSize(900, 730);

        pane.getChildren().add(frameNode);

        nodeDialog.getDialogPane().setContent(pane);

        nodeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == commitButtonType) {

                return true;
            }
            return null;

        });
        Optional result = nodeDialog.showAndWait();

        if (result.isPresent()) {
            return true;
        }
        return false;

    }
}
