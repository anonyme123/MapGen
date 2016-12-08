package ptut.gui;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import ptut.mapgen.ImageTab;

/**
 * Fenêtre principale de MapGen
 * @author Dakan
 */
public class MainFrame extends JFrame {

    private MapZone mapZone;
    private ToolZone toolZone;


    public MainFrame() {

        this.setTitle("Mapgen"); //titre de la fenêtre
        setIconImage(new ImageIcon(this.getClass().getResource("../resources/mainicon.png")).getImage());
        this.setResizable(true);//resize la fenêtre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fermeture de la fenêtre
        mapZone = new MapZone(); //création des zones , zone de map
        toolZone = new ToolZone();
        
        this.add(mapZone, BorderLayout.CENTER); //ajout de ces zones a la fenêtre
        this.add(toolZone, BorderLayout.SOUTH);
        mapZone.setVisible(true); // rendre visible les zones
        toolZone.setVisible(true);

        this.pack();
        this.setSize(900, 600);  //taille de la fenêtre
        this.setLocationRelativeTo(null);
       //concerver cette ordre pack > SIZE > SETLOCATION > set visible

    }
    
    public void choiceMap(ImageTab imageTab , String render)
    {
        if (mapZone != null) remove(mapZone);
        if(render.equals("color"))
        {
            mapZone = new MapZone(imageTab.getColorBufferedImage());
        }
        else
        {
            if(render.equals("greyLevel"))
            {
                mapZone = new MapZone(imageTab.getGreyBufferedImage()); 
            } 
        }
        add(mapZone, BorderLayout.CENTER);
        revalidate();
    }
        public MapZone getMapZone(){
            return(mapZone);
        }    

        
        public ToolZone getToolZone(){
            return(toolZone);
        }
}


