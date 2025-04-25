package display.screen;

import display.MainContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainMenu extends JPanel {

    //TODO à lire j'ai copié un truc trouvé
    private final MainContainer mainFrame;

    public MainMenu(MainContainer frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        // Image de fond
        JLabel background = new JLabel(new ImageIcon("images/firstScreen.jpg"));
        System.out.println(background.getIcon());
        background.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 600)); // Positionner les boutons
        add(background);

        // Bouton Start
        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.addActionListener((ActionEvent e) -> mainFrame.startGame());

        // Ajouter les boutons à l’image de fond
        background.add(startButton);
    }
}