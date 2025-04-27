package display;

import components.entity.Ghosts;
import components.entity.Pacman;
import display.screen.*;

import javax.swing.*;
import java.awt.*;

public class MainContainer extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    private Labyrinth labyrinthPanel;
    private Timer gameTimer;

    private GameState gameState = GameState.RUNNING;
    private int respawnTimer = 0;

    private GamePanel gamePanel;

    public boolean random;

    enum GameState {
        MENU, RUNNING, GAME_OVER, WIN
    }

    public MainContainer() {
        super("Pacman Game");
        // Initialisation des composants
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setWindow(667, 1000);

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
        setWindow(667, 1000);
        cardLayout.show(container, GameState.MENU.name());
    }

    public void startGame(boolean random) {
        this.random = random;
        initializeGame();
        setWindow(labyrinthPanel.SCREEN_WIDTH, labyrinthPanel.SCREEN_HEIGHT + HUDPanel.HUD_HEIGHT + 23);

        cardLayout.show(container, GameState.RUNNING.name());
    }

    public void endGame(boolean win) {
        setWindow(1300, 1000);
        labyrinthPanel.reset();
        gameTimer.stop();
        this.gameState = GameState.GAME_OVER;
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
        gameState = GameState.RUNNING;
        labyrinthPanel.chooseMaze(random);
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

    public void setWindow(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(null);
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
                labyrinthPanel.getPersonnages().initGhostsPositions(labyrinthPanel,null);
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


        if (labyrinthPanel.getDotLeft() == 0 && gameState != GameState.GAME_OVER) {
            endGame(true);
        }

        // 6. Rafraîchissement
        labyrinthPanel.repaint();
        gamePanel.updateHUD();
    }

}