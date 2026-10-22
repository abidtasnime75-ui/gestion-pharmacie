package pharmacie;

//Commande.java
import java.util.Date;

public class Commande {
 private int id;
 private int idGestionnaire;
 private int idMedicament;
 private int quantite;
 private Date date;

 public Commande(int id, int idGestionnaire, int idMedicament, int quantite, Date date) {
     this.id = id;
     this.idGestionnaire = idGestionnaire;
     this.idMedicament = idMedicament;
     this.quantite = quantite;
     this.date = date;
 }

 // Getters and Setters
 public int getId() {
     return id;
 }

 public void setId(int id) {
     this.id = id;
 }

 public int getIdGestionnaire() {
     return idGestionnaire;
 }

 public void setIdGestionnaire(int idGestionnaire) {
     this.idGestionnaire = idGestionnaire;
 }

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