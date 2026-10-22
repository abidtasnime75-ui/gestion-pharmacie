package pharmacie;

/**
 * Point d'entrée « non-JavaFX » : permet de lancer l'interface depuis un JAR
 * sans erreur « JavaFX runtime components are missing ».
 */
public class Launcher {
    public static void main(String[] args) {
        PharmacieApp.main(args);
    }
}
