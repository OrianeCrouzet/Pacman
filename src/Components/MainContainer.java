import java.awt.*;
import javax.swing.*;

public class MainContainer {
    private final Labyrinth labyrinth;
    private Timer gameTimer;
    private final JFrame frame;

    public MainContainer() {
        // Initialisation des composants
        labyrinth = new Labyrinth();
        frame = createMainWindow();
        gameTimer = null;
        initializeGame();
    }

    
    /** 
     * @return JFrame
     */
    private JFrame createMainWindow() {
        JFrame window = new JFrame("PACMAN");
        int width = Labyrinth.COLS * Cell.size;
        int height = Labyrinth.ROWS * Cell.size;
        window.setSize(width + 16, height + 39); // Compensation pour les bordures
        //window.setSize(750, 800);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());
        window.add(labyrinth, BorderLayout.CENTER);
        return window;
    }

    /**
     * 
     */
    private void initializeGame() {
        labyrinth.initialiseGhostInMaze();  // Initialisation après la création du labyrinthe
        setupGameLoop();
        frame.setVisible(true);  // On rend la fenêtre visible seulement quand tout est prêt
    }

    /**
     * 
     */
    private void setupGameLoop() {
        gameTimer = new Timer(30, e -> updateGame());
        gameTimer.start();
    }

    /**
     * 
     */
    private void updateGame() {
        labyrinth.getPersonnages().getGhost1().move();
        labyrinth.repaint();
    }

}