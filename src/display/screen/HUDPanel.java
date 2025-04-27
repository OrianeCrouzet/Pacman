package display.screen;

import components.entity.Pacman;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class HUDPanel extends JPanel {

    public static final int HUD_HEIGHT = 80;
    public static final int HUD_WIDTH = 80;
    private Pacman pacman;

    public HUDPanel() {
        setPreferredSize(new Dimension(HUD_WIDTH, HUD_HEIGHT));
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
    }

    public void setPacman(Pacman pacman) {
        this.pacman = pacman;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Couleur du texte
        g.setColor(getForeground());
        Font pixelFont = null;

        try {
            // Charger la police depuis le classpath en utilisant un InputStream
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("font/PressStart2P-Regular.ttf");
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


    public void refresh() {
        repaint();
    }
}
