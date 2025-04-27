package display.screen;

import components.entity.Pacman;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;

/**
 * HUDPanel est le panneau affichant les informations de jeu en haut de l'écran.
 *
 * Ce panneau affiche :
 * - Le score du joueur
 * - Le nombre de vies restantes
 * - Une ligne de séparation décorative
 */
public class HUDPanel extends JPanel {

    public static final int HUD_HEIGHT = 80;
    public static final int HUD_WIDTH = 80;
    private Pacman pacman;

    /**
     * Constructeur de HUDPanel.
     * Définit la taille préférée, la couleur de fond et de premier plan du HUD.
     */
    public HUDPanel() {
        setPreferredSize(new Dimension(HUD_WIDTH, HUD_HEIGHT));
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
    }

    /**
     * Associe un Pacman pour pouvoir afficher son score et ses vies.
     *
     * @param pacman l'objet Pacman du jeu
     */
    public void setPacman(Pacman pacman) {
        this.pacman = pacman;
    }

    /**
     * Méthode de dessin du HUD.
     * Affiche le score, les vies du joueur et une ligne bleue décorative.
     *
     * @param g l'objet Graphics utilisé pour dessiner
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Couleur du texte
        g.setColor(getForeground());
        Font pixelFont = null;

        try {
            // Charger la police depuis le classpath en utilisant un InputStream
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("ressources/font/PressStart2P-Regular.ttf");
            if (fontStream == null) {
                throw new IOException("Fichier de police non trouvé dans le classpath");
            }
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(24f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            pixelFont = g.getFont();
        }

        g.setFont(pixelFont);

        // Dessiner les textes
        g.drawString("HIGH SCORE: ", 100, 40);
        g.drawString(String.valueOf(pacman.getScore()), 100, 70);
        g.drawString("Vies: " + pacman.getLives(), 600, 40);

        g.setColor(Color.BLUE);
        g.drawLine(0, 75, 1000, 75);
    }

    /**
     * Rafraîchit le HUD en redessinant le panneau.
     */
    public void refresh() {
        repaint();
    }
}
