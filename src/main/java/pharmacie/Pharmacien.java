package pharmacie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Pharmacien extends Utilisateur {

    public Pharmacien(int id, String nom, String prenom, String login, String pwd) {
        super(id, nom, prenom, login, pwd);
    }

    @Override
    public boolean authentifier(String loginSaisi, String pwdSaisi, Connection conn) throws SQLException {
        String sql = "SELECT id_pharmacien, nom, prenom FROM Pharmacien WHERE login = ? AND pwd = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginSaisi);
            ps.setString(2, pwdSaisi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    this.id = rs.getInt("id_pharmacien");
                    this.nom = rs.getString("nom");
                    this.prenom = rs.getString("prenom");
                    return true;
                }
                return false;
            }
        }
    }

    public void enregistrerVente(Connection conn, Scanner scanner) {
        try {
            System.out.print("ID du médicament : ");
            int idMed = Integer.parseInt(scanner.nextLine());

            System.out.print("ID du client (ou 0 si inconnu) : ");
            int idClient = Integer.parseInt(scanner.nextLine());

            System.out.print("Quantité : ");
            int quantite = Integer.parseInt(scanner.nextLine());

            // Vérifier stock suffisant
            String checkSql = "SELECT stock FROM Medicament WHERE id_medicament = ?";
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, idMed);
                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next() || rs.getInt("stock") < quantite) {
                        System.out.println("Stock insuffisant ou médicament inexistant !");
                        return;
                    }
                }
            }

            // Insérer la vente
            String insertVente = "INSERT INTO Vente " +
                    "(id_vente, id_pharmacien, id_client, id_medicament, quantite, date_vente) " +
                    "VALUES (seq_vente.NEXTVAL, ?, ?, ?, ?, SYSDATE)";

            try (PreparedStatement ps = conn.prepareStatement(insertVente)) {
                ps.setInt(1, this.id);
                if (idClient > 0) {
                    ps.setInt(2, idClient);
                } else {
                    ps.setNull(2, java.sql.Types.NUMERIC);
                }
                ps.setInt(3, idMed);
                ps.setInt(4, quantite);
                ps.executeUpdate();
            }

            // Mettre à jour stock
            String updateStock = "UPDATE Medicament SET stock = stock - ? WHERE id_medicament = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateStock)) {
                ps.setInt(1, quantite);
                ps.setInt(2, idMed);
                ps.executeUpdate();
            }

            // Historique
            String hist ="INSERT INTO StockHistorique " +
                    "(id_historique, id_medicament, quantite, date_mouvement, type_mouvement) " +
                    "VALUES (seq_historique.NEXTVAL, ?, ?, SYSDATE, 'VENTE')";

            try (PreparedStatement ps = conn.prepareStatement(hist)) {
                ps.setInt(1, idMed);
                ps.setInt(2, -quantite);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Vente enregistrée avec succès !");

        } catch (Exception e) {
            System.out.println("Erreur lors de la vente : " + e.getMessage());
            try { conn.rollback(); } catch (Exception ignored) {}
        }
    }

    public void consulterVentesDuJour(Connection conn) {
        String sql = "SELECT v.id_vente, m.nom, v.quantite, v.date_vente, c.nom || ' ' || c.prenom as client " +
                     "FROM Vente v JOIN Medicament m ON v.id_medicament = m.id_medicament " +
                     "LEFT JOIN Client c ON v.id_client = c.id_client " +
                     "WHERE v.id_pharmacien = ? AND TRUNC(v.date_vente) = TRUNC(SYSDATE)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, this.id);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\nVentes du jour :");
                while (rs.next()) {
                    System.out.printf("ID: %d | Médicament: %-15s | Qté: %d | Client: %s | Date: %s\n",
                            rs.getInt("id_vente"), rs.getString("nom"), rs.getInt("quantite"),
                            rs.getString("client") != null ? rs.getString("client") : "Inconnu",
                            rs.getTimestamp("date_vente"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur consultation ventes : " + e.getMessage());
        }
    }

    public void annulerVente(Connection conn, Scanner scanner) {
        System.out.print("ID de la vente à annuler : ");
        try {
            int idVente = Integer.parseInt(scanner.nextLine());

            // Récupérer infos vente
            String getVente = "SELECT id_medicament, quantite FROM Vente WHERE id_vente = ? AND id_pharmacien = ?";
            int idMed = 0, qte = 0;
            try (PreparedStatement ps = conn.prepareStatement(getVente)) {
                ps.setInt(1, idVente);
                ps.setInt(2, this.id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Vente non trouvée ou non autorisée.");
                        return;
                    }
                    idMed = rs.getInt("id_medicament");
                    qte = rs.getInt("quantite");
                }
            }

            // Supprimer vente
            String delete = "DELETE FROM Vente WHERE id_vente = ?";
            try (PreparedStatement ps = conn.prepareStatement(delete)) {
                ps.setInt(1, idVente);
                ps.executeUpdate();
            }

            // Remettre stock
            String update = "UPDATE Medicament SET stock = stock + ? WHERE id_medicament = ?";
            try (PreparedStatement ps = conn.prepareStatement(update)) {
                ps.setInt(1, qte);
                ps.setInt(2, idMed);
                ps.executeUpdate();
            }

            // Historique
            String hist = "INSERT INTO StockHistorique " +
                    "(id_historique, id_medicament, quantite, date_mouvement, type_mouvement) " +
                    "VALUES (seq_historique.NEXTVAL, ?, ?, SYSDATE, 'ANNULATION_VENTE')";

            try (PreparedStatement ps = conn.prepareStatement(hist)) {
                ps.setInt(1, idMed);
                ps.setInt(2, qte);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Vente annulée et stock remis à jour.");

        } catch (Exception e) {
            System.out.println("Erreur annulation : " + e.getMessage());
            try { conn.rollback(); } catch (Exception ignored) {}
        }
    }
}