
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;

public class Labyrinth extends JPanel {

    public Characters personnages = new Characters();
    public Cell cells = new Cell();

    public static final int ROWS = 20, COLS = 20;
    private static final int CELL_SIZE = CellType.SIZE.getValue();
    private static final int DOT_SIZE = 6;
    private static final int DOT_OFFSET = CELL_SIZE/2 - 3;

    public Cell[][] maze = new Cell[ROWS][COLS];

    private final List<Edge> edges = new ArrayList<>();
    private final UnionFind uf = new UnionFind(ROWS * COLS);

    public Labyrinth() {
        /*initialisation case du labyrinth */
        for (int x = 0; x< ROWS; x++) {
            for (int y= 0; y< COLS; y++) {
                maze[x][y] = new Cell();
            }
        }
        
        generateMaze();
    }

    public void initialiseGhostInMaze(){
        personnages.initGhostRandomPosition(this);
    }

    /*Set cell state */
    public void setCell(){
        
    }

    // Algorithme de Kruskal pour générer le labyrinthe
    private void generateMaze() {
        // Créer les arêtes (murs) pour chaque cellule voisine
        for (int x= 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (x < COLS - 1) {
                    edges.add(new Edge(y * COLS + x, y * COLS + (x + 1))); // Mur horizontal
                }
                if (y < ROWS - 1) {
                    edges.add(new Edge(y * COLS + x, (y + 1) * COLS + x)); // Mur vertical
                }
            }
        }

        // Trier les arêtes (on pourrait les mélanger pour la randomisation)
        Collections.shuffle(edges);

        // Kruskal : ajouter les arêtes pour créer des chemins
        for (Edge edge : edges) {
            int root1 = uf.find(edge.node1);
            int root2 = uf.find(edge.node2);

            // Si les cellules ne sont pas déjà dans le même ensemble, on les relie
            if (root1 != root2) {
                uf.union(root1, root2);
                int x1 = edge.node1 % COLS;
                int y1 = edge.node1 / COLS;
                int x2 = edge.node2 % COLS;
                int y2 = edge.node2 / COLS;

                // Ouvrir le passage en supprimant le mur entre les cellules
                if (x1 == x2) {  // Mur vertical
                    maze[Math.min(y1, y2)][x1].setCellVal(CellType.POINT);
                } else {  // Mur horizontal
                    maze[y1][Math.min(x1, x2)].setCellVal(CellType.POINT);
                }
            }
        }

        //vérifier qu'il n'y a pas de cycle , et les enlever s'il y en a 

        //si c'est un coin

        //haut-gauche
        if((maze[0][0].cellval == CellType.POINT.getValue())
        &&(maze[1][0].cellval == CellType.WALL.getValue())
        &&(maze[0][1].cellval == CellType.WALL.getValue()))
        {
            maze[0][1].setCellVal(CellType.POINT);
        }

        //haut droite
        if((maze[COLS-1][0].cellval == CellType.POINT.getValue())
        &&(maze[COLS-2][0].cellval == CellType.WALL.getValue())
        &&(maze[COLS-1][1].cellval == CellType.WALL.getValue()))
        {
            maze[COLS-1][1].setCellVal(CellType.POINT);
        }

        //bas droite
        if((maze[COLS-1][ROWS-1].cellval == CellType.POINT.getValue())
        &&(maze[COLS-2][ROWS-1].cellval == CellType.WALL.getValue())
        &&(maze[COLS-1][ROWS-2].cellval == CellType.WALL.getValue()))
        {
            maze[COLS-1][ROWS-2].setCellVal(CellType.POINT);
        }

        //bas gauche
        if((maze[0][ROWS-1].cellval == CellType.POINT.getValue())
        &&(maze[0][ROWS-2].cellval == CellType.WALL.getValue())
        &&(maze[1][ROWS-1].cellval == CellType.WALL.getValue()))
        {
            maze[1][ROWS-1].setCellVal(CellType.POINT);
        }

       //si c'est une case au niveau de la bordure
        for (int x = 1; x < COLS-1; x++) {

            if(maze[x][1].cellval == CellType.POINT.getValue()){
                
                //bordure du haut
                 if((maze[x-1][0].cellval == CellType.WALL.getValue())
                 &&(maze[x+1][0].cellval == CellType.WALL.getValue())
                 &&(maze[x][1].cellval == CellType.WALL.getValue())) 
                {
                    //on choisi un mur q'on enleve
                    maze[x-1][0].setCellVal(CellType.POINT);
                }
                
            }

             //bordure du bas
            if(maze[x][ROWS-1].cellval == CellType.POINT.getValue()){
               
                if((maze[x-1][ROWS-1].cellval== CellType.WALL.getValue())
                &&(maze[x+1][ROWS-1].cellval == CellType.WALL.getValue())
                &&(maze[x][ROWS-2].cellval == CellType.WALL.getValue()))
                {
                    //on choisi un mur q'on enleve
                    maze[x][ROWS-2].setCellVal(CellType.POINT);
                }
            }
        }

        //sur les cotés
        for (int y= 1; y< COLS-1; y++) {

            if(maze[0][y].cellval == CellType.POINT.getValue()){
                
                //gauche
                 if((maze[0][y-1].cellval == CellType.WALL.getValue())
                 &&(maze[0][y+1].cellval == CellType.WALL.getValue())
                 &&(maze[1][y].cellval == CellType.WALL.getValue()))
                 {
                    //on choisi un mur q'on enleve
                    maze[1][y].setCellVal(CellType.POINT);
                }
                
            }

            if(maze[COLS-1][y].cellval == CellType.POINT.getValue()){
                
                //droite
                 if((maze[COLS-1][y-1].cellval == CellType.WALL.getValue())
                 &&(maze[COLS-1][y+1].cellval == CellType.WALL.getValue())
                 &&(maze[COLS-2][y].cellval == CellType.WALL.getValue()))
                 {
                    //on choisi un mur q'on enleve
                    maze[COLS-2][y].setCellVal(CellType.POINT);
                }
                
            }

        }

        //les cases du milieu
        for (int x = 1; x < COLS-1; x++) {

            for (int y= 1; y< COLS-2; y++) {

                if(maze[x][y].cellval == CellType.POINT.getValue()){

                    if((maze[x][y-1].cellval == CellType.WALL.getValue())
                    &&(maze[x][y+1].cellval == CellType.WALL.getValue())
                    &&(maze[x-1][y].cellval == CellType.WALL.getValue())
                    &&(maze[x+1][y].cellval == CellType.WALL.getValue()))
                    {
                        maze[x][y-1].setCellVal(CellType.POINT);
                    }

                }
            }
        }


    }

    // Classe représentant une arête
    private static class Edge {
        int node1, node2;

        Edge(int node1, int node2) {
            this.node1 = node1;
            this.node2 = node2;
        }
    }

    // Union-Find (Disjoint-Set) pour Kruskal
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if( maze [x][y].cellval == CellType.POINT.getValue()){
                    g.setColor(Color.BLACK);
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.WHITE);
                    g.fillOval(
                        x * CELL_SIZE + DOT_OFFSET,
                        y * CELL_SIZE + DOT_OFFSET,
                        DOT_SIZE,
                        DOT_SIZE
                    );
                }
                else{
                    g.setColor(Color.BLUE);
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // Dessin des personnages
        if (personnages.ghost1 != null && personnages.ghost_yellow_left != null) {
            
            // Position centrée dans la cellule
            int drawX = personnages.ghost1.x + (Cell.size - Ghosts.GHOST_WIDTH)/2;
            int drawY = personnages.ghost1.y + (Cell.size - Ghosts.GHOST_HEIGHT)/2;
            
            g.drawImage(personnages.ghost_yellow_left, 
                       drawX, drawY, 
                       Ghosts.GHOST_WIDTH, Ghosts.GHOST_HEIGHT, 
                       this);
        }
    }

    public Characters getPersonnages() {
        return personnages;
    }

    @Override
    public int getWidth() {
        return COLS * CELL_SIZE;
    }
    
    @Override
    public int getHeight() {
        return ROWS * CELL_SIZE;
    }
}
