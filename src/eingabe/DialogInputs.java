/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eingabe;

/**
 *
 * @author lennaertn
 */
//hilfsklasse um die dialogdaten beim erstellen eines neuen Graphen zu persistieren
public class DialogInputs {
    private boolean direction;
    private int numberofnodes;
    private boolean random;
    private int min;
    private int max;
    private int dichte;
    
    public DialogInputs(boolean direction, int numberofnodes, boolean random, int min, int max,int dichte) {
        this.direction = direction;
        this.numberofnodes = numberofnodes;
        this.random = random;
        this.min = min;
        this.max = max;
        this.dichte = dichte;
    }

    public DialogInputs(boolean direction, int numberofnodes) {
        this.direction = direction;
        this.numberofnodes = numberofnodes;
    }
    
    
    public int getDichte() {
        return dichte;
    }

    public void setDichte(int dichte) {
        this.dichte = dichte;
    }

    
    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public int getNumberofnodes() {
        return numberofnodes;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
    
    
    public void setNumberofnodes(int numberofnodes) {
        this.numberofnodes = numberofnodes;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public DialogInputs() {
    }
}
