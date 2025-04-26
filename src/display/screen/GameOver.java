package display.screen;

import display.MainContainer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GameOver extends JPanel {

    private final MainContainer mainFrame;
    private Font pixelFont;

    public GameOver(MainContainer frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        // Charger la police une seule fois
        try {
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("font/PressStart2P-Regular.ttf");
            if (fontStream == null) throw new IOException("Fichier de police non trouvé");
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(40f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            pixelFont = getFont();
        }

        // Panel des boutons
        JPanel buttonPanel = new JPanel();

        buttonPanel.setOpaque(false); // transparent
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));

        JButton retryButton = new JButton("Retry?");
        JButton quitButton = new JButton("Rage Quit");

        Dimension buttonSize = new Dimension(200, 50);
        retryButton.setPreferredSize(buttonSize);
        quitButton.setPreferredSize(buttonSize);

        retryButton.addActionListener(e -> mainFrame.startGame(mainFrame.random));
        quitButton.addActionListener(e -> mainFrame.dispose());

        buttonPanel.add(retryButton);
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.SOUTH);
        setBackground(Color.BLACK);
        setForeground(Color.RED);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(pixelFont);
        g.setColor(getForeground());

        // Centrage du texte
        String message = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() / 2 - fm.getHeight();

        g.drawString(message, x, y);
    }
}
