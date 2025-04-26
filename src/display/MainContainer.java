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

        // End Game Screen
        EndGameScreen gameOverScreen = new EndGameScreen(this, false);
        container.add(gameOverScreen, GameState.GAME_OVER.name());

        EndGameScreen winningScreen = new EndGameScreen(this, true);
        container.add(winningScreen, GameState.WIN.name());


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

    public void endGame(boolean win) {
        labyrinthPanel.reset();
        gameTimer.stop();
        if (win) {
            cardLayout.show(container, GameState.WIN.name());
        } else {
            cardLayout.show(container, GameState.GAME_OVER.name());
        }
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
                endGame(false);
            }
        }

        // 5. Recompte le nombre de dotLeft

        labyrinthPanel.seeDotLeft();
        if (labyrinthPanel.getDotLeft() == 0) {
            endGame(true);
        }

        // 6. Rafraîchissement
        labyrinthPanel.repaint();
        gamePanel.updateHUD();
    }

}