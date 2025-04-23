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
}