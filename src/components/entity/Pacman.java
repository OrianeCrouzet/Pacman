package components.entity;

import components.CellType;
import components.Characters;
import components.Direction;
import display.screen.Labyrinth;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Pacman {

    public int c, r;

    private static final int SPEED = 3;
    private static final int PACMAN_SIZE = Cell.SIZE - 7; // Marge interne
    public static final int PACMAN_WIDTH = Cell.SIZE * 3 / 4;  // 75% de la cellule
    public static final int PACMAN_HEIGHT = Cell.SIZE * 3 / 4;

    private static final int POINT_VALUE = 10;

    private int score = 0;

    private int lives = 1;
    private final Labyrinth lab;
    private Direction direction;
    private Image img;
    private final Characters characters;
    private int animationCounter = 0;

    private boolean mouthOpen;

    public Pacman(int c, int r, Labyrinth lab, Image pacmanImage, Characters characters,Direction direction) {
        this.lab = lab;
        // Alignement initial garanti sur la grille
        this.c = ((c / Cell.SIZE) * Cell.SIZE) + (Cell.SIZE / 2) -( PACMAN_SIZE / 2);
        this.r = ((r / Cell.SIZE) * Cell.SIZE) + (Cell.SIZE / 2) - (PACMAN_SIZE / 2);
        this.img = pacmanImage;

        this.direction = direction;
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
        c = ((c + Cell.SIZE / 2) / Cell.SIZE) * Cell.SIZE;
        r = ((r + Cell.SIZE / 2) / Cell.SIZE) * Cell.SIZE;
    }

    public void move() {
        // Calcule la nouvelle position
        int newC = c;
        int newR = r;

        switch (direction) {
            case UP -> newR -= SPEED;
            case DOWN -> newR += SPEED;
            case LEFT -> newC -= SPEED;
            case RIGHT -> newC += SPEED;
        }

        // Vérifie la collision avant de déplacer
        if (canMoveTo(newC, newR)) {
            c = newC;
            r = newR;
            checkPointCollision();
        }

        // Garantit qu'on reste dans les limites de l'écran
        c = Math.max(0, Math.min(c, lab.getWidth() - PACMAN_WIDTH));
        r = Math.max(0, Math.min(r, lab.getHeight() - PACMAN_HEIGHT));
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
                {xPixel + PACMAN_SIZE / 2, yPixel + PACMAN_SIZE / 2}, // Centre
                {xPixel, yPixel}, // Coin supérieur gauche
                {xPixel + PACMAN_SIZE, yPixel}, // Coin supérieur droit
                {xPixel, yPixel + PACMAN_SIZE}, // Coin inférieur gauche
                {xPixel + PACMAN_SIZE, yPixel + PACMAN_SIZE} // Coin inférieur droit
        };

        for (int[] point : checkPoints) {
            int cellC = point[0] / Cell.SIZE;
            int cellR = point[1] / Cell.SIZE;

            // Vérifie si on est dans les limites du labyrinthe
            if (cellC < 0 || cellR < 0 || cellC >= lab.cols || cellR >= lab.rows) {
                return false;
            }

            // Vérifie si on ne rentre pas en collisions avec un mur
            if (lab.maze[cellC][cellR].cellval == CellType.WALL.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void checkPointCollision() {
        // On prend le centre de Pacman pour la détection
        int centerC = c + PACMAN_WIDTH / 2;
        int centerY = r + PACMAN_HEIGHT / 2;

        // Conversion en coordonnées de grille
        int gridC = centerC / Cell.SIZE;
        int gridY = centerY / Cell.SIZE;

        // Vérification des limites
        if (gridC >= 0 && gridC < lab.cols &&
                gridY >= 0 && gridY < lab.rows) {

            Cell cell = lab.maze[gridC][gridY];

            if (cell.cellval == CellType.POINT.getValue()) {
                cell.cellval = CellType.EMPTY.getValue();
                score += POINT_VALUE;

                // Debug
                System.out.println("Point mangé en [" + gridC + "," + gridY + "]");
                System.out.println("Position Pacman: " + c + "," + r);

                // Rafraîchissement précis
                lab.repaint(gridC * Cell.SIZE,
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
            if (Math.abs(c - ghost.c) < Cell.SIZE / 2 && Math.abs(r - ghost.r) < Cell.SIZE / 2) {
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
            int tempC = rand.nextInt(lab.cols);
            int tempR = rand.nextInt(lab.rows);

            if (lab.maze[tempC][tempR].cellval != CellType.WALL.getValue()) {
                this.c = tempC * Cell.SIZE + Cell.SIZE / 2;
                this.r = tempR * Cell.SIZE + Cell.SIZE / 2;
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

    public int getScore() {
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
