package pharmacie;

//Vente.java
import java.util.Date;

public class Vente {
 private int id;
 private int idPharmacien;
 private int idClient;
 private int idMedicament;
 private int quantite;
 private Date date;

 public Vente(int id, int idPharmacien, int idClient, int idMedicament, int quantite, Date date) {
     this.id = id;
     this.idPharmacien = idPharmacien;
     this.idClient = idClient;
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

 public int getIdPharmacien() {
     return idPharmacien;
 }

 public void setIdPharmacien(int idPharmacien) {
     this.idPharmacien = idPharmacien;
 }

 public int getIdClient() {
     return idClient;
 }

 public void setIdClient(int idClient) {
     this.idClient = idClient;
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