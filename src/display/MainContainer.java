package display;

import components.entity.Ghosts;
import components.entity.Pacman;
import display.screen.*;
import java.awt.*;
import javax.swing.*;

public class MainContainer extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    private Labyrinth labyrinthPanel;
    private Timer gameTimer;

    private GameState gameState = GameState.RUNNING;
    private int respawnTimer = 0;

    private GamePanel gamePanel;

    enum GameState {MENU, RUNNING, GAME_OVER}

    public MainContainer() {
        super("Pacman Game");
        // Initialisation des composants
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(667, 1000);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Menu Screen
        MainMenu menuScreen = new MainMenu(this);
        container.add(menuScreen, GameState.MENU.name());

        // Game Panel
        labyrinthPanel = new Labyrinth();
        gamePanel = new GamePanel(labyrinthPanel);
        container.add(gamePanel, GameState.RUNNING.name());

        // GAME OVER Screen
        GameOver gameOver = new GameOver(this);
        container.add(gameOver, GameState.GAME_OVER.name());

        gameTimer = null;

        add(container);
        setVisible(true);

        showMenu();

    }


    public void showMenu() {
        cardLayout.show(container, GameState.MENU.name());
    }

    public void startGame() {
        initializeGame();

        cardLayout.show(container, GameState.RUNNING.name());
    }

    public void gameOver() {
        setSize(1300, 1000);
        cardLayout.show(container, GameState.GAME_OVER.name());
        labyrinthPanel.reset();
        gameTimer.stop();
    }


    /**
     *
     */
    private void initializeGame() {
        setSize(Labyrinth.SCREEN_WIDTH, Labyrinth.SCREEN_HEIGHT + HUDPanel.HUD_HEIGHT + 23);
        gameState = GameState.RUNNING;
        labyrinthPanel.generateMaze();
        setupGameLoop();
        gamePanel.setPacman(labyrinthPanel.getPersonnages().getPacman());
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
                System.out.println("Game Over!");
                gameState = GameState.GAME_OVER;
                gameOver();
            }
        }

        // 5. Rafraîchissement
        labyrinthPanel.repaint();
        gamePanel.updateHUD();
    }

}