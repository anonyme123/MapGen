package ptut.mapgen;

/**
 * Génération de bruit de Perlin
 * @author Vincent
 */
public class PerlinNoise {
   
    private final static int pas2D = 150;
    private static int nombre_octaves2D = 0;
    private static int hauteur = 0;
    private static int longueur = 0;
    private static int longueur_max = 0;
    private static int hauteur_max = 0;
    private static double valeurs2D[];
    
   /**
    * 
    * @param largeur Largeur du HeightTab désiré.
    * @param hauteur Hauteur du HeightTab désiré.
    * @param nombreOctaves Nombre de bruits "élémentaires". Un nombre élevé (exemple : 7) renforce la 'netteté' du résultat.
    * @param persistance Compris entre 0 et 1 : définie la 'granularité' du résultat.
    * @return une HeightTab avec le bruit généré
    */
    public static HeightTab generatePerlinHeightTab(int largeur, int hauteur, int nombreOctaves, double persistance){
               
        HeightTab heightTab = new HeightTab(largeur, hauteur);
        int valeur;
        
        
        initBruit2D(largeur, hauteur, nombreOctaves);
        
        for(int i=0; i < largeur; i++){
            for (int j=0; j < hauteur; j++) {
                valeur = (int)( bruit_coherent2D(i, j, persistance) * 1000 );
                heightTab.getCell(i, j).setHeight(valeur);
            }
        }
        
        return heightTab;
    }
    

    private static double interpolation_cos1D(double a, double b, double x) {
       double k = (1 - Math.cos(x * Math.PI)) / 2;
        return a * (1 - k) + b * k;
    }

    private static void initBruit2D(int l, int h, int nombreOctaves) {
        nombre_octaves2D = nombreOctaves;
        longueur = l;
        hauteur = h;
        longueur_max = (int) Math.ceil(longueur * Math.pow(2, nombre_octaves2D  - 1)  / pas2D);
        hauteur_max = (int) Math.ceil(hauteur * Math.pow(2, nombre_octaves2D  - 1)  / pas2D);

        //valeurs2D = new double[longueur_max * hauteur_max *3];
        valeurs2D = new double[l * h];
        

        int i;
        for(i = 0; i < longueur_max * hauteur_max; i++){
            valeurs2D[i] = Math.random();
            
        }
    }


    private static double bruit2D(int i, int j) {
        if (hauteur > longueur){
            return valeurs2D[i * longueur_max + j];
        }else{
            return valeurs2D[i * hauteur_max + j];
        }
        
    }

    private static double interpolation_cos2D(double a, double b, double c, double d, double x, double y) {
       double y1 = interpolation_cos1D(a, b, x);
       double y2 = interpolation_cos1D(c, d, x);
       return  interpolation_cos1D(y1, y2, y);
    }

    private static double fonction_bruit2D(double x, double y) {
       int i = (int) (x / pas2D);
       int j = (int) (y / pas2D);
       return interpolation_cos2D(bruit2D(i, j), bruit2D(i + 1, j), bruit2D(i, j + 1), bruit2D(i + 1, j + 1), (x / pas2D)%1, (y / pas2D)%1);
    }

    private static double bruit_coherent2D(double x, double y, double persistance) {
        double somme = 0;
        double p = 1;
        int f = 1;
        int i;

        for(i = 0 ; i < nombre_octaves2D ; i++) {
            somme += p * fonction_bruit2D(x * f, y * f);
            p *= persistance;
            f *= 2;
        }
        return somme * (1 - persistance) / (1 - p);
    }

}
