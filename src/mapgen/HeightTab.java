package ptut.mapgen;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * Matrice de Cell. Objet sur lequel travaillent les méthodes de génération.
 * @author Vincent
 */
public class HeightTab {

    private Cell tab[][];
    private int sizeX;
    private int sizeY;

    /**
     * Crée un HeightTab aux dimensions indiquées. L'altitude de chaque case est
     * initialisée à 0.
     *
     * @param sizeX Largeur en pixels du HeightTab
     * @param sizeY Hauteur en pixels du HeightTab
     */
    public HeightTab(int sizeX, int sizeY) {
        //On remplit une matrice de Cell, appelée tab, aux dimensions données
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.tab = new Cell[this.sizeX][this.sizeY];

        for (int i = 0; i < sizeX; i++) { //Le tableau d'altitudes de chaque HeightTab est initialisé à 0 d'altitude
            for (int j = 0; j < sizeY; j++) {
                tab[i][j] = new Cell(0);
            }
        }

    }

    //Methodes
    /**
     * Méthode de génération. Appelle les autres méthodes de génération pour
     * obtenir une génération finale correspondant à un terrain de type
     * montagneux.
     */
    public void generateMountains() { //On ne passe pas les dimensions en paramètre, la methode s'applique sur la heightTab courante
        int borderMin = sizeX;
        int borderMax = sizeY;
        if (sizeY < sizeX) {
            borderMin = sizeY;
            borderMax = sizeX;
        }

        generatePerlinNoise(7, 0.8);
        generateBumps(
                5 + (int) (borderMin * 0.014f * borderMax * 0.0065f), // number
                50, // min Height 
                120, // max height
                150, // min radius
                250, // max radius
                75); // delta
        generateBumps(
                5 + (int) (borderMin * 0.014f * borderMax * 0.0065f * 0.42f), // number min 
                5 + (int) (borderMin * 0.014f * borderMax * 0.0065f * 0.60f), // number max 
                80, // min Height
                250, // max height
                60, // min radius
                120, // max radius
                50); // delta

        adjustHeightDelta(200, 850);
        generateBumps(
                5 + (int) (borderMin * 0.014f * borderMax * 0.0065f * 0.42f), // number min 
                5 + (int) (borderMin * 0.014f * borderMax * 0.0065f * 0.60f), // number max 
                80, // min Height
                250, // max height
                100, // min radius
                250, // max radius
                80); // delta

        HeightTab tempHeightTab = new HeightTab(sizeX, sizeY);
        tempHeightTab.generatePerlinNoise(6, 0.65f);
        tempHeightTab.adjustHeightDelta(0, 250);
        this.addHeightTab(tempHeightTab);

        ArrayList<Ridge> ridges = findMountainRidges(1, 3, -100, -100, 150);
        paintMountainRidges(ridges, 1);

        tempHeightTab.setHeight(0);
        tempHeightTab.generateBumps(
                5 + (int) (borderMin * 0.014f * borderMax * 0.0065f * 0.45f), // number min 
                5 + (int) (borderMin * 0.014f * borderMax * 0.0065f * 0.65f), // number max 
                0, // min Height
                100, // max height
                100, // min radius
                250, // max radius
                80); // delta
        this.substractHeightTab(tempHeightTab);

        normalizeHeightTab(); //WARNING : Ne pas utiliser pour le debbugage. Hors debuggage, toujours faire ceci à la fin d'une génération pour être sûr d'avoir des altitudes entre 0 et 1000.

    }
    /**
     * Méthode de génération. Appelle les autres méthodes de génération pour
     * obtenir une génération finale correspondant à un terrain de type île.
     */
    public void generateIsland() {
        int borderMin = sizeX;
        int borderMax = sizeY;
        if (sizeY < sizeX) {
            borderMin = sizeY;
            borderMax = sizeX;
        }
        //On génère des bosses selon la taille de la map choisie
        generateBumps((int) (borderMax * 0.065f), (int) (borderMax * 0.1f), 15, 34, (int) (borderMin * 0.1f), (int) (borderMin * 0.2f), (int) (-borderMin * 0.30f));

        //On ajoute un Perlin faible/moyen
        HeightTab tempHeightTab = new HeightTab(sizeX, sizeY);
        tempHeightTab.generatePerlinNoise(7, 0.8);

        HeightTab cloneHeightTab = cloneHeightTab();
        cloneHeightTab.addHeightTab(tempHeightTab);

        tempHeightTab.generatePerlinNoise(4, 0.8);
        tempHeightTab.adjustHeightDelta(0, 220);
        addHeightTab(tempHeightTab);

        //Tracé des arêtes
        ArrayList<Ridge> ridges = cloneHeightTab.findMountainRidges(1, 3, -100, 600, 0);
        paintMountainRidges(ridges, 0.42f);

        normalizeHeightTab();

        alterateBeaches(525, 150, 0.25f);
        alterateBeaches(475, 50, 0.6f);
    }
    
    /**
     * Méthode de génération. Appelle les autres méthodes de génération pour
     * obtenir une génération finale correspondant à un terrain de type
     * montagneux avec en particulier un volcan.
     */
    public void generateVolcanos() {
        int borderMin = sizeX;
        int borderMax = sizeY;
        if (sizeY < sizeX) {
            borderMin = sizeY;
            borderMax = sizeX;
        }
        //On génère des bosses selon la taille de la map choisie
        generateBumps(3, 5, 80, 250, 60, 120, 50);
        generateBumps((int) (borderMax * 0.01f),
                (int) (borderMax * 0.15f),
                10,
                25,
                (int) (borderMin * 0.3f),
                (int) (borderMin * 0.5f),
                (int) (-borderMin * 0.30f));

        //On ajoute un Perlin faible/moyen
        HeightTab tempHeightTab = new HeightTab(sizeX, sizeY);
        tempHeightTab.generatePerlinNoise(7, 0.8);

        HeightTab cloneHeightTab = cloneHeightTab();
        cloneHeightTab.addHeightTab(tempHeightTab);

        tempHeightTab.generatePerlinNoise(4, 0.8);
        tempHeightTab.adjustHeightDelta(0, 220);
        addHeightTab(tempHeightTab);

        //Tracé des arêtes
        ArrayList<Ridge> ridges = cloneHeightTab.findMountainRidges(1, 3, -100, 600, 0);
        paintMountainRidges(ridges, 0.55f);

        // Surélévation du volcan
        Random random = new Random();
        Point highestPoint = searchHighestPoint();
        for (int i = 0; i < 6; i++) {
            generateBump(
                    80, // height
                    100,
                    15 + borderMin / 6, // radius
                    15 + borderMin / 2,
                    highestPoint.x + (random.nextInt(50) - 25),
                    highestPoint.y + (random.nextInt(50) - 25));
        }

        // Creux du volcan
        tempHeightTab.setHeight(0);
        for (int i = 0; i < 50; i++) {
            tempHeightTab.generateBump(
                    40, // height
                    76,
                    10 + borderMin / 18, // radius
                    10 + borderMin / 12,
                    highestPoint.x + (random.nextInt(50) - 25),
                    highestPoint.y + (random.nextInt(50) - 25));
        }
        ridges = tempHeightTab.findMountainRidges(1, 3, 0, 400, borderMax);
        tempHeightTab.paintMountainRidges(ridges, 0.28f);
        this.substractHeightTab(tempHeightTab);

        normalizeHeightTab();

    }

    /**
     * Renvoie une copie du HeightTab courant.
     */
    private HeightTab cloneHeightTab() {
        int i, j, height;
        HeightTab clonedHeightTab = new HeightTab(this.sizeX, this.sizeY);
        for (i = 0; i < sizeX; i++) {
            for (j = 0; j < sizeY; j++) {
                height = this.getCell(i, j).getHeight();
                clonedHeightTab.getCell(i, j).setHeight(height);
            }
        }
        return clonedHeightTab;
    }

    /**
     * Génération de bruit totalement aléatoire compris entre les valeurs
     * spécifiées (incluses). S'applique à l'attribut tab de l'objet courant.
     * @param min height minimal (inclu) du bruit à générer
     * @param max height maximal (inclu) du bruit à générer
     */
    private void generateNoise(int min, int max) {
        int i, j;
        Random r = new Random();
        int plage = (max) - (min - 1);
        int height;

        for (i = 0; i < sizeX; i++) {
            for (j = 0; j < sizeY; j++) {
                height = r.nextInt(plage);
                height += min;
                tab[i][j] = new Cell(height);
            }
        }
    }

    /**
     * Génération de bruit totalement aléatoire entre 0 et 1000 (inclus)
     * S'applique à l'attribut tab de l'objet courant.
     */
    private void generateNoise() {
        generateNoise(0, 1000);
    }

    /**
     * Met la hauteur de chaque Cell de la HeightTab à la valeur indiquée.
     *
     * @param height hauteur que la HeightTab doit prendre pourque chacun de ses
     * Cell.
     */
    private void setHeight(int height) {
        int i, j;
        for (i = 0; i < sizeX; i++) {
            for (j = 0; j < sizeY; j++) {
                getCell(i, j).setHeight(height);
            }
        }
    }

    /**
     * Génération de bruit de Perlin sur la HeightTab courante.
     *
     * @param nbOctaves Nombre de bruits "élémentaires". Un nombre élevé
     * (exemple : 7) renforce la 'netteté' du résultat.
     * @param persistance Compris entre 0 et 1 : définie la 'granularité' du
     * résultat.
     * @return
     */
    private void generatePerlinNoise(int nbOctaves, double persistance) {
        HeightTab heightTab = PerlinNoise.generatePerlinHeightTab(sizeX, sizeY, nbOctaves, persistance);
        this.tab = heightTab.tab;
    }

    /**
     * Modifie toutes les altitudes de la HeightTab de sorte que les nouvelles
     * altitudes deviennent obligatoirement comprises entre les nouvelles
     * valeurs indiquées.
     *
     * @param newMin Determine quelle sera la nouvelle altitude minimale
     * possible du HeightTab.
     * @param newMax Determine quelle sera la nouvelle altitude maximale
     * possible du HeightTab.
     */
    private void adjustHeightDelta(int newMin, int newMax) {
        int actualMin, actualMax; //altitude minimale et maximale trouvée
        float ratio; //ratio de l'altitude d'une Cell (entre actualMin et actualMax).
        int delta = newMax - newMin;

        actualMin = actualMax = tab[0][0].getHeight();

        //On cherche les hauteurs min et max actuelles du tableau dans un 1er tour
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if (actualMin > tab[i][j].getHeight()) {
                    actualMin = tab[i][j].getHeight();
                }
                if (actualMax < tab[i][j].getHeight()) {
                    actualMax = tab[i][j].getHeight();
                }
            }
        }

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                ratio = ((float) tab[i][j].getHeight()) / (actualMax + actualMin);
                tab[i][j].setHeight((int) (newMin + delta * ratio));
            }
        }
    }

    /**
     * Recentre les altitudes de la HeightTab entre 0 et 1000, si (et seulement
     * si) ces bornes sont dépassées.
     */
    private void normalizeHeightTab() {
        int actualMin, actualMax; //altitude minimale et maximale trouvée
        float ratio; //ratio de l'altitude d'une Cell (entre 0 et 1000).
        final int intervalle = 1000;

        actualMin = actualMax = tab[0][0].getHeight();

        for (int i = 0; i < sizeX; i++) { //On cherche les hauteurs min et max actuelles du tableau dans un 1er tour
            for (int j = 0; j < sizeY; j++) {
                if (actualMin > tab[i][j].getHeight()) {
                    actualMin = tab[i][j].getHeight();
                }
                if (actualMax < tab[i][j].getHeight()) {
                    actualMax = tab[i][j].getHeight();
                }
            }
        }

        if (actualMin < 0) { //Si min < 0 on ajoute l'écart à chaque Cell, pour 'remonter' l'ensemble des altitudes
            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    tab[i][j].addHeight(Math.abs(actualMin));
                }
            }
            actualMax += Math.abs(actualMin);
        }

        if (actualMax > 1000) { //Si max > 0 on 'écrase' toutes les altitudes pour recentrer entre 0 et 1000.
            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    ratio = ((float) tab[i][j].getHeight()) / actualMax;
                    tab[i][j].setHeight((int) (intervalle * ratio));
                }
            }
        }

    }

    /**
     * Affiche le heightTab dans la console, avec la valeur numérique de chaque
     * case. Utile pour débugger. Ralentie sensiblement la génération.
     */
    private void printHeightTab() {
        int i, j;
        for (i = 0; i < sizeX; i++) {
            for (j = 0; j < sizeY; j++) {
                System.out.print(this.tab[i][j].getHeight() + " ");
            }
            System.out.println("");
        }
        System.out.println("======================");
    }

    /**
     * Soustrait les altitudes du HeightTab passé en paramètre au HeightTab
     * courrant
     * @param heightTab HeightTab à soustraire
     */
    private void substractHeightTab(HeightTab heightTab) {
        for (int i = 0; i < this.sizeX; i++) {
            for (int j = 0; j < this.sizeY; j++) {
                this.tab[i][j].setHeight(this.tab[i][j].getHeight() - heightTab.tab[i][j].getHeight());
            }
        }
    }

    /**
     * Additionne les altitudes du HeightTab passé en paramètre au HeightTab
     * courrant
     * @param heightTab HeightTab à additionner
     */
    private void addHeightTab(HeightTab heightTab) {
        for (int i = 0; i < this.sizeX; i++) {
            for (int j = 0; j < this.sizeY; j++) {
                this.tab[i][j].setHeight(this.tab[i][j].getHeight() + heightTab.tab[i][j].getHeight());
            }
        }
    }

    /**
     * Crée une bosse à la position indiquée. La bosse s'ajoute à la hauteur
     * existante.
     *
     * @param minHeight Hauteur minimale de la bosse ajoutée (au centre de la
     * bosse)
     * @param maxHeight Hauteur maximale de la bosse ajoutée (au centre de la
     * bosse)
     * @param minRadius Rayon minimal de la bosse à ajouter
     * @param maxRadius Rayon maximal de la bosse à ajouter
     * @param posX Position en X de la bosse sur la HeightTab
     * @param posY Position en Y de la bosse sur la HeightTab
     */
    private void generateBump(int minHeight, int maxHeight, int minRadius, int maxRadius, int posX, int posY) {
        Random random = new Random();

        int centralHeight, radius;
        float distance;
        int addedHeight;
        int i, j;

        if (minHeight != maxHeight) {
            centralHeight = random.nextInt(maxHeight - minHeight) + minHeight;
        } else {
            centralHeight = minHeight;
        }

        if (minRadius != maxRadius) {
            radius = random.nextInt(maxRadius - minRadius) + minRadius;
        } else {
            radius = minRadius;
        }
        for (i = posX - radius; i < posX + radius + 1; i++) {
            for (j = posY - radius; j < posY + radius + 1; j++) {
                if ((i >= 0) && (j >= 0) && (i < this.sizeX) && (j < this.sizeY)) {
                    distance = MathUtils.calculDistance(i, j, posX, posY);
                    if (distance == 0) {
                        addedHeight = centralHeight;
                    } else {
                        //On détermine la hauteur à ajouter (pour chaque point dans le cercle), selon la hauteur au centre et la taille du rayon. 
                        addedHeight = (int) ((1 - (distance / radius)) * centralHeight);
                        //On transforme la hauteur à ajouter, pour un aspect bosse plutôt que cône
//                       if (addedHeight > 0) addedHeight = (int)(addedHeight + ((centralHeight-addedHeight)*(1-(distance/radius))));
                    }
                    if (addedHeight >= 0) {
                        getCell(i, j).addHeight(addedHeight);
                    }
                }
            }
        }
    }

    /**
     * Crée une bosse à la position indiquée. La bosse s'ajoute à la hauteur
     * existante.
     *
     * @param minHeight Hauteur minimale de la bosse ajoutée (au centre de la
     * bosse)
     * @param maxHeight Hauteur maximale de la bosse ajoutée (au centre de la
     * bosse)
     * @param minRadius Rayon minimal de la bosse à ajouter
     * @param maxRadius Rayon maximal de la bosse à ajouter
     * @param point Position en de la bosse sur la HeightTab
     */
    private void generateBump(int minHeight, int maxHeight, int minRadius, int maxRadius, Point point) {
        generateBump(minHeight, maxHeight, minRadius, maxRadius, point.x, point.y);
    }

    /**
     * Crée une bosse à un emplacement aléatoire du HeightTab. La bosse s'ajoute
     * à la hauteur existante.
     *
     * @param minHeight Hauteur minimale de la bosse ajoutée (au centre de la
     * bosse)
     * @param maxHeight Hauteur maximale de la bosse ajoutée (au centre de la
     * bosse)
     * @param minRadius Rayon minimal de la bosse à ajouter
     * @param maxRadius Rayon maximal de la bosse à ajouter
     * @param delta La valeur de delta détermine la zone dans laquelle la bosse
     * peut être générée. Le delta peut-être négatif. Exemples : Si 0, le centre
     * est forcément dans le cadre. A 50, le centre de la bosse peut apparaître
     * jusqu'à 50 cases en dehors du tableau.
     */
    private void generateBump(int minHeight, int maxHeight, int minRadius, int maxRadius, int delta) {
        Random random = new Random();
        int posX = random.nextInt(this.sizeX + delta * 2) - delta;
        int posY = random.nextInt(this.sizeY + delta * 2) - delta;

        this.generateBump(minHeight, maxHeight, minRadius, maxRadius, posX, posY);
    }

    /**
     * Crée le nombre de bosses indiqué à des positions aléatoires. Les bosses
     * s'ajoutent à la hauteur existante.
     *
     * @param number Nombre de bosses à créer
     * @param minHeight Hauteur minimale des bosses ajoutées (hauteur à leur
     * centre)
     * @param maxHeight Hauteur maximale des bosses ajoutées (hauteur à leur
     * centre)
     * @param minRadius Rayon minimal des bosses ajoutées
     * @param maxRadius Rayon maximal des bosses ajoutées
     * @param delta La valeur de delta détermine la zone dans laquelle les
     * bosses peuvent être générées. Le delta peut-être négatif. Exemples : Si
     * 0, le centre est forcément dans le cadre. A 50, le centre de chaque bosse
     * peut apparaître jusqu'à 50 cases en dehors du tableau.
     */
    private void generateBumps(int number, int minHeight, int maxHeight, int minRadius, int maxRadius, int delta) {
        int i;
        for (i = 0; i < number; i++) {
            generateBump(minHeight, maxHeight, minRadius, maxRadius, delta);
        }
    }

    /**
     * Crée le nombre de bosses indiqué à des positions aléatoires. Les bosses
     * s'ajoutent à la hauteur existante.
     *
     * @param numberMin Nombre minimal de bosses à créer
     * @param numberMax Nombre maximal de bosses à créer
     * @param minHeight Hauteur minimale des bosses ajoutées (hauteur à leur
     * centre)
     * @param maxHeight Hauteur maximale des bosses ajoutées (hauteur à leur
     * centre)
     * @param minRadius Rayon minimal des bosses ajoutées
     * @param maxRadius Rayon maximal des bosses ajoutées
     * @param delta La valeur de delta détermine la zone dans laquelle les
     * bosses peuvent être générées. Le delta peut-être négatif. Exemples : Si
     * 0, le centre est forcément dans le cadre. A 50, le centre de chaque bosse
     * peut apparaître jusqu'à 50 cases en dehors du tableau.
     */
    private void generateBumps(int numberMin, int numberMax, int minHeight, int maxHeight, int minRadius, int maxRadius, int delta) {
        int i;
        int nbBumps;
        Random random = new Random();
        nbBumps = random.nextInt(numberMax - numberMin + 1) + numberMin;
        for (i = 0; i < nbBumps; i++) {
            generateBump(minHeight, maxHeight, minRadius, maxRadius, delta);
        }
    }

    /**
     * Change le contraste d'une heightab par rapport à un pourcentage spécifié
     *
     * @param percent pourcentage de renforcement de contraste (valeurs négatives acceptées)
     */
    private void changeContrast(int percent) {
        int averageHeight;
        averageHeight = searchAverageHeight();

        for (int i = 0; i < this.sizeX; i++) {
            for (int j = 0; j < this.sizeY; j++) {
                this.tab[i][j].setHeight(this.tab[i][j].getHeight() + (int) (((float) (this.tab[i][j].getHeight() - averageHeight) / 100) * percent));
            }
        }
    }

    /**
     * Retourne l'altitude moyenne de la HeightTab
     */
    private int searchAverageHeight() {
        int averageHeight = 0;
        for (int i = 0; i < this.sizeX; i++) {
            for (int j = 0; j < this.sizeY; j++) {
                averageHeight = averageHeight + this.tab[i][j].getHeight();
            }
        }
        averageHeight = (int) (averageHeight / (this.sizeX * this.sizeY));
        return (averageHeight);
    }

    /**
     * Retourne le point le plus haut de la heightTab
     */
    private Point searchHighestPoint() {
        Point highestPoint = new Point(0, 0);
        for (int i = 0; i < this.sizeX; i++) {
            for (int j = 0; j < this.sizeY; j++) {
                if (getCell(i, j).getHeight() > getCell(highestPoint).getHeight()) {
                    highestPoint = new Point(i, j);
                }
            }
        }
        return (highestPoint);
    }

    private ArrayList<Point> searchHighLowPoints(int pas, boolean searchHighPoints) {
        int cpt3;  //ils servent à avoir la bonne dimension de zone
        int cpt4;
        Point areaDimension = new Point();  //on stocke ici les dimensions
        Point tempHighLowPoint = new Point();  //on stock les coordonnée des points culminants ici
        ArrayList<Point> matchPoint = new ArrayList(); //cette array stocke les tableaux de coordonnées
        int averageHeight;  //hauteur moyenn de la map
        averageHeight = this.searchAverageHeight();
//        int zone = (int) sqrt((float) ((0.010F - (float) (averageHeight / 100000.000)) * (this.sizeX * this.sizeY))); //on defini ici la taille des zone
        int zone = 16;
        for (int i = 0, cpt = 0; i < this.sizeX; cpt++, i = cpt * zone) {
            for (int j = 0, cpt2 = 0; j < this.sizeY; cpt2++, j = cpt2 * zone) {
                int averageHeightMap = 0;
                int wantedPoint = this.tab[i][j].getHeight(); //point le plus bas ou le plus haut suivant le cas
                cpt3 = 0;
                for (int k = i; k < i + zone; k++) {
                    cpt3++;
                    if (k >= this.sizeX) {
                        k = i + zone;
                    } else {
                        cpt4 = 0;
                        for (int l = j; l < j + zone; l++) {
                            cpt4++;
                            if (l >= this.sizeY) {
                                l = j + zone;
                            } else {
                                averageHeightMap = averageHeightMap + this.tab[k][l].getHeight();
                                if (searchHighPoints == true) {
                                    //si on est en mode recherche des points hauts
                                    if (wantedPoint < this.tab[k][l].getHeight()) {
                                        wantedPoint = this.tab[k][l].getHeight();
                                        tempHighLowPoint = new Point();
                                        tempHighLowPoint.x = k;
                                        tempHighLowPoint.y = l;
                                    }
                                } else {
                                    //si on est en mode recherche des points bas
                                    if (wantedPoint > this.tab[k][l].getHeight()) {
                                        wantedPoint = this.tab[k][l].getHeight();
                                        tempHighLowPoint = new Point();
                                        tempHighLowPoint.x = k;
                                        tempHighLowPoint.y = l;
                                    }
                                }
                            }
                            areaDimension.x = cpt4;
                        }
                    }
                    areaDimension.y = cpt3;
                }
                averageHeightMap = averageHeightMap / (areaDimension.x * areaDimension.y);
                if (searchHighPoints == true) {
                    //si on est en mode recherche des points hauts
                    if (averageHeightMap > (averageHeight + pas)) {
                        matchPoint.add(tempHighLowPoint);
                    }
                } else {
                    //si on est en mode recherche des point bas
                    if (averageHeightMap < (averageHeight - pas)) {
                        matchPoint.add(tempHighLowPoint);
                    }

                }
            }
        }

        return (matchPoint);
    }

    /**
     * Cette méthode va rechercher les sommets de montagnes par rapport à un pas
     * Renvoie une liste de points considérés comme des sommets de montagne.
     *
     * @param pas Influe la quantité de points retournés. Un pas négatif donne
     * plus de points (max -1000) un pas positif en donne moins(max 1000)
     * @return une arraylist contenant les positions des sommets trouvés
     */
    private ArrayList<Point> searchMountains(int pas) {
        return (searchHighLowPoints(pas, true));
    }

    /**
     * Cette méthode va rechercher les zones creuses d'une HeightTab par rapport
     * à un pas. Renvoie une liste de points considérés comme des creux de zones
     * basses.
     *
     * @param pas Influe la quantité de points retournés. Un pas négatif donne
     * plus de points (max -1000) un pas positif en donne moins(max 1000)
     * @return une arraylist contenant les positions des sommets trouvés
     */
    private ArrayList<Point> searchHollow(int pas) {
        return (searchHighLowPoints(pas, false));
    }

    /**
     * Peint sur la HeightTab des arêtes de montagnes, à partir de l'ArrayList
     * de Ridges passé en paramètre
     *
     * @param ridges ArrayList de Ridges à partir desquels peindre les arêtes.
     * @param heightFactor Multiplicateur de la hauteur des arêtes
     */
    private void paintMountainRidges(ArrayList<Ridge> ridges, float heightFactor) {
        for (Ridge r : ridges) {
            r.findRidgePoints((int) r.getLength() / 3);
            for (Point point : r.getRidgePoints()) {
                generateBump((int) (5 * heightFactor), (int) (8 * heightFactor), 50, 60, point);
                generateBump((int) (8 * heightFactor), (int) (12 * heightFactor), 15, 20, point);
                generateBump((int) (16 * heightFactor), (int) (16 * heightFactor), 6, 6, point);
            }
            generateBump(5, 10, 100, 150, r.getStartCell());
        }
    }

    /**
     * Calcule et renvoie des Ridges du HeightTab correspondants à des arêtes de
     * montages, tracés des points hauts vers les points hauts et bas proches.
     *
     * @param minRidges Nombre d'arêtes minimal pour un sommet
     * @param maxRidges Nombre d'arêtes maximal pour un sommet
     * @param pasMountains Pas à utiliser pour la recherche des points hauts
     * (methode searchMountains)
     * @param pasHollows Pas à utiliser pour la recherche des points bas
     * (methode searchHollow)
     * @param distanceBetweenTops distance minimale entre deux 'sommets' de
     * montagne
     * @return Renvoie un ArrayList comportant tous les Ridges trouvés.
     */
    private ArrayList<Ridge> findMountainRidges(int minRidges, int maxRidges, int pasMountains, int pasHollows, int distanceBetweenTops) {

        ArrayList<Point> highPoints = searchMountains(pasMountains);
        ArrayList<Point> lowPoints = searchHollow(pasHollows);
        ArrayList<Ridge> ridges = new ArrayList();
        ArrayList<Point> allPoints = new ArrayList();
        allPoints.addAll(lowPoints);
        allPoints.addAll(highPoints);
        Point highest = null;
        ArrayList<Point> previousTopPoints = new ArrayList();

        while (!highPoints.isEmpty()) {
            highest = highPoints.get(0);
            for (Point point : allPoints) {
                if (getCell(point).getHeight() > getCell(highest).getHeight()) {
                    highest = point;
                }
            }
            Random random = new Random();

            boolean farEnough = true; //On veut éviter que deux sommets de montagnes ne soient trop proches
            for (Point previousPoint : previousTopPoints) {
                if (MathUtils.calculDistance(highest, previousPoint) < distanceBetweenTops) {
                    farEnough = false;
                    highPoints.remove(highest);
                    allPoints.remove(highest);
                }
            }
            if (farEnough) {
                previousTopPoints.add(highest);
                int nbRidge = random.nextInt(maxRidges + 1 - minRidges) + minRidges;
                for (int i = 0; i < nbRidge; i++) {
                    findNextRidge(highest, ridges, allPoints, highPoints);
                }
            }
        }
        return ridges;
    }

    /**
     * Méthode récursive, utilisée par findMountainRidges uniquement, pour tracer
     * des Ridges de montagnes.
     *
     * @param currentPoint
     * @param ridges
     * @param allPoints
     * @param highPoints
     */
    private void findNextRidge(Point currentPoint, ArrayList<Ridge> ridges, ArrayList<Point> allPoints, ArrayList<Point> highPoints) {
        final int maxDistance = 60;
        float distance;
        Point nextPoint = null;
        allPoints.remove(currentPoint);
        highPoints.remove(currentPoint);
        if (!allPoints.isEmpty()) {
            distance = new Float(MathUtils.calculDistance(currentPoint, allPoints.get(0)));
            for (Point point : allPoints) {
                if (MathUtils.calculDistance(currentPoint, point) < distance) {
                    distance = MathUtils.calculDistance(currentPoint, point);
                    if (getCell(point).getHeight() < getCell(currentPoint).getHeight()) {
                        if (distance < maxDistance) {
                            nextPoint = point;
                        }
                    }
                }
            }
            if (nextPoint != null) {
                Ridge ridge = new Ridge(currentPoint, nextPoint);
                ridges.add(ridge);
                Random random = new Random();
                if (random.nextFloat() > 0.5f) {
                    findNextRidge(nextPoint, ridges, allPoints, highPoints);
                }
                findNextRidge(nextPoint, ridges, allPoints, highPoints);
            }
        }

    }

    /**
     * Aplatie selon un facteur sur un delta de hauteur donné.
     *
     * @param level Hauteur en dessous de laquelle aplatir
     * @param range Delta de hauteur sur lequel aplatir en dessous de level
     * @param factor Force de l'aplatissement (0 = nulle, 1 = plat complet)
     */
    private void alterateBeaches(int level, int range, float factor) {
        int i, j;
        float delta;

        for (i = 0; i < sizeX; i++) {
            for (j = 0; j < sizeY; j++) {
                if (tab[i][j].getHeight() < level) {
                    if (tab[i][j].getHeight() > level - range) {
                        delta = level - tab[i][j].getHeight();
                        tab[i][j].addHeight((int) (delta * factor));
                    } else {
                        tab[i][j].addHeight((int) (range * factor));
                    }
                }
            }
        }
    }

    //Getters & Setters
    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public Cell getCell(int x, int y) {
        return tab[x][y];
    }

    public Cell getCell(Point point) {
        return tab[point.x][point.y];
    }
}