package display.screen;

import display.MainContainer;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;

/**
 * EndGameScreen est l'écran affiché à la fin du jeu.
 * 
 * Cet écran permet d'afficher :
 * - Un message de victoire ou de défaite
 * - Deux boutons : rejouer ou revenir au menu principal
 * - Une personnalisation des couleurs et du texte en fonction du résultat
 */
public class EndGameScreen extends JPanel {

    private boolean win = false;
    private final MainContainer mainFrame;
    private Font pixelFont;
    private String mainText = "";
    private String buttonLeft = "";
    private String buttonRight = "";
    private Color color = Color.PINK;

    /**
     * Constructeur de EndGameScreen.
     * Initialise l'écran de fin avec l'état du jeu (gagné ou perdu),
     * charge la police personnalisée, configure les boutons et l'affichage.
     *
     * @param frame la fenêtre principale contenant le jeu
     * @param win true si le joueur a gagné, false sinon
     */
    public EndGameScreen(MainContainer frame, boolean win) {
        this.mainFrame = frame;
        setWin(win);
        setLayout(new BorderLayout());

        // Charger la police une seule fois
        try {
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("ressources/font/PressStart2P-Regular.ttf");
            if (fontStream == null) throw new IOException("Fichier de police non trouvé");
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(40f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            pixelFont = getFont();
        }

        // Panel des boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // transparent
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));

        JButton restart = new JButton(buttonLeft);
        JButton mainMenu = new JButton(buttonRight);

        Dimension buttonSize = new Dimension(200, 50);
        restart.setPreferredSize(buttonSize);
        mainMenu.setPreferredSize(buttonSize);

        restart.addActionListener(e -> mainFrame.startGame(mainFrame.random));
        mainMenu.addActionListener(e -> mainFrame.showMenu());

        buttonPanel.add(restart);
        buttonPanel.add(mainMenu);

        add(buttonPanel, BorderLayout.SOUTH);
        setBackground(Color.BLACK);
        setForeground(color);
    }

    /**
     * Dessine l'écran de fin avec le message centré.
     *
     * @param g l'objet Graphics pour dessiner
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(pixelFont);
        g.setColor(getForeground());

        // Centrage du texte
        String message = mainText;
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() / 2 - fm.getHeight();

        g.drawString(message, x, y);
    }

    /**
     * Définit l'état du jeu (victoire ou défaite) et met à jour le texte affiché.
     *
     * @param win true si le joueur a gagné, false sinon
     */
    private void setWin(boolean win) {
        this.win = win;
        setText();
    }

    /**
     * Met à jour les textes, couleurs et intitulés des boutons
     * en fonction du résultat de la partie.
     */
    private void setText() {
        if (win) {
            mainText = "YOU WIN !";
            buttonLeft = "New Game";
            color = Color.GREEN;
        } else {
            mainText = "GAME OVER";
            buttonLeft = "Retry?";
            color = Color.RED;
        }
        buttonRight = "Main Menu";
    }
}
