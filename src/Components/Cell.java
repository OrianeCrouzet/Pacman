
import java.awt.*;
import java.awt.image.ImageObserver;

public class Cell {

    public Image firstScreen;
    public Image fraise;
    public static int size;
    public boolean cellstate;
    public int cellval;


    public Cell(){
        this.firstScreen= Toolkit.getDefaultToolkit().getImage("images/firstScreen.jpg");
        this.fraise = Toolkit.getDefaultToolkit().getImage("images/point.jpg");
        Cell.size = CellType.SIZE.getValue();
        this.cellstate = true;
    }

    public void drawI(Graphics g, int x, int y, ImageObserver im){
        g.drawImage(fraise, x*size+25,y*size+25,8,8,im);
    }

    public void setCellVal(CellType type){
        cellval = type.getValue();
    }
  
}
