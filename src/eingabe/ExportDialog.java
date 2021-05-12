/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eingabe;

import java.io.File;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;

/**
 *
 * @author lennaertn
 */
//klassen die die Dialogfenster aufbaut, die zum exportieren nötig sind
public class ExportDialog {
    //Dialogfenster für Graphen ohne Visualisierung
    public ExportInputsWOVis createExportDialogWOVis(int count, boolean undirected){
        
          Dialog<ExportInputsWOVis> newGraphDialog = new Dialog<>();
        ObservableList<String> choices = FXCollections.observableArrayList();
        for (int i = 0; i < count; i++) {
            choices.add(Integer.toString(i));
        }

        GridPane grid = new GridPane();

        newGraphDialog.setTitle("Graphen Exportieren");
        newGraphDialog.setHeaderText("Füllen sie die untenstehenden Felder aus und Bearbeiten Sie anschließend den Graphen");
        ButtonType nextButtonType = new ButtonType("Commit", ButtonBar.ButtonData.OK_DONE);

        newGraphDialog.getDialogPane().getButtonTypes().addAll(nextButtonType, ButtonType.CANCEL);
        newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(true);
        VBox content = new VBox(40);
        HBox pathBox = new HBox(25);
        HBox bfordBox = new HBox(20);
        HBox tsuche  = new HBox(20);
        HBox bsuche = new HBox(20);
          HBox netz   = new HBox(20);
        Label path = new Label("Wählen Sie ein Verzeichnis, indem das Projekt gespeichert werden soll");

        Label pathset = new Label("Bitte gültigen Pfad auswählen");
        pathset.setTextFill(Color.RED);
        Button choose = new Button("Verzeichnis wählen");
        TextField dirname = new TextField("GraphProject");
        dirname.setVisible(false);
        choose.setOnAction((ActionEvent e) -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDir = directoryChooser.showDialog(null);
            if (selectedDir != null) {
                path.setText(selectedDir.getAbsolutePath());
                newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(false);
                pathset.setVisible(false);
                dirname.setVisible(true);
            }

        });
       
        CheckBox dotFormat = new CheckBox("Graph im DOT Format");
        dotFormat.setSelected(true);
        CheckBox graphFormat = new CheckBox("Graph im .Graph Format");
        graphFormat.setSelected(true);
        CheckBox bellmanFord = new CheckBox("Bellman-Ford-Algorithmus anwenden");
        bellmanFord.setSelected(false);
        Label startv = new Label("Startvertex");
        startv.setVisible(false);
        ChoiceBox startvertex = new ChoiceBox(choices);
        startvertex.setValue("0");
        startvertex.setVisible(false);
        bellmanFord.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            startvertex.setVisible(newValue);
            startv.setVisible(newValue);
        });

        CheckBox minspantree = new CheckBox("Minimaler Spannbaum ");
        minspantree.setVisible(undirected);
        
        Label startvTSuche = new Label("Startvertex");
        startvTSuche.setVisible(false);
        CheckBox tsucheBox = new CheckBox("Tiefensuche anwenden");
        tsucheBox.setSelected(false);
        ChoiceBox vertexTSuche = new ChoiceBox(choices);
        vertexTSuche.setVisible(false);
        vertexTSuche.setValue("0");
        tsucheBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            vertexTSuche.setVisible(newValue);
            startvTSuche.setVisible(newValue);
        });
        
        
        Label lbsuche = new Label("Startvertex");
        lbsuche.setVisible(false);
        CheckBox cbbsuche = new CheckBox("Breitensuche anwenden");
        cbbsuche.setSelected(false);
        ChoiceBox choicebsuche = new ChoiceBox(choices);
        choicebsuche.setVisible(false);
        choicebsuche.setValue("0");
         cbbsuche.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            lbsuche.setVisible(newValue);
            choicebsuche.setVisible(newValue);
        });
         
         
          CheckBox netzwerk = new CheckBox("Netzwerkgraph berechnen(funktioniert nur bei nicht negativen Kantengewichten)");
        Label senke    = new Label("Senke");
        senke.setVisible(false);
        Label quelle   = new Label("Quelle");
        quelle.setVisible(false);
        
        ChoiceBox cbquelle = new ChoiceBox(choices);
        cbquelle.setValue("0");
        cbquelle.setVisible(false);
        ChoiceBox cbsenke  = new ChoiceBox(choices);
        cbsenke.setValue("1");
        cbsenke.setVisible(false);
        
        netzwerk.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            cbquelle.setVisible(newValue);
            cbsenke.setVisible(newValue);
            senke.setVisible(newValue);
            quelle.setVisible(newValue);
        });
        
        netz.getChildren().add(netzwerk);
        netz.getChildren().add(quelle);
        netz.getChildren().add(cbquelle);
        netz.getChildren().add(senke);
        netz.getChildren().add(cbsenke);
        
        bfordBox.getChildren().add(bellmanFord);
        bfordBox.getChildren().add(startv);
        bfordBox.getChildren().add(startvertex);
        
        pathBox.getChildren().add(path);
        pathBox.getChildren().add(dirname);
        pathBox.getChildren().add(choose);
        
        tsuche.getChildren().add(tsucheBox);
        tsuche.getChildren().add(startvTSuche);
        tsuche.getChildren().add(vertexTSuche);
        
        bsuche.getChildren().add(cbbsuche);
        bsuche.getChildren().add(lbsuche);
        bsuche.getChildren().add(choicebsuche);
        
        content.getChildren().add(pathBox);
        content.getChildren().add(dotFormat);
        content.getChildren().add(graphFormat);
        content.getChildren().add(tsuche);
        content.getChildren().add(bsuche);
        content.getChildren().add(bfordBox);
        content.getChildren().add(netz);
        if (undirected) {
            content.getChildren().add(minspantree);
        }

        content.getChildren().add(pathset);

        grid.add(content, 0, 0);
        newGraphDialog.getDialogPane().setContent(grid);

        newGraphDialog.setResultConverter(dialogButton -> {
            if (dialogButton == nextButtonType) {
                Pair<Boolean, String> bellmanPair;
                Pair<Boolean, String> tiefensuchePair;
                Pair<Boolean, String> breitensuchePair;
                Pair<Boolean, Pair<String, String >> netzwerkPair;
                bellmanPair = checkBellmanFord(bellmanFord, startvertex);  
                tiefensuchePair = checkTiefenSuche(tsucheBox, vertexTSuche);
                breitensuchePair = checkBreitenSuche(cbbsuche,choicebsuche);
                netzwerkPair = checkNetzwerk(netzwerk,cbquelle, cbsenke);
                
                boolean kruskal = false;
                if (undirected && minspantree.isSelected()) {
                    kruskal = true;
                }
                newGraphDialog.close();
                return new ExportInputsWOVis(path.getText(),dirname.getText(),dotFormat.isSelected(),graphFormat.isSelected(), bellmanPair,count,kruskal, tiefensuchePair, breitensuchePair, netzwerkPair );
                
              

            }

            return null;
        });
        Optional<ExportInputsWOVis> result = newGraphDialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }
    // Dialogfenster für das Exportieren von Graphen mit visualisierung
    public ExportInputs createExportDialog(int count, boolean undirected) {
        Dialog<ExportInputs> newGraphDialog = new Dialog<>();
        ObservableList<String> choices = FXCollections.observableArrayList();
        for (int i = 0; i < count; i++) {
            choices.add(Integer.toString(i));
        }

        GridPane grid = new GridPane();

        newGraphDialog.setTitle("Graphen Exportieren");
        newGraphDialog.setHeaderText("Füllen sie die untenstehenden Felder aus und Bearbeiten Sie anschließend den Graphen");
        ButtonType nextButtonType = new ButtonType("Commit", ButtonBar.ButtonData.OK_DONE);

        newGraphDialog.getDialogPane().getButtonTypes().addAll(nextButtonType, ButtonType.CANCEL);
        newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(true);
        VBox content = new VBox(40);
        HBox pathBox = new HBox(25);
        HBox bfordBox = new HBox(20);
        HBox tsuche  = new HBox(20);
        HBox bsuche = new HBox(20);
        HBox netz   = new HBox(20);
        Label path = new Label("Wählen Sie ein Verzeichnis, indem das Projekt gespeichert werden soll");

        Label pathset = new Label("Bitte gültigen Pfad auswählen");
        pathset.setTextFill(Color.RED);
        Button choose = new Button("Verzeichnis wählen");
        TextField dirname = new TextField("GraphProject");
        dirname.setVisible(false);
        choose.setOnAction((ActionEvent e) -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDir = directoryChooser.showDialog(null);
            if (selectedDir != null) {
                path.setText(selectedDir.getAbsolutePath());
                newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(false);
                pathset.setVisible(false);
                dirname.setVisible(true);
            }

        });
        CheckBox picture = new CheckBox("Visualisierung des Graphen exportieren");
        picture.setSelected(true);
        CheckBox dotFormat = new CheckBox("Graph im DOT Format");
        dotFormat.setSelected(true);
        CheckBox graphFormat = new CheckBox("Graph im .Graph Format");
        graphFormat.setSelected(true);
        CheckBox bellmanFord = new CheckBox("Bellman-Ford-Algorithmus anwenden");
        bellmanFord.setSelected(false);
        Label startv = new Label("Startvertex");
        startv.setVisible(false);
        ChoiceBox startvertex = new ChoiceBox(choices);
        startvertex.setValue("0");
        startvertex.setVisible(false);
        bellmanFord.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            startvertex.setVisible(newValue);
            startv.setVisible(newValue);
        });

        CheckBox minspantree = new CheckBox("Minimaler Spannbaum ");
        minspantree.setVisible(undirected);
        
        Label startvTSuche = new Label("Startvertex");
        startvTSuche.setVisible(false);
        CheckBox tsucheBox = new CheckBox("Tiefensuche anwenden");
        tsucheBox.setSelected(false);
        ChoiceBox vertexTSuche = new ChoiceBox(choices);
        vertexTSuche.setVisible(false);
        vertexTSuche.setValue("0");
        tsucheBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            vertexTSuche.setVisible(newValue);
            startvTSuche.setVisible(newValue);
        });
        
        CheckBox netzwerk = new CheckBox("Netzwerkgraph berechnen(funktioniert nur bei nicht negativen Kantengewichten)");
        Label senke    = new Label("Senke");
        senke.setVisible(false);
        Label quelle   = new Label("Quelle");
        quelle.setVisible(false);
        
        ChoiceBox cbquelle = new ChoiceBox(choices);
        cbquelle.setValue("0");
        cbquelle.setVisible(false);
        ChoiceBox cbsenke  = new ChoiceBox(choices);
        cbsenke.setValue("1");
        cbsenke.setVisible(false);
        
        netzwerk.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            cbquelle.setVisible(newValue);
            cbsenke.setVisible(newValue);
            senke.setVisible(newValue);
            quelle.setVisible(newValue);
        });
        
        Label lbsuche = new Label("Startvertex");
        lbsuche.setVisible(false);
        CheckBox cbbsuche = new CheckBox("Breitensuche anwenden");
        cbbsuche.setSelected(false);
        ChoiceBox choicebsuche = new ChoiceBox(choices);
        choicebsuche.setVisible(false);
        choicebsuche.setValue("0");
         cbbsuche.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            lbsuche.setVisible(newValue);
            choicebsuche.setVisible(newValue);
        });
        
        netz.getChildren().add(netzwerk);
        netz.getChildren().add(quelle);
        netz.getChildren().add(cbquelle);
        netz.getChildren().add(senke);
        netz.getChildren().add(cbsenke);
             
        bfordBox.getChildren().add(bellmanFord);
        bfordBox.getChildren().add(startv);
        bfordBox.getChildren().add(startvertex);
        
        pathBox.getChildren().add(path);
        pathBox.getChildren().add(dirname);
        pathBox.getChildren().add(choose);
        
        tsuche.getChildren().add(tsucheBox);
        tsuche.getChildren().add(startvTSuche);
        tsuche.getChildren().add(vertexTSuche);
        
        bsuche.getChildren().add(cbbsuche);
        bsuche.getChildren().add(lbsuche);
        bsuche.getChildren().add(choicebsuche);
        
        content.getChildren().add(pathBox);
        content.getChildren().add(picture);
        content.getChildren().add(dotFormat);
        content.getChildren().add(graphFormat);
        content.getChildren().add(tsuche);
        content.getChildren().add(bsuche);
        content.getChildren().add(bfordBox);
        content.getChildren().add(netz);
        if (undirected) {
            content.getChildren().add(minspantree);
        }

        content.getChildren().add(pathset);

        grid.add(content, 0, 0);
        newGraphDialog.getDialogPane().setContent(grid);

        newGraphDialog.setResultConverter(dialogButton -> {
            if (dialogButton == nextButtonType) {
                Pair<Boolean, String> bellmanPair;
                Pair<Boolean, String> tiefensuchePair;
                Pair<Boolean, String> breitensuchePair;
                Pair<Boolean, Pair<String,String>> netzwerkPair;
                bellmanPair = checkBellmanFord(bellmanFord, startvertex);  
                tiefensuchePair = checkTiefenSuche(tsucheBox, vertexTSuche);
                breitensuchePair = checkBreitenSuche(cbbsuche,choicebsuche);
                netzwerkPair = checkNetzwerk(netzwerk,cbquelle, cbsenke);
                
                boolean kruskal = false;
                if (undirected && minspantree.isSelected()) {
                    kruskal = true;
                }
                
                return new ExportInputs(path.getText(), dirname.getText(), picture.isSelected(), dotFormat.isSelected(), 
                        graphFormat.isSelected(), bellmanPair, kruskal,tiefensuchePair, breitensuchePair, netzwerkPair);

            }

            return null;
        });
        Optional<ExportInputs> result = newGraphDialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;

    }
    //hilfsfunktionem um den Inhalt der Checkboxen abzufragen
    private Pair<Boolean, String> checkTiefenSuche(CheckBox tsucheBox, ChoiceBox vertexTSuche) {
        Pair<Boolean, String> tiefensuchePair;
        if(tsucheBox.isSelected()){
            tiefensuchePair = new Pair(true,vertexTSuche.getValue().toString());
        }
        else{
            tiefensuchePair = new Pair(false, "");
        }
        return tiefensuchePair;
    }

    private Pair<Boolean, String> checkBellmanFord(CheckBox bellmanFord, ChoiceBox startvertex) {
        Pair<Boolean, String> bellmanPair;
        if (bellmanFord.isSelected()) {
            bellmanPair = new Pair(true, startvertex.getValue().toString());
        } else {
            bellmanPair = new Pair(false, "");
        }
        return bellmanPair;
    }

    private Pair<Boolean, String> checkBreitenSuche(CheckBox cbbsuche, ChoiceBox choicebsuche) {
         Pair<Boolean, String> breitensuchePair;
        if(cbbsuche.isSelected()){
            breitensuchePair = new Pair(true,choicebsuche.getValue().toString());
        }
        else{
            breitensuchePair = new Pair(false, "");
        }
        return breitensuchePair;
    }
    
    private Pair<Boolean, Pair<String,String>> checkNetzwerk(CheckBox netz, ChoiceBox quelle, ChoiceBox senke){
         Pair<Boolean, Pair<String,String>> netzwerkPair;
         
         if(netz.isSelected()){
             netzwerkPair = new Pair(true,new Pair(quelle.getValue().toString(),senke.getValue().toString()));
         }
         else{
             netzwerkPair = new Pair(false, null);
         }
         return netzwerkPair;
    }
    
    

}
