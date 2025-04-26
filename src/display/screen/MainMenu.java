package display.screen;

import display.MainContainer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenu extends JPanel {

    //TODO à lire j'ai copié un truc trouvé
    private final MainContainer mainFrame;

    public MainMenu(MainContainer frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        // Image de fond
        URL imgUrl = getClass().getClassLoader().getResource("images/firstScreen.jpg");
        if (imgUrl == null) {
            System.err.println("Image non trouvée !");
        } else {
            JLabel background = new JLabel(new ImageIcon(imgUrl));
            background.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 600));
            add(background);

            JButton startButton = new JButton("Start Game");
            startButton.setPreferredSize(new Dimension(200, 50));
            startButton.addActionListener(e -> mainFrame.startGame());
            background.add(startButton);
        }
    }
}