package components;

/**
 * L'énumération Direction représente les quatre directions cardinales utilisées dans le jeu.
 * Chaque direction est associée à :
 * - Un code caractère pour la représentation textuelle
 * - Des méthodes pour obtenir les déplacements (dx, dy) correspondants
 * - Une méthode pour obtenir la direction opposée
 * 
 * Cette énumération est utilisée pour gérer le déplacement des personnages (Pacman et fantômes)
 * et l'orientation des sprites graphiques.
 */
public enum Direction {
    LEFT('L'),
    RIGHT('R'),
    UP('U'),
    DOWN('D');

    private final char code;

    Direction(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }

    public static Direction fromCode(char code) {
        for (Direction dir : values()) {
            if (dir.code == code) {
                return dir;
            }
        }
        return UP; // Fallback
    }

    public int dx() {
        return switch (this) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
    }
    
    public int dy() {
        return switch (this) {
            case UP -> -1;
            case DOWN -> 1;
            default -> 0;
        };
    }

    public Direction oppositeDirection(){
        return switch (fromCode(code)){
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            default -> throw new RuntimeException("direction not availidable");
        };
    }
}