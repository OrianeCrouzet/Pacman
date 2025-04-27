package display;

import components.entity.Ghosts;
import components.entity.Pacman;
import display.screen.*;
import java.awt.*;
import javax.swing.*;

/**
 * MainContainer est la classe principale de l'application Pacman.
 *
 * Elle hérite de JFrame et sert de conteneur principal pour les différentes
 * vues du jeu : menu principal, partie en cours, écran de victoire, écran de défaite.
 *
 * Cette classe gère :
 * - Le système de navigation entre les écrans via un CardLayout
 * - Le démarrage, la mise à jour et la fin du jeu
 * - Le timer principal du jeu et la logique de mise à jour (boucle de jeu)
 * - La gestion des états du jeu (menu, en cours, gagné, perdu)
 */
public class MainContainer extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    private Labyrinth labyrinthPanel;
    private Timer gameTimer;

    private GameState gameState = GameState.RUNNING;
    private int respawnTimer = 0;

    private GamePanel gamePanel;

    public boolean random;

    /**
     * Enumération représentant les différents états du jeu.
     */
    enum GameState {
        MENU, RUNNING, GAME_OVER, WIN
    }

    /**
     * Constructeur principal de MainContainer.
     * Initialise les composants graphiques, les différents écrans
     * et affiche le menu principal.
     */
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

    /**
     * Affiche l'écran du menu principal.
     */
    public void showMenu() {
        setWindow(667, 1000);
        cardLayout.show(container, GameState.MENU.name());
    }

    /**
     * Démarre une nouvelle partie.
     *
     * @param random indique si un labyrinthe aléatoire doit être choisi
     */
    public void startGame(boolean random) {
        this.random = random;
        initializeGame();
        setWindow(labyrinthPanel.SCREEN_WIDTH, labyrinthPanel.SCREEN_HEIGHT + HUDPanel.HUD_HEIGHT + 23);

        cardLayout.show(container, GameState.RUNNING.name());
    }

    /**
     * Termine la partie et affiche l'écran correspondant selon si le joueur a gagné ou perdu.
     *
     * @param win true si le joueur a gagné, false sinon
     */
    public void endGame(boolean win) {
        setWindow(1300, 1000);
        labyrinthPanel.reset();
        if (gameTimer != null) {
            gameTimer.stop();
        }
        this.gameState = GameState.GAME_OVER;
        if (win) {
            cardLayout.show(container, GameState.WIN.name());
        } else {
            cardLayout.show(container, GameState.GAME_OVER.name());
        }
    }

    /**
     * Initialise les éléments nécessaires pour démarrer une nouvelle partie :
     * choix du labyrinthe, démarrage de la boucle de jeu, affectation de Pacman.
     */
    private void initializeGame() {
        gameState = GameState.RUNNING;
        labyrinthPanel.chooseMaze(random);
        setupGameLoop();
        gamePanel.setPacman(labyrinthPanel.getPersonnages().getPacman());
    }

    /**
     * Configure et lance le Timer qui met à jour le jeu à intervalle régulier.
     */
    private void setupGameLoop() {
        gameTimer = new Timer(30, e -> updateGame());
        gameTimer.start();
    }

    /**
     * Définit la taille de la fenêtre et la centre sur l'écran.
     *
     * @param width largeur de la fenêtre
     * @param height hauteur de la fenêtre
     */
    public void setWindow(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    /**
     * Met à jour l'état du jeu :
     * - Gestion du respawn
     * - Déplacement de Pacman et des fantômes
     * - Gestion des collisions
     * - Vérification des conditions de victoire
     * - Rafraîchissement de l'affichage
     */
    private void updateGame() {
        // 0. Vérification de l'état du jeu
        if (gameState != GameState.RUNNING) return;

        Pacman pacman = labyrinthPanel.getPersonnages().getPacman();

        // 1. Gestion du respawn si nécessaire
        if (respawnTimer > 0) {
            respawnTimer--;
            if (respawnTimer == 60) {
                if (!random){
                    labyrinthPanel.chooseMaze(random);
                    labyrinthPanel.classicalMaze();
                }else {
                    labyrinthPanel.replaceDot();
                    labyrinthPanel.initialiseCharactersInMaze();
                }
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
                respawnTimer = 120; // 2 seconde de délai (à 60 FPS)
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
