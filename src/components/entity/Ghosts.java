package components.entity;

import components.CellType;
import components.Characters;
import components.Direction;
import display.screen.Labyrinth;
import java.awt.*;
import java.util.Random;

/**
 * La classe Ghosts représente un fantôme dans le jeu Pacman.
 * Elle gère le comportement de base des fantômes, incluant :
 * - Le déplacement autonome dans le labyrinthe
 * - La détection de collisions avec les murs et autres fantômes
 * - Le suivi de la position et de la direction
 * 
 * Les fantômes ont une taille légèrement inférieure aux cellules pour faciliter
 * leur déplacement et sont caractérisés par une couleur spécifique.
 */
public class Ghosts {
    /** Position horizontale en pixels */
    public int c;
    /** Position verticale en pixels */
    public int r;
    /** Vitesse de déplacement en pixels */
    protected static final int SPEED = 3;
    /** Taille du fantôme avec marge interne */
    protected static final int GHOST_SIZE = Cell.SIZE - 7;
    /** Largeur du fantôme (75% de la taille d'une cellule) */
    public static final int GHOST_WIDTH = Cell.SIZE * 3 / 4;
    /** Hauteur du fantôme (75% de la taille d'une cellule) */
    public static final int GHOST_HEIGHT = Cell.SIZE * 3 / 4;
    /** Référence au labyrinthe contenant le fantôme */
    protected final Labyrinth lab;
    /** Couleur du fantôme */
    protected final Characters.GhostColor color;
    /** Direction actuelle du fantôme */
    protected Direction direction;
    /** Image représentant le fantôme */
    protected final Image img;

    /**
     * Constructeur du fantôme
     * @param c Position horizontale initiale en pixels
     * @param r Position verticale initiale en pixels
     * @param lab Labyrinthe dans lequel évolue le fantôme
     * @param img Image représentant le fantôme
     * @param color Couleur du fantôme
     */
    public Ghosts(int c, int r, Labyrinth lab, Image img, Characters.GhostColor color) {
        this.lab = lab;
        // Alignement initial garanti sur la grille
        this.c = (c / Cell.SIZE) * Cell.SIZE + Cell.SIZE / 2 - GHOST_SIZE / 2;
        this.r = (r / Cell.SIZE) * Cell.SIZE + Cell.SIZE / 2 - GHOST_SIZE / 2;
        this.img = img;
        this.color = color;
        this.direction = Direction.LEFT;
    }

    /**
     * Déplace le fantôme en essayant de conserver sa direction actuelle
     * ou en trouvant une nouvelle direction valide si nécessaire.
     * Gère les collisions avec les murs et autres fantômes.
     */
    public void move() {
        char currentDirCode = direction.getCode();

        if (!tryMove(currentDirCode) || checkGhostCollisions()) {
            char newDirCode = getValidDirection();
            direction = Direction.fromCode(newDirCode);
            snapToGrid();
            tryMove(newDirCode);
        }
    }

    /**
     * Tente de déplacer le fantôme dans une direction donnée
     * @param dirCode Code de direction ('L', 'R', 'U', 'D')
     * @return true si le déplacement a réussi, false en cas d'obstacle
     */
    protected boolean tryMove(char dirCode) {
        int newC = getNextC(dirCode);
        int newR = getNextR(dirCode);

        if (isValid(newC, newR)) {
            c = newC;
            r = newR;

            Direction newDir = Direction.fromCode(dirCode);
            if (!newDir.equals(direction)) {
                direction = newDir;
            }
            return true;
        }
        return false;
    }

    /**
     * Vérifie si une position est valide pour le fantôme
     * @param cPixel Position horizontale en pixels
     * @param rPixel Position verticale en pixels
     * @return true si la position est valide, false sinon
     */
    protected boolean isValid(int cPixel, int rPixel) {
        // Vérification des bords de l'écran
        if (cPixel < 0 || rPixel < 0 ||
                cPixel + GHOST_SIZE > lab.getWidth() ||
                rPixel + GHOST_SIZE > lab.getHeight()) {
            return false;
        }

        // Points de contrôle pour la détection des murs
        int[][] checkPoints = {
                {cPixel + GHOST_SIZE / 2, rPixel + GHOST_SIZE / 2}, // Centre
                {cPixel, rPixel}, // Coin supérieur gauche
                {cPixel + GHOST_SIZE, rPixel}, // Coin supérieur droit
                {cPixel, rPixel + GHOST_SIZE}, // Coin inférieur gauche
                {cPixel + GHOST_SIZE, rPixel + GHOST_SIZE} // Coin inférieur droit
        };

        for (int[] point : checkPoints) {
            int cellC = point[0] / Cell.SIZE;
            int cellR = point[1] / Cell.SIZE;

            if (cellC < 0 || cellR < 0 || cellC >= lab.cols || cellR >= lab.rows) {
                return false;
            }

            if (lab.maze[cellC][cellR].cellval == CellType.WALL.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Trouve une direction de déplacement valide de manière aléatoire
     * @return Code caractère de la direction valide trouvée
     */
    protected char getValidDirection() {
        Direction[] directions = Direction.values();
        shuffleDirections(directions);

        for (Direction dir : directions) {
            if (isValid(getNextC(dir.getCode()), getNextR(dir.getCode()))) {
                return dir.getCode();
            }
        }

        return 'U'; // Direction par défaut si aucune trouvée
    }

    /**
     * Aligne le fantôme sur la grille du labyrinthe
     */
    public void snapToGrid() {
        c = ((c + Cell.SIZE / 2) / Cell.SIZE) * Cell.SIZE;
        r = ((r + Cell.SIZE / 2) / Cell.SIZE) * Cell.SIZE;
    }

    /**
     * Mélange un tableau de directions pour un choix aléatoire
     * @param array Tableau de directions à mélanger
     */
    private void shuffleDirections(Direction[] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            Direction temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Calcule la prochaine position horizontale
     * @param direction Code de direction
     * @return Nouvelle position horizontale
     */
    private int getNextC(char direction) {
        return switch (direction) {
            case 'L' -> c - SPEED;
            case 'R' -> c + SPEED;
            default -> c;
        };
    }

    /**
     * Calcule la prochaine position verticale
     * @param direction Code de direction
     * @return Nouvelle position verticale
     */
    private int getNextR(char direction) {
        return switch (direction) {
            case 'U' -> r - SPEED;
            case 'D' -> r + SPEED;
            default -> r;
        };
    }

    /**
     * Vérifie les collisions potentielles avec d'autres fantômes
     * @return true si une collision est détectée, false sinon
     */
    protected boolean checkGhostCollisions() {
        for (Ghosts other : lab.personnages.getGhosts()) {
            if (other != this && distanceTo(other) < GHOST_SIZE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcule la distance avec un autre fantôme
     * @param other L'autre fantôme
     * @return Distance en pixels entre les deux fantômes
     */
    private double distanceTo(Ghosts other) {
        return Math.sqrt(Math.pow(c - other.c, 2) + Math.pow(r - other.r, 2));
    }

    /*********************************************** GETTERS & SETTERS ***************************************************/

    /**
     * @return La couleur du fantôme
     */
    public Characters.GhostColor getColor() {
        return color;
    }

    /**
     * @return La direction actuelle du fantôme
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Modifie la direction du fantôme
     * @param newDirection Nouvelle direction
     */
    public void setDirection(Direction newDirection) {
        this.direction = newDirection;
    }

    /**
     * @return L'image représentant le fantôme
     */
    public Image getImg() {
        return img;
    }
}