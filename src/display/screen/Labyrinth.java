package display.screen;

import components.CellType;
import components.Characters;
import components.Direction;
import components.entity.Cell;
import components.entity.Ghosts;
import components.entity.Pacman;
import display.MainContainer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class Labyrinth extends JPanel {

    public Characters personnages = new Characters();


    public static final int ROWS = 20, COLS = 20;
    public static final int SCREEN_WIDTH = Labyrinth.COLS * Cell.SIZE + 16;
    public static final int SCREEN_HEIGHT = Labyrinth.COLS * Cell.SIZE + 16;

    private static final int CELL_SIZE = CellType.SIZE.getValue();
    private static final int DOT_SIZE = 6;
    private static final int DOT_OFFSET = CELL_SIZE / 2 - 3;

    public Cell[][] maze = new Cell[ROWS][COLS];

    private List<Edge> edges = new ArrayList<>();
    private UnionFind uf = new UnionFind(ROWS * COLS);

    private int dotLeft;

    public Labyrinth() {
        emptyMaze();
        //TODO déplacer dans un handler mais conflit de fenêtre

        setupListener();
        this.setFocusable(true);
        this.requestFocusInWindow();
    }


    /**
     *
     */
    public void initialiseCharactersInMaze() {
        personnages.initPacmanPosition(this);
        personnages.initGhostsRandomPositions(this);
    }

    /*Set cell state */

    /**
     *
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
     * Algorithme pour générer un labyrinthe avec Kruskal
     */
    public void generateMaze() {
        generateAllPossibleWalls();
        shuffleEdges();
        createMazePaths();
        fixCornerCases();
        fixBorderCases();
        fixCenterCases();
        initialiseCharactersInMaze();
    }

    public void printMaze() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                System.out.print(" " + maze[j][i] + " ");
            }
            System.out.println();
        }
    }

    public void seeDotLeft() {
        dotLeft = 0;
        for (Cell[] i : maze) {
            for (Cell j : i) {
                if (j.cellval == 1) {
                    dotLeft++;
                }
            }
        }
        System.out.println(dotLeft);
    }


    public void emptyMaze() {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                maze[x][y] = new Cell();
            }
        }
    }

    /**
     * Fonction qui génère toutes les parois possibles entre cellules voisines
     */
    private void generateAllPossibleWalls() {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (x < COLS - 1) {
                    edges.add(new Edge(y * COLS + x, y * COLS + (x + 1))); // Mur horizontal
                }
                if (y < ROWS - 1) {
                    edges.add(new Edge(y * COLS + x, (y + 1) * COLS + x)); // Mur vertical
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
        int x1 = edge.node1 % COLS;
        int y1 = edge.node1 / COLS;
        int x2 = edge.node2 % COLS;
        int y2 = edge.node2 / COLS;

        if (x1 == x2) {  // Mur vertical
            maze[Math.min(y1, y2)][x1].setCellVal(CellType.POINT);
        } else {  // Mur horizontal
            maze[y1][Math.min(x1, x2)].setCellVal(CellType.POINT);
        }
    }

    /**
     * Fonction qui corrige les cas particuliers des coins
     */
    private void fixCornerCases() {
        // Haut-gauche
        fixCorner(0, 0, 0, 1, 1, 0);

        // Haut-droite
        fixCorner(COLS - 1, 0, COLS - 1, 1, COLS - 2, 0);

        // Bas-droite
        fixCorner(COLS - 1, ROWS - 1, COLS - 1, ROWS - 2, COLS - 2, ROWS - 1);

        // Bas-gauche
        fixCorner(0, ROWS - 1, 0, ROWS - 2, 1, ROWS - 1);
    }

    /**
     * Fonction qui corrige le coin particulier en question
     */
    private void fixCorner(int cornerX, int cornerY, int adjX1, int adjY1, int adjX2, int adjY2) {
        if (maze[cornerY][cornerX].cellval == CellType.POINT.getValue() &&
                maze[adjY1][adjX1].cellval == CellType.WALL.getValue() &&
                maze[adjY2][adjX2].cellval == CellType.WALL.getValue()) {
            maze[adjY1][adjX1].setCellVal(CellType.POINT);
        }
    }

    /**
     * Fonction qui corrige les cas des bords
     */
    private void fixBorderCases() {
        // Bordure haut/bas
        for (int x = 1; x < COLS - 1; x++) {
            fixBorderCell(x, 0, x, 1);       // Haut
            fixBorderCell(x, ROWS - 1, x, ROWS - 2); // Bas
        }

        // Bordure gauche/droite
        for (int y = 1; y < ROWS - 1; y++) {
            fixBorderCell(0, y, 1, y);       // Gauche
            fixBorderCell(COLS - 1, y, COLS - 2, y); // Droite
        }
    }

    /**
     * Fonction qui corrige les bords des cellules
     *
     * @param x
     * @param y
     * @param adjX
     * @param adjY
     */
    private void fixBorderCell(int x, int y, int adjX, int adjY) {
        if (maze[y][x].cellval == CellType.POINT.getValue() &&
                maze[adjY][adjX].cellval == CellType.WALL.getValue()) {
            maze[adjY][adjX].setCellVal(CellType.POINT);
        }
    }

    /**
     * Fonction qui corrige les cas du centre
     */
    private void fixCenterCases() {
        for (int x = 1; x < COLS - 1; x++) {
            for (int y = 1; y < ROWS - 1; y++) {
                if (maze[y][x].cellval == CellType.POINT.getValue() &&
                        maze[y - 1][x].cellval == CellType.WALL.getValue() &&
                        maze[y + 1][x].cellval == CellType.WALL.getValue() &&
                        maze[y][x - 1].cellval == CellType.WALL.getValue() &&
                        maze[y][x + 1].cellval == CellType.WALL.getValue()) {
                    maze[y - 1][x].setCellVal(CellType.POINT);
                }
            }
        }
    }

    /**
     * Classe représentant une arête
     */
    private static class Edge {
        int node1, node2;

        Edge(int node1, int node2) {
            this.node1 = node1;
            this.node2 = node2;
        }
    }

    /**
     * Union-Find (Disjoint-Set) pour Kruskal
     */
    private static class UnionFind {
        private final int[] parent;

        public UnionFind(int size) {
            parent = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);  // Compression de chemin
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                parent[rootX] = rootY;
            }
        }
    }


    /**
     *
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessin du labyrinthe
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (maze[x][y].cellval == CellType.POINT.getValue()) {
                    g.setColor(Color.BLACK);
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.WHITE);
                    g.fillOval(
                            x * CELL_SIZE + DOT_OFFSET,
                            y * CELL_SIZE + DOT_OFFSET,
                            DOT_SIZE,
                            DOT_SIZE
                    );
                } else if (maze[x][y].cellval == CellType.EMPTY.getValue()) {
                    g.setColor(Color.BLACK);
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // Dessin des fantômes
        for (Ghosts ghost : personnages.getGhosts()) {
            if (ghost != null) {
                int drawX = ghost.x + (CellType.SIZE.getValue() - Ghosts.GHOST_WIDTH) / 2;
                int drawY = ghost.y + (CellType.SIZE.getValue() - Ghosts.GHOST_HEIGHT) / 2;

                Image ghostImage = personnages.getGhostImage(
                        ghost.getColor(),
                        ghost.getDirection()
                );

                if (ghostImage != null) {
                    g.drawImage(ghostImage,
                            drawX, drawY,
                            Ghosts.GHOST_WIDTH, Ghosts.GHOST_HEIGHT,
                            null);
                }
            }

            // Dessin de Pacman
            Pacman pacman = personnages.getPacman();
            if (pacman != null) {
                int drawX = pacman.x + (CellType.SIZE.getValue() - Pacman.PACMAN_WIDTH) / 2;
                int drawY = pacman.y + (CellType.SIZE.getValue() - Pacman.PACMAN_HEIGHT) / 2;

                Image pacmanImage = personnages.getPacmanImage(
                        pacman.getDirection(),
                        pacman.getMouthOpening()
                );

                if (pacmanImage != null) {
                    g.drawImage(pacmanImage,
                            drawX, drawY,
                            Pacman.PACMAN_WIDTH, Pacman.PACMAN_HEIGHT,
                            null);
                }
            }
        }

    }


    /**
     * @return
     */
    public Characters getPersonnages() {
        return personnages;
    }

    /**
     * @return :
     */
    @Override
    public int getWidth() {
        return COLS * CELL_SIZE;
    }

    /**
     * @return :
     */
    @Override
    public int getHeight() {
        return ROWS * CELL_SIZE;
    }

    public void reset() {
        personnages = new Characters();
        emptyMaze();
        edges = new ArrayList<>();
        uf = new UnionFind(ROWS * COLS);
    }


    public int getDotLeft() {
        return dotLeft;
    }
}
