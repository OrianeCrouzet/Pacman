package components.entity;

import components.CellType;
import components.Characters;
import components.Direction;
import display.screen.Labyrinth;
import java.awt.Image;
import java.util.Random;

public class Ghosts {
    public int x, y;
    protected static final int SPEED = 3;
    protected static final int GHOST_SIZE = Cell.SIZE - 7; // Marge interne
    public static final int GHOST_WIDTH = Cell.SIZE * 3/4;  // 75% de la cellule
    public static final int GHOST_HEIGHT = Cell.SIZE * 3/4;
    protected final Labyrinth lab;

    protected final Characters.GhostColor color;
    protected Direction direction;
    protected final Image img;

    public Ghosts(int x, int y, Labyrinth lab, Image img, Characters.GhostColor color) {
        this.lab = lab;
        // Alignement initial garanti sur la grille
        this.x = (x / Cell.SIZE) * Cell.SIZE + Cell.SIZE /2 - GHOST_SIZE/2;
        this.y = (y / Cell.SIZE) * Cell.SIZE + Cell.SIZE /2 - GHOST_SIZE/2;
        this.img = img;
        this.color = color;
        this.direction = Direction.LEFT;
    }

    /**
     * Déplace le fantôme en essayant de conserver sa direction actuelle
     * ou en trouvant une nouvelle direction valide si nécessaire
     */
    public void move() {
        // Convertir la direction actuelle en code caractère
        char currentDirCode = direction.getCode();
        
        if (!tryMove(currentDirCode) || checkGhostCollisions()) {
            char newDirCode = getValidDirection();
            direction = Direction.fromCode(newDirCode);
            snapToGrid();
            tryMove(newDirCode);
        }
    }

    /** 
     * Tente de déplacer le fantôme dans la direction spécifiée
     * @param dirCode le code caractère de la direction ('L', 'R', 'U', 'D')
     * @return true si le déplacement a réussi, false sinon
     */
    protected boolean tryMove(char dirCode) {
        int newX = getNextX(dirCode);
        int newY = getNextY(dirCode);
        
        if (isValid(newX, newY)) {
            x = newX;
            y = newY;
            
            // Mettre à jour la direction (si différente)
            Direction newDir = Direction.fromCode(dirCode);
            if (!newDir.equals(direction)) {
                direction = newDir;
            }
            return true;
        }
        return false;
    }

    /**
     * 
     * @param xPixel
     * @param yPixel
     * @return
     */
    protected boolean isValid(int xPixel, int yPixel) {
        // 1. Vérification des bords de l'écran
        if (xPixel < 0 || yPixel < 0 ||
            xPixel + GHOST_SIZE > lab.getWidth() || 
            yPixel + GHOST_SIZE > lab.getHeight()) {
            return false; // Considère les bords comme des murs
        }
        
        // 2. Vérification des murs du labyrinthe
        int[][] checkPoints = {
            {xPixel + GHOST_SIZE/2, yPixel + GHOST_SIZE/2}, // Centre
            {xPixel, yPixel}, // Coin supérieur gauche
            {xPixel + GHOST_SIZE, yPixel}, // Coin supérieur droit
            {xPixel, yPixel + GHOST_SIZE}, // Coin inférieur gauche
            {xPixel + GHOST_SIZE, yPixel + GHOST_SIZE} // Coin inférieur droit
        };
        
        for (int[] point : checkPoints) {
            int cellX = point[0] / Cell.SIZE;
            int cellY = point[1] / Cell.SIZE;
            
            // Vérifie si on est dans les limites du labyrinthe
            if (cellX < 0 || cellY < 0 || cellX >= Labyrinth.COLS || cellY >= Labyrinth.ROWS) {
                return false;
            }
            
            if (lab.maze[cellX][cellY].cellval == CellType.WALL.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Trouve une direction valide de manière aléatoire
     * @return le code caractère de la direction ('L', 'R', 'U' ou 'D')
     */
    protected char getValidDirection() {
        // On mélange les directions pour un choix aléatoire
        Direction[] directions = Direction.values();
        shuffleDirections(directions);
        
        // On teste chaque direction
        for (Direction dir : directions) {
            if (isValid(getNextX(dir.getCode()), getNextY(dir.getCode()))) {
                return dir.getCode();
            }
        }
        
        return 'U'; // Fallback (normalement jamais atteint si le fantôme n'est pas bloqué)
    }

    public void snapToGrid() {
        x = ((x + Cell.SIZE /2) / Cell.SIZE) * Cell.SIZE;
        y = ((y + Cell.SIZE /2) / Cell.SIZE) * Cell.SIZE;
    }

    /**
     * Mélange un tableau de directions
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

    private int getNextX(char direction) {
        return switch (direction) {
            case 'L' -> x - SPEED;
            case 'R' -> x + SPEED;
            default -> x;
        }; // Pas de changement horizontal pour U/D
    }
    
    private int getNextY(char direction) {
        return switch (direction) {
            case 'U' -> y - SPEED;
            case 'D' -> y + SPEED;
            default -> y;
        }; // Pas de changement vertical pour L/R
    }


    /**
     * Vérifie si les fantômes finiront par se croiser ou pas
     * @return : true si les fantômes risquent de se croiser
     *         : false sinon
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
     * Calcul la distance du fantôme courant par rapport à un autre
     * @param other : l'autre fantôme avec lequel on mesure notre distance
     * @return : la distance entre les deux fantômes
     */
    private double distanceTo(Ghosts other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

      /*********************************************** GETTERS ***********************************************************/

    public Characters.GhostColor getColor() {
        return color;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction newDirection) {
        this.direction = newDirection;
    }

    public Image getImg() {
        return img;
    }
}