package components;

/**
 * L'énumération CellType représente les différents types de cellules composant le labyrinthe du jeu.
 * Chaque type de cellule est associé à une valeur numérique permettant son identification.
 * 
 * Cette énumération est utilisée principalement pour :
 * - La génération et l'interprétation du labyrinthe
 * - La détection des collisions
 * - La gestion des points à manger par Pacman
 */
public enum CellType {
    /**
     * Case contenant un point que Pacman peut manger.
     * Valeur associée : 1
     */
    POINT(1),
    
    /**
     * Mur infranchissable par les personnages.
     * Valeur associée : 0
     */
    WALL(0),
    
    /**
     * Case vide sans point (passage libre).
     * Valeur associée : 2
     */
    EMPTY(2),
    
    /**
     * Taille graphique d'une cellule en pixels.
     * Valeur associée : 40
     * 
     * Note : Cette valeur est utilisée pour le rendu graphique et les calculs de positionnement.
     */
    SIZE(40);

    private final int value;

    /**
     * Constructeur interne de l'énumération.
     * @param value La valeur numérique associée au type de cellule
     */
    CellType(int value) {
        this.value = value;
    }

    /**
     * Retourne la valeur numérique associée au type de cellule.
     * @return La valeur numérique du type de cellule
     */
    public int getValue() {
        return value;
    }
    
}