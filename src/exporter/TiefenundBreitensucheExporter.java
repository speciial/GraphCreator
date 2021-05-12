/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;

/**
 *
 * @author lennaertn
 */
// Klasse die die Dateien für die Tiefen und Breitensuche exportiert
public class TiefenundBreitensucheExporter {

    String filename;

    public TiefenundBreitensucheExporter(String filename) {
        this.filename = filename;

    }

    public void exportTiefensucheToFile(List<String> vertexList) throws IOException {

        String filecontent;
        filecontent = "";
        for (String s : vertexList) {
            filecontent += s + System.lineSeparator();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename));
        writer.write(filecontent);
        writer.close();

    }
}
