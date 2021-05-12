/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exporter;

import com.mxgraph.util.mxCellRenderer;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import model.DataBean;

/**
 *
 * @author lennaertn
 */
//Klasse die das exportieren der PNG dateien übernimmt
public class ImageExporter {
    
    public Image exportImageofaGraph(DataBean bean) throws IOException {

        FileChooser fileChooser = new FileChooser();
        File imgFile = fileChooser.showSaveDialog(null);
        String suffix = ".png";

        if (!imgFile.toString().contains(suffix)) {
            imgFile = new File(imgFile.toString() + suffix);
        }

        imgFile.createNewFile();

        BufferedImage image = mxCellRenderer.createBufferedImage(bean.getGraphComponent().getGraph(), null, 2, Color.white, true, null);

        ImageIO.write(image, "PNG", imgFile);
        Image graphVisual = new Image(new FileInputStream(imgFile.getAbsolutePath()));
        return graphVisual;

    }
}
