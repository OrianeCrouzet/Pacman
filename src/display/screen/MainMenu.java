package display.screen;

import display.MainContainer;
import java.awt.*;
import java.net.URL;
import javax.swing.*;

/**
 * MainMenu est l'écran principal affiché au lancement du jeu.
 *
 * Il propose au joueur deux options :
 * - Lancer une partie classique
 * - Lancer une partie aléatoire
 */
public class MainMenu extends JPanel {

    private final MainContainer mainFrame;

    /**
     * Constructeur de MainMenu.
     * Initialise l'écran du menu principal avec une image de fond et deux boutons de démarrage de partie.
     *
     * @param frame la fenêtre principale contenant le menu
     */
    public MainMenu(MainContainer frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        // Image de fond
        URL imgUrl = getClass().getClassLoader().getResource("ressources/images/firstScreen.jpg");
        if (imgUrl == null) {
            System.err.println("Image non trouvée !");
        } else {
            JLabel background = new JLabel(new ImageIcon(imgUrl));
            background.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 600));
            add(background);

            JButton startRButton = new JButton("Random Game");
            startRButton.setPreferredSize(new Dimension(200, 50));

            JButton startCButton = new JButton("Classic Game");
            startCButton.setPreferredSize(new Dimension(200, 50));

            startRButton.addActionListener(e -> mainFrame.startGame(true));
            startCButton.addActionListener(e -> mainFrame.startGame(false));
            background.add(startCButton);
            background.add(startRButton);
        }
    }
}
