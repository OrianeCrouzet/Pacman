import java.awt.*;
import javax.swing.*;

public class MainContainer {
    private final Labyrinth labyrinth;
    private Timer gameTimer;
    private final JFrame frame;

    private GameState gameState = GameState.RUNNING;

    enum GameState { RUNNING, GAME_OVER }

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
        labyrinth.initialiseCharactersInMaze();  // Initialisation après la création du labyrinthe
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
        // 1. Mise à jour de Pacman
        labyrinth.getPersonnages().getPacman().update();
        
        // 2. Mise à jour des fantômes
        for (Ghosts ghost : labyrinth.getPersonnages().getGhosts()) {
            ghost.move();
        }
        
        // 3. Vérification des collisions
        if (labyrinth.getPersonnages().getPacman().checkGhostCollisions(labyrinth.getPersonnages().getGhosts())) {
            if (labyrinth.getPersonnages().getPacman().getLives() > 0) {
                // Respawn si il reste des vies
                labyrinth.getPersonnages().initGhostsRandomPositions(labyrinth); // Réinitialiser uniquement les fantômes
            } else {
                // Game Over
                gameState = GameState.GAME_OVER;
                //showGameOverScreen();
            }
        }
        
        // 4. Rafraîchissement de l'affichage
        labyrinth.repaint();
    }

}