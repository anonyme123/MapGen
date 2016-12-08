package ptut.mapgen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import ptut.gui.MapZone;

/**
 * Permet la conversion d'une HeightTab en BufferedImage, qu'elle stocke ensuite. Un
 * BufferedImage va permettre de manipuler et afficher un HeightTab en tant
 * qu'image.
 *
 * @author Vincent
 */
public final class ImageTab {

    private HeightTab heightTab;
    private BufferedImage colorBufferedImage;
    private BufferedImage greyBufferedImage;
    private int seaLevel = 0;

    public ImageTab(HeightTab heightTab) {
        this.heightTab = heightTab;
        convertToColorBufferedImage();
        convertToGreyBufferedImage();

    }

    /**
     * Convertie l'attribut heightTab en BufferedImage et le stocke dans
     * colorBufferedImage.
     */
    public void convertToColorBufferedImage() { 
        BufferedImage bufferedImage = new BufferedImage(heightTab.getSizeX(), heightTab.getSizeY(), BufferedImage.TYPE_INT_RGB);
        Color color;
        int rgbValue;
        int cellHeight;
        int red = 0, green = 0, blue = 0;

        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                cellHeight = heightTab.getCell(x, y).getHeight();
                cellHeight *= 0.255; //On recentre cellHeight entre 0 et 255 pour transformer facilement l'altitude en couleurs

//                if (cellHeight <= 85) {
//                    red = 0;
//                    green = 255;
//                    blue = 255 - cellHeight*3;
//                } else if (cellHeight <= 170) {
//                    red = 255 - (int)((170-cellHeight)*3);
//                    green = 255;
//                    blue = 0;
//                } else if (cellHeight <= 255) {
//                    red = 255;
//                    green = (int)((255-cellHeight)*3);
//                    blue = 0;
//                }

                if (cellHeight <= 127) {
                    red = 255 - (int)((127-cellHeight)*1.5);
                    green = 255;
                    blue = 0;
                } else {
                    red = 255;
                    green = (int)((255-cellHeight)*2);
                    blue = 0;
                }
                //Affichage de l'eau
                if ((seaLevel > 0) && (cellHeight <= seaLevel * 0.255)) {
                    red = 0;
                    green = 128 + cellHeight / 2;
                    blue = 255;
                }
                color = new Color(red, green, blue);
                rgbValue = color.getRGB();
                bufferedImage.setRGB(x, y, rgbValue);
            }
        }
        colorBufferedImage = bufferedImage;
    }

    /**
     * Convertie l'attribut heightTab en BufferedImage et le stocke dans
     * greyBufferedImage.
     */
    public void convertToGreyBufferedImage() {
        BufferedImage bufferedImage = new BufferedImage(heightTab.getSizeX(), heightTab.getSizeY(), BufferedImage.TYPE_INT_RGB);
        Color color;
        int greyLevel, rgbValue;

        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                greyLevel = heightTab.getCell(x, y).getHeight();
                greyLevel *= 0.255; //greyLevel doit être compris entre 0 et 255
                color = new Color(greyLevel, greyLevel, greyLevel); //Pour un niveau de gris, on met la même quantité de R, V et B.
                rgbValue = color.getRGB();
                bufferedImage.setRGB(x, y, rgbValue);
            }
        }
        greyBufferedImage = bufferedImage;

    }

    /**
     * Enregistre une des images dans l'explorateur
     *
     * @param typeMap "color" si on veut enregistrer la version couleur. 
     * "greyLevel" si on veut enregistrer la version en niveau de gris.
     */
    public void exportWindow(String typeMap) {

        JFileChooser filechoose = new JFileChooser();

        filechoose.setCurrentDirectory(new File("."));  // ouvrir la boite de dialogue dans le répertoire courant
        filechoose.setDialogTitle("Enregistrer sous"); // nom de la boite de dialogue
        filechoose.setAcceptAllFileFilterUsed(false); //masquer l'option "tout les fichiers"
        filechoose.addChoosableFileFilter(new FileNameExtensionFilter("Image jpg", "jpg"));
        filechoose.addChoosableFileFilter(new FileNameExtensionFilter("Image png", "png"));
        filechoose.addChoosableFileFilter(new FileNameExtensionFilter("Image gif", "gif"));
        filechoose.setFileFilter(filechoose.getChoosableFileFilters()[1]);

        String approve = "Enregistrer"; // Le bouton pour valider l’enregistrement portera la mention Enregistrer
        int resultatEnregistrer = filechoose.showDialog(filechoose, approve);


        if (resultatEnregistrer == JFileChooser.APPROVE_OPTION) { // Si l’utilisateur clique sur le bouton Enregistrer
            String path = filechoose.getSelectedFile().getPath(); /* pour avoir le chemin absolu */
            String[] extension = ((FileNameExtensionFilter) filechoose.getFileFilter()).getExtensions();

            File outputfile = new File(path + "." + extension[0]);


            try {
                switch (typeMap) {
                    case "color":
                        ImageIO.write(colorBufferedImage, extension[0], outputfile);
                        break;
                    case "greyLevel":
                        ImageIO.write(greyBufferedImage, extension[0], outputfile);
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(MapZone.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public BufferedImage getColorBufferedImage() {
        return colorBufferedImage;
    }

    public BufferedImage getGreyBufferedImage() {
        return greyBufferedImage;
    }

    public HeightTab getHeightTab() {
        return (heightTab);
    }

    public int getSeaLevel() {
        return seaLevel;
    }

    public void setSeaLevel(int seaLevel) {
        this.seaLevel = seaLevel;
    }
    
    
}
