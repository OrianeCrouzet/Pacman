package display.screen;

import static Util.FileHandler.readFile;
import static Util.FileHandler.streamStringToIntDoubleArray;
import components.CellType;
import components.Characters;
import components.Direction;
import components.entity.Cell;
import components.entity.Ghosts;
import components.entity.Pacman;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.*;

public class Labyrinth extends JPanel {

    private static final int CELL_SIZE = CellType.SIZE.getValue();
    private static final int DOT_SIZE = 6;
    private static final int DOT_OFFSET = CELL_SIZE / 2 - 3;
    public Characters personnages = new Characters();
    public int rows = 0, cols = 0;
    public int SCREEN_WIDTH;
    public int SCREEN_HEIGHT;
    public Cell[][] maze;

    private List<Edge> edges = new ArrayList<>();
    private UnionFind uf;


    private int dotLeft;

    /**
     * Constructeur de Labyrinth.
     * Initialise les écouteurs clavier pour contrôler Pacman et demande le focus sur la fenêtre.
     */
    public Labyrinth() {
        setupListener();
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    /**
     * Calcule et définit la taille de l'écran en fonction du nombre de colonnes et lignes du labyrinthe.
     */
    private void setSCREEN() {
        SCREEN_WIDTH = cols * Cell.SIZE + 16;
        SCREEN_HEIGHT = rows * Cell.SIZE + 16;
    }

    /**
     * Choisit et génère un labyrinthe, soit aléatoire soit classique.
     *
     * @param random true pour un labyrinthe aléatoire, false pour le labyrinthe classique
     */
    public void chooseMaze(boolean random) {
        if (random) {
            rows = 18;
            cols = 28;
            emptyMaze();
            setSCREEN();
            generateMaze();
        } else {
            rows = 31;
            cols = 28;
            emptyMaze();
            setSCREEN();
            classicalMaze();
        }
    }

    /**
     * Charge et initialise le labyrinthe classique à partir d'un fichier texte.
     */
    private void classicalMaze() {
        String classicMapPath = "ressources/map/classicMap.txt";
        initCustomMaze(streamStringToIntDoubleArray(Objects.requireNonNull(readFile(classicMapPath))));
        int[] pacman = {13, 23};
        int[][] ghosts = {{13, 11}, {9, 14}, {16, 17}, {16, 11}};
        putCharacters(pacman, ghosts);
    }

    /**
     * Place Pacman et les fantômes dans le labyrinthe aux positions spécifiées.
     *
     * @param pacman position initiale de Pacman (ligne, colonne)
     * @param ghosts positions initiales des fantômes
     */
    private void putCharacters(int[] pacman, int[][] ghosts) {
        personnages.putPacman(this, pacman[0], pacman[1], Direction.LEFT);
        personnages.initGhostsPositions(this, ghosts);
    }

    /**
     * Initialise le labyrinthe personnalisé à partir d'un tableau d'entiers.
     *
     * @param mapInt représentation du labyrinthe sous forme de tableau d'entiers
     */
    private void initCustomMaze(int[][] mapInt) {
        for (int c = 0; c < mapInt.length; c++) {
            for (int r = 0; r < mapInt[c].length; r++) {
                intToCell(mapInt[c][r], maze[r][c]);
            }
        }
    }

    /**
     * Convertit un entier représentant un type de cellule en un objet Cell correspondant.
     *
     * @param value valeur entière représentant un type de cellule
     * @param cell cellule du labyrinthe à mettre à jour
     */
    private void intToCell(int value, Cell cell) {
        switch (value) {
            case 0 -> cell.setCellVal(CellType.WALL);
            case 1 -> cell.setCellVal(CellType.POINT);
            case 2 -> cell.setCellVal(CellType.EMPTY);
            case 3 -> cell.setCellVal(CellType.POINT); // Super Dot
            case 4 -> cell.setCellVal(CellType.EMPTY); // Spawn Point
            case 5 -> cell.setCellVal(CellType.WALL); // Secret Passage
            case 9 -> cell.setCellVal(CellType.EMPTY); // Ghost Spawn
            default -> throw new IllegalStateException("Unexpected value: " + value);
        }
    }

    /**
     * Initialise les positions de Pacman et des fantômes après la création du labyrinthe.
     */
    public void initialiseCharactersInMaze() {
        personnages.initPacmanPosition(this);
        personnages.initGhostsPositions(this, null);
    }

    /**
     * Configure les touches du clavier pour déplacer Pacman dans le labyrinthe.
     */
    public void setupListener() {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "moveUp");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "moveLeft");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveRight");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveDown");

        am.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                personnages.getPacman().handleInput(Direction.UP);
            }
        });
        am.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                personnages.getPacman().handleInput(Direction.LEFT);
            }
        });
        am.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                personnages.getPacman().handleInput(Direction.RIGHT);
            }
        });
        am.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                personnages.getPacman().handleInput(Direction.DOWN);
            }
        });
    }

    /**
     * Génère un labyrinthe aléatoire en utilisant l'algorithme de Kruskal.
     */
    private void generateMaze() {
        generateAllPossibleWalls();
        shuffleEdges();
        createMazePaths();
        fixCornerCases();
        fixBorderCases();
        fixCenterCases();
        initialiseCharactersInMaze();
    }

    /**
     * Affiche la représentation du labyrinthe dans la console pour du débogage.
     */
    public void printMaze() {
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                System.out.print(" " + maze[c][r] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Compte le nombre de points (dots) encore présents dans le labyrinthe.
     */
    public void checkDotLeft() {
        dotLeft = 0;
        for (Cell[] c : maze) {
            for (Cell r : c) {
                if (r.cellval == 1) {
                    dotLeft++;
                }
            }
        }
    }

    /**
     * Initialise un labyrinthe vide avec uniquement des cellules par défaut.
     */
    private void emptyMaze() {
        maze = new Cell[cols][rows];
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                maze[c][r] = new Cell();
            }
        }
    }


    /**
     * Fonction qui génère toutes les parois possibles entre cellules voisines
     */
    private void generateAllPossibleWalls() {
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (c < cols - 1) {
                    edges.add(new Edge(r * cols + c, r * cols + (c + 1))); // Mur horizontal
                }
                if (r < cols - 1) {
                    edges.add(new Edge(r * cols + c, (r + 1) * cols + c)); // Mur vertical
                }
            }
        }
    }

    /**
     * Fonction qui mélange les parois pour randomisation
     */
    private void shuffleEdges() {
        Collections.shuffle(edges);
    }

    /**
     * Fonction qui crée les chemins principaux avec Kruskal
     */
    private void createMazePaths() {
        if (cols>rows){
            uf = new UnionFind(cols * cols);
        }else {
            uf = new UnionFind(rows * rows);
        }
        for (Edge edge : edges) {
            int root1 = uf.find(edge.node1);
            int root2 = uf.find(edge.node2);

            if (root1 != root2) {
                uf.union(root1, root2);
                connectCells(edge);
            }
        }
    }

    /**
     * Fonction qui connecte deux cellules adjacentes
     *
     * @param edge : l'arête commune aux deux cellules adjacentes
     */
    private void connectCells(Edge edge) {
        int c1 = edge.node1 % cols;
        int r1 = edge.node1 / cols;
        int c2 = edge.node2 % cols;
        int r2 = edge.node2 / cols;

        if (c1 == c2) {  // Mur vertical
            maze[Math.min(c1, c2)][r1].setCellVal(CellType.POINT);
        } else {  // Mur horizontal
            maze[c1][Math.min(r1, r2)].setCellVal(CellType.POINT);
        }
    }

    /**
     * Fonction qui corrige les cas particuliers des coins
     */
    private void fixCornerCases() {
        // Haut-gauche
        fixCorner(0, 0, 0, 1, 1, 0);

        // Haut-droite
        fixCorner(cols - 1, 0, cols - 1, 1, cols - 2, 0);

        // Bas-droite
        fixCorner(cols - 1, rows - 1, cols - 1, rows - 2, cols - 2, rows - 1);

        // Bas-gauche
        fixCorner(0, rows - 1, 0, rows - 2, 1, rows - 1);
    }

    /**
     * Fonction qui corrige le coin particulier en question
     */
    private void fixCorner(int cornerC, int cornerR, int adjC1, int adjR1, int adjC2, int adjR2) {
        if (maze[cornerC][cornerR].cellval == CellType.POINT.getValue() &&
                maze[adjC1][adjR1].cellval == CellType.WALL.getValue() &&
                maze[adjC2][adjR2].cellval == CellType.WALL.getValue()) {
            maze[adjC1][adjR1].setCellVal(CellType.POINT);
        }
    }

    /**
     * Fonction qui corrige les cas des bords
     */
    private void fixBorderCases() {
        // Bordure haut/bas
        for (int c = 1; c < cols - 1; c++) {
            fixBorderCell(c, 0, c, 1);       // Haut
            fixBorderCell(c, rows - 1, c, rows - 2); // Bas
        }

        // Bordure gauche/droite
        for (int r = 1; r < rows - 1; r++) {
            fixBorderCell(0, r, 1, r);       // Gauche
            fixBorderCell(cols - 1, r, cols - 2, r); // Droite
        }
    }

    /**
     * Corrige les bords du labyrinthe en transformant un mur adjacent en point si nécessaire.
     *
     * @param c colonne de la cellule actuelle
     * @param r ligne de la cellule actuelle
     * @param adjC colonne de la cellule adjacente
     * @param adjR ligne de la cellule adjacente
     */
    private void fixBorderCell(int c, int r, int adjC, int adjR) {
        if (maze[c][r].cellval == CellType.POINT.getValue() &&
                maze[adjC][adjR].cellval == CellType.WALL.getValue()) {
            maze[adjC][adjR].setCellVal(CellType.POINT);
        }
    }


    /**
     * Corrige les cas particuliers au centre du labyrinthe.
     * 
     * Transforme un mur adjacent en point si une cellule est entourée de murs
     * de toutes parts (haut, bas, gauche, droite).
     */
    private void fixCenterCases() {
        for (int c = 1; c < cols - 1; c++) {
            for (int r = 1; r < rows - 1; r++) {
                if (maze[c][r].cellval == CellType.POINT.getValue() &&
                        maze[c - 1][r].cellval == CellType.WALL.getValue() &&
                        maze[c + 1][r].cellval == CellType.WALL.getValue() &&
                        maze[c][r - 1].cellval == CellType.WALL.getValue() &&
                        maze[c][r + 1].cellval == CellType.WALL.getValue()) {
                    maze[c - 1][r].setCellVal(CellType.POINT);
                }
            }
        }
    }



    /**
     * Classe représentant une arête entre deux cellules dans le labyrinthe.
     *
     * Utilisée pour l'algorithme de génération du labyrinthe (Kruskal).
     */
    private static class Edge {
        int node1, node2;

        /**
         * Constructeur d'une arête reliant deux cellules.
         *
         * @param node1 première cellule (index)
         * @param node2 deuxième cellule (index)
         */
        Edge(int node1, int node2) {
            this.node1 = node1;
            this.node2 = node2;
        }
    }


    /**
     * Union-Find (Disjoint-Set) pour l'algorithme de Kruskal.
     *
     * Permet de gérer les ensembles disjoints et de vérifier si deux cellules
     * sont connectées dans le labyrinthe.
     */
    private static class UnionFind {
        private final int[] parent;

        /**
         * Initialise une structure Union-Find avec un nombre donné d'éléments.
         *
         * @param size nombre d'éléments à gérer
         */
        public UnionFind(int size) {
            parent = new int[size];
            for (int c = 0; c < size; c++) {
                parent[c] = c;
            }
        }

        /**
         * Trouve le représentant (racine) de l'ensemble auquel appartient un élément.
         * Utilise la compression de chemin pour accélérer les recherches.
         *
         * @param c élément dont on cherche la racine
         * @return racine de l'élément
         */
        public int find(int c) {
            if (parent[c] != c) {
                parent[c] = find(parent[c]);  // Compression de chemin
            }
            return parent[c];
        }

        /**
         * Fusionne deux ensembles contenant chacun un des éléments donnés.
         *
         * @param c premier élément
         * @param r second élément
         */
        public void union(int c, int r) {
            int rootC = find(c);
            int rootR = find(r);
            if (rootC != rootR) {
                parent[rootC] = rootR;
            }
        }
    }



    /**
     * Dessine le labyrinthe, les points, les fantômes et Pacman sur le panneau.
     *
     * @param g l'objet Graphics utilisé pour dessiner
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessin du labyrinthe
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (maze[c][r].cellval == CellType.POINT.getValue()) {
                    g.setColor(Color.BLACK);
                    g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.WHITE);
                    g.fillOval(
                            c * CELL_SIZE + DOT_OFFSET,
                            r * CELL_SIZE + DOT_OFFSET,
                            DOT_SIZE,
                            DOT_SIZE
                    );
                } else if (maze[c][r].cellval == CellType.EMPTY.getValue()) {
                    g.setColor(Color.BLACK);
                    g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // Dessin des fantômes
        for (Ghosts ghost : personnages.getGhosts()) {
            if (ghost != null) {
                int drawC = ghost.c + (CellType.SIZE.getValue() - Ghosts.GHOST_WIDTH) / 2;
                int drawR = ghost.r + (CellType.SIZE.getValue() - Ghosts.GHOST_HEIGHT) / 2;

                Image ghostImage = personnages.getGhostImage(
                        ghost.getColor(),
                        ghost.getDirection()
                );

                if (ghostImage != null) {
                    g.drawImage(ghostImage,
                            drawC, drawR,
                            Ghosts.GHOST_WIDTH, Ghosts.GHOST_HEIGHT,
                            null);
                }
            }
        }

        // Dessin de Pacman
        Pacman pacman = personnages.getPacman();
        if (pacman != null) {
            int drawC = pacman.c + (CellType.SIZE.getValue() - Pacman.PACMAN_WIDTH) / 2;
            int drawR = pacman.r + (CellType.SIZE.getValue() - Pacman.PACMAN_HEIGHT) / 2;

            Image pacmanImage = personnages.getPacmanImage(
                    pacman.getDirection(),
                    pacman.getMouthOpening()
            );

            if (pacmanImage != null) {
                g.drawImage(pacmanImage,
                        drawC, drawR,
                        Pacman.PACMAN_WIDTH, Pacman.PACMAN_HEIGHT,
                        null);
            }
        }
    }

    /**
     * Retourne l'objet Characters qui contient Pacman et les fantômes.
     *
     * @return l'objet Characters utilisé dans le labyrinthe
     */
    public Characters getPersonnages() {
        return personnages;
    }

    /**
     * Retourne la largeur totale du labyrinthe en pixels.
     *
     * @return largeur du labyrinthe
     */
    @Override
    public int getWidth() {
        return cols * CELL_SIZE;
    }

    /**
     * Retourne la hauteur totale du labyrinthe en pixels.
     *
     * @return hauteur du labyrinthe
     */
    @Override
    public int getHeight() {
        return rows * CELL_SIZE;
    }

    /**
     * Réinitialise entièrement le labyrinthe et les personnages.
     * Crée un nouveau tableau vide et un nouvel ensemble d'arêtes.
     */
    public void reset() {
        personnages = new Characters();
        emptyMaze();
        edges = new ArrayList<>();
        uf = new UnionFind(rows * cols);
    }

    /**
     * Calcule et retourne le nombre de points (dots) restants dans le labyrinthe.
     *
     * @return nombre de points restants
     */
    public int getDotLeft() {
        checkDotLeft();
        return dotLeft;
    }

}
