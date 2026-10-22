# Scripts SQL

À exécuter dans l'ordre :

| Script | Exécuté par | Rôle |
|---|---|---|
| `00_create_user.sql` | `SYSTEM` | Crée l'utilisateur `PHARMACIE` |
| `01_schema.sql` | `PHARMACIE` | Tables, contraintes, séquences |
| `02_seed.sql` | `PHARMACIE` | Comptes et médicaments de démonstration |
