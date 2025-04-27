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

/**
 * La classe SpecialGhost représente un fantôme spécial qui utilise l'algorithme A*
 * pour poursuivre Pacman de manière intelligente.
 * 
 * Ce fantôme hérite de la classe Ghosts et ajoute :
 * - Un comportement de pathfinding avancé (A*)
 * - Une heuristique de distance de Manhattan optimisée pour Pacman
 * - Un système de fallback vers un mouvement aléatoire si A* échoue
 * 
 * L'implémentation utilise une version simplifiée de l'algorithme A* avec :
 * - Une file de priorité pour les noeuds ouverts
 * - Un ensemble pour les noeuds fermés
 * - Une heuristique de distance de Manhattan
 */
public class SpecialGhost extends Ghosts {

    /** Coût de déplacement pour un mouvement droit (utilisé dans A*) */
    private static final int COST_STRAIGHT = 10;

    /**
     * Constructeur du fantôme spécial
     * @param x Position horizontale initiale en pixels
     * @param y Position verticale initiale en pixels
     * @param lab Labyrinthe dans lequel évolue le fantôme
     * @param img Image représentant le fantôme
     * @param color Couleur du fantôme
     */
    public SpecialGhost(int x, int y, Labyrinth lab, Image img, GhostColor color) {
        super(x, y, lab, img, color);
    }

    /**
     * Classe interne représentant un noeud dans l'algorithme A*
     */
    private static class Node implements Comparable<Node> {
        /** Position x en coordonnées de grille */
        int x;
        /** Position y en coordonnées de grille */
        int y;
        /** Score F (G + H) pour l'algorithme A* */
        int fScore;
        
        /**
         * Constructeur d'un noeud
         * @param x Position x en grille
         * @param y Position y en grille
         */
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
     * Déplace le fantôme en utilisant l'algorithme A* pour trouver Pacman.
     * Si A* ne trouve pas de chemin, utilise un mouvement aléatoire comme fallback.
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
     * Tente d'utiliser l'algorithme A* pour se déplacer vers Pacman
     * @return true si un chemin a été trouvé et suivi, false sinon
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
     * Implémentation de l'algorithme A* pour trouver un chemin vers la cible
     * @param start Noeud de départ (position actuelle du fantôme)
     * @param target Noeud cible (position de Pacman)
     * @return Liste des noeuds formant le chemin, ou null si aucun chemin trouvé
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

    /**
     * Reconstruit le chemin à partir des relations entre noeuds
     * @param cameFrom Map des relations entre noeuds
     * @param current Noeud final
     * @return Liste ordonnée des noeuds du chemin
     */
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
     * Retourne les cases voisines accessibles depuis une position donnée
     * @param node Position actuelle en coordonnées de grille
     * @return Liste des noeuds voisins accessibles
     */
    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        
        checkDirection(node, Direction.LEFT, neighbors);
        checkDirection(node, Direction.RIGHT, neighbors);
        checkDirection(node, Direction.UP, neighbors);
        checkDirection(node, Direction.DOWN, neighbors);
        
        return neighbors;
    }
    
    /**
     * Vérifie si une direction est valide et ajoute le voisin correspondant
     * @param node Noeud actuel
     * @param dir Direction à vérifier
     * @param neighbors Liste des voisins à compléter
     */
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

    /**
     * Vérifie si une position est dans les limites du labyrinthe
     * @param x Position x en grille
     * @param y Position y en grille
     * @return true si la position est valide, false sinon
     */
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < lab.cols && y >= 0 && y < lab.rows;
    }
    
    /**
     * Vérifie si une cellule est un mur
     * @param c Position en colonnes
     * @param r Position en lignes
     * @return true si la cellule est un mur, false sinon
     */
    private boolean isWall(int c, int r) {
        return lab.maze[c][r].cellval == CellType.WALL.getValue();
    }

    /**
     * Heuristique de distance de Manhattan (optimale pour les déplacements dans Pacman)
     * @param a Noeud de départ
     * @param b Noeud cible
     * @return Distance de Manhattan entre les deux noeuds
     */
    private int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Fait suivre au fantôme le chemin trouvé par A*
     * @param path Chemin à suivre
     */
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

    /**
     * Mouvement de fallback aléatoire utilisé quand A* ne trouve pas de chemin
     */
    private void moveRandomly(){
        System.out.println("Dans la méthode fallBack du fantôme spécial !!");
        char currentDirCode = direction.getCode();
        
        if (!tryMove(currentDirCode) || checkGhostCollisions()) {
            char newDirCode = getValidDirection();
            direction = Direction.fromCode(newDirCode);
            snapToGrid();
            tryMove(newDirCode);
        }
    }
}