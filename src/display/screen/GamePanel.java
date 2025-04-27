package display.screen;

import components.entity.Pacman;
import java.awt.*;
import javax.swing.*;

/**
 * GamePanel est le panneau principal du jeu en cours.
 *
 * Ce panneau contient :
 * - Le HUD (affichage des informations comme les vies, score, etc.)
 * - Le labyrinthe de jeu (où se déplacent Pacman et les fantômes)
 */
public class GamePanel extends JPanel {

    private final HUDPanel hud;

    /**
     * Constructeur de GamePanel.
     * Initialise le panneau avec un HUD en haut et le labyrinthe au centre.
     *
     * @param labyrinth le panneau représentant le labyrinthe de jeu
     */
    public GamePanel(Labyrinth labyrinth) {
        super(new BorderLayout());
        this.hud = new HUDPanel();

        add(hud, BorderLayout.NORTH);
        add(labyrinth, BorderLayout.CENTER);
    }

    /**
     * Associe Pacman au HUD pour permettre l'affichage de ses informations.
     *
     * @param pacman l'objet Pacman du jeu
     */
    public void setPacman(Pacman pacman) {
        hud.setPacman(pacman);
    }

    /**
     * Met à jour les informations affichées dans le HUD.
     */
    public void updateHUD() {
        hud.refresh();
    }
}
