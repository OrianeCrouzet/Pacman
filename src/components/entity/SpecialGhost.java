package components.entity;

import components.CellType;
import components.Characters.GhostColor;
import components.Direction;
import display.screen.Labyrinth;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

public class SpecialGhost extends Ghosts{

    // Nouvel attribut pour A*
    private static final int COST_STRAIGHT = 10;

    public SpecialGhost(int x, int y, Labyrinth lab, Image img, GhostColor color) {
        super(x, y, lab, img, color);
    }
    
    /****************************************** ALGORITHME A* *******************************************************/

    /**
     * Classe interne pour A*
     */
    private static class Node implements Comparable<Node> {
        int x, y;
        int fScore;
        
        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
        
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.fScore, other.fScore);
        }
    }

    
    /**
     * Déplace le fantôme avec un algorithme de pathfinding : A*
     * Permet au fantôme de poursuivre Pacman pour essayer de le toucher
     */
    @Override
    public void move() {
        // 1. Essayer A* vers Pacman
        if (tryAStarMove()) {
            return;
        }
        
        // 2. Fallback: mouvement aléatoire
        moveRandomly();
    }

    /**
     * Fonction qui tente de trouver d'appliquer un chemin A* si celui-ci est trouvé
     * @return : true si un chemin A* a été trouvé et qu'on peut le suivre
     *           false sinon
     */
    private boolean tryAStarMove() {
        Node start = new Node(c / Cell.SIZE, r / Cell.SIZE);
        Node target = new Node(
            lab.getPersonnages().getPacman().c / Cell.SIZE,
            lab.getPersonnages().getPacman().r / Cell.SIZE
        );
        
        List<Node> path = findPathAStar(start, target);
        if (path != null && path.size() > 1) {
            followPath(path);
            return true;
        }
        return false;
    }

    /**
     * Trouve un chemin A* valide
     * @param start : Le noeud de départ (position actuelle du fantôme)
     * @param target : Le noeud cible (position de Pacman)
     * @return : Une liste de noeuds représentant le chemin, ou null si aucun chemin trouvé
     */
    private List<Node> findPathAStar(Node start, Node target) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        Map<Node, Integer> gScore = new HashMap<>();
        
        openSet.add(start);
        gScore.put(start, 0);
        start.fScore = heuristic(start, target);
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            
            if (current.equals(target)) {
                return reconstructPath(cameFrom, current);
            }
            
            closedSet.add(current);
            
            for (Node neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor)) continue;
                
                int tentativeG = gScore.get(current) + COST_STRAIGHT;
                
                if (!openSet.contains(neighbor) || tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    neighbor.fScore = tentativeG + heuristic(neighbor, target);
                    
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        
        return null; // Pas de chemin trouvé
    }

    private List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        path.add(current);
        
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        
        return path;
    }
    
    /**
     * Retourne les cases voisines accessibles
     * @param node : La position actuelle (en coordonnées grille)
     * @return : la liste des noeuds voisins accessibles
     */
    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        
        checkDirection(node, Direction.LEFT, neighbors);
        checkDirection(node, Direction.RIGHT, neighbors);
        checkDirection(node, Direction.UP, neighbors);
        checkDirection(node, Direction.DOWN, neighbors);
        
        return neighbors;
    }
    
    private void checkDirection(Node node, Direction dir, List<Node> neighbors) {
        int dx = 0, dy = 0;
        
        switch (dir) {
            case LEFT -> dx = -1;
            case RIGHT -> dx = 1;
            case UP -> dy = -1;
            case DOWN -> dy = 1;
        }
        
        int nx = node.x + dx;
        int ny = node.y + dy;
        
        if (isWithinBounds(nx, ny) && !isWall(nx, ny)) {
            neighbors.add(new Node(nx, ny));
        }
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < lab.cols && y >= 0 && y < lab.rows;
    }
    
    private boolean isWall(int c, int r) {
        return lab.maze[c][r].cellval == CellType.WALL.getValue();
    }

    /**
     * Distance de Manhattan (optimale pour Pacman)
     */
    private int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private void followPath(List<Node> path) {
        Node nextNode = path.get(1);
        int dx = nextNode.x * Cell.SIZE + Cell.SIZE/2 - (c + GHOST_WIDTH/2);
        int dy = nextNode.y * Cell.SIZE + Cell.SIZE/2 - (r + GHOST_HEIGHT/2);
        
        // Normaliser la direction
        if (Math.abs(dx) > Math.abs(dy)) {
            tryMove(dx > 0 ? 'R' : 'L');
        } else {
            tryMove(dy > 0 ? 'D' : 'U');
        }
    }


    /****************************************** MOUVEMENT DE FALLBACK *******************************************************/

    /**
     * Mouvement de base des fantômes, utilisé en tant que comportement de dernier recours si A* ne fonctionne pas
     * (même version que move() dans Ghosts.java)
     */
    private void moveRandomly(){
        System.out.println("Dans la méthode fallBack du fantôme spécial !!");
        // Convertir la direction actuelle en code caractère
        char currentDirCode = direction.getCode();
        
        if (!tryMove(currentDirCode) || checkGhostCollisions()) {
            char newDirCode = getValidDirection();
            direction = Direction.fromCode(newDirCode);
            snapToGrid();
            tryMove(newDirCode);
        }
    }
}
