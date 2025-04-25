package display.screen;

import components.entity.Pacman;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private final HUDPanel hud;

    public GamePanel(Labyrinth labyrinth) {
        super(new BorderLayout());
        this.hud = new HUDPanel();

        add(hud, BorderLayout.NORTH);
        add(labyrinth, BorderLayout.CENTER);
    }

    public void setPacman(Pacman pacman) {
        hud.setPacman(pacman);
    }

    public void updateHUD() {
        hud.refresh();
    }
}
