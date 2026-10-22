-- =====================================================================
--  Données de démonstration (comptes en clair : DÉMO UNIQUEMENT)
-- =====================================================================

-- Pharmacien : karim / pharm2025
INSERT INTO Pharmacien (id_pharmacien, nom, prenom, login, pwd)
VALUES (seq_pharmacien.NEXTVAL, 'Ben Ali', 'Karim', 'karim', 'pharm2025');

-- Gestionnaire : gestionnaire1 / admin
INSERT INTO Gestionnaire (id_gestionnaire, nom, prenom, login, pwd)
VALUES (seq_gestionnaire.NEXTVAL, 'Martin', 'Sophie', 'gestionnaire1', 'admin');

-- Clients
INSERT INTO Client (id_client, nom, prenom, email, adresse)
VALUES (seq_client.NEXTVAL, 'Ben Ali', 'Ahmed', 'ahmed@email.com', 'Tunis');
INSERT INTO Client (id_client, nom, prenom, email, adresse)
VALUES (seq_client.NEXTVAL, 'Trabelsi', 'Salma', 'salma@email.com', 'Jendouba');

-- Médicaments (le 3e est en stock critique pour tester les alertes)
INSERT INTO Medicament (id_medicament, nom, dosage, stock, prix_unitaire)
VALUES (seq_medicament.NEXTVAL, 'Paracétamol', '500mg', 120, 2.50);
INSERT INTO Medicament (id_medicament, nom, dosage, stock, prix_unitaire)
VALUES (seq_medicament.NEXTVAL, 'Amoxicilline', '1g', 45, 8.90);
INSERT INTO Medicament (id_medicament, nom, dosage, stock, prix_unitaire)
VALUES (seq_medicament.NEXTVAL, 'Ibuprofène', '400mg', 6, 4.20);

COMMIT;
