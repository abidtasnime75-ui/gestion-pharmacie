-- =====================================================================
--  Gestion de Pharmacie — schéma Oracle
--  À exécuter connecté en tant que PHARMACIE
--  Reconstitué à partir du rapport de projet (chapitre 5) et du code Java.
-- =====================================================================

-- 1. PHARMACIEN
CREATE TABLE Pharmacien (
    id_pharmacien  NUMBER         PRIMARY KEY,
    nom            VARCHAR2(50)   NOT NULL,
    prenom         VARCHAR2(50)   NOT NULL,
    login          VARCHAR2(50)   UNIQUE NOT NULL,
    pwd            VARCHAR2(100)  NOT NULL
);
CREATE SEQUENCE seq_pharmacien START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 2. GESTIONNAIRE
CREATE TABLE Gestionnaire (
    id_gestionnaire NUMBER        PRIMARY KEY,
    nom             VARCHAR2(50)  NOT NULL,
    prenom          VARCHAR2(50)  NOT NULL,
    login           VARCHAR2(50)  UNIQUE NOT NULL,
    pwd             VARCHAR2(100) NOT NULL
);
CREATE SEQUENCE seq_gestionnaire START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 3. CLIENT
CREATE TABLE Client (
    id_client  NUMBER         PRIMARY KEY,
    nom        VARCHAR2(50)   NOT NULL,
    prenom     VARCHAR2(50)   NOT NULL,
    email      VARCHAR2(100),
    adresse    VARCHAR2(200)
);
CREATE SEQUENCE seq_client START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 4. MEDICAMENT
CREATE TABLE Medicament (
    id_medicament  NUMBER         PRIMARY KEY,
    nom            VARCHAR2(100)  NOT NULL,
    dosage         VARCHAR2(50),
    stock          NUMBER         DEFAULT 0 NOT NULL CHECK (stock >= 0),
    prix_unitaire  NUMBER(10,2)   NOT NULL CHECK (prix_unitaire >= 0)
);
CREATE SEQUENCE seq_medicament START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 5. VENTE
CREATE TABLE Vente (
    id_vente       NUMBER     PRIMARY KEY,
    id_pharmacien  NUMBER     NOT NULL,
    id_client      NUMBER,                      -- NULL = client inconnu
    id_medicament  NUMBER     NOT NULL,
    quantite       NUMBER     NOT NULL CHECK (quantite > 0),
    date_vente     TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vente_pharmacien FOREIGN KEY (id_pharmacien) REFERENCES Pharmacien(id_pharmacien),
    CONSTRAINT fk_vente_client     FOREIGN KEY (id_client)     REFERENCES Client(id_client),
    CONSTRAINT fk_vente_medicament FOREIGN KEY (id_medicament) REFERENCES Medicament(id_medicament)
);
CREATE SEQUENCE seq_vente START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 6. COMMANDE (réapprovisionnement)
CREATE TABLE Commande (
    id_commande     NUMBER     PRIMARY KEY,
    id_gestionnaire NUMBER     NOT NULL,
    id_medicament   NUMBER     NOT NULL,
    quantite        NUMBER     NOT NULL CHECK (quantite > 0),
    date_commande   TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cmd_gestionnaire FOREIGN KEY (id_gestionnaire) REFERENCES Gestionnaire(id_gestionnaire),
    CONSTRAINT fk_cmd_medicament   FOREIGN KEY (id_medicament)   REFERENCES Medicament(id_medicament)
);
CREATE SEQUENCE seq_commande START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 7. STOCK_HISTORIQUE (traçabilité de tous les mouvements)
CREATE TABLE StockHistorique (
    id_historique   NUMBER       PRIMARY KEY,
    id_medicament   NUMBER       NOT NULL,
    quantite        NUMBER       NOT NULL,                 -- + = entrée, - = sortie
    date_mouvement  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    type_mouvement  VARCHAR2(20) DEFAULT 'AUTRE',          -- VENTE, COMMANDE, AJUSTEMENT_MANUEL, ANNULATION_VENTE
    CONSTRAINT fk_hist_medicament FOREIGN KEY (id_medicament) REFERENCES Medicament(id_medicament)
);
CREATE SEQUENCE seq_historique START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

COMMIT;
