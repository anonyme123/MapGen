package ptut.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import ptut.gui.MainFrame;
import ptut.gui.MapZone;
import ptut.mapgen.HeightTab;
import ptut.mapgen.ImageTab;

/**
 * Controlleur des entrée utilisateurs.
 * @author Dakan
 */
public class MainFrameController {

    private MainFrame mainFrame;
    private JPanel tool;
    private ImageTab imageTab;
    private int width, height;
    private DragDropAdapter dragDropAdapter;
    private Zoom zoom;
    private Point imagePosition;
    private double steps = 1;

    public MainFrameController(MainFrame fenetrePrincipale) {
        this.mainFrame = fenetrePrincipale;
        this.tool = mainFrame.getToolZone();
        dragDropAdapter = new DragDropAdapter(fenetrePrincipale.getMapZone());
        zoom = new Zoom(fenetrePrincipale.getMapZone());
        initHandler();
    }

    /**
     * Méthode regroupant les actionsPerformed des éléments de toolZone
     */
    private void initHandler() {
                
        mainFrame.getToolZone().getButtonGenerate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                mainFrame.getToolZone().getButtonGenerate().setEnabled(false);
                
                // Création d'un nouveau thread pour la génération, pour permettre de griser correctement le bouton Generer en parrallèle.
                new Thread(new Runnable() { 

                    @Override
                    public void run() {
                        int minSize = 30; // Dimension minimale pour le bord d'une HeightTab
                        steps = 1;   

                        // Sécurité : si l'utilisateur laisse un champs vide
                        if (mainFrame.getToolZone().getHeightTextField().getText().equals("")){
                             mainFrame.getToolZone().getHeightTextField().setText(String.valueOf(minSize));
                        }
                        if (mainFrame.getToolZone().getWidthTextField().getText().equals("")){
                             mainFrame.getToolZone().getWidthTextField().setText(String.valueOf(minSize));
                        }  

                        // Récupèration des dimensions indiquées
                        int heightTabWidth = Integer.parseInt(mainFrame.getToolZone().getWidthTextField().getText());
                        int heightTabHeight = Integer.parseInt(mainFrame.getToolZone().getHeightTextField().getText());

                        // Si les dimensions sont trop basses, 
                        if (heightTabHeight < minSize){
                            heightTabHeight = minSize;
                            mainFrame.getToolZone().getHeightTextField().setText(String.valueOf(minSize));
                        }
                        if (heightTabWidth < minSize){
                            heightTabWidth = minSize;
                            mainFrame.getToolZone().getWidthTextField().setText(String.valueOf(minSize));
                        }

                        HeightTab heightTab = new HeightTab(heightTabWidth, heightTabHeight);
                        if (mainFrame.getToolZone().getButtonMountains().isSelected()) {
                            heightTab.generateMountains();
                        } else if (mainFrame.getToolZone().getButtonIsland().isSelected()) {
                            heightTab.generateIsland();
                        } else if (mainFrame.getToolZone().getButtonVolcano().isSelected()) {
                            heightTab.generateVolcanos();
                        }
                        imageTab = new ImageTab(heightTab);

                        mainFrame.getToolZone().getButtonExport().setEnabled(true);
                        width = imageTab.getHeightTab().getSizeX();
                        height = imageTab.getHeightTab().getSizeY();
                        imagePosition = new Point(((mainFrame.getMapZone().getSize().width / 2)), ((mainFrame.getMapZone().getSize().height / 2)));
                        mainFrame.getMapZone().setSteps(steps);
                        mainFrame.getMapZone().translateCanvas((int) imagePosition.getX(), (int) imagePosition.getY());


                        if (mainFrame.getToolZone().getGreyLevelButton().isSelected() == true) {
                            updateMapZone("greyLevel");
                        } else {
                            updateMapZone("color");
                        }
                        mainFrame.getToolZone().getButtonGenerate().setEnabled(true);
                    }
                }).start();
                
            }
        });

        mainFrame.getToolZone().getButtonExport().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (mainFrame.getToolZone().getGreyLevelButton().isSelected() == true) {
                    imageTab.exportWindow("greyLevel");
                } else {
                    imageTab.exportWindow("color");
                }
            }
        ;

        });
        
        mainFrame.getToolZone().getColorButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMapZone("color");
            }
        ;

        });
        
        mainFrame.getToolZone().getGreyLevelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMapZone("greyLevel");
                
            }
        ;

        });
        // Modification du niveau de la mer
        mainFrame.getToolZone().getSeaLevelSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {

                if (dragDropAdapter != null) {
                    dragDropAdapter.stop();
                }
                if (zoom != null) {
                    zoom.stop();
                }
                int seaLevelValue = mainFrame.getToolZone().getSeaLevelSlider().getValue();
                imageTab.setSeaLevel(seaLevelValue);
                imageTab.convertToColorBufferedImage();
                mainFrame.choiceMap(imageTab, "color");
                mainFrame.getMapZone().setSteps(steps);
                mainFrame.getMapZone().translateCanvas((int) imagePosition.getX(), (int) imagePosition.getY());
                mainFrame.getMapZone().revalidate();

                if (dragDropAdapter != null) {
                    dragDropAdapter.setMapZone(mainFrame.getMapZone());
                    dragDropAdapter.start();
                }
                if (zoom != null) {
                    zoom.setMapZone(mainFrame.getMapZone());
                    zoom.start();
                }

            }
        });

        // Ajoute une contrainte de saisie : seuls les caractères numériques peuvent être saisis
        mainFrame.getToolZone().getHeightTextField().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c)) { //On ne garde que les caractères numériques
                    evt.consume();
                }
            }
        });

        mainFrame.getToolZone().getWidthTextField().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c)) { //On ne garde que les caractères numériques
                    evt.consume();
                }
            }
        });
    }

    /**
     * Manage mapZone drag & drop
     */
    private class DragDropAdapter extends MouseAdapter {

        private boolean inArea = false;
        private Point dragStartLocation, mouseLocation;
        private MapZone mapZone;
        private int deltaX;
        private int deltaY;

        public DragDropAdapter(MapZone mapZone) {
            this.mapZone = mapZone;
        }

        public void start() {
            if (mapZone != null) {
                mapZone.addMouseListener(this);
                mapZone.addMouseMotionListener(this);
            }
        }

        public void stop() {
            if (mapZone != null) {
                mapZone.removeMouseListener(this);
                mapZone.removeMouseMotionListener(this);
            }
        }

        public void setMapZone(MapZone mapZone) {
            this.mapZone = mapZone;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (inArea) {
                dragStartLocation = new Point(e.getPoint());
            }
        }

        @Override
        public void mouseEntered(MouseEvent me) {
            inArea = true;
        }

        @Override
        public void mouseExited(MouseEvent me) {
            inArea = false;
        }

        /**
         * Détermine si la souris est au dessus de l'image (pour la déplacer avec la souris)
         */
        @Override
        public void mouseMoved(MouseEvent me) {
            inArea = false;
            mouseLocation = new Point(me.getPoint());
            if ((int) mouseLocation.getX() > imagePosition.getX() - (width * steps) / 2 && (int) mouseLocation.getX() < (imagePosition.getX() + (width * steps) / 2)) {
                if ((int) mouseLocation.getY() > imagePosition.getY() - (height * steps) / 2 && (int) mouseLocation.getY() < (imagePosition.getY() + (height * steps) / 2)) {
                    inArea = true;
                } else {
                    inArea = false;
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (inArea) {
                deltaX = e.getX() - (int) dragStartLocation.getX();
                deltaY = e.getY() - (int) dragStartLocation.getY();
                // déplacement de l'image
                mapZone.translateCanvas((int) imagePosition.getX() + deltaX, (int) imagePosition.getY() + deltaY);
                // Actualisation des variables
                dragStartLocation = new Point(e.getPoint());
                imagePosition.setLocation(imagePosition.getX() + deltaX, imagePosition.getY() + deltaY);
            }
        }
    }

    private class Zoom implements MouseWheelListener {

        private MapZone mapZone;

        public Zoom(MapZone mapZone) {
            this.mapZone = mapZone;
        }

        public void start() {
            if (mapZone != null) {
                mapZone.addMouseWheelListener(this);
            }
        }

        public void stop() {
            if (mapZone != null) {
                mapZone.removeMouseWheelListener(this);
            }
        }

        public void setMapZone(MapZone mapZone) {
            this.mapZone = mapZone;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getWheelRotation() > 0) {
                if (steps > 0.25D) {
                    steps -= 0.25D;
                }
            } else {
                if (steps < 3.00D) {
                    steps += 0.25D;
                }
            }

            this.mapZone.setSteps(steps);
            this.mapZone.setSize(width, height);
            mapZone.revalidate();
        }
    }

    /**
     * Actualise l'apparence de l'image de la map
     * @param renderMode "color" ou "greyLevel" selon le mode de rendu souhaité 
     */
    private void updateMapZone(String renderMode) {
        
        if (dragDropAdapter != null) {
            dragDropAdapter.stop();
        }

        if (zoom != null) {
            zoom.stop();
        }

        if (imageTab != null) {
            switch (renderMode) {
                case "color":
                    mainFrame.choiceMap(imageTab, "color");
                    mainFrame.getToolZone().getSeaLevelSlider().setEnabled(true);
                    int seaLevelValue = mainFrame.getToolZone().getSeaLevelSlider().getValue();
                    if (seaLevelValue > 0) {
                        imageTab.setSeaLevel(seaLevelValue);
                        imageTab.convertToColorBufferedImage();
                        mainFrame.choiceMap(imageTab, "color");
                    }
                    break;
                case "greyLevel":
                    mainFrame.choiceMap(imageTab, "greyLevel");
                    mainFrame.getToolZone().getSeaLevelSlider().setEnabled(false);
                    break;
            }

            mainFrame.getMapZone().setSteps(steps);
            mainFrame.getMapZone().translateCanvas((int) imagePosition.getX(), (int) imagePosition.getY());
            mainFrame.getMapZone().revalidate();
        }

        if (dragDropAdapter != null) {
            dragDropAdapter.setMapZone(mainFrame.getMapZone());
            dragDropAdapter.start();
        }
        if (zoom != null) {
            zoom.setMapZone(mainFrame.getMapZone());

            zoom.start();
        }
        
    }
}