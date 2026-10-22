package pharmacie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("=====================================");
            System.out.println("   BIENVENUE DANS LA GESTION PHARMACIE");
            System.out.println("=====================================");

            while (true) {
                System.out.print("\nLogin : ");
                String login = scanner.nextLine().trim();

                System.out.print("Mot de passe : ");
                String pwd = scanner.nextLine().trim();

                // Tentative pharmacien
                Pharmacien pharmacien = new Pharmacien(0, "", "", login, pwd);
                if (pharmacien.authentifier(login, pwd, conn)) {
                    System.out.println("\nBienvenue " + pharmacien.getPrenom() + " " + pharmacien.getNom() + " (Pharmacien)");
                    menuPharmacien(pharmacien, conn, scanner);
                    continue; // retour au login après déconnexion
                }

                // Tentative gestionnaire
                Gestionnaire gestionnaire = new Gestionnaire(0, "", "", login, pwd);
                if (gestionnaire.authentifier(login, pwd, conn)) {
                    System.out.println("\nBienvenue " + gestionnaire.getPrenom() + " " + gestionnaire.getNom() + " (Gestionnaire)");
                    menuGestionnaire(gestionnaire, conn, scanner);
                    continue;
                }

                System.out.println("\n→ Identifiants incorrects. Veuillez réessayer.\n");
            }

        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base : " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nAu revoir !");
        }
    }

    // =============================================
    //               MENU PHARMACIEN
    // =============================================
    private static void menuPharmacien(Pharmacien pharmacien, Connection conn, Scanner scanner) {
        while (true) {
            System.out.println("\n--- Menu Pharmacien ---");
            System.out.println("1. Enregistrer une vente");
            System.out.println("2. Consulter les ventes du jour");
            System.out.println("3. Consulter le stock actuel");
            System.out.println("4. Annuler une vente (remboursement stock)");
            System.out.println("5. Déconnexion");
            System.out.print("Votre choix : ");

            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("→ Entrée invalide. Choisissez un numéro.");
                continue;
            }

            switch (choix) {
                case 1:
                    pharmacien.enregistrerVente(conn, scanner);
                    break;
                case 2:
                    pharmacien.consulterVentesDuJour(conn);
                    break;
                case 3:
                    afficherStock(conn);
                    break;
                case 4:
                    pharmacien.annulerVente(conn, scanner);
                    break;
                case 5:
                    System.out.println("Déconnexion...");
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    // =============================================
    //             MENU GESTIONNAIRE
    // =============================================
    private static void menuGestionnaire(Gestionnaire gestionnaire, Connection conn, Scanner scanner) {
        DAO dao = new DAO();

        while (true) {
            System.out.println("\n--- Menu Gestionnaire ---");
            System.out.println("1. Ajouter un médicament");
            System.out.println("2. Mettre à jour le stock d'un médicament");
            System.out.println("3. Consulter les stocks critiques");
            System.out.println("4. Passer une commande de réapprovisionnement");
            System.out.println("5. Consulter l'historique des stocks");
            System.out.println("6. Générer un rapport simple des ventes");
            System.out.println("7. Déconnexion");
            System.out.print("Votre choix : ");

            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("→ Entrée invalide.");
                continue;
            }

            switch (choix) {
                case 1:
                    gestionnaire.ajouterMedicament(conn, scanner);
                    break;
                case 2:
                    gestionnaire.mettreAJourStock(conn, scanner);
                    break;
                case 3:
                    gestionnaire.consulterStockCritique(conn);
                    break;
                case 4:
                    gestionnaire.passerCommande(conn, scanner);
                    break;
                case 5:
                    gestionnaire.consulterHistoriqueStock(conn);
                    break;
                case 6:
                    genererRapportVentes(conn);
                    break;
                case 7:
                    System.out.println("Déconnexion...");
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    // =============================================
    //        Fonctions utilitaires communes
    // =============================================

    private static void afficherStock(Connection conn) {
        DAO dao = new DAO();
        try {
            Vector<Medicament> medicaments = dao.getAllMedicaments(conn);
            if (medicaments.isEmpty()) {
                System.out.println("Aucun médicament en stock.");
                return;
            }
            System.out.println("\nStock actuel :");
            System.out.println("ID   Nom                  Dosage     Stock   Prix unitaire");
            System.out.println("-------------------------------------------------------");
            for (Medicament m : medicaments) {
                System.out.printf("%-4d %-20s %-10s %-7d %.2f TND\n",
                        m.getId(), m.getNom(), m.getDosage(), m.getStock(), m.getPrixUnitaire());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lecture stock : " + e.getMessage());
        }
    }

    private static void genererRapportVentes(Connection conn) {
        String sql = "SELECT m.nom, SUM(v.quantite) as total_vendu, SUM(v.quantite * m.prix_unitaire) as chiffre_affaires " +
                     "FROM Vente v JOIN Medicament m ON v.id_medicament = m.id_medicament " +
                     "GROUP BY m.nom ORDER BY total_vendu DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nRapport des ventes (tous temps) :");
            System.out.println("Médicament            Quantité vendue   Chiffre d'affaires");
            System.out.println("------------------------------------------------------");

            boolean vide = true;
            while (rs.next()) {
                vide = false;
                System.out.printf("%-21s %-17d %,.2f TND\n",
                        rs.getString("nom"),
                        rs.getInt("total_vendu"),
                        rs.getDouble("chiffre_affaires"));
            }
            if (vide) {
                System.out.println("Aucune vente enregistrée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur rapport : " + e.getMessage());
        }
    }
}
