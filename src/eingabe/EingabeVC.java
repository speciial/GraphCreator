/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eingabe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.control.TextField;

/**
 *
 * @author lennaertn
 */
public class EingabeVC {

    // zufällige matrix für zufällige Graphen generieren
    public List<List<TextField>> generateRandomMatrix(int number, int min, int max, int dichte, boolean directed) {
        List<List<TextField>> matrix = this.initMatrix(number);

        double percent = (number * number) / 100.0;

        switch (dichte) {
            case 1:
                percent = percent * 25;
                break;
            case 2:
                percent = percent * 50;
                break;
            case 3:
                percent = percent * 75;
                break;
        }

        int numberofFieldstoInit = (int) Math.round(percent);

        if (matrix != null) {
            int row = 0;
            for (int k = 0; k < numberofFieldstoInit; k++) {

                int random = this.getRandomNumberInRange(min, max);

                int j = this.getRandomNumberInRange(0, number - 1);

                if (row != j) {
                    if (directed) {

                        if (matrix.get(j).get(row).getText().equals("0") && directed) {
                            matrix.get(row).get(j).setText(Integer.toString(random));
                        }

                    }

                    if (!directed) {
                        matrix.get(row).get(j).setText(Integer.toString(random));
                        matrix.get(j).get(row).setText(Integer.toString(random));
                    }

                    row++;
                } else {
                    k--;
                }
                if (row == number - 1) {
                    row = 0;
                }
            }
        }

        return matrix;
    }

    private void printMatrix(List<List<TextField>> matrix) {
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                System.out.print(matrix.get(i).get(j).getText() + "  ");
            }
            System.out.println("");
        }
    }

    // hilfsfunktion, die die matrix vorinitalisiert
    private List<List<TextField>> initMatrix(int number) {
        List<List<TextField>> matrix = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            matrix.add(new ArrayList<>());
            for (int j = 0; j < number; j++) {
                matrix.get(i).add(new TextField("0"));
            }
        }
        return matrix;
    }

    //hilfsfunktion um zufällige zahl innerhalb eines Wertebereichs zu bekommen
    private int getRandomNumberInRange(int min, int max) {

        Random r = new Random();
        int number = r.nextInt((max - min) + 1) + min;

        return number;
    }

}
