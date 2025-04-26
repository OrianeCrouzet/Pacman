package components;

import components.entity.Cell;
import components.entity.Ghosts;
import components.entity.Pacman;
import display.screen.Labyrinth;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.*;

public class Characters {

    public enum GhostColor {RED, BLUE, YELLOW}

    public static final Random rand = new Random();


    /**
     * Sprites des fantômes ordonnés par couleur
     */
    private final Map<GhostColor, GhostSprites> ghostSprites = new EnumMap<>(GhostColor.class);

    /**
     * Sprites de pacman ordonnés par bouche ouverte ou fermée
     */
    private final Map<Direction, PacmanSprites> pacmanSprites = new EnumMap<>(Direction.class);

    /**
     * Liste de tous les fantômes, y compris le fantôme spécial !
     */
    private final List<Ghosts> ghosts = new ArrayList<>();

    private Pacman pacman;

    /**
     * Structure pour stocker les sprites des fantômes par couleur
     */
    private static class GhostSprites {
        Image left, right;

        public GhostSprites(Image left, Image right) {
            this.left = left;
            this.right = right;
        }
    }

    /**
     * Structure pour stocker les sprites de pacman par bouche ouverte ou fermée
     */
    private static class PacmanSprites {
        Image mouthOpen, mouthClosed;

        public PacmanSprites(Image open, Image closed) {
            this.mouthOpen = open;
            this.mouthClosed = closed;
        }
    }

    public Characters() {
        loadAllGhostSprites();
        loadPacmanSprites();
    }

    /********************************** INITIALISATION DES FANTÔMES BASIQUES ***********************************************/

    /**
     * Fonction qui charge touts les sprites des fantômes par couleur (yeux vers la gauche et vers la droite)
     */
    private void loadAllGhostSprites() {
        for (GhostColor color : GhostColor.values()) {
            String colorName = color.name().toLowerCase();
            Image left = loadGhostImage("ghost_" + colorName + "_left.png");
            Image right = loadGhostImage("ghost_" + colorName + "_right.png");
            ghostSprites.put(color, new GhostSprites(left, right));
        }
    }

    /**
     * Méthode robuste de chargement d'image avec gestion des erreurs
     *
     * @param path : le chemin vers la ressource image.png souhaitée
     * @return : l'image chargée depuis les sources
     */
    private Image loadGhostImage(String path) {
        try (InputStream is = getClass().getResourceAsStream("/images/" + path)) {
            BufferedImage img = ImageIO.read(is);
            BufferedImage compatible = new BufferedImage(
                    img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = compatible.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            return compatible;
        } catch (Exception e) {
            System.err.println("Erreur chargement " + path + ": " + e.getMessage());
            return createFallbackGhostImage();
        }
    }

    /**
     * Fonction qui créé une image de secours au cas où les images des fantômes ne soient pas chargées correctement
     *
     * @return : une image d'un fantôme rond et rouge, avec des yeux noirs
     */
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

    /**
     * Méthode utilitaire pour obtenir une image de fantôme
     *
     * @param color : la couleur du fantôme
     * @param dir   : la direction du regard du fantôme
     * @return : l'image du fantôme en fonction des paramètres d'entrée
     */
    public Image getGhostImage(GhostColor color, Direction dir) {
        GhostSprites sprites = ghostSprites.get(color);
        return (dir == Direction.LEFT) ? sprites.left : sprites.right;
    }

    /********************************** INITIALISATION DE PACMAN ***********************************************/

    /**
     * Fonction qui charge tous les sprites de Pacman en fonction de s'il a la bouche ouverte ou fermée
     */
    private void loadPacmanSprites() {
        for (Direction dir : Direction.values()) {
            String dirName = dir.name().toLowerCase();
            Image open = loadPacmanImage("pac_open_" + dirName + ".png");
            Image closed = loadPacmanImage("pac_close_" + dirName + ".png");
            pacmanSprites.put(dir, new PacmanSprites(open, closed));
        }
    }

    /**
     * Méthode robuste de chargement d'image avec gestion des erreurs
     *
     * @param path : le chemin vers la ressource image.png souhaitée
     * @return : l'image chargée depuis les sources
     */
    private Image loadPacmanImage(String path) {
        try (InputStream is = getClass().getResourceAsStream("/images/" + path)) {
            BufferedImage img = ImageIO.read(is);
            BufferedImage compatible = new BufferedImage(
                    img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = compatible.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            return compatible;
        } catch (Exception e) {
            System.err.println("Erreur chargement " + path + ": " + e.getMessage());
            return createFallbackPacmanImage();
        }
    }

    private Image createFallbackPacmanImage() {
        BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.YELLOW);
        g.fillArc(0, 0, 30, 30, 45, 270); // Forme Pacman de base
        g.dispose();
        return img;
    }

    // Méthode d'accès principale
    public Image getPacmanImage(Direction dir, boolean isMouthOpen) {
        PacmanSprites sprites = pacmanSprites.get(dir);
        return isMouthOpen ? sprites.mouthOpen : sprites.mouthClosed;
    }

    /*************************** POSITIONNEMENT DES FANTÔMES BASIQUES ET DU FANTÔME SPECIAL ********************************/

    /**
     * Initialisation des fantômes avec des couleurs variées
     *
     * @param lab : le labyrinthe courant
     */
    public void initGhostsRandomPositions(Labyrinth lab) {
        ghosts.clear();
        GhostColor[] allColors = GhostColor.values();
        Random rand = new Random();
        int lastIndex = allColors.length - 1;

        //Fantômes basiques
        for (int i = 0; i < lastIndex; i++) {
            GhostColor color = allColors[i];
            Ghosts ghost = createGhostAtRandomPosition(rand, lab, color);
            ghosts.add(ghost);
            System.out.println("Ghost " + color + " créé à (" + ghost.x + "," + ghost.y + ")");
        }

/*        //Fantôme spécial
        GhostColor color = allColors[lastIndex];
        SpecialGhost specialGhost = createSpecialGhostAtRandomPosition(rand, lab, color);
        ghosts.add(specialGhost);
        System.out.println("Ghost " + color + " créé à (" + specialGhost.x + "," + specialGhost.y + ")");*/

    }

    /**
     * Création d'un fantôme à une position arbitraire
     *
     * @param rand  : numéro aléatoire
     * @param lab   : le labyrinthe courant
     * @param color : la couleur du fantôme à créer
     * @return : un fantôme créé à une position aléatoire, de la couleur souhaitée
     */
    private Ghosts createGhostAtRandomPosition(Random rand, Labyrinth lab, GhostColor color) {
    (Labyrinth lab, GhostColor color) {
        while (true) {
            int x = rand.nextInt(Labyrinth.COLS);
            int y = rand.nextInt(Labyrinth.ROWS);

            if (isValidPosition(lab, x, y)) {
                return new Ghosts(
                        x * Cell.SIZE + Cell.SIZE / 2,
                        y * Cell.SIZE + Cell.SIZE / 2,
                        lab,
                        getGhostImage(color, Direction.LEFT),
                        color
                );
            }
        }
    }

    /**
     * Vérifie si la position de départ est valide pour un personnage
     *
     * @param lab : Le labyrinthe de différence
     * @param x   : position x
     * @param y   : position y
     * @return : true si la position du personnage est valide
     * : false sinon
     */
    private boolean isValidPosition(Labyrinth lab, int x, int y) {
        // 1. La cellule doit être un chemin
        if (lab.maze[x][y].cellval != CellType.POINT.getValue()) {
            return false;
        }

        // 2. Vérifie qu'aucun fantôme n'est déjà trop proche
        for (Ghosts existing : ghosts) {
            int ghostCellX = existing.x / Cell.SIZE;
            int ghostCellY = existing.y / Cell.SIZE;

            if (Math.abs(ghostCellX - x) < 2 && Math.abs(ghostCellY - y) < 2) {
                return false; // Évite le chevauchement
            }
        }

        return true;
    }

    /*    *//************************************** CREATION DU FANTÔME SPECIAL ***********************************************//*

    private SpecialGhost createSpecialGhostAtRandomPosition(Random rand, Labyrinth lab, GhostColor color) {
        while (true) {
            int x = rand.nextInt(Labyrinth.COLS);
            int y = rand.nextInt(Labyrinth.ROWS);

            if (isValidPosition(lab, x, y)) {
                return new SpecialGhost(
                    x * Cell.SIZE + Cell.SIZE /2,
                    y * Cell.SIZE + Cell.SIZE /2,
                    lab,
                    getGhostImage(color, Direction.LEFT),
                    color
                );
            }
        }
    }

 */   /********************************** POSITIONNEMENT DE PACMAN ***********************************************/

    /**
     * Initialisation de Pacman
     *
     * @param lab : le labyrinthe courant
     */
    public void initPacmanPosition(Labyrinth lab) {

        // Trouver une position valide
        while (true) {
            int x = rand.nextInt(Labyrinth.COLS);
            int y = rand.nextInt(Labyrinth.ROWS);

            if (isValidPosition(lab, x, y)) {
                // Créer Pacman au centre de la cellule
                this.pacman = new Pacman(
                        x * Cell.SIZE + Cell.SIZE / 2,
                        y * Cell.SIZE + Cell.SIZE / 2,
                        lab,
                        getPacmanImage(Direction.RIGHT, true),
                        this
                );

                System.out.println("Pacman initialisé en (" + x + "," + y + ")");
                return;
            }
        }
    }


    /*********************************************** GETTERS ****************************************************************/

    /**
     * Fonction qui permet de récupérer le fantôme numéro i
     *
     * @param i : l'indice du fantôme qu'on veut retrouver
     * @return : le fantôme à l'indice i
     */
    public Ghosts getGhost(int i) {
        return ghosts.get(i);
    }

    /**
     * Fonction qui retourne la liste de tous les fantômes
     *
     * @return : la liste de tous les fantômes
     */
    public List<Ghosts> getGhosts() {
        return ghosts;
    }

    /**
     * Fonction qui retourne Pacman
     *
     * @return : Pacman
     */
    public Pacman getPacman() {
        return pacman;
    }


}



