
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

    void random_pos(Graphics g, Labyrinth lab) {
        Random rand = new Random();
    
        int maxAttempts = 100; // Nombre d'essais avant d'abandonner
        int attempts = 0;
        int x, y;
    
        // Tenter de placer le fantôme
        while (attempts < maxAttempts) {
            x = rand.nextInt(19); // Générer une position aléatoire
            y = rand.nextInt(19);
    
            // Vérifier si c'est un chemin (cellule libre)
            if (lab.maze[y][x].cellval == 1) {
                // Si la cellule est un chemin, on place le fantôme
                g.drawImage(phJG, x *50, y * 50, 50, 50, lab);
                return; // Exit après avoir placé le fantôme
            }
            
            attempts++; // Compter un essai supplémentaire
        }
    
        // Si on arrive ici, c'est que le fantôme n'a pas pu être placé après plusieurs essais
        System.out.println("Impossible de placer le fantôme après " + maxAttempts + " essais.");
    }
    
}



