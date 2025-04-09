
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Color;



public class Labyrinth extends JPanel {

    Characters personnages= new Characters();
    Cell cells= new Cell();
    private final int ROWS = 20, COLS = 20;
    private final int CELL_SIZE = Cell.size;
    public Cell[][] maze = new Cell[ROWS][COLS];  // 0 = mur, 1 = chemin

    private final List<Edge> edges = new ArrayList<>();
    private final UnionFind uf = new UnionFind(ROWS * COLS);

    public Labyrinth() {
        /*initialisation case du labyrinth */
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                maze[y][x] = new Cell();
            }
        }
        
        generateMaze();
      
        
    }

    /*Set cell state */
    public void setCell(){
        
    }

    // Algorithme de Kruskal pour générer le labyrinthe
    private void generateMaze() {
        // Créer les arêtes (murs) pour chaque cellule voisine
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
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
                    maze[Math.min(y1, y2)][x1].setCellVall();
                } else {  // Mur horizontal
                    maze[y1][Math.min(x1, x2)].setCellVall();
                }
            }
        }

       /* for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {

                if(maze[x][y])



            }
        }*/

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
        private int[] parent;

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

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {

                if( maze [y][x].cellval==1){
                    g.setColor(Color.BLACK);
                    g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.WHITE);
                    g.fillOval(x *50+25, y * 50+25, 6,6);

                }
              else{
                g.setColor(Color.BLUE);
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                }
            }
        }

        personnages.random_pos(g,this);

      
        //g.drawImage(firstScreen, 150, 0, this); 
    


    }
}
