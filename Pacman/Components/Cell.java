
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics;


public class Cell {

    Image firstScreen;
    Image fraise;
    static int size;
    boolean cellstate;
    public int cellval;


    public Cell(){
        this.firstScreen= Toolkit.getDefaultToolkit().getImage("images/firstScreen.jpg");
        this.fraise = Toolkit.getDefaultToolkit().getImage("images/point.jpg");
        this.size=50;
        this.cellstate=true;
    }

    public void drawI(Graphics g,int x,int y,ImageObserver im){
        g.drawImage(fraise, x*size+25,y*size+25,8,8,im);

    }

    public void setCellVall(){
        cellval=1;
    }

    public void removeCellVall(){
        cellval=1;
    }

   
    
}
