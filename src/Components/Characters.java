
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;

public class Characters {

    /*phantom droite et gauche */
    public Image ghost_yellow_left;
    public Image ghost_red_right;
    public Image ghost_yellow_right;
    public Image ghost_red_left;
    public Image ghost_blue_right;
    public Image ghost_blue_left;

    /*Pacman droite gauche bas haut, bouche fermée et ouverte */
    public Image pac_open_right;
    public Image pac_open_left;
    public Image pac_open_up;
    public Image pac_open_down;
    public Image pac_close_up;
    public Image pac_close_down;
    public Image pac_close_right;
    public Image pac_close_left;

    public Ghosts ghost1;

    public Characters(){
     
        this.ghost_yellow_left=Toolkit.getDefaultToolkit().getImage("Images/ghost_yellow_left.png");
        this.ghost_yellow_right=Toolkit.getDefaultToolkit().getImage("Images/ghost_yellow_right.png");
        this.ghost_red_right=Toolkit.getDefaultToolkit().getImage("Images/ghost_red_right.png");
        this.ghost_red_left=Toolkit.getDefaultToolkit().getImage("Images/ghost_red_left.png");
        this.ghost_blue_right=Toolkit.getDefaultToolkit().getImage("Images/ghost_blue_right.png");
        this.ghost_blue_left=Toolkit.getDefaultToolkit().getImage("Images/ghost_blue_left.png");

        this.pac_open_right=Toolkit.getDefaultToolkit().getImage("Images/pac_open_right.png");
        this.pac_open_left=Toolkit.getDefaultToolkit().getImage("Images/pac_open_left.png");
        this.pac_open_down=Toolkit.getDefaultToolkit().getImage("Images/pac_open_down.png");
        this.pac_open_up=Toolkit.getDefaultToolkit().getImage("Images/pac_open_up.png");
        this.pac_close_up=Toolkit.getDefaultToolkit().getImage("Images/pac_close_up.png");
        this.pac_close_down=Toolkit.getDefaultToolkit().getImage("Images/pac_close_down.png");
        this.pac_close_right=Toolkit.getDefaultToolkit().getImage("Images/pac_close_right.png");
        this.pac_close_left=Toolkit.getDefaultToolkit().getImage("Images/pac_close_left.png");
    
    }

    
    /** 
     * @param lab
     */
    public void initGhostRandomPosition(Labyrinth lab) {
        Random rand = new Random();
        int x, y;
    
        while (true) {
            x = rand.nextInt(19);
            y = rand.nextInt(19);
    
            if (lab.maze[x][y].cellval == CellType.POINT.getValue()) {
                // 1. Initialise la position du fantôme
                ghost1 = new Ghosts(x * Cell.size, y * Cell.size, lab);
                
                // 2. Charge l'image AVANT de quitter la méthode
                loadGhostImage();
                
                // Debug
                System.out.printf("Fantôme placé en [%d,%d] | Image %s\n",
                    ghost1.x, ghost1.y,
                    (ghost_yellow_left != null ? "chargée (" + ghost_yellow_left.getWidth(null) + "x" + ghost_yellow_left.getHeight(null) + ")" : "manquante"));
                
                return;
            }
        }
    }

    /**
     * 
     * @return
     */
    public Ghosts getGhost1() {
        return ghost1;
    }

    /**
     * 
     */
    public void loadGhostImage() {
        try {
            // Méthode garantie pour charger depuis les ressources
            InputStream imgStream = getClass().getResourceAsStream("Images/ghost_yellow_left.png");
            if (imgStream != null) {
                // Conversion en format ARGB pour la transparence
                BufferedImage original = ImageIO.read(imgStream);
                BufferedImage compatible = new BufferedImage(
                    original.getWidth(),
                    original.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
                
                Graphics2D g2d = compatible.createGraphics();
                g2d.drawImage(original, 0, 0, null);
                g2d.dispose();
                
                this.ghost_yellow_left = compatible;
            } else {
                throw new IOException("Fichier introuvable dans les ressources");
            }
        } catch (IOException e) {
            System.err.println("Erreur de chargement : " + e.getMessage());
            // Fallback visuel
            this.ghost_yellow_left = createFallbackImage();
        }
    }

    /**
     * 
     * @return
     */
    private BufferedImage createFallbackImage() {
        BufferedImage img = new BufferedImage(45, 45, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Dessin d'un fantôme de test (cercle rouge avec yeux)
        g.setColor(Color.RED);
        g.fillOval(5, 5, 35, 35);
        g.setColor(Color.WHITE);
        g.fillOval(15, 15, 8, 8);
        g.fillOval(25, 15, 8, 8);
        
        g.dispose();
        return img;
    }
    
}



