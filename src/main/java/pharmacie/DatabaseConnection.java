package pharmacie;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fournit la connexion Oracle. Les identifiants ne sont plus écrits dans le code :
 * ils sont lus (par ordre de priorité) dans
 *   1. les variables d'environnement PHARMACIE_DB_URL / PHARMACIE_DB_USER / PHARMACIE_DB_PASSWORD
 *   2. le fichier db.properties (à la racine du projet, ignoré par Git)
 *   3. les valeurs par défaut pour URL et USER (Oracle XE local)
 */
public class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DEFAULT_USER = "PHARMACIE";

    private static final Properties FILE_PROPS = loadFile();

    private static Properties loadFile() {
        Properties p = new Properties();
        Path path = Path.of("db.properties");
        if (Files.isRegularFile(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                p.load(in);
            } catch (IOException e) {
                System.err.println("Lecture de db.properties impossible : " + e.getMessage());
            }
        }
        return p;
    }

    private static String setting(String envName, String propName, String fallback) {
        String v = System.getenv(envName);
        if (v == null || v.isBlank()) v = FILE_PROPS.getProperty(propName);
        return (v == null || v.isBlank()) ? fallback : v;
    }

    public static Connection getConnection() throws SQLException {
        String url = setting("PHARMACIE_DB_URL", "db.url", DEFAULT_URL);
        String user = setting("PHARMACIE_DB_USER", "db.user", DEFAULT_USER);
        String password = setting("PHARMACIE_DB_PASSWORD", "db.password", null);

        if (password == null) {
            throw new SQLException("Mot de passe Oracle manquant : copiez db.properties.example en "
                    + "db.properties et renseignez db.password (ou définissez PHARMACIE_DB_PASSWORD).");
        }

        Connection conn = DriverManager.getConnection(url, user, password);
        conn.setAutoCommit(false); // transactions gérées explicitement (commit / rollback)
        return conn;
    }

    // Test rapide : mvn exec:java -Dexec.mainClass=pharmacie.DatabaseConnection
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion Oracle réussie !");
        } catch (SQLException e) {
            System.err.println("Erreur connexion : " + e.getMessage());
        }
    }
}
