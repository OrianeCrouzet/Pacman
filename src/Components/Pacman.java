import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class Pacman  {

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
        Direction newDir = switch (keyCode) {
            case KeyEvent.VK_Z -> Direction.UP; 
            case KeyEvent.VK_S -> Direction.DOWN;
            case KeyEvent.VK_Q -> Direction.LEFT;
            case KeyEvent.VK_D -> Direction.RIGHT;
            default -> this.direction;
        };

        System.out.println("Key detected");
        
        // Mise à jour de la direction si elle a changé
        if (newDir != this.direction) {
            tryChangeDirection(newDir);
        }
    }    

    private void tryChangeDirection(Direction newDir) {
        // Teste le mouvement dans la nouvelle direction
        int testX = x, testY = y;
        
        switch (newDir) {
            case UP -> testY -= SPEED;
            case DOWN -> testY += SPEED;
            case LEFT -> testX -= SPEED;
            case RIGHT -> testX += SPEED;
        }

        if (canMoveTo(testX, testY)) {
            this.direction = newDir;
            snapToGrid();
        }
    }

    private void snapToGrid() {
        x = ((x + Cell.size/2) / Cell.size) * Cell.size;
        y = ((y + Cell.size/2) / Cell.size) * Cell.size;
    }

    public void move() {
        // Applique le mouvement selon la direction actuelle
        switch (direction) {
            case UP -> y -= SPEED;
            case DOWN -> y += SPEED;
            case LEFT -> x -= SPEED;
            case RIGHT -> x += SPEED;
        }
        
        // Garde Pacman dans les limites
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
            int cellX = point[0] / Cell.size;
            int cellY = point[1] / Cell.size;
            
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

    public boolean checkGhostCollisions(List<Ghosts> ghosts) {
        for (Ghosts ghost : ghosts) {
            if (Math.abs(x - ghost.x) < Cell.size/2 && Math.abs(y - ghost.y) < Cell.size/2) {
                loseLife();
                return true;
            }
        }
        return false;
    }

    public void loseLife() {
        lives--;
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
            this.x = tempX * Cell.size + Cell.size/2;
            this.y = tempY * Cell.size + Cell.size/2;
            this.direction = Direction.RIGHT;
            break;
        }
    }
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
