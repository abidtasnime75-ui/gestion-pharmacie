-- À exécuter UNE fois, connecté en SYSTEM (ou SYS) sur Oracle XE.
-- Remplace 'ChangeMe_123' par un mot de passe de ton choix.
-- (Sur Oracle 12c+ en mode multitenant, connecte-toi d'abord au PDB : ALTER SESSION SET CONTAINER = XEPDB1;
--  l'URL JDBC devient alors jdbc:oracle:thin:@//localhost:1521/XEPDB1)

CREATE USER PHARMACIE IDENTIFIED BY "ChangeMe_123"
    DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;

GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO PHARMACIE;
