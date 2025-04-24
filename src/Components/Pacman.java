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
        }
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
