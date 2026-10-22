package pharmacie;

// StockHistorique.java
import java.util.Date;

public class StockHistorique {
    private int idMedicament;
    private int quantite;
    private Date date;

    public StockHistorique(int idMedicament, int quantite, Date date) {
        this.idMedicament = idMedicament;
        this.quantite = quantite;
        this.date = date;
    }

    // Getters and Setters
    public int getIdMedicament() {
        return idMedicament;
    }

    public void setIdMedicament(int idMedicament) {
        this.idMedicament = idMedicament;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}