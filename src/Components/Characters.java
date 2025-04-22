
import java.awt.*;
import java.util.Random;

public class Characters {

    /*phantom droite et gauche */
    public Image ghost_yellow_left;
    public Image ghost_red_right;
    public Image ghost_yellow_right;
    public Image ghost_red_left;
    public Image ghost_blue_right;
    public Image ghost_blue_left;

    /*Pacman droite gauche bas haut, bouche fermée et ouverte */
    public Image pac_open_right;
    public Image pac_open_left;
    public Image pac_open_up;
    public Image pac_open_down;
    public Image pac_close_up;
    public Image pac_close_down;
    public Image pac_close_right;
    public Image pac_close_left;

    public Ghosts ghost1;

    public Characters(){
     
        this.ghost_yellow_left=Toolkit.getDefaultToolkit().getImage("Images/ghost_yellow_left.png");
        this.ghost_yellow_right=Toolkit.getDefaultToolkit().getImage("Images/ghost_yellow_right.png");
        this.ghost_red_right=Toolkit.getDefaultToolkit().getImage("Images/ghost_red_right.png");
        this.ghost_red_left=Toolkit.getDefaultToolkit().getImage("Images/ghost_red_left.png");
        this.ghost_blue_right=Toolkit.getDefaultToolkit().getImage("Images/ghost_blue_right.png");
        this.ghost_blue_left=Toolkit.getDefaultToolkit().getImage("Images/ghost_blue_left.png");

        this.pac_open_right=Toolkit.getDefaultToolkit().getImage("Images/pac_open_right.png");
        this.pac_open_left=Toolkit.getDefaultToolkit().getImage("Images/pac_open_left.png");
        this.pac_open_down=Toolkit.getDefaultToolkit().getImage("Images/pac_open_down.png");
        this.pac_open_up=Toolkit.getDefaultToolkit().getImage("Images/pac_open_up.png");
        this.pac_close_up=Toolkit.getDefaultToolkit().getImage("Images/pac_close_up.png");
        this.pac_close_down=Toolkit.getDefaultToolkit().getImage("Images/pac_close_down.png");
        this.pac_close_right=Toolkit.getDefaultToolkit().getImage("Images/pac_close_right.png");
        this.pac_close_left=Toolkit.getDefaultToolkit().getImage("Images/pac_close_left.png");
    
    }

    public void initGhostRandomPosition(Labyrinth lab) {
        Random rand = new Random();
        int x, y;
    
        while (true) {
            x = rand.nextInt(19);
            y = rand.nextInt(19);
    
            if (lab.maze[x][y].cellval == CellType.POINT.getValue()) {
                ghost1 = new Ghosts(x*50, y*50, lab);
                return;
            }
        }
    }

    public Ghosts getGhost1() {
        return ghost1;
    }
    
}



