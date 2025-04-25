package components.entity;

import components.CellType;
import components.Characters;
import components.Direction;
import display.screen.Labyrinth;

import javax.swing.*;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class Pacman  {

    public int x, y;

    private static final int SPEED = 3;
    private static final int PACMAN_SIZE = Cell.SIZE - 7; // Marge interne
    public static final int PACMAN_WIDTH = Cell.SIZE * 3/4;  // 75% de la cellule
    public static final int PACMAN_HEIGHT = Cell.SIZE * 3/4;

    private static final int POINT_VALUE = 10;
    //TODO
    private int score = 0;

    private int lives = 1;
    private final Labyrinth lab;
    private Direction direction;
    private Image img;
    private final Characters characters;
    private int animationCounter = 0;

    private boolean mouthOpen;

    public Pacman(int i, int j, Labyrinth lab, Image pacmanImage, Characters characters) {
        this.lab = lab;
        // Alignement initial garanti sur la grille
        this.x = (x / Cell.SIZE) * Cell.SIZE + Cell.SIZE /2 - PACMAN_SIZE/2;
        this.y = (y / Cell.SIZE) * Cell.SIZE + Cell.SIZE /2 - PACMAN_SIZE/2;
        this.img = pacmanImage;

        this.direction = Direction.RIGHT;
        this.mouthOpen = true;
        this.characters = characters;

    }

    public void update() {
        // Animation bouche
        if (++animationCounter % 8 == 0) {
            mouthOpen = !mouthOpen;
        }
        updateSprite();
    }

    public void handleInput(Direction direction) {
        //TODO passer avec inputMap()
        Direction newDir = direction;

        System.out.println("Key detected");
        
        // Mise à jour de la direction si elle a changé
        if (newDir != this.direction) {
            this.direction = newDir;
            snapToGrid();
            move();
        }
    }

    public void snapToGrid() {
        x = ((x + Cell.SIZE /2) / Cell.SIZE) * Cell.SIZE;
        y = ((y + Cell.SIZE /2) / Cell.SIZE) * Cell.SIZE;
    }

    public void move() {
        // Calcule la nouvelle position
        int newX = x;
        int newY = y;
        
        switch (direction) {
            case UP -> newY -= SPEED;
            case DOWN -> newY += SPEED;
            case LEFT -> newX -= SPEED;
            case RIGHT -> newX += SPEED;
        }
        
        // Vérifie la collision avant de déplacer
        if (canMoveTo(newX, newY)) {
            x = newX;
            y = newY;
            checkPointCollision();
        } 
        
        // Garantit qu'on reste dans les limites de l'écran
        x = Math.max(0, Math.min(x, lab.getWidth() - PACMAN_WIDTH));
        y = Math.max(0, Math.min(y, lab.getHeight() - PACMAN_HEIGHT));
    }
    
    private boolean canMoveTo(int xPixel, int yPixel) {
        // 1. Vérification des bords de l'écran
        if (xPixel < 0 || yPixel < 0 ||
            xPixel + PACMAN_SIZE > lab.getWidth() || 
            yPixel + PACMAN_SIZE > lab.getHeight()) {
            return false; // Considère les bords comme des murs
        }
        
        // 2. Vérification des murs du labyrinthe
        int[][] checkPoints = {
            {xPixel + PACMAN_SIZE/2, yPixel + PACMAN_SIZE/2}, // Centre
            {xPixel, yPixel}, // Coin supérieur gauche
            {xPixel + PACMAN_SIZE, yPixel}, // Coin supérieur droit
            {xPixel, yPixel + PACMAN_SIZE}, // Coin inférieur gauche
            {xPixel + PACMAN_SIZE, yPixel + PACMAN_SIZE} // Coin inférieur droit
        };
        
        for (int[] point : checkPoints) {
            int cellX = point[0] / Cell.SIZE;
            int cellY = point[1] / Cell.SIZE;
            
            // Vérifie si on est dans les limites du labyrinthe
            if (cellX < 0 || cellY < 0 || cellX >= Labyrinth.COLS || cellY >= Labyrinth.ROWS) {
                return false;
            }
            
            // Vérifie si on ne rentre pas en collisions avec un mur
            if (lab.maze[cellX][cellY].cellval == CellType.WALL.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void checkPointCollision() {
        // On prend le centre de Pacman pour la détection
        int centerX = x + PACMAN_WIDTH / 2;
        int centerY = y + PACMAN_HEIGHT / 2;
        
        // Conversion en coordonnées de grille
        int gridX = centerX / Cell.SIZE;
        int gridY = centerY / Cell.SIZE;
        
        // Vérification des limites
        if (gridX >= 0 && gridX < Labyrinth.COLS &&
            gridY >= 0 && gridY < Labyrinth.ROWS) {
            
            Cell cell = lab.maze[gridX][gridY];
            
            if (cell.cellval == CellType.POINT.getValue()) {
                cell.cellval = CellType.EMPTY.getValue();
                score += POINT_VALUE;
                
                // Debug
                System.out.println("Point mangé en [" + gridX + "," + gridY + "]");
                System.out.println("Position Pacman: " + x + "," + y);
                
                // Rafraîchissement précis
                lab.repaint(gridX * Cell.SIZE,
                                 gridY * Cell.SIZE,
                                 Cell.SIZE,
                                 Cell.SIZE);
            }
        }
    }
    
    private void checkWinCondition() {
        boolean allPointsEaten = true;
        for (Cell[] row : lab.maze) {
            for (Cell cell : row) {
                if (cell.cellval == CellType.POINT.getValue()) {
                    allPointsEaten = false;
                    break;
                }
            }
        }
        if (allPointsEaten) {
            System.out.println("Tous les points ont été mangés!");
            // Ici vous pourriez déclencher un écran de victoire
        }
    }

    public boolean checkGhostCollisions(List<Ghosts> ghosts) {
        for (Ghosts ghost : ghosts) {
            if (Math.abs(x - ghost.x) < Cell.SIZE /2 && Math.abs(y - ghost.y) < Cell.SIZE /2) {
                loseLife();
                return true;
            }
        }
        return false;
    }

    public void loseLife() {
        lives--;
        System.out.println("Lives : " + lives);
    }

    private void updateSprite() {
        img = characters.getPacmanImage(direction, mouthOpen);
    }

    public void respawn() {
    // Réinitialisation position
    Random rand = new Random();
    while (true) {
        int tempX = rand.nextInt(Labyrinth.COLS);
        int tempY = rand.nextInt(Labyrinth.ROWS);
        
        if (lab.maze[tempX][tempY].cellval == CellType.POINT.getValue()) {
            this.x = tempX * Cell.SIZE + Cell.SIZE /2;
            this.y = tempY * Cell.SIZE + Cell.SIZE /2;
            this.direction = Direction.RIGHT;
            snapToGrid();
            break;
        }
    }
}

    /*********************************************** GETTERS ***********************************************************/
 
    public Image getSprite() {
        return img;
    }

    public int getScore(){
        return score;
    }

    public int getLives() {
        return lives;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean getMouthOpening() {
        return mouthOpen;
    }

}
