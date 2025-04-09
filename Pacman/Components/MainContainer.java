import java.awt.BorderLayout;

import javax.swing.*;


public class MainContainer{

    Labyrinth lab= new Labyrinth();
    public MainContainer(){
        JFrame frame = new JFrame();
        frame.setSize(1000,1037);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(lab,BorderLayout.CENTER);
        frame.setTitle("PACMAN");

        frame.setVisible(true);
     
    }


}

