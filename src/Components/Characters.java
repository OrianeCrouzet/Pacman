
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Random;

public class Characters {

    /*phantom droite et gauche */
    public Image  phJG;
    public Image phRD;
    public Image phJD;
    public Image phRG;
    public Image phBD;
    public Image phBG;

    /*Pacman droite gauche bas haut, bouche fermée et ouverte */
    public Image pac_bouche_ouvD;
    public Image pac_bouche_ouvG;
    public Image pac_bouche_ouvH;
    public Image pac_bouche_ouvB;
    public Image pac_bouche_fermH;
    public Image pac_bouche_fermB;
    public Image pac_bouche_fermD;
    public Image pac_bouche_fermG;

    public Ghosts ghost1;

    public Characters(){
     
        this.phJG=Toolkit.getDefaultToolkit().getImage("Images/jgauche.png");
        this.phJD=Toolkit.getDefaultToolkit().getImage("Images/pngimg.com - pacman_PNG19.png");
        this.phRD=Toolkit.getDefaultToolkit().getImage("Images/images.png");
        this.phRG=Toolkit.getDefaultToolkit().getImage("Images/rgauche.png");
        this.phBD=Toolkit.getDefaultToolkit().getImage("Images/png-transparent-pac-man-blue-ghost-illustration-pac-man-world-3-pong-video-game-pacman-blue-game-smiley-thumbnail.png");
        this.phBG=Toolkit.getDefaultToolkit().getImage("Images/bgauche.png");

        this.pac_bouche_ouvD=Toolkit.getDefaultToolkit().getImage("Images/pacman-1 (Copie).png");
        this.pac_bouche_ouvG=Toolkit.getDefaultToolkit().getImage("Images/pacmangauche (Copie).png");
        this.pac_bouche_ouvB=Toolkit.getDefaultToolkit().getImage("Images/pacman-1.png");
        this.pac_bouche_ouvH=Toolkit.getDefaultToolkit().getImage("Images/pacmangauche.png");
        this.pac_bouche_fermH=Toolkit.getDefaultToolkit().getImage("Images/pacman-haut.png");
        this.pac_bouche_fermB=Toolkit.getDefaultToolkit().getImage("Images/pacman-2.png");
        this.pac_bouche_fermD=Toolkit.getDefaultToolkit().getImage("Images/pacman-2 (Copie).png");
        this.pac_bouche_fermG=Toolkit.getDefaultToolkit().getImage("Images/pacman-gauche (Copie).png");
    
    }

    public void initGhostRandomPosition(Labyrinth lab) {
        Random rand = new Random();
        int x, y;
    
        while (true) {
            x = rand.nextInt(19);
            y = rand.nextInt(19);
    
            if (lab.maze[x][y].cellval == CellType.WALL.getValue()) {
                ghost1 = new Ghosts(x * 50, y * 50,lab);
                return;
            }
        }
    }
    
}



