public enum CellType {
    POINT(0),   // Point à manger
    WALL(1),    // Mur
    EMPTY(2),   // Case vide
    SIZE(40);   // Taille d'une cellule

    private final int value;

    CellType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
