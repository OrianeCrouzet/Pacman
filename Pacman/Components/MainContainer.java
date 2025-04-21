import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class MainContainer{

    Labyrinth lab= new Labyrinth();
    Timer timer;
    public MainContainer(){
        JFrame frame = new JFrame();
        frame.setSize(1000,1037);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(lab,BorderLayout.CENTER);
        frame.setTitle("PACMAN");
        frame.setVisible(true);
        timer=new Timer(30, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e){
               lab.personnages.ghost1.move();
               lab.repaint();


            }
        });

        timer.start();

     
    }


}

