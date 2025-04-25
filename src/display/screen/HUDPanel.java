package display.screen;

import components.entity.Pacman;

import javax.swing.*;
import java.awt.*;

public class HUDPanel extends JPanel {

    public static final int HUD_HEIGHT = 80;
    public static final int HUD_WIDTH = 80;
    private Pacman pacman;

    public HUDPanel() {
        setPreferredSize(new Dimension(HUD_WIDTH, HUD_HEIGHT));
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
    }

    public void setPacman(Pacman pacman){
        this.pacman = pacman;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getForeground());
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + pacman.getScore(), 20, 40);
        g.drawString("Vies: " + pacman.getLives(), 200, 40);
    }

    public void refresh() {
        repaint();
    }
}
