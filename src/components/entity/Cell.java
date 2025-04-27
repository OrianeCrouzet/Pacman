package components.entity;

import components.CellType;
import java.awt.*;
import java.awt.image.ImageObserver;


/**
 * La classe Cell représente une cellule individuelle du labyrinthe dans le jeu Pacman.
 * Chaque cellule peut être de différents types (mur, point à manger, espace vide) et
 * contient les informations nécessaires pour son affichage graphique.
 * 
 * Cette classe est responsable de :
 * - Stocker le type et l'état de la cellule
 * - Gérer l'affichage des éléments graphiques de la cellule
 * - Fournir la taille constante des cellules
 */
public class Cell {

    /**
     * Image de fond pour l'écran d'accueil (usage à préciser)
     */
    public Image firstScreen;
    
    /**
     * Image représentant un point/une fraise à manger par Pacman
     */
    public Image fraise;
    
    /**
     * Taille constante d'une cellule en pixels (40px selon CellType.SIZE)
     */
    public static final int SIZE = CellType.SIZE.getValue();
    
    /**
     * État de la cellule (true = active, false = inactive)
     */
    public boolean cellstate;
    
    /**
     * Valeur numérique représentant le type de cellule (correspond à CellType)
     */
    public int cellval;

    /**
     * Constructeur par défaut qui initialise une cellule :
     * - Charge les images par défaut
     * - Initialise l'état de la cellule à true (active)
     */
    public Cell() {
        this.firstScreen = Toolkit.getDefaultToolkit().getImage("images/firstScreen.jpg");
        this.fraise = Toolkit.getDefaultToolkit().getImage("images/point.jpg");
        this.cellstate = true;
    }

    /**
     * Dessine la représentation graphique de la cellule (point à manger)
     * @param g L'objet Graphics utilisé pour le dessin
     * @param x Position x en coordonnées de grille
     * @param y Position y en coordonnées de grille
     * @param im L'ImageObserver pour le chargement d'image
     */
    public void drawI(Graphics g, int x, int y, ImageObserver im) {
        g.drawImage(fraise, x * SIZE + 25, y * SIZE + 25, 8, 8, im);
    }

    /**
     * Définit le type de la cellule en fonction d'une valeur CellType
     * @param type Le type de cellule à définir (POINT, WALL, EMPTY)
     */
    public void setCellVal(CellType type) {
        cellval = type.getValue();
    }
}