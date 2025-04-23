
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;

public class Characters {
    // Enum pour les couleurs de fantômes
    public enum GhostColor {
        RED, BLUE, YELLOW
    }

    // Stockage des images de fantômes (Color -> Direction -> Image)
    private Map<GhostColor, Map<Direction, Image>> ghostImages;

    // Images de Pacman (Direction -> [ouvert, fermé])
    private Map<Direction, Image[]> pacmanImages;

    //Liste des fantômes
    private final List<Ghosts> ghosts = new ArrayList<>();

    public Characters() {
        loadGhostImages();
        //loadPacmanImages();
    }

    private void loadGhostImages() {
        ghostImages = new EnumMap<>(GhostColor.class);
        
        for (GhostColor color : GhostColor.values()) {
            Map<Direction, Image> dirImages = new EnumMap<>(Direction.class);
            String colorName = color.name().toLowerCase();
            
            // Chargement des images gauche/droite
            dirImages.put(Direction.LEFT, loadGhostImage("Images/ghost_" + colorName + "_left.png"));
            dirImages.put(Direction.RIGHT, loadGhostImage("Images/ghost_" + colorName + "_right.png"));
            
            ghostImages.put(color, dirImages);
        }
    }

    /**
     * Méthode robuste de chargement d'image avec gestion des erreurs
     */
    private Image loadGhostImage(String src) {
        try {
            InputStream imgStream = getClass().getResourceAsStream(src);
            if (imgStream != null) {
                BufferedImage original = ImageIO.read(imgStream);
                BufferedImage compatible = new BufferedImage(
                    original.getWidth(),
                    original.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
                
                Graphics2D g2d = compatible.createGraphics();
                g2d.drawImage(original, 0, 0, null);
                g2d.dispose();
                
                return compatible;
            }
            throw new IOException("Fichier introuvable: " + src);
        } catch (IOException e) {
            System.err.println("Erreur de chargement: " + e.getMessage());
            return createFallbackGhostImage();
        }
    }

    private BufferedImage createFallbackGhostImage() {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Dessin d'un fantôme de base (cercle avec yeux)
        g.setColor(Color.RED);
        g.fillRoundRect(0, 0, 30, 30, 10, 10);
        g.setColor(Color.WHITE);
        g.fillOval(5, 8, 8, 8);
        g.fillOval(17, 8, 8, 8);
        
        g.dispose();
        return img;
    }

    private void loadPacmanImages() {
        pacmanImages = new EnumMap<>(Direction.class);
        
        // Directions principales (on pourrait ajouter UP/DOWN si besoin)
        Direction[] directions = {Direction.LEFT, Direction.RIGHT, Direction.UP, Direction.DOWN};
        
        for (Direction dir : directions) {
            String dirName = dir.name().toLowerCase();
            pacmanImages.put(dir, new Image[] {
                Toolkit.getDefaultToolkit().getImage("Images/pac_open_" + dirName + ".png"),
                Toolkit.getDefaultToolkit().getImage("Images/pac_close_" + dirName + ".png")
            });
        }
    }

    // Méthode utilitaire pour obtenir une image de fantôme
    public Image getGhostImage(GhostColor color, Direction direction) {
        return ghostImages.get(color).get(direction);
    }

    // Méthode utilitaire pour obtenir une image de Pacman
    public Image getPacmanImage(Direction direction, boolean mouthOpen) {
        return pacmanImages.get(direction)[mouthOpen ? 0 : 1];
    }

    // Initialisation des fantômes avec des couleurs variées
    public void initGhostsRandomPositions(Labyrinth lab) {
        Random rand = new Random();
        GhostColor[] colors = GhostColor.values();
        
        for (GhostColor color : colors) {
            Ghosts ghost = createGhostAtRandomPosition(rand, lab, color);
            ghosts.add(ghost);
        }
    }

    private Ghosts createGhostAtRandomPosition(Random rand, Labyrinth lab, GhostColor color) {
        while (true) {
            int x = rand.nextInt(Labyrinth.COLS);
            int y = rand.nextInt(Labyrinth.ROWS);
            
            if (isValidGhostPosition(lab, x, y)) {
                return new Ghosts(
                    x * Cell.size + Cell.size/2,
                    y * Cell.size + Cell.size/2,
                    lab,
                    getGhostImage(color, Direction.LEFT),
                    color 
                );
            }
        }
    }

    /**
     * Vérifie si la position est valide pour un fantôme
     * @param lab : Le labyrinthe de différence
     * @param x : position x
     * @param y : position y
     * @return : true si la position du fantôme est valide
     *         : false sinon
     */
    private boolean isValidGhostPosition(Labyrinth lab, int x, int y) {
        // 1. La cellule doit être un chemin
        if (lab.maze[x][y].cellval != CellType.POINT.getValue()) {
            return false;
        }
        
        // 2. Vérifie qu'aucun fantôme n'est déjà trop proche
        for (Ghosts existing : ghosts) {
            int ghostCellX = existing.x / Cell.size;
            int ghostCellY = existing.y / Cell.size;
            
            if (Math.abs(ghostCellX - x) < 2 && Math.abs(ghostCellY - y) < 2) {
                return false; // Évite le chevauchement
            }
        }
        
        return true;
    }

    /**
     * Fonction qui permet de récupérer le fantôme numéro i
     * @param i : l'indice du fantôme qu'on veut retrouver
     * @return : le fantôme à l'indice i
     */
    public Ghosts getGhost(int i) {
        return ghosts.get(i);
    }

    /**
     * Fonction qui retourne la liste de tous les fantômes
     * @return : la liste de tous les fantômes
     */
    public List<Ghosts> getGhosts() {
        return ghosts;
    }
    
}



