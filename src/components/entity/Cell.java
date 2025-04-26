package components.entity;

import components.CellType;

import java.awt.*;
import java.awt.image.ImageObserver;

public class Cell {

    public Image firstScreen;
    public Image fraise;
    public static final int SIZE = CellType.SIZE.getValue();
    public boolean cellstate;
    public int cellval;


    //TODO voir à quoi sert first screen et fraise

    public Cell() {
        this.firstScreen = Toolkit.getDefaultToolkit().getImage("images/firstScreen.jpg");
        this.fraise = Toolkit.getDefaultToolkit().getImage("images/point.jpg");
        this.cellstate = true;
    }


    /**
     * @param g
     * @param x
     * @param y
     * @param im
     */
    public void drawI(Graphics g, int x, int y, ImageObserver im) {
        g.drawImage(fraise, x * SIZE + 25, y * SIZE + 25, 8, 8, im);
    }

    /**
     * @param type
     */
    public void setCellVal(CellType type) {
        cellval = type.getValue();
    }

}
