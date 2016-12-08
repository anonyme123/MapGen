package ptut.mapgen;
/**
 * Informations sur un un point d'une HeightTab. 
 * @author Vincent
 */
public class Cell {
    private int height;
        
    /**
     * Un Cell correspond à un élément de HeightTab, et définie une altitude.
     * @param height Compris entre 0 et 1000. Définie l'altitude du Cell.
     */
    public Cell(int height){
        this.height = height;
    }
    
    
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * Ajoute la valeur height à l'altitude de la Cell. Ne remplace pas l'altitude comme le fait setHeight.
     * @param height Valeur à ajouter
     */
    public void addHeight(int height) {
        this.height += height;
    }
    
}
