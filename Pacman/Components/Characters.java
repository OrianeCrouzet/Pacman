
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.Graphics;
import java.util.Random;





public class Characters {

    /*phantom droite et gauche */
  public  Image  phJG;
    Image phRD;
    Image phJD;
    Image phRG;
    Image phBD;
    Image phBG;

    /*Pacman droite gauche bas haut, bouche fermeé et ouverte */
    Image pac_bouche_ouvD;
    Image pac_bouche_ouvG;
    Image pac_bouche_ouvH;
    Image pac_bouche_ouvB;
    Image pac_bouche_fermH;
    Image pac_bouche_fermB;
    Image pac_bouche_fermD;
    Image pac_bouche_fermG;

    
    Ghosts ghost1;


    public Characters(){
     
        this.phJG=Toolkit.getDefaultToolkit().getImage("images/jgauche.png");
        this.phJD=Toolkit.getDefaultToolkit().getImage("images/pngimg.com - pacman_PNG19.png");
        this.phRD=Toolkit.getDefaultToolkit().getImage("images/images.png");
        this.phRG=Toolkit.getDefaultToolkit().getImage("images/rgauche.png");
        this.phBD=Toolkit.getDefaultToolkit().getImage("images/png-transparent-pac-man-blue-ghost-illustration-pac-man-world-3-pong-video-game-pacman-blue-game-smiley-thumbnail.png");
        this.phBG=Toolkit.getDefaultToolkit().getImage("images/bgauche.png");

        this.pac_bouche_ouvD=Toolkit.getDefaultToolkit().getImage("images/pacman-1 (Copie).png");
        this.pac_bouche_ouvG=Toolkit.getDefaultToolkit().getImage("images/pacmangauche (Copie).png");
        this.pac_bouche_ouvB=Toolkit.getDefaultToolkit().getImage("images/pacman-1.png");
        this.pac_bouche_ouvH=Toolkit.getDefaultToolkit().getImage("images/pacmangauche.png");
        this.pac_bouche_fermH=Toolkit.getDefaultToolkit().getImage("images/pacman-haut.png");
        this.pac_bouche_fermB=Toolkit.getDefaultToolkit().getImage("images/pacman-2.png");
        this.pac_bouche_fermD=Toolkit.getDefaultToolkit().getImage("images/pacman-2 (Copie).png");
        this.pac_bouche_fermG=Toolkit.getDefaultToolkit().getImage("images/pacman-gauche (Copie).png");
    
    }

    public void initGhostRandomPosition(Labyrinth lab) {
        Random rand = new Random();
        int x, y;
    
        while (true) {
            x = rand.nextInt(19);
            y = rand.nextInt(19);
    
            if (lab.maze[x][y].cellval == 1) {
                ghost1 = new Ghosts(x * 50, y * 50,lab);
                return;
            }
        }
    }
    
    
}



