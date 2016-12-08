package ptut.mapgen;

import java.awt.Point;

/**
 * Regroupe des méthodes de calcul fréquemment nécessaires pour la génération,
 * mais qui ne correspondent pas directement à la manipulation de l'une des
 * autres classes.
 *
 * @author Vincent
 */
public final class MathUtils {

    /**
     * Calcule la distance entre deux points
     *
     * @return un float égal à la distance entre les deux points
     * indiqués
     */
    public static float calculDistance(int X1, int X2, int Y1, int Y2) {
        return ((float) Math.sqrt((X1 - Y1) * (X1 - Y1) + (X2 - Y2) * (X2 - Y2)));
    }

    /**
     * Calcule la distance entre deux points
     *
     * @return un float égal à la distance entre les deux points
     * indiqués
     */
    public static float calculDistance(Point posA, Point posB) {
        return (calculDistance(posA.x, posA.y, posB.x, posB.y));
    }

    /**
     * Vérifie qu'un Point est bien dans la zone spécifiée
     * @param point Le point à vérifier
     * @param xMin limite min en x (inclusif)
     * @param xMax limite max en x (inclusif)
     * @param yMin limite min en y (inclusif)
     * @param yMax limite max en y (inclusif)
     * @return 
     */
    public static boolean isPointUnderLimits(Point point, int xMin, int xMax, int yMin, int yMax) {
        if ((point.x <= xMax && point.x >= xMin) && (point.y <= yMax && point.y >= yMin)) {
            return true;
        } else {
            return false;
        }
    }
}
