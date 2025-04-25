package display.screen;

import display.MainContainer;

import javax.swing.*;
import java.awt.*;

public class GameOver extends JPanel {
    private final MainContainer mainFrame;
    public GameOver(MainContainer frame){
        this.mainFrame = frame;
        setLayout(new BorderLayout());

    }
}
