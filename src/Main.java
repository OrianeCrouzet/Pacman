import display.MainContainer;

import javax.swing.*;

public class Main {

    //ecrire dans le rapport le delir de map procedural
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainContainer::new);   //senser être mieux pour gérer des input après changement de fenêtre (fonctionne pas pour l'instant)
    }

}
