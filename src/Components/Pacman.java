import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.List;

public class Pacman {

    public int x, y;

    private static final int SPEED = 2;
    private static final int PACMAN_SIZE = Cell.size - 7; // Marge interne
    public static final int PACMAN_WIDTH = Cell.size * 3/4;  // 75% de la cellule
    public static final int PACMAN_HEIGHT = Cell.size * 3/4;

    private int lives;
    private final Labyrinth lab;
    private Direction direction;
    private Image img;
    private final Characters characters;
    private int animationCounter = 0;

    private boolean mouthOpen;

    public Pacman(int i, int j, Labyrinth lab, Image pacmanImage, Characters characters) {
        this.lab = lab;
        // Alignement initial garanti sur la grille
        this.x = (x / Cell.size) * Cell.size + Cell.size/2 - PACMAN_SIZE/2;
        this.y = (y / Cell.size) * Cell.size + Cell.size/2 - PACMAN_SIZE/2;
        this.img = pacmanImage;

        this.direction = Direction.RIGHT;
        this.mouthOpen = true;
        this.lives = 3;
        this.characters = characters;
    }

    public void update() {
        // Animation bouche
        if (++animationCounter % 8 == 0) {
            mouthOpen = !mouthOpen;
        }
        updateSprite();
    }

    public void handleInput(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_Z -> requestMove(Direction.UP);
            case KeyEvent.VK_S -> requestMove(Direction.DOWN);
            case KeyEvent.VK_Q -> requestMove(Direction.LEFT);
            case KeyEvent.VK_D -> requestMove(Direction.RIGHT);
        }
    }

    private void requestMove(Direction newDir) {
        // Pré-mouvement pour vérification
        int newX = x, newY = y;
        switch (newDir) {
            case UP -> newY -= SPEED;
            case DOWN -> newY += SPEED;
            case LEFT -> newX -= SPEED;
            case RIGHT -> newX += SPEED;
        }

        if (canMoveTo(newX, newY)) {
            direction = newDir;
            x = newX;
            y = newY;
        }
    }

    private boolean canMoveTo(int x, int y) {
        // Vérification des bords
        if (x < 0 || y < 0 || x >= lab.getWidth() || y >= lab.getHeight()) {
            return false;
        }

        // Conversion en coordonnées de cellule
        int cellX = x / Cell.size;
        int cellY = y / Cell.size;

        // Vérification des murs
        return lab.maze[cellX][cellY].cellval != CellType.WALL.getValue();
    }

    public boolean checkGhostCollisions(List<Ghosts> ghosts) {
        for (Ghosts ghost : ghosts) {
            if (Math.abs(x - ghost.x) < Cell.size/2 && Math.abs(y - ghost.y) < Cell.size/2) {
                loseLife();
                return true;
            }
        }
        return false;
    }

    private void loseLife() {
        lives--;
        if (lives > 0) {
            characters.initPacmanPosition(lab);
        } else {
            gameOver();
        }
    }

    private void gameOver() {
        System.out.println("Game Over!");
        // Ici vous pourrez ajouter une logique de fin de jeu
    }

    private void updateSprite() {
        img = characters.getPacmanImage(direction, mouthOpen);
    }

    /*********************************************** GETTERS ***********************************************************/
 
    public Image getSprite() {
        return img;
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
