/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Project;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * This class presents handling of a picture added to UI view.
 *
 * @author Jutta Pietila
 */
public class Picture extends JPanel {
    Image image;
    
    public Image getImage() {
        return image;
    }
    
    public void setImage(Image image) {
        this.image = image;
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null)
            g.drawImage(image, 0, 0, this);
    }     
}
