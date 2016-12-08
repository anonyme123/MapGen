package ptut.mapgen;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Segment entre deux Point, sert aux tracé des arêtes de montagnes
 * @author Vincent
 */
public class Ridge {
    
    private Point startCell = new Point(); //Coordonnées du Cell de départ
    private Point endCell = new Point();   //Coordonnées du Cell d'arrivée
    private ArrayList<Point> ridgePoints = new ArrayList(); 
    private float length;
    
    /**
     * Un Ridge est un segment entre deux points d'une heightTab, qui comporte un nombre défini par l'utilisateur de points intémédiaires
     * Ce constructeur ne détermine que les points de départ et d'arrivée, pas les points intermédiaires.
     * @param startCell Point de départ du Ridge
     * @param endCell Point d'arrivée du Ridge
     */
    public Ridge(Point startCell, Point endCell){
        this.startCell = startCell;
        this.endCell = endCell;
        length = MathUtils.calculDistance(startCell, endCell);
    }
    
    /**
     * Un Ridge est un segment entre deux points d'une heightTab, qui comporte un nombre défini par l'utilisateur de points intémédiaires
     * @param startCell Point de départ du Ridge
     * @param endCell Point d'arrivée du Ridge
     * @param nbPoints Nombre de points intermédiaire à calculer, et à stocker dans ridgePoints. L'écart entre chaque point est toujours le même
     * et dépend de la taille du Ridge et du nombre de points souhaité.
     */
    public Ridge(Point startCell, Point endCell, int nbPoints){
        this(startCell, endCell);
        findRidgePoints(nbPoints);
    }
    
    /**
     * Calcule et stocke dans ridgePoints un nombre nbPoints de points entre startCell et endCell.
     * ridgePoints est préalablement vidé.
     * @param nbPoints Nombre de points à déterminer.
     */
    public final void findRidgePoints(int nbPoints){      
        ridgePoints.clear();
        float distanceX = (endCell.x-startCell.x);
        float distanceY = (endCell.y-startCell.y);
        float gapX = distanceX/(nbPoints+1);
        float gapY = distanceY/(nbPoints+1);
        
        float deltaX, deltaY;
        
        for (int i = 1; i <= nbPoints; i++){
            deltaX = gapX*i;
            deltaY = gapY*i;
            
            Point point = new Point();
            point.x = (int)(startCell.x + deltaX);
            point.y = (int)(startCell.y + deltaY);
            ridgePoints.add(point);
        }
        
    }

    public Point getStartCell() {
        return startCell;
    }

    public Point getEndCell() {
        return endCell;
    }

    public ArrayList<Point> getRidgePoints() {
        return ridgePoints;
    }   
    
    public float getLength() {
        return length;
    }
    
    
}
