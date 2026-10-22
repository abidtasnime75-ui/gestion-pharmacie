package pharmacie;

public class Medicament {
    private int id;
    private String nom;
    private String dosage;
    private int stock;
    private double prixUnitaire;

    public Medicament(int id, String nom, String dosage, int stock, double prixUnitaire) {
        this.id = id;
        this.nom = nom;
        this.dosage = dosage;
        this.stock = stock;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDosage() { return dosage; }
    public int getStock() { return stock; }
    public double getPrixUnitaire() { return prixUnitaire; }
}