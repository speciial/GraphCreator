/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eingabe;

import java.io.IOException;
import java.util.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import model.GridPaneWithAdjacencList;
import model.GridPaneWithMatrixList;

/**
 *
 * @author lennaertn
 */
// Klasse, die für das Handeln aller Dialogfenster innerhalb der Anwendung zuständig ist
public class DialogEingabeVC {

    private ViewController vc;
    private ArrayList<ArrayList<TextField>> textfieldList = new ArrayList<>();

    @FXML
    private Button closeButton;
    private GridPane gp;

    //Funktion, die den Dialog zum Bestimmen des kürzesten Weges innerhalb eines Graphen bereitstellt
    public Pair<String, String> createSolveDialog(int count, String header, String title, boolean network) {

        ObservableList<String> choices = FXCollections.observableArrayList();
        for (int i = 0; i < count; i++) {
            choices.add(Integer.toString(i));
        }

        ChoiceBox<String> cb1 = new ChoiceBox(choices);
        ChoiceBox<String> cb2 = new ChoiceBox(choices);

        Dialog<Pair<String, String>> nodeDialog = new Dialog<>();

        nodeDialog.setTitle(title);
        nodeDialog.setHeaderText(header);
        ButtonType commitButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        nodeDialog.getDialogPane().getButtonTypes().addAll(commitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        if (network) {
            grid.add(new Label("Quelle"), 0, 0);
            grid.add(new Label("Senke"), 0, 1);
        } else {
            grid.add(new Label("Startknoten"), 0, 0);
            grid.add(new Label("Zielknoten"), 0, 1);
        }
        grid.add(cb1, 1, 0);

        grid.add(cb2, 1, 1);

        nodeDialog.getDialogPane().setContent(grid);

        nodeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == commitButtonType) {

                return new Pair<>(cb1.getValue().toString(), cb2.getValue().toString());
            }

            return null;
        });
        Optional<Pair<String, String>> result = nodeDialog.showAndWait();

        if (result.isPresent()) {
            return result.get();
        }
        return null;

    }

    public String createStartvertexDialog(int count) {
        ObservableList<String> choices = FXCollections.observableArrayList();
        for (int i = 0; i < count; i++) {
            choices.add(Integer.toString(i));
        }

        ChoiceBox cb1 = new ChoiceBox(choices);
        Dialog<String> nodeDialog = new Dialog<>();

        nodeDialog.setTitle("Lösen des Graphen");
        nodeDialog.setHeaderText("Startvertex angeben, von dem die Breiten/Tiefensuche starten soll.");
        ButtonType commitButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        nodeDialog.getDialogPane().getButtonTypes().addAll(commitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Startvertex"), 0, 0);
        grid.add(cb1, 1, 0);
        nodeDialog.getDialogPane().setContent(grid);

        nodeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == commitButtonType) {

                return cb1.getValue().toString();
            }

            return "";
        });
        Optional<String> result = nodeDialog.showAndWait();

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    //Dialog zum Erstellen eines neuen Graphen 
    public DialogInputs createNewGraphDialog() throws IOException {

        Dialog<DialogInputs> newGraphDialog = new Dialog<>();

        GridPane grid = new GridPane();
        this.gp = new GridPane();
        newGraphDialog.setTitle("Neuen Graphen erstellen");
        newGraphDialog.setHeaderText("Füllen sie die untenstehenden Felder aus und Bearbeiten Sie anschließend den Graphen");
        ButtonType nextButtonType = new ButtonType("Commit", ButtonBar.ButtonData.OK_DONE);

        newGraphDialog.getDialogPane().getButtonTypes().addAll(nextButtonType, ButtonType.CANCEL);
        TextField fromRange = new TextField("1");
        TextField toRange = new TextField("100");
        Label fromLabel = new Label("Min Kantengewicht: ");
        Label toLabel = new Label("Max Kantengewicht: ");
        fromLabel.setVisible(false);
        toLabel.setVisible(false);
        fromRange.setVisible(false);
        toRange.setVisible(false);
        fromRange.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("-?\\d*")) {
                fromRange.setText(oldValue);
            }
            if (newValue.matches("-?\\d*")) {
                try {
                    int val = Integer.parseInt(newValue);
                    if (val >= Integer.parseInt(toRange.getText())) {
                        toRange.setText(Integer.toString(val + 1));

                    }
                } catch (NumberFormatException e) {

                }

            }

        });

        // bei leerem textfeld 0 einsetzen 
        fromRange.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                -> {
            if (!newPropertyValue && fromRange.getText().equals("") || !newPropertyValue && fromRange.getText().equals("-")) {
                fromRange.setText("1");

            }

        });
        toRange.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("-?\\d*") || newValue.equals("0")) {
                toRange.setText(oldValue);
            }
            if (newValue.matches("-?\\d*")) {
                try {
                    int val = Integer.parseInt(newValue);
                    if (val <= Integer.parseInt(fromRange.getText())) {
                        fromRange.setText(Integer.toString(val - 1));

                    }
                } catch (NumberFormatException e) {

                }
            }

        });

        // bei leerem textfeld 0 einsetzen 
        toRange.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                -> {
            if (!newPropertyValue && toRange.getText().equals("") || !newPropertyValue && toRange.getText().equals("-")) {
                toRange.setText("100");

            }

        });
        fromRange.setPrefWidth(50);
        toRange.setPrefWidth(50);
        Label labelGewicht = new Label("Gewichtebereich der zufälligen Knoten ");
        labelGewicht.setVisible(false);

        Slider slider = new Slider(25, 100, 25);

        slider.setBlockIncrement(1);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(0);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.setVisible(false);

        Label labelSlider = new Label("Vollständigkeit des Graphen in %");
        labelSlider.setVisible(false);

        ToggleGroup group = new ToggleGroup();
        CheckBox cb = new CheckBox("Zufällige Kanten");
        HBox rangeRandom = new HBox();
        cb.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                fromLabel.setVisible(true);
                toLabel.setVisible(true);
                fromRange.setVisible(true);
                toRange.setVisible(true);
                labelGewicht.setVisible(true);
                labelSlider.setVisible(true);
                slider.setVisible(true);

            } else {
                fromLabel.setVisible(false);
                toLabel.setVisible(false);
                fromRange.setVisible(false);
                toRange.setVisible(false);
                labelGewicht.setVisible(false);
                labelSlider.setVisible(false);
                slider.setVisible(false);
            }
        });

        RadioButton rbList = new RadioButton("Gerichteter Graph");
        rbList.setToggleGroup(group);
        rbList.setSelected(true);
        rbList.setUserData("Directed");
        RadioButton matrix = new RadioButton("Ungerichteter Graph");
        matrix.setUserData("Undirected");
        matrix.setToggleGroup(group);

        TextField input = new TextField();
        input.setPrefWidth(50);
        input.setText("0");
        Label error = new Label("Bitte eine Anzahl an Knoten in das Textfeld eintragen.");
        Label knotenanzahl = new Label("Graph kann nur 1-25 Knoten enthalten.");
        knotenanzahl.setTextFill(Color.RED);
        knotenanzahl.setVisible(false);
        error.setTextFill(Color.RED);
        error.setVisible(false);
        newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(true);
        //abfangen der Falscheingaben des Nutzers
        input.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                input.setText(newValue.replaceAll("[^\\d]", ""));

            }
            if (newValue.matches("")) {
                error.setVisible(true);
                knotenanzahl.setVisible(false);
                newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(true);

            } else {
                if (!newValue.equals("") && newValue.matches("\\d*")) {

                    int nodeCount = Integer.parseInt(newValue);
                    if (nodeCount < 1 || nodeCount > 25) {
                        knotenanzahl.setVisible(true);
                        newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(true);
                    } else if (newValue.matches("\\d*") && nodeCount >= 1 && nodeCount <= 25) {
                        error.setVisible(false);
                        knotenanzahl.setVisible(false);
                        newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(false);

                    }
                }
            }
        });

        Label label = new Label("Anzahl Knoten: ");

        HBox hbox = new HBox();
        VBox vbox = new VBox();
        VBox vboxleft = new VBox();

        rangeRandom.getChildren().addAll(fromLabel, fromRange, toLabel, toRange);
        rangeRandom.setSpacing(10);
        vboxleft.setSpacing(30);
        vbox.setSpacing(10);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(label, input);
        vboxleft.getChildren().addAll(hbox, error, knotenanzahl);
        vbox.getChildren().addAll(rbList, matrix, cb, labelGewicht, rangeRandom, labelSlider, slider);

        grid.setHgap(300);
        grid.setVgap(100);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.add(vboxleft, 0, 0);
        grid.add(vbox, 1, 0);

        newGraphDialog.getDialogPane().setContent(grid);

        newGraphDialog.setResultConverter(dialogButton -> {
            if (dialogButton == nextButtonType && group.getSelectedToggle() != null && !"".equals(input.getText())) {
                int number = Integer.parseInt(input.getText());
                int minVal = Integer.parseInt(fromRange.getText());

                int maxVal = Integer.parseInt(toRange.getText());
                int dichte = (int) slider.getValue();
                if (group.getSelectedToggle().getUserData().toString().equals("Directed")) {

                    DialogInputs result = new DialogInputs(true, number, cb.isSelected(), minVal, maxVal, dichte);
                    return result;

                } else {

                    DialogInputs result = new DialogInputs(false, number, cb.isSelected(), minVal, maxVal, dichte);
                    return result;
                }

            }
            return null;
        });
        Optional<DialogInputs> result = newGraphDialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;

    }

    // Dialogfenster für Graphen Ohne Visualisierung
    public DialogInputs newGraphWOVisualisation() throws IOException {

        Dialog<DialogInputs> newGraphDialog = new Dialog<>();

        GridPane grid = new GridPane();
        this.gp = new GridPane();
        newGraphDialog.setTitle("Neuen Graphen erstellen");
        newGraphDialog.setHeaderText("Füllen sie die untenstehenden Felder aus und Bearbeiten Sie anschließend den Graphen");
        ButtonType nextButtonType = new ButtonType("Commit", ButtonBar.ButtonData.OK_DONE);

        newGraphDialog.getDialogPane().getButtonTypes().addAll(nextButtonType, ButtonType.CANCEL);
        TextField fromRange = new TextField("1");
        TextField toRange = new TextField("100");

        fromRange.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("-?\\d*") || newValue.equals("0")) {
                fromRange.setText(oldValue);
            }
            if (newValue.matches("-?\\d*")) {
                try {
                    int val = Integer.parseInt(newValue);
                    if (val >= Integer.parseInt(toRange.getText())) {
                        toRange.setText(Integer.toString(val + 1));

                    }
                } catch (NumberFormatException e) {

                }

            }

        });

        // bei leerem textfeld 0 einsetzen 
        fromRange.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                -> {
            if (!newPropertyValue && fromRange.getText().equals("") || !newPropertyValue && fromRange.getText().equals("-")) {
                fromRange.setText("1");

            }

        });
        toRange.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("-?\\d*")) {
                toRange.setText(oldValue);
            }
            if (newValue.matches("-?\\d*")) {
                try {
                    int val = Integer.parseInt(newValue);
                    if (val <= Integer.parseInt(fromRange.getText())) {
                        fromRange.setText(Integer.toString(val - 1));

                    }
                } catch (NumberFormatException e) {

                }
            }

        });

        // bei leerem textfeld 0 einsetzen 
        toRange.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                -> {
            if (!newPropertyValue && toRange.getText().equals("") || !newPropertyValue && toRange.getText().equals("-")) {
                toRange.setText("100");

            }

        });
        fromRange.setPrefWidth(50);
        toRange.setPrefWidth(50);

        Label labelGewicht = new Label("Gewichtebereich der zufälligen Kanten ");
        labelGewicht.setVisible(false);

        Slider slider = new Slider(25, 100, 25);

        slider.setBlockIncrement(1);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(0);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.setVisible(false);

        Label labelSlider = new Label("Vollständigkeit des Graphen in %");
        labelSlider.setVisible(false);

        ToggleGroup group = new ToggleGroup();

        labelGewicht.setVisible(true);
        labelSlider.setVisible(true);
        slider.setVisible(true);

        RadioButton rbList = new RadioButton("Gerichteter Graph");
        rbList.setToggleGroup(group);
        rbList.setSelected(true);
        rbList.setUserData("Directed");
        RadioButton matrix = new RadioButton("Ungerichteter Graph");
        matrix.setUserData("Undirected");
        matrix.setToggleGroup(group);

        TextField input = new TextField();
        input.setPrefWidth(50);
        input.setText("0");
        Label error = new Label("Bitte eine Anzahl an Knoten in das Textfeld eintragen.");
        Label knotenanzahl = new Label("Graph kann nur 1-1000 Knoten enthalten.");
        knotenanzahl.setTextFill(Color.RED);
        knotenanzahl.setVisible(false);
        error.setTextFill(Color.RED);
        error.setVisible(false);
        newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(true);
        //abfangen der Falscheingaben des Nutzers
        input.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                input.setText(newValue.replaceAll("[^\\d]", ""));

            }
            if (newValue.matches("")) {
                error.setVisible(true);
                knotenanzahl.setVisible(false);
                newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(true);

            } else {
                if (!newValue.equals("") && newValue.matches("\\d*")) {

                    int nodeCount = Integer.parseInt(newValue);
                    if (nodeCount < 1 || nodeCount > 1000) {
                        knotenanzahl.setVisible(true);
                        newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(true);
                    } else if (newValue.matches("\\d*") && nodeCount >= 1 && nodeCount <= 1000) {
                        error.setVisible(false);
                        knotenanzahl.setVisible(false);
                        newGraphDialog.getDialogPane().lookupButton(nextButtonType).setDisable(false);

                    }
                }
            }
        });

        Label label = new Label("Anzahl Knoten: ");

        HBox hbox = new HBox();
        VBox vbox = new VBox();
        HBox rangeRandom = new HBox();

        Label fromLabel = new Label("Min Kantengewicht: ");
        Label toLabel = new Label("Max Kantengewicht: ");
        rangeRandom.getChildren().addAll(fromLabel, fromRange, toLabel, toRange);
        rangeRandom.setSpacing(10);
        VBox vboxleft = new VBox();
        vboxleft.setSpacing(30);
        vbox.setSpacing(10);
        hbox.setSpacing(10);
        hbox.getChildren().addAll(label, input);
        vboxleft.getChildren().addAll(hbox, error, knotenanzahl);
        vbox.getChildren().addAll(rbList, matrix, labelGewicht, rangeRandom, labelSlider, slider);

        grid.setHgap(300);
        grid.setVgap(100);
        grid.setPadding(new Insets(40, 40, 40, 40));
        grid.add(vboxleft, 0, 0);
        grid.add(vbox, 1, 0);

        newGraphDialog.getDialogPane().setContent(grid);

        newGraphDialog.setResultConverter(dialogButton -> {
            if (dialogButton == nextButtonType && group.getSelectedToggle() != null && !"".equals(input.getText())) {
                int number = Integer.parseInt(input.getText());
                int minVal = Integer.parseInt(fromRange.getText());
                int maxVal = Integer.parseInt(toRange.getText());
                int dichte = (int) slider.getValue();
                if (group.getSelectedToggle().getUserData().toString().equals("Directed")) {

                    DialogInputs result = new DialogInputs(true, number, true, minVal, maxVal, dichte);
                    return result;

                } else {

                    DialogInputs result = new DialogInputs(false, number, true, minVal, maxVal, dichte);
                    return result;
                }

            }
            return null;
        });
        Optional<DialogInputs> result = newGraphDialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;

    }

    //falls ein Graph erstellt wurde, und der Nutzer erstmalig eine Änderung tätigt wird diese Funktion aufgerufen 
    //und erstellt einen Dialog, indem die Matrix aufgebaut wird
    public GridPaneWithMatrixList editGraphviaMatrix(int number, boolean directed) {
        ScrollPane sp = new ScrollPane();
        Dialog<GridPaneWithMatrixList> newGraphDialog = new Dialog<>();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(50, 50, 50, 50));
        GridPaneWithMatrixList gpWithMatrixList = this.generateMatrixList(number, directed);
        GridPane content = gpWithMatrixList.getG();
        newGraphDialog.setHeaderText("Bearbeiten sie die untenstehende Matrix durch Hinzufügen der Gewichte an den jeweiligen Kanten");
        ButtonType commitButtonType = new ButtonType("Commit", ButtonBar.ButtonData.OK_DONE);
        newGraphDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, commitButtonType);

        sp.setContent(content);

        grid.add(sp, 1, 0);

        newGraphDialog.getDialogPane().setContent(grid);

        newGraphDialog.setResultConverter(dialogButton -> {
            if (dialogButton == commitButtonType) {

                return gpWithMatrixList;

            }
            return null;
        });

        Optional<GridPaneWithMatrixList> result = newGraphDialog.showAndWait();

        if (result.isPresent()) {
            return result.get();
        }

        return null;

    }

    // wird innerhalb von editGraphviaMatrix aufgerufen und generiert den tatsächlichen Inhalt der matrix des graphen
    public GridPaneWithMatrixList generateMatrixList(int count, boolean directed) {

        GridPaneWithMatrixList gpWithMatrix = new GridPaneWithMatrixList();

        GridPane gridMatrix = new GridPane();

        List<List<TextField>> matrixList = new ArrayList<>();

        Node[] headerlistRow = new Node[count + 1];
        Node[] headerlistCollumn = new Node[count];
        headerlistRow[0] = createHeaderNode(" ");

        //matrix mit eingabeFeldern erstellen 
        for (int i = 0; i < count; i++) {
            matrixList.add(new ArrayList<>());
            for (int j = 0; j < count; j++) {
                TextField tmp = new TextField();

                tmp.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("-?\\d*") || newValue.equals("00")) {

                        tmp.setText(oldValue);

                    }

                    if (newValue.matches("-0")) {
                        tmp.setText("0");
                    }
                    if (!directed && newValue.matches("-?\\d*")) {
                        correctMatrixUndirected(tmp.getId(), newValue, count, matrixList);
                    }

                });

                // bei leerem textfeld 0 einsetzen 
                tmp.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                        -> {
                    if (!newPropertyValue && tmp.getText().equals("") || !newPropertyValue && tmp.getText().equals("-")) {
                        tmp.setText("0");
                        if (!directed) {
                            correctMatrixNegative(tmp.getId(), matrixList);
                        }

                    }

                });
                tmp.setId(Integer.toString(i) + "-" + Integer.toString(j));
                tmp.setText("0");
                tmp.setPrefWidth(40);
                tmp.setMaxWidth(40);
                matrixList.get(i).add(tmp);

                gridMatrix.add(matrixList.get(i).get(j), i + 1, j + 1);
                gridMatrix.setVgap(10);
                gridMatrix.setHgap(10);

            }

        }

        //zeilen/spalten ueberschriften erstellen
        for (int i = 0; i < count + 1; i++) {
            if (i != 0) {

                headerlistRow[i] = createHeaderNode("K" + Integer.toString(i - 1));

            }
            if (i < count) {
                headerlistCollumn[i] = createHeaderNode("K" + Integer.toString(i));
            }

        }

        gridMatrix.addRow(0, headerlistRow);
        gridMatrix.addColumn(0, headerlistCollumn);

        gpWithMatrix.setG(gridMatrix);
        gpWithMatrix.setMatrixList(matrixList);

        return gpWithMatrix;

    }

    //baut den Inhalt des dialogfensters mit einer zufälligen Matrix auf
    public GridPaneWithMatrixList generateMatrixListRandom(int count, List<List<TextField>> randomList, boolean directed) {

        GridPaneWithMatrixList gpWithMatrix = new GridPaneWithMatrixList();

        GridPane gridMatrix = new GridPane();

        List<List<TextField>> matrixList = new ArrayList<>();

        Node[] headerlistRow = new Node[count + 1];
        Node[] headerlistCollumn = new Node[count];
        headerlistRow[0] = createHeaderNode(" ");

        //matrix mit eingabeFeldern erstellen 
        for (int i = 0; i < count; i++) {
            matrixList.add(new ArrayList<>());
            for (int j = 0; j < count; j++) {
                TextField tmp = new TextField();
                // keine anderen zeichen ausser zahlen zulassen
                tmp.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("-?\\d*") || newValue.equals("00")) {
                        tmp.setText(oldValue);
                    }

                    if (newValue.matches("-0")) {
                        tmp.setText("0");
                    }
                    if (!directed && newValue.matches("-?\\d*")) {
                        correctMatrixUndirected(tmp.getId(), newValue, count, matrixList);
                    }

                });

                // bei leerem textfeld 0 einsetzen 
                tmp.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                        -> {
                    if (!newPropertyValue && tmp.getText().equals("") || !newPropertyValue && tmp.getText().equals("-")) {
                        tmp.setText("0");
                        if (!directed) {
                            correctMatrixNegative(tmp.getId(), matrixList);
                        }

                    }

                });
                tmp.setId(Integer.toString(i) + "-" + Integer.toString(j));

                tmp.setText(randomList.get(i).get(j).getText());
                tmp.setPrefWidth(40);
                tmp.setMaxWidth(40);
                matrixList.get(i).add(tmp);

                gridMatrix.add(matrixList.get(i).get(j), i + 1, j + 1);
                gridMatrix.setVgap(10);
                gridMatrix.setHgap(10);

            }

        }

        //zeilen/spalten ueberschriften erstellen
        for (int i = 0; i < count + 1; i++) {
            if (i != 0) {

                headerlistRow[i] = createHeaderNode("K" + Integer.toString(i - 1));

            }
            if (i < count) {
                headerlistCollumn[i] = createHeaderNode("K" + Integer.toString(i));
            }

        }

        gridMatrix.addRow(0, headerlistRow);
        gridMatrix.addColumn(0, headerlistCollumn);

        gpWithMatrix.setG(gridMatrix);
        gpWithMatrix.setMatrixList(matrixList);

        return gpWithMatrix;

    }

    // in ungerichteten Graphen muss die Adjazenzmatrix gespielt sein, die Matrix wird durch diese Funktion korrigiert
    public void correctMatrixUndirected(String id, String value, int count, List<List<TextField>> matrixList) {

        String[] parts = id.split("-");
        int i = Integer.parseInt(parts[0]);
        int j = Integer.parseInt(parts[1]);
        // diagonale pruefen
        if (value.equals("") && i != j) {
            matrixList.get(j).get(i).setText("0");

        } else {
            if (!value.equals("")) {

                if (!value.equals("0") && !value.equals("-") && !value.equals("-0")) {
                    try {
                        matrixList.get(j).get(i).setText(value);
                    } catch (Exception e) {

                    }
                }
            }

        }

    }

    //funktion für das korrigieren von negativen Kantengewichten
    private void correctMatrixNegative(String id, List<List<TextField>> matrixList) {
        String[] parts = id.split("-");
        int i = Integer.parseInt(parts[0]);
        int j = Integer.parseInt(parts[1]);
        TextField tmp = matrixList.get(j).get(i);
        if (!tmp.getText().equals(matrixList.get(i).get(j).getText()) && i != j) {
            matrixList.get(j).get(i).setText("0");
        }
    }

    //erstellt die Überschriften der Knoten in der Matrix
    private Label createHeaderNode(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle("-fx-border-style: solid; -fx-background-color: white;");
        return label;
    }
    //falls ein Graph erstellt wurde, und der Nutzer erstmalig eine Änderung tätigt wird diese Funktion aufgerufen 
    //und erstellt einen Dialog, indem die die Liste aufgebaut wird

    public GridPaneWithAdjacencList editGraphviaList(int number, boolean directed) {
        ScrollPane sp = new ScrollPane();
        Dialog<GridPaneWithAdjacencList> newGraphDialog = new Dialog<>();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(50, 50, 50, 50));
        newGraphDialog.setHeaderText("Bearbeiten sie die untenstehende AdjazenzListe durch hinzufuegen von Knoten in der Form <Knoten,Gewicht>");
        ButtonType commitButtonType = new ButtonType("Edit Graph", ButtonBar.ButtonData.OK_DONE);
        newGraphDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, commitButtonType);
        GridPane content = this.generateAdjacencyList(number, directed);
        content.setPadding(new Insets(30, 30, 30, 30));

        sp.setPrefViewportWidth(1000);
        sp.setContent(content);
        grid.add(sp, 1, 0);

        newGraphDialog.getDialogPane().setContent(grid);

        newGraphDialog.setResultConverter(dialogButton -> {
            if (dialogButton == commitButtonType) {
                GridPaneWithAdjacencList result = new GridPaneWithAdjacencList();

                result.setAdjacencList(this.convertTextFieldToPairList());
                result.setG(grid);
                return result;

            }
            return null;
        });

        Optional<GridPaneWithAdjacencList> result = newGraphDialog.showAndWait();

        if (result.isPresent()) {
            return result.get();
        }

        return null;
    }

    //updated das Gridpane mit der Adjazenzliste bei Änderungen
    public GridPaneWithAdjacencList updateGraphviaList(int number, GridPane grid) {
        Dialog<GridPaneWithAdjacencList> newGraphDialog = new Dialog<>();
        newGraphDialog.getDialogPane().setContent(grid);
        newGraphDialog.setHeaderText("Bearbeiten sie die untenstehende AdjazenzListe durch Hinzufügen von Knoten in der Form <Knoten,Gewicht>");
        ButtonType commitButtonType = new ButtonType("Edit Graph", ButtonBar.ButtonData.OK_DONE);
        newGraphDialog.getDialogPane().getButtonTypes().addAll(commitButtonType, ButtonType.CANCEL);

        newGraphDialog.setResultConverter(dialogButton -> {
            if (dialogButton == commitButtonType) {

                GridPaneWithAdjacencList result = new GridPaneWithAdjacencList();

                result.setAdjacencList(this.convertTextFieldToPairList());
                result.setG(grid);
                return result;

            }
            return null;
        });

        Optional<GridPaneWithAdjacencList> result = newGraphDialog.showAndWait();

        if (result.isPresent()) {
            return result.get();
        }

        return null;

    }

    //generiert inhalt des Dialogfenster mit der Adjazenzliste
    public GridPane generateAdjacencyList(int number, boolean directed) {

        GridPane grid = new GridPane();
        ArrayList<Button> buttonlist = new ArrayList<>();

        grid.setVgap(20);
        grid.setHgap(20);

        for (int i = 0; i < number; i++) {
            textfieldList.add(new ArrayList<>());
        }

        for (int i = 0; i < number; i++) {

            Image imageAdd = new Image("/icons/add.png", 25, 25, false, false);
            Button b = new Button();
            b.setId(Integer.toString(i));
            b.setGraphic(new ImageView(imageAdd));
            buttonlist.add(b);

            Label l = new Label();
            l.setText(String.valueOf(i));
            grid.add(l, 0, i);

            for (int j = 0; j < number; j++) {
                TextField temp = new TextField();
                temp.setId(Integer.toString(i));
                temp.setDisable(true);
                // [0-"+ number +  "],
                temp.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!newValue.matches("-?\\d*") || newValue.equals("00")) {
                        System.out.println("sind drin");
                        temp.setText(oldValue);
                    }
                    if (newValue.matches("-0")) {
                        temp.setText("0");
                    }

                });

                if (!directed) {
                    temp.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
                            -> {
                        if (!newPropertyValue && !temp.getText().equals("")) {

                            String tmp = temp.getText();
                            String[] parts = tmp.split(",");
                            String part1 = parts[0];
                            String part2 = parts[1];
                            int pos = this.findPos(number, Integer.parseInt(part1));

                            if (pos > -1) {

                                textfieldList.get(Integer.parseInt(part1)).get(pos).setText(temp.getId() + "," + part2);

                            }
                        }

                        //removen und aufklappen fehlt 
                    });
                }

                temp.setMinWidth(10);
                temp.setMaxWidth(50);
                textfieldList.get(i).add(temp);
            }
            setButtonToRightPosList(buttonlist, i, number, grid);

            grid.add(buttonlist.get(i), 1, i);
        }
        this.gp = grid;
        return grid;
    }

    //hilfsfunktion um den hinzufügen button in der adjazenzliste an die richtige stelle zu setzen
    private void setButtonToRightPosList(ArrayList<Button> buttonlist, int i, int number, GridPane grid) {
        Button tmp = buttonlist.get(i);
        tmp.setOnAction(event -> {

            int collumn = GridPane.getColumnIndex(tmp);

            int row = GridPane.getRowIndex(tmp);

            if (collumn < number) {
                grid.getChildren().remove(tmp);
                grid.add(textfieldList.get(row).get(collumn - 1), collumn, row);
                grid.add(tmp, collumn + 1, row);
                this.gp = grid;
            } else if (collumn == number) {
                grid.getChildren().remove(tmp);
                grid.add(textfieldList.get(row).get(collumn - 1), collumn, row);
                this.gp = grid;

            } else {
                grid.getChildren().remove(tmp);
                this.gp = grid;
            }

        });
    }

    //hilfsfunktion um beim ersten Aufruf durch die Matrix eine äquivalente Adjazenzliste zu generieren
    public GridPane generateAdjacencyListFromNewList(int number, ArrayList<ArrayList<Pair>> newList) {

        GridPane grid = new GridPane();
        ArrayList<Button> buttonlist = new ArrayList<>();

        grid.setVgap(20);
        grid.setHgap(20);
        if (this.textfieldList != null) {
            this.textfieldList = new ArrayList<>();
        }

        for (int i = 0; i < number; i++) {

            Image imageAdd = new Image("/icons/add.png", 25, 25, false, false);
            Button b = new Button();
            b.setId(Integer.toString(i));
            b.setGraphic(new ImageView(imageAdd));
            buttonlist.add(b);
            textfieldList.add(new ArrayList<>());
            Label l = new Label();
            l.setText(String.valueOf(i));
            grid.add(l, 0, i);

            for (int j = 0; j < number; j++) {
                TextField temp = new TextField();
                temp.setDisable(true);
                temp.setMinWidth(10);
                temp.setMaxWidth(50);
                textfieldList.get(i).add(temp);

                if (j < newList.get(i).size()) {

                    textfieldList.get(i).get(j).setText(newList.get(i).get(j).getKey().toString() + "," + newList.get(i).get(j).getValue().toString());
                    if (!textfieldList.get(i).get(j).getText().equals("")) {

                        grid.add(textfieldList.get(i).get(j), j + 1, i);
                    }

                }

            }

            setButtonToRightPosList(buttonlist, i, number, grid);

        }

        for (int i = 0; i < number; i++) {
            int pos = findPos(number, i);
            if (pos != -1) {

                grid.add(buttonlist.get(i), pos + 1, i);
            }
        }
        this.gp = grid;
        return grid;
    }

    //hilfsposition um die richtige stelle für den button in der liste zu finden
    private int findPos(int number, int pos) {
        for (int i = 0; i < number; i++) {
            if (this.textfieldList.get(pos).get(i).getText().equals("")) {
                return i;
            }
        }
        return -1;
    }

    //hilfsfunktion, um die textfelder in paare zu konvertieren die weiterverarbeitet werden können
    public ArrayList<ArrayList<Pair>> convertTextFieldToPairList() {
        ArrayList<ArrayList<Pair>> adjacencList = new ArrayList<>();
        ArrayList<ArrayList<TextField>> txtFieldList = this.textfieldList;

        for (int i = 0; i < txtFieldList.size(); i++) {
            adjacencList.add(new ArrayList<>());
            for (TextField get : txtFieldList.get(i)) {

                if (get.getText().isEmpty()) {
                    break;
                }
                String tmp = get.getText();
                String[] parts = tmp.split(",");
                String part1 = parts[0];
                String part2 = parts[1];
                Pair<String, String> p = new Pair<>(part1, part2);
                adjacencList.get(i).add(p);

            }

        }

        return adjacencList;
    }

    //updaten des graphen über die matrix per dialogfenster
    public GridPaneWithMatrixList updateGraphviaMatrix(int number, GridPaneWithMatrixList grid) {
        Dialog<GridPaneWithMatrixList> newGraphDialog = new Dialog<>();
        newGraphDialog.getDialogPane().setContent(grid.getG());
        newGraphDialog.setHeaderText("Bearbeiten sie die untenstehende Matrix durch einfügen der Kantengewichte");
        ButtonType commitButtonType = new ButtonType("Edit Graph", ButtonBar.ButtonData.OK_DONE);
        newGraphDialog.getDialogPane().getButtonTypes().addAll(commitButtonType, ButtonType.CANCEL);

        newGraphDialog.setResultConverter(dialogButton -> {
            if (dialogButton == commitButtonType) {

                return grid;

            }
            return null;
        });

        Optional<GridPaneWithMatrixList> result = newGraphDialog.showAndWait();

        if (result.isPresent()) {
            return result.get();
        }

        return null;
    }

    //dialog fenster zum schließen eines graphen
    public boolean sureToClose() {

        Dialog closeDialog = new Dialog<>();

        closeDialog.setHeaderText("Wollen sie den aktuellen Graphen schließen?");
        ButtonType commitButtonType = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        closeDialog.getDialogPane().getButtonTypes().addAll(commitButtonType, ButtonType.CANCEL);
        closeDialog.setResultConverter(dialogButton -> {
            return dialogButton == commitButtonType;
        });

        Optional result = closeDialog.showAndWait();

        if (result.isPresent()) {
            return result.get().equals(true);

        }
        return false;

    }

    public void setStage(ViewController vc) {
        this.vc = vc;
    }

    public ArrayList<ArrayList<TextField>> getTextfieldList() {
        return textfieldList;
    }

    public void setTextfieldList(ArrayList<ArrayList<TextField>> textfieldList) {
        this.textfieldList = textfieldList;
    }

}
