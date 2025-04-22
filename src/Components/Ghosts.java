import java.util.Random;

public class Ghosts {
    public int x, y;
    private char direction;
    private static final int SPEED = 4;
    private static final int GHOST_SIZE = Cell.size - 5; // Marge interne
    private final Labyrinth lab;
    private final Random random = new Random();

    public Ghosts(int x, int y, Labyrinth lab) {
        this.lab = lab;
        // Alignement initial garanti sur la grille
        this.x = (x / Cell.size) * Cell.size + Cell.size/2 - GHOST_SIZE/2;
        this.y = (y / Cell.size) * Cell.size + Cell.size/2 - GHOST_SIZE/2;
        this.direction = getValidDirection(); // Direction valide dès le départ
    }

    public void move() {
        // Essayer la direction actuelle d'abord
        if (!tryMove(direction)) {
            direction = getValidDirection(); // Nouvelle direction valide
            tryMove(direction);
        }
    }

    private boolean tryMove(char dir) {
        int newX = getNextX(dir);
        int newY = getNextY(dir);
        
        if (isValid(newX, newY)) {
            x = newX;
            y = newY;
            return true;
        }
        return false;
    }

    private boolean isValid(int xPixel, int yPixel) {
        // Vérifie le centre et les bords du fantôme
        int[][] checkPoints = {
            {xPixel + GHOST_SIZE/2, yPixel + GHOST_SIZE/2}, // Centre
            {xPixel, yPixel}, // Coin supérieur gauche
            {xPixel + GHOST_SIZE, yPixel}, // Coin supérieur droit
            {xPixel, yPixel + GHOST_SIZE}, // Coin inférieur gauche
            {xPixel + GHOST_SIZE, yPixel + GHOST_SIZE} // Coin inférieur droit
        };
        
        for (int[] point : checkPoints) {
            int cellX = point[0] / Cell.size;
            int cellY = point[1] / Cell.size;
            
            if (cellX < 0 || cellY < 0 || cellX >= Labyrinth.COLS || cellY >= Labyrinth.ROWS) {
                return false;
            }
            
            if (lab.maze[cellX][cellY].cellval != CellType.POINT.getValue()) {
                return false;
            }
        }
        return true;
    }

    private char getValidDirection() {
        char[] directions = {'L', 'R', 'U', 'D'};
        shuffleArray(directions);
        
        for (char dir : directions) {
            if (isValid(getNextX(dir), getNextY(dir))) {
                return dir;
            }
        }
        return 'U'; // Fallback (normalement jamais atteint)
    }

    private int getNextX(char dir) {
        return x + (dir == 'L' ? -SPEED : dir == 'R' ? SPEED : 0);
    }

    private int getNextY(char dir) {
        return y + (dir == 'U' ? -SPEED : dir == 'D' ? SPEED : 0);
    }

    private void shuffleArray(char[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}