package ptut.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Zone d'affichage de la map générée
 * @author Dakan
 */
public class MapZone extends JPanel {

    private BufferedImage bufferedImage;
    private Point currentImagePosition;
    private double steps=1;

    public MapZone() {
        this(null);        
    }

    public MapZone(BufferedImage bufferedImage) {
        this.setBackground(new Color(100, 100, 100));
        this.bufferedImage = bufferedImage;
        //Initialisation de la position actuelle
        if(bufferedImage!=null){
            this.currentImagePosition = new Point((int)steps*bufferedImage.getWidth()/2, (int)steps*bufferedImage.getHeight()/2);
        } else {
            this.currentImagePosition = new Point(0,0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bufferedImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.drawImage(
                bufferedImage,
                currentImagePosition.x - (int)(bufferedImage.getWidth()*steps)/2,
                currentImagePosition.y-(int)(bufferedImage.getHeight()*steps)/2,
                (int)(bufferedImage.getWidth()*steps),
                (int)(bufferedImage.getHeight()*steps),
                (this)
             );          
            g2d.dispose();
        }
    }
    
    public void translateCanvas(int valX, int valY){
        currentImagePosition.setLocation(valX, valY);
        repaint();
    }
    
    public void setSteps(double steps){
        this.steps = steps;
    }
    
    public double getSteps(){
        return steps;
    }
}