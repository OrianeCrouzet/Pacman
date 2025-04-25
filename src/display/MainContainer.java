package display;

import components.entity.Cell;
import components.entity.Ghosts;
import components.entity.Pacman;
import display.screen.Labyrinth;
import display.screen.MainMenu;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class MainContainer extends JFrame implements KeyListener {

    private CardLayout cardLayout;
    private JPanel container;

    private Labyrinth labyrinthPanel;
    private Timer gameTimer;
//    private final JFrame frame;

    private GameState gameState = GameState.RUNNING;
    private int respawnTimer = 0;

    enum GameState {MENU, RUNNING, GAME_OVER}

    public MainContainer() {
        super("Pacman Game");
        // Initialisation des composants
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Menu Screen
        MainMenu menuScreen = new MainMenu(this);
        container.add(menuScreen, GameState.MENU.name());

        // Game Panel
        labyrinthPanel = new Labyrinth(this);
        container.add(labyrinthPanel, GameState.RUNNING.name());
        gameTimer = null;
        initializeGame();

        add(container);
        setVisible(true);

        showMenu();
    }


    public void showMenu() {
        cardLayout.show(container, GameState.MENU.name());
    }

    public void startGame() {
        cardLayout.show(container, GameState.RUNNING.name());
    }

    /**
     * @return JFrame
     */
    private JFrame createMainWindow() {
        JFrame window = new JFrame("PACMAN");
        int width = Labyrinth.COLS * Cell.SIZE;
        int height = Labyrinth.ROWS * Cell.SIZE;
        window.setSize(width + 16, height + 39); // Compensation pour les bordures
        //window.setSize(750, 800);
        window.setDefaultCloseOperation(EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());
        window.add(labyrinthPanel, BorderLayout.CENTER);
        return window;
    }

    /**
     *
     */
    private void initializeGame() {
        labyrinthPanel.initialiseCharactersInMaze();  // Initialisation après la création du labyrinthe
        setUpKeyContent();
        setupGameLoop();
//        frame.setVisible(true);  // On rend la fenêtre visible seulement quand tout est prêt
    }

    private void setUpKeyContent() {
        setFocusable(true);
        requestFocusInWindow();
//        frame.addKeyListener(this);
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
        // 0. Vérification de l'état du jeu
        if (gameState != GameState.RUNNING) return;

        Pacman pacman = labyrinthPanel.getPersonnages().getPacman();

        // 1. Gestion du respawn si nécessaire
        if (respawnTimer > 0) {
            respawnTimer--;
            if (respawnTimer == 0) {
                pacman.respawn();
                labyrinthPanel.getPersonnages().initGhostsRandomPositions(labyrinthPanel);
            }
            labyrinthPanel.repaint();
            return;
        }

        // 2. Mise à jour de Pacman
        pacman.move();
        pacman.update();

        // 3. Mise à jour des fantômes
        for (Ghosts ghost : labyrinthPanel.getPersonnages().getGhosts()) {
            ghost.move();
        }

        // 4. Vérification des collisions entre les fantômes et Pacman
        if (pacman.checkGhostCollisions(labyrinthPanel.getPersonnages().getGhosts())) {
            if (pacman.getLives() > 0) {
                respawnTimer = 60; // 1 seconde de délai (à 60 FPS)
            } else {
                gameState = GameState.GAME_OVER;
                System.out.println("Game Over!");
            }
        }

        // 5. Rafraîchissement
        labyrinthPanel.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        labyrinthPanel.getPersonnages().getPacman().handleInput(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}