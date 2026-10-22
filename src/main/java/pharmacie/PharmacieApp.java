package pharmacie;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.SQLException;

public class PharmacieApp extends Application {
    
    private Connection conn;
    private Stage primaryStage;
    
    // Palette de couleurs moderne et claire
    private static final String PRIMARY_COLOR = "#4A90E2";      // Bleu moderne
    private static final String SUCCESS_COLOR = "#52C41A";      // Vert vif
    private static final String WARNING_COLOR = "#FA8C16";      // Orange
    private static final String DANGER_COLOR = "#F5222D";       // Rouge
    private static final String INFO_COLOR = "#13C2C2";         // Cyan
    private static final String PURPLE_COLOR = "#722ED1";       // Violet
    private static final String BACKGROUND = "#F5F7FA";         // Gris très clair
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_DARK = "#2C3E50";
    private static final String TEXT_LIGHT = "#6C757D";
    
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            showError("Erreur de connexion", "Impossible de se connecter à la base de données : " + e.getMessage());
            return;
        }
        
        showLoginScreen();
        primaryStage.setTitle("Gestion Pharmacie - Système Moderne");
        primaryStage.show();
    }
    
    // Écran de connexion moderne
    private void showLoginScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");
        
        // Panneau de connexion avec carte blanche
        VBox loginCard = new VBox(25);
        loginCard.setMaxWidth(450);
        loginCard.setMaxHeight(550);
        loginCard.setPadding(new Insets(50, 40, 50, 40));
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 30, 0, 0, 10);"
        );
        
        // Logo/Icône
        Label iconLabel = new Label("💊");
        iconLabel.setStyle("-fx-font-size: 60px;");
        
        Label titleLabel = new Label("GESTION PHARMACIE");
        titleLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT_DARK + ";"
        );
        
        Label subtitleLabel = new Label("Connectez-vous à votre espace");
        subtitleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + TEXT_LIGHT + ";"
        );
        
        VBox header = new VBox(10, iconLabel, titleLabel, subtitleLabel);
        header.setAlignment(Pos.CENTER);
        
        // Champs de saisie avec style moderne
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        
        VBox loginBox = new VBox(8);
        Label loginLabel = new Label("Identifiant");
        loginLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: 600;");
        TextField loginField = new TextField();
        loginField.setPromptText("Entrez votre login");
        loginField.setStyle(
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14px;"
        );
        loginField.setPrefWidth(350);
        loginBox.getChildren().addAll(loginLabel, loginField);
        
        VBox pwdBox = new VBox(8);
        Label pwdLabel = new Label("Mot de passe");
        pwdLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: 600;");
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText("Entrez votre mot de passe");
        pwdField.setStyle(
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14px;"
        );
        pwdField.setPrefWidth(350);
        pwdBox.getChildren().addAll(pwdLabel, pwdField);
        
        formBox.getChildren().addAll(loginBox, pwdBox);
        
        Button loginBtn = createStyledButton("Se connecter", PRIMARY_COLOR, 350);
        loginBtn.setStyle(loginBtn.getStyle() + "-fx-padding: 15;");
        
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: " + DANGER_COLOR + "; -fx-font-size: 13px; -fx-font-weight: 500;");
        
        loginBtn.setOnAction(e -> {
            String login = loginField.getText().trim();
            String pwd = pwdField.getText().trim();
            
            if (login.isEmpty() || pwd.isEmpty()) {
                messageLabel.setText("⚠ Veuillez remplir tous les champs");
                return;
            }
            
            try {
                Pharmacien pharmacien = new Pharmacien(0, "", "", login, pwd);
                if (pharmacien.authentifier(login, pwd, conn)) {
                    showPharmacienMenu(pharmacien);
                    return;
                }
                
                Gestionnaire gestionnaire = new Gestionnaire(0, "", "", login, pwd);
                if (gestionnaire.authentifier(login, pwd, conn)) {
                    showGestionnaireMenu(gestionnaire);
                    return;
                }
                
                messageLabel.setText("❌ Identifiants incorrects");
                
            } catch (SQLException ex) {
                showError("Erreur", "Erreur d'authentification : " + ex.getMessage());
            }
        });
        
        pwdField.setOnAction(e -> loginBtn.fire());
        
        loginCard.getChildren().addAll(header, formBox, loginBtn, messageLabel);
        
        StackPane centerPane = new StackPane(loginCard);
        centerPane.setPadding(new Insets(40));
        root.setCenter(centerPane);
        
        Scene scene = new Scene(root, 900, 650);
        primaryStage.setScene(scene);
    }
    
    // Menu Pharmacien avec design moderne
    private void showPharmacienMenu(Pharmacien pharmacien) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");
        
        // En-tête moderne
        HBox header = new HBox(20);
        header.setPadding(new Insets(25, 30, 25, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + WHITE + ";" +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        
        Label iconLabel = new Label("👨‍⚕️");
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Espace Pharmacien");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label userLabel = new Label(pharmacien.getPrenom() + " " + pharmacien.getNom());
        userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_LIGHT + ";");
        titleBox.getChildren().addAll(titleLabel, userLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button decoBtn = new Button("Déconnexion");
        decoBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + DANGER_COLOR + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 2;" +
            "-fx-text-fill: " + DANGER_COLOR + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10 25;" +
            "-fx-cursor: hand;"
        );
        decoBtn.setOnAction(e -> showLoginScreen());
        
        header.getChildren().addAll(iconLabel, titleBox, spacer, decoBtn);
        root.setTop(header);
        
        // Grille de cartes pour les actions
        GridPane grid = new GridPane();
        grid.setHgap(25);
        grid.setVgap(25);
        grid.setPadding(new Insets(40));
        grid.setAlignment(Pos.CENTER);
        
        VBox venteCard = createActionCard("💰", "Nouvelle Vente", "Enregistrer une vente", SUCCESS_COLOR);
        venteCard.setOnMouseClicked(e -> showEnregistrerVente(pharmacien));
        
        VBox consultCard = createActionCard("📊", "Ventes du Jour", "Consulter l'historique", PRIMARY_COLOR);
        consultCard.setOnMouseClicked(e -> showVentesDuJour(pharmacien));
        
        VBox stockCard = createActionCard("📦", "Stock Actuel", "Voir les médicaments", INFO_COLOR);
        stockCard.setOnMouseClicked(e -> showStock());
        
        VBox annulerCard = createActionCard("↩️", "Annuler Vente", "Remboursement", DANGER_COLOR);
        annulerCard.setOnMouseClicked(e -> showAnnulerVente(pharmacien));
        
        grid.add(venteCard, 0, 0);
        grid.add(consultCard, 1, 0);
        grid.add(stockCard, 0, 1);
        grid.add(annulerCard, 1, 1);
        
        root.setCenter(grid);
        
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
    }
    
    // Menu Gestionnaire
    private void showGestionnaireMenu(Gestionnaire gestionnaire) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND + ";");
        
        HBox header = new HBox(20);
        header.setPadding(new Insets(25, 30, 25, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + WHITE + ";" +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        
        Label iconLabel = new Label("👨‍💼");
        iconLabel.setStyle("-fx-font-size: 32px;");
        
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Espace Gestionnaire");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        Label userLabel = new Label(gestionnaire.getPrenom() + " " + gestionnaire.getNom());
        userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_LIGHT + ";");
        titleBox.getChildren().addAll(titleLabel, userLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button decoBtn = new Button("Déconnexion");
        decoBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + DANGER_COLOR + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 2;" +
            "-fx-text-fill: " + DANGER_COLOR + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 10 25;" +
            "-fx-cursor: hand;"
        );
        decoBtn.setOnAction(e -> showLoginScreen());
        
        header.getChildren().addAll(iconLabel, titleBox, spacer, decoBtn);
        root.setTop(header);
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(30));
        grid.setAlignment(Pos.CENTER);
        
        VBox ajouterCard = createActionCard("➕", "Ajouter", "Nouveau médicament", SUCCESS_COLOR);
        ajouterCard.setOnMouseClicked(e -> showAjouterMedicament(gestionnaire));
        
        VBox majCard = createActionCard("🔄", "MAJ Stock", "Modifier quantités", PRIMARY_COLOR);
        majCard.setOnMouseClicked(e -> showMajStock(gestionnaire));
        
        VBox critiqueCard = createActionCard("⚠️", "Stock Critique", "Alertes rupture", DANGER_COLOR);
        critiqueCard.setOnMouseClicked(e -> showStockCritique(gestionnaire));
        
        VBox commandeCard = createActionCard("🛒", "Commande", "Réapprovisionner", PURPLE_COLOR);
        commandeCard.setOnMouseClicked(e -> showPasserCommande(gestionnaire));
        
        VBox historiqueCard = createActionCard("📜", "Historique", "Mouvements stock", WARNING_COLOR);
        historiqueCard.setOnMouseClicked(e -> showHistorique(gestionnaire));
        
        VBox rapportCard = createActionCard("📈", "Rapport", "Statistiques ventes", INFO_COLOR);
        rapportCard.setOnMouseClicked(e -> showRapportVentes());
        
        grid.add(ajouterCard, 0, 0);
        grid.add(majCard, 1, 0);
        grid.add(critiqueCard, 2, 0);
        grid.add(commandeCard, 0, 1);
        grid.add(historiqueCard, 1, 1);
        grid.add(rapportCard, 2, 1);
        
        root.setCenter(grid);
        
        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setScene(scene);
    }
    
    // Carte d'action moderne
    private VBox createActionCard(String emoji, String title, String description, String color) {
        VBox card = new VBox(15);
        card.setPrefSize(250, 180);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);" +
            "-fx-cursor: hand;"
        );
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 48px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT_DARK + ";"
        );
        
        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + TEXT_LIGHT + ";"
        );
        
        card.getChildren().addAll(emojiLabel, titleLabel, descLabel);
        
        // Effet hover
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: " + WHITE + ";" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, " + color + ", 20, 0, 0, 8);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: " + WHITE + ";" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        return card;
    }
    
    // Enregistrer une vente avec dialog moderne
    private void showEnregistrerVente(Pharmacien pharmacien) {
        Stage dialog = new Stage();
        dialog.setTitle("Nouvelle Vente");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("💰 Enregistrer une vente");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        VBox form = new VBox(15);
        
        VBox medBox = createFormField("ID Médicament", "");
        TextField medField = (TextField) medBox.getChildren().get(1);
        
        VBox clientBox = createFormField("ID Client (0 si inconnu)", "0");
        TextField clientField = (TextField) clientBox.getChildren().get(1);
        
        VBox qteBox = createFormField("Quantité", "");
        TextField qteField = (TextField) qteBox.getChildren().get(1);
        
        form.getChildren().addAll(medBox, clientBox, qteBox);
        
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        
        Button validerBtn = createStyledButton("Valider", SUCCESS_COLOR, 150);
        Button annulerBtn = createStyledButton("Annuler", TEXT_LIGHT, 150);
        
        buttons.getChildren().addAll(validerBtn, annulerBtn);
        
        validerBtn.setOnAction(e -> {
            try {
                int idMed = Integer.parseInt(medField.getText());
                int idClient = Integer.parseInt(clientField.getText());
                int qte = Integer.parseInt(qteField.getText());
                
                enregistrerVenteGraphique(pharmacien, idMed, idClient, qte);
                showInfo("Succès", "✓ Vente enregistrée avec succès !");
                dialog.close();
                
            } catch (NumberFormatException ex) {
                showError("Erreur", "Veuillez entrer des nombres valides");
            }
        });
        
        annulerBtn.setOnAction(e -> dialog.close());
        
        root.getChildren().addAll(titleLabel, form, buttons);
        
        Scene scene = new Scene(root, 450, 450);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private VBox createFormField(String label, String defaultValue) {
        VBox box = new VBox(8);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: 600;");
        
        TextField field = new TextField(defaultValue);
        field.setStyle(
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14px;"
        );
        
        box.getChildren().addAll(lbl, field);
        return box;
    }
    

    /** Ajoute une ligne dans StockHistorique (même transaction que l'opération appelante). */
    private void logMouvement(int idMed, int delta, String type) throws SQLException {
        try (java.sql.PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO StockHistorique (id_historique, id_medicament, quantite, date_mouvement, type_mouvement) " +
                "VALUES (seq_historique.NEXTVAL, ?, ?, SYSDATE, ?)")) {
            ps.setInt(1, idMed);
            ps.setInt(2, delta);
            ps.setString(3, type);
            ps.executeUpdate();
        }
    }

    private void enregistrerVenteGraphique(Pharmacien pharmacien, int idMed, int idClient, int qte) {
        try {
            java.sql.PreparedStatement check = conn.prepareStatement(
                "SELECT stock FROM Medicament WHERE id_medicament = ?"
            );
            check.setInt(1, idMed);
            java.sql.ResultSet rs = check.executeQuery();
            
            if (!rs.next() || rs.getInt("stock") < qte) {
                showError("Erreur", "Stock insuffisant ou médicament inexistant !");
                return;
            }
            
            String insertVente = "INSERT INTO Vente " +
                "(id_vente, id_pharmacien, id_client, id_medicament, quantite, date_vente) " +
                "VALUES (seq_vente.NEXTVAL, ?, ?, ?, ?, SYSDATE)";
            
            java.sql.PreparedStatement ps = conn.prepareStatement(insertVente);
            ps.setInt(1, pharmacien.getId());
            if (idClient > 0) {
                ps.setInt(2, idClient);
            } else {
                ps.setNull(2, java.sql.Types.NUMERIC);
            }
            ps.setInt(3, idMed);
            ps.setInt(4, qte);
            ps.executeUpdate();
            
            String updateStock = "UPDATE Medicament SET stock = stock - ? WHERE id_medicament = ?";
            ps = conn.prepareStatement(updateStock);
            ps.setInt(1, qte);
            ps.setInt(2, idMed);
            ps.executeUpdate();
            
            logMouvement(idMed, -qte, "VENTE");
            conn.commit();
            
        } catch (SQLException e) {
            showError("Erreur", "Erreur lors de la vente : " + e.getMessage());
            try { conn.rollback(); } catch (Exception ignored) {}
        }
    }
    
    private void showStock() {
        Stage dialog = new Stage();
        dialog.setTitle("Stock Actuel");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("📦 Stock Actuel des Médicaments");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefSize(750, 450);
        textArea.setStyle(
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + TEXT_DARK + ";"
        );
        
        try {
            DAO dao = new DAO();
            java.util.Vector<Medicament> meds = dao.getAllMedicaments(conn);
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-5s %-30s %-15s %-10s %s\n", "ID", "Nom", "Dosage", "Stock", "Prix"));
            sb.append("=".repeat(90)).append("\n");
            
            for (Medicament m : meds) {
                sb.append(String.format("%-5d %-30s %-15s %-10d %.2f TND\n",
                    m.getId(), m.getNom(), m.getDosage(), m.getStock(), m.getPrixUnitaire()));
            }
            
            textArea.setText(sb.toString());
            
        } catch (SQLException e) {
            textArea.setText("Erreur : " + e.getMessage());
        }
        
        root.getChildren().addAll(titleLabel, textArea);
        
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void showVentesDuJour(Pharmacien pharmacien) {
        Stage dialog = new Stage();
        dialog.setTitle("Ventes du Jour");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("📊 Ventes du Jour");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefSize(750, 450);
        textArea.setStyle(
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 13px;"
        );
        
        try {
            String sql = "SELECT v.id_vente, m.nom, v.quantite, v.date_vente, c.nom || ' ' || c.prenom as client " +
                "FROM Vente v JOIN Medicament m ON v.id_medicament = m.id_medicament " +
                "LEFT JOIN Client c ON v.id_client = c.id_client " +
                "WHERE v.id_pharmacien = ? AND TRUNC(v.date_vente) = TRUNC(SYSDATE)";
            
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, pharmacien.getId());
            java.sql.ResultSet rs = ps.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            
            while (rs.next()) {
                sb.append(String.format("ID: %d | Médicament: %s | Qté: %d | Client: %s | Date: %s\n",
                    rs.getInt("id_vente"), rs.getString("nom"), rs.getInt("quantite"),
                    rs.getString("client") != null ? rs.getString("client") : "Inconnu",
                    rs.getTimestamp("date_vente")));
            }
            
            if (sb.length() == 0) {
                sb.append("Aucune vente enregistrée aujourd'hui.");
            }
            
            textArea.setText(sb.toString());
            
        } catch (SQLException e) {
            textArea.setText("Erreur : " + e.getMessage());
        }
        
        root.getChildren().addAll(titleLabel, textArea);
        
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void showAnnulerVente(Pharmacien pharmacien) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Annuler une vente");
        dialog.setHeaderText("Annulation de vente");
        dialog.setContentText("ID de la vente à annuler :");
        
        dialog.showAndWait().ifPresent(idStr -> {
            try {
                int idVente = Integer.parseInt(idStr);
                
                String getVente = "SELECT id_medicament, quantite FROM Vente WHERE id_vente = ? AND id_pharmacien = ?";
                java.sql.PreparedStatement ps = conn.prepareStatement(getVente);
                ps.setInt(1, idVente);
                ps.setInt(2, pharmacien.getId());
                java.sql.ResultSet rs = ps.executeQuery();
                
                if (!rs.next()) {
                    showError("Erreur", "Vente non trouvée");
                    return;
                }
                
                int idMed = rs.getInt("id_medicament");
                int qte = rs.getInt("quantite");
                
                ps = conn.prepareStatement("DELETE FROM Vente WHERE id_vente = ?");
                ps.setInt(1, idVente);
                ps.executeUpdate();
                
                ps = conn.prepareStatement("UPDATE Medicament SET stock = stock + ? WHERE id_medicament = ?");
                ps.setInt(1, qte);
                ps.setInt(2, idMed);
                ps.executeUpdate();
                
                logMouvement(idMed, qte, "ANNULATION_VENTE");
                conn.commit();
                showInfo("Succès", "✓ Vente annulée et stock remis à jour");
                
            } catch (Exception e) {
                try { conn.rollback(); } catch (Exception ignored) {}
                showError("Erreur", e.getMessage());
            }
        });
    }
    
    private void showAjouterMedicament(Gestionnaire gestionnaire) {
        Stage dialog = new Stage();
        dialog.setTitle("Ajouter un Médicament");
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("➕ Nouveau Médicament");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        VBox form = new VBox(15);
        
        VBox nomBox = createFormField("Nom du médicament", "");
        TextField nomField = (TextField) nomBox.getChildren().get(1);
        
        VBox dosageBox = createFormField("Dosage", "");
        TextField dosageField = (TextField) dosageBox.getChildren().get(1);
        
        VBox stockBox = createFormField("Stock initial", "");
        TextField stockField = (TextField) stockBox.getChildren().get(1);
        
        VBox prixBox = createFormField("Prix unitaire (TND)", "");
        TextField prixField = (TextField) prixBox.getChildren().get(1);
        
        form.getChildren().addAll(nomBox, dosageBox, stockBox, prixBox);
        
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        
        Button validerBtn = createStyledButton("Ajouter", SUCCESS_COLOR, 150);
        Button annulerBtn = createStyledButton("Annuler", TEXT_LIGHT, 150);
        
        buttons.getChildren().addAll(validerBtn, annulerBtn);
        
        validerBtn.setOnAction(e -> {
            try {
                String sql = "INSERT INTO Medicament (id_medicament, nom, dosage, stock, prix_unitaire) " +
                    "VALUES (seq_medicament.NEXTVAL, ?, ?, ?, ?)";
                
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nomField.getText());
                ps.setString(2, dosageField.getText());
                ps.setInt(3, Integer.parseInt(stockField.getText()));
                ps.setDouble(4, Double.parseDouble(prixField.getText()));
                ps.executeUpdate();
                
                conn.commit();
                showInfo("Succès", "✓ Médicament ajouté avec succès");
                dialog.close();
                
            } catch (Exception ex) {
                showError("Erreur", ex.getMessage());
            }
        });
        
        annulerBtn.setOnAction(e -> dialog.close());
        
        root.getChildren().addAll(titleLabel, form, buttons);
        
        Scene scene = new Scene(root, 500, 500);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void showMajStock(Gestionnaire gestionnaire) {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Mise à jour stock");
        idDialog.setHeaderText("🔄 Mise à jour du stock");
        idDialog.setContentText("ID du médicament :");
        
        idDialog.showAndWait().ifPresent(idStr -> {
            TextInputDialog qteDialog = new TextInputDialog();
            qteDialog.setTitle("Mise à jour stock");
            qteDialog.setHeaderText("Nouvelle quantité en stock");
            qteDialog.setContentText("Quantité :");
            
            qteDialog.showAndWait().ifPresent(qteStr -> {
                try {
                    int id = Integer.parseInt(idStr);
                    int qte = Integer.parseInt(qteStr);
                    
                    int ancien;
                    try (java.sql.PreparedStatement sel = conn.prepareStatement(
                            "SELECT stock FROM Medicament WHERE id_medicament = ?")) {
                        sel.setInt(1, id);
                        try (java.sql.ResultSet rs = sel.executeQuery()) {
                            if (!rs.next()) {
                                showError("Erreur", "Médicament non trouvé");
                                return;
                            }
                            ancien = rs.getInt("stock");
                        }
                    }
                    
                    java.sql.PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Medicament SET stock = ? WHERE id_medicament = ?"
                    );
                    ps.setInt(1, qte);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                    
                    logMouvement(id, qte - ancien, "AJUSTEMENT_MANUEL");
                    conn.commit();
                    showInfo("Succès", "✓ Stock mis à jour avec succès");
                    
                } catch (Exception e) {
                    showError("Erreur", e.getMessage());
                }
            });
        });
    }
    
    private void showStockCritique(Gestionnaire gestionnaire) {
        Stage dialog = new Stage();
        dialog.setTitle("Stocks Critiques");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("⚠️ Médicaments en Stock Critique");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + DANGER_COLOR + ";");
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefSize(700, 400);
        textArea.setStyle(
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 13px;"
        );
        
        try {
            String sql = "SELECT nom, dosage, stock, prix_unitaire FROM Medicament WHERE stock <= 10 ORDER BY stock";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            sb.append("Médicaments avec stock ≤ 10 unités :\n\n");
            
            boolean trouve = false;
            while (rs.next()) {
                trouve = true;
                sb.append(String.format("⚠ %-30s %-15s Stock: %d Prix: %.2f TND\n",
                    rs.getString("nom"), rs.getString("dosage"),
                    rs.getInt("stock"), rs.getDouble("prix_unitaire")));
            }
            
            if (!trouve) {
                sb.append("✓ Aucun médicament en stock critique.");
            }
            
            textArea.setText(sb.toString());
            
        } catch (SQLException e) {
            textArea.setText("Erreur : " + e.getMessage());
        }
        
        root.getChildren().addAll(titleLabel, textArea);
        
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void showPasserCommande(Gestionnaire gestionnaire) {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Passer une commande");
        idDialog.setHeaderText("🛒 Réapprovisionnement");
        idDialog.setContentText("ID du médicament :");
        
        idDialog.showAndWait().ifPresent(idStr -> {
            TextInputDialog qteDialog = new TextInputDialog();
            qteDialog.setTitle("Passer une commande");
            qteDialog.setHeaderText("Quantité à commander");
            qteDialog.setContentText("Quantité :");
            
            qteDialog.showAndWait().ifPresent(qteStr -> {
                try {
                    int idMed = Integer.parseInt(idStr);
                    int qte = Integer.parseInt(qteStr);
                    
                    String sql = "INSERT INTO Commande (id_commande, id_gestionnaire, id_medicament, quantite, date_commande) " +
                        "VALUES (seq_commande.NEXTVAL, ?, ?, ?, SYSDATE)";
                    
                    java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, gestionnaire.getId());
                    ps.setInt(2, idMed);
                    ps.setInt(3, qte);
                    ps.executeUpdate();
                    
                    ps = conn.prepareStatement("UPDATE Medicament SET stock = stock + ? WHERE id_medicament = ?");
                    ps.setInt(1, qte);
                    ps.setInt(2, idMed);
                    ps.executeUpdate();
                    
                    logMouvement(idMed, qte, "COMMANDE");
                    conn.commit();
                    showInfo("Succès", "✓ Commande enregistrée et stock mis à jour");
                    
                } catch (Exception e) {
                    try { conn.rollback(); } catch (Exception ignored) {}
                    showError("Erreur", e.getMessage());
                }
            });
        });
    }
    
    private void showHistorique(Gestionnaire gestionnaire) {
        Stage dialog = new Stage();
        dialog.setTitle("Historique des Stocks");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("📜 Historique des Mouvements de Stock");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefSize(800, 450);
        textArea.setStyle(
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 12px;"
        );
        
        try {
            String sql = "SELECT m.nom, h.quantite, h.date_mouvement, h.type_mouvement " +
                "FROM StockHistorique h JOIN Medicament m ON h.id_medicament = m.id_medicament " +
                "ORDER BY h.date_mouvement DESC";
            
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            
            while (rs.next()) {
                sb.append(String.format("%-30s | Qté: %+5d | Type: %-20s | %s\n",
                    rs.getString("nom"), rs.getInt("quantite"),
                    rs.getString("type_mouvement"), rs.getTimestamp("date_mouvement")));
            }
            
            if (sb.length() == 0) {
                sb.append("Aucun mouvement enregistré.");
            }
            
            textArea.setText(sb.toString());
            
        } catch (SQLException e) {
            textArea.setText("Erreur : " + e.getMessage());
        }
        
        root.getChildren().addAll(titleLabel, textArea);
        
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void showRapportVentes() {
        Stage dialog = new Stage();
        dialog.setTitle("Rapport des Ventes");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + WHITE + ";");
        
        Label titleLabel = new Label("📈 Rapport Global des Ventes");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefSize(750, 450);
        textArea.setStyle(
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 13px;"
        );
        
        try {
            String sql = "SELECT m.nom, SUM(v.quantite) as total_vendu, SUM(v.quantite * m.prix_unitaire) as ca " +
                "FROM Vente v JOIN Medicament m ON v.id_medicament = m.id_medicament " +
                "GROUP BY m.nom ORDER BY total_vendu DESC";
            
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-35s %-15s %s\n", "Médicament", "Qté vendue", "Chiffre d'affaires"));
            sb.append("=".repeat(75)).append("\n");
            
            boolean vide = true;
            while (rs.next()) {
                vide = false;
                sb.append(String.format("%-35s %-15d %,.2f TND\n",
                    rs.getString("nom"), rs.getInt("total_vendu"), rs.getDouble("ca")));
            }
            
            if (vide) {
                sb.append("Aucune vente enregistrée.");
            }
            
            textArea.setText(sb.toString());
            
        } catch (SQLException e) {
            textArea.setText("Erreur : " + e.getMessage());
        }
        
        root.getChildren().addAll(titleLabel, textArea);
        
        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private Button createStyledButton(String text, String color, double width) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-padding: 12 25;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        
        return btn;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("❌ " + title);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + WHITE + ";");
        
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("ℹ️ " + title);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: " + WHITE + ";");
        
        alert.showAndWait();
    }
    
    @Override
    public void stop() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}