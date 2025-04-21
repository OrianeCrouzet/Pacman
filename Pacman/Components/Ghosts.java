import java.util.Random;

public class Ghosts {
    public int x;
    public int y;
    private char direction;
    static int speed = 4;
    Labyrinth lab;
    private static final int GHOST_SIZE = Cell.size - 2; // Slightly smaller than cell
    private Random random = new Random();

    public Ghosts(int x, int y, Labyrinth lab) {
        this.x = x;
        this.y = y;
        this.direction = 'L';
        this.lab = lab;
    }

    public void move() {
        int newX = x;
        int newY = y;
        
        // Calculate proposed movement
        switch (direction) {
            case 'L': newX -= speed; break;
            case 'R': newX += speed; break;
            case 'U': newY -= speed; break;
            case 'D': newY += speed; break;
        }
        
        if (isValid(newX, newY)) {
            // Move if path is clear
            x = newX;
            y = newY;
        } else {
            // Hit a wall - snap to grid and choose random new direction
            snapToGrid();
            direction = getRandomDirection();
        }
    }
    
    private char getRandomDirection() {
        // Try all directions in random order until finding a valid one
        char[] directions = {'L', 'R', 'U', 'D'};
        shuffleArray(directions);
        
        for (char dir : directions) {
            if (isValid(getNextX(dir), getNextY(dir))) {
                return dir;
            }
        }
        
        // If all else fails, reverse direction (shouldn't happen in proper maze)
        return getOppositeDirection(direction);
    }
    
    private void shuffleArray(char[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
    
    private int getNextX(char dir) {
        return x + (dir == 'L' ? -speed : dir == 'R' ? speed : 0);
    }
    
    private int getNextY(char dir) {
        return y + (dir == 'U' ? -speed : dir == 'D' ? speed : 0);
    }
    
    private char getOppositeDirection(char dir) {
        switch (dir) {
            case 'L': return 'R';
            case 'R': return 'L';
            case 'U': return 'D';
            case 'D': return 'U';
            default: return dir;
        }
    }
    
    private void snapToGrid() {
        if (direction == 'L' || direction == 'R') {
            x = (x / Cell.size) * Cell.size;
        } else {
            y = (y / Cell.size) * Cell.size;
        }
    }
    
    public boolean isValid(int xPixel, int yPixel) {
        // Check all four corners of ghost's bounding box
        int[] xChecks = {xPixel, xPixel + GHOST_SIZE};
        int[] yChecks = {yPixel, yPixel + GHOST_SIZE};
        
        for (int xCheck : xChecks) {
            for (int yCheck : yChecks) {
                int cellX = xCheck / Cell.size;
                int cellY = yCheck / Cell.size;
                
                if (xPixel <0|| yPixel < 0 || cellX >= Labyrinth.COLS || cellY >= Labyrinth.ROWS) {
                    return false;
                }
                
                if (lab.maze[cellX][cellY].cellval != 1) {
                    return false;
                }
            }
        }
        return true;
    }
}