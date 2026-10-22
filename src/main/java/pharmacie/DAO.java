package pharmacie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class DAO {

    public Vector<Medicament> getAllMedicaments(Connection conn) throws SQLException {
        Vector<Medicament> liste = new Vector<>();
        String sql = "SELECT id_medicament, nom, dosage, stock, prix_unitaire FROM Medicament ORDER BY nom";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Medicament m = new Medicament(
                        rs.getInt("id_medicament"),
                        rs.getString("nom"),
                        rs.getString("dosage"),
                        rs.getInt("stock"),
                        rs.getDouble("prix_unitaire")
                );
                liste.add(m);
            }
        }
        return liste;
    }
 public void addMedicament(Medicament med, Connection conn) throws SQLException {
     String query = "INSERT INTO Medicament (id_medicament, nom, dosage, stock, prix_unitaire) VALUES (?, ?, ?, ?, ?)";
     PreparedStatement stmt = conn.prepareStatement(query);
     stmt.setInt(1, med.getId());
     stmt.setString(2, med.getNom());
     stmt.setString(3, med.getDosage());
     stmt.setInt(4, med.getStock());
     stmt.setDouble(5, med.getPrixUnitaire());
     stmt.executeUpdate();
 }

 // Get sales by medicament using Vector
 public Vector<Vente> getVentesByMedicament(int idMedicament, Connection conn) throws SQLException {
     Vector<Vente> ventes = new Vector<>();
     String query = "SELECT * FROM Vente WHERE id_medicament = ?";
     PreparedStatement stmt = conn.prepareStatement(query);
     stmt.setInt(1, idMedicament);
     ResultSet rs = stmt.executeQuery();
     while (rs.next()) {
         Vente vente = new Vente(
             rs.getInt("id_vente"),
             rs.getInt("id_pharmacien"),
             rs.getInt("id_client"),
             rs.getInt("id_medicament"),
             rs.getInt("quantite"),
             rs.getTimestamp("date_vente")
         );
         ventes.add(vente);
     }
     return ventes;
 }
}