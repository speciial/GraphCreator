/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DataBean;
import eingabe.ViewController;


/**
 *
 * @author lennaertn
 */
//startet die JavaFX anwendung
public class Start extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //laden der fxml datei und initalisieren der DataBean

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLDocument.fxml"));
        Parent root = loader.load();
        ViewController controller = loader.getController();
        DataBean dataBean = new DataBean(stage);
        
        controller.setStage(dataBean);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
