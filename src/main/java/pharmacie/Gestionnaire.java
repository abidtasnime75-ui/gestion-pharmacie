package pharmacie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Gestionnaire extends Utilisateur {

    public Gestionnaire(int id, String nom, String prenom, String login, String pwd) {
        super(id, nom, prenom, login, pwd);
    }

    @Override
    public boolean authentifier(String loginSaisi, String pwdSaisi, Connection conn) throws SQLException {
        String sql = "SELECT id_gestionnaire, nom, prenom FROM Gestionnaire WHERE login = ? AND pwd = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginSaisi);
            ps.setString(2, pwdSaisi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    this.id = rs.getInt("id_gestionnaire");
                    this.nom = rs.getString("nom");
                    this.prenom = rs.getString("prenom");
                    return true;
                }
                return false;
            }
        }
    }

    public void ajouterMedicament(Connection conn, Scanner scanner) {
        try {
            System.out.print("Nom du médicament : ");
            String nom = scanner.nextLine();

            System.out.print("Dosage : ");
            String dosage = scanner.nextLine();

            System.out.print("Stock initial : ");
            int stock = Integer.parseInt(scanner.nextLine());

            System.out.print("Prix unitaire : ");
            double prix = Double.parseDouble(scanner.nextLine());

            String sql ="INSERT INTO Medicament"+
            		"(id_medicament, nom, dosage, stock, prix_unitaire)"+
            		"VALUES (seq_medicament.NEXTVAL, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nom);
                ps.setString(2, dosage);
                ps.setInt(3, stock);
                ps.setDouble(4, prix);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Médicament ajouté.");

        } catch (Exception e) {
            System.out.println("Erreur ajout médicament : " + e.getMessage());
            try { conn.rollback(); } catch (Exception ignored) {}
        }
    }

    public void mettreAJourStock(Connection conn, Scanner scanner) {
        try {
            System.out.print("ID du médicament : ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Nouvelle quantité en stock : ");
            int nouveauStock = Integer.parseInt(scanner.nextLine());

            // Ancien stock (pour journaliser la variation réelle)
            int ancienStock;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT stock FROM Medicament WHERE id_medicament = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Médicament non trouvé.");
                        return;
                    }
                    ancienStock = rs.getInt("stock");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE Medicament SET stock = ? WHERE id_medicament = ?")) {
                ps.setInt(1, nouveauStock);
                ps.setInt(2, id);
                ps.executeUpdate();
            }

            // Historique : on enregistre la variation (+ entrée, - sortie)
            String hist = "INSERT INTO StockHistorique (id_historique, id_medicament, quantite, date_mouvement, type_mouvement) " +
                          "VALUES (seq_historique.NEXTVAL, ?, ?, SYSDATE, 'AJUSTEMENT_MANUEL')";
            try (PreparedStatement ps = conn.prepareStatement(hist)) {
                ps.setInt(1, id);
                ps.setInt(2, nouveauStock - ancienStock);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Stock mis à jour.");

        } catch (Exception e) {
            System.out.println("Erreur mise à jour stock : " + e.getMessage());
            try { conn.rollback(); } catch (Exception ignored) {}
        }
    }

    public void consulterStockCritique(Connection conn) {
        String sql = "SELECT nom, dosage, stock, prix_unitaire FROM Medicament WHERE stock <= 10 ORDER BY stock";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nMédicaments en stock critique (≤ 10 unités) :");
            boolean trouve = false;
            while (rs.next()) {
                trouve = true;
                System.out.printf("%-20s %-10s Stock: %d   Prix: %.2f TND\n",
                        rs.getString("nom"), rs.getString("dosage"),
                        rs.getInt("stock"), rs.getDouble("prix_unitaire"));

                // Simulation notification
                System.out.println("   → Notification envoyée : RUPTURE PROCHAINE - " + rs.getString("nom"));
            }
            if (!trouve) {
                System.out.println("Aucun médicament en stock critique.");
            }

        } catch (SQLException e) {
            System.out.println("Erreur consultation critique : " + e.getMessage());
        }
    }

    public void passerCommande(Connection conn, Scanner scanner) {
        try {
            System.out.print("ID du médicament à commander : ");
            int idMed = Integer.parseInt(scanner.nextLine());

            System.out.print("Quantité à commander : ");
            int qte = Integer.parseInt(scanner.nextLine());

            String sql = "INSERT INTO Commande " +
                    "(id_commande, id_gestionnaire, id_medicament, quantite, date_commande) " +
                    "VALUES (seq_commande.NEXTVAL, ?, ?, ?, SYSDATE)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, this.id);
                ps.setInt(2, idMed);
                ps.setInt(3, qte);
                ps.executeUpdate();
            }

            // Mise à jour stock (simplification : on ajoute directement)
            String update = "UPDATE Medicament SET stock = stock + ? WHERE id_medicament = ?";
            try (PreparedStatement ps = conn.prepareStatement(update)) {
                ps.setInt(1, qte);
                ps.setInt(2, idMed);
                ps.executeUpdate();
            }

            // Historique
            String hist = "INSERT INTO StockHistorique " +
                    "(id_historique, id_medicament, quantite, date_mouvement, type_mouvement) " +
                    "VALUES (seq_historique.NEXTVAL, ?, ?, SYSDATE, 'COMMANDE')";

            try (PreparedStatement ps = conn.prepareStatement(hist)) {
                ps.setInt(1, idMed);
                ps.setInt(2, qte);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Commande enregistrée et stock mis à jour.");

        } catch (Exception e) {
            System.out.println("Erreur commande : " + e.getMessage());
            try { conn.rollback(); } catch (Exception ignored) {}
        }
    }

    public void consulterHistoriqueStock(Connection conn) {
        String sql = "SELECT m.nom, h.quantite, h.date_mouvement, h.type_mouvement " +
                     "FROM StockHistorique h JOIN Medicament m ON h.id_medicament = m.id_medicament " +
                     "ORDER BY h.date_mouvement DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nHistorique des mouvements de stock :");
            while (rs.next()) {
                System.out.printf("%-20s | Qté: %+5d | Type: %-12s | %s\n",
                        rs.getString("nom"), rs.getInt("quantite"),
                        rs.getString("type_mouvement"), rs.getTimestamp("date_mouvement"));
            }

        } catch (SQLException e) {
            System.out.println("Erreur historique : " + e.getMessage());
        }
    }
}