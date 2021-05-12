/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eingabe;

import javafx.util.Pair;


/**
 *
 * @author lennaertn
 */
//hilfsklassen für das weitergeben der Export Inhalte an andere Klassen
public class ExportInputs {

    private String path;
    private String dirname;
    private boolean picture;
    private boolean dotFormat;
    private boolean graphFormat;
    private Pair<Boolean, String> bellmanFord;
    private int nodecount;
    private boolean kruskal;
    private Pair<Boolean, String> tiefensuche;
    private Pair<Boolean, String> breitensuche;
    private Pair<Boolean, Pair<String,String>> netzwerk;
    public ExportInputs(String path, String dirname, boolean picture, boolean dotFormat, boolean graphFormat, Pair<Boolean, 
            String> bellmanFord, boolean kruskal, Pair<Boolean, String> tiefensuche, Pair<Boolean, String> breitensuche, Pair<Boolean, Pair<String,String>> netzwerk) {
        
        this.path = path;
        this.dirname = dirname;
        this.picture = picture;
        this.dotFormat = dotFormat;
        this.graphFormat = graphFormat;
        this.bellmanFord = bellmanFord;
        this.kruskal = kruskal;
        this.tiefensuche = tiefensuche;
        this.breitensuche = breitensuche;
        this.netzwerk = netzwerk;
    }

    public boolean isKruskal() {
        return kruskal;
    }

    public void setKruskal(boolean kruskal) {
        this.kruskal = kruskal;
    }

    public Pair<Boolean, Pair<String, String>> getNetzwerk() {
        return netzwerk;
    }

    public void setNetzwerk(Pair<Boolean, Pair<String, String>> netzwerk) {
        this.netzwerk = netzwerk;
    }
    
    public String getPath() {
        return path;
    }

    public Pair<Boolean, String> getBreitensuche() {
        return breitensuche;
    }

    public void setBreitensuche(Pair<Boolean, String> breitensuche) {
        this.breitensuche = breitensuche;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Pair<Boolean, String> getTiefensuche() {
        return tiefensuche;
    }

    public void setTiefensuche(Pair<Boolean, String> tiefensuche) {
        this.tiefensuche = tiefensuche;
    }
    
    public String getDirname() {
        return dirname;
    }

    public void setDirname(String dirname) {
        this.dirname = dirname;
    }

    public boolean isPicture() {
        return picture;
    }

    public void setPicture(boolean picture) {
        this.picture = picture;
    }

    public boolean isDotFormat() {
        return dotFormat;
    }

    public void setDotFormat(boolean dotFormat) {
        this.dotFormat = dotFormat;
    }

    public boolean isGraphFormat() {
        return graphFormat;
    }

    public void setGraphFormat(boolean graphFormat) {
        this.graphFormat = graphFormat;
    }

    public Pair<Boolean, String> getBellmanFord() {
        return bellmanFord;
    }

    public void setBellmanFord(Pair<Boolean, String> bellmanFord) {
        this.bellmanFord = bellmanFord;
    }

    public int getNodecount() {
        return nodecount;
    }

    public void setNodecount(int nodecount) {
        this.nodecount = nodecount;
    }

}
