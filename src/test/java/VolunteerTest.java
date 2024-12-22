import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class VolunteerTest {
    private Connection connection;

    @BeforeEach
    void setup() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://srv-bdens.insa-toulouse.fr:3306/projet_gei_018",
                    "projet_gei_018",
                    "ahLah8ie"
            );
        } catch (SQLException e) {
            fail("La connexion à la base de données a échoué : " + e.getMessage());
        }
    }

    @Test
    void testAccepterTache() {
        try {
            // Insérer 2 utilisateurs fictifs dans la base de données pour le test
            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setString(1, "benef_test");
            stmtInsert.setString(2, "password");
            stmtInsert.setString(3, "BENEFICIAIRE");
            stmtInsert.executeUpdate();
            insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setString(1, "benev_test");
            stmtInsert.setString(2, "password");
            stmtInsert.setString(3, "BENEVOLE");
            stmtInsert.executeUpdate();
            stmtInsert.close();

            // Récupérer l'ID des utilisateurs insérés
            String selectQuery = "SELECT id FROM users WHERE username = ?";
            PreparedStatement stmtSelect = connection.prepareStatement(selectQuery);
            stmtSelect.setString(1, "benef_test");
            ResultSet rsid = stmtSelect.executeQuery();
            assertTrue(rsid.next(), "Aucun utilisateur trouvé pour le test.");
            int userIdBenef = rsid.getInt("id");

            selectQuery = "SELECT id FROM users WHERE username = ?";
            stmtSelect = connection.prepareStatement(selectQuery);
            stmtSelect.setString(1, "benev_test");
            rsid = stmtSelect.executeQuery();
            assertTrue(rsid.next(), "Aucun utilisateur trouvé pour le test.");
            int userIdBenev = rsid.getInt("id");

            rsid.close();
            stmtSelect.close();

            // Ajoute une TAche
            insertQuery = "INSERT INTO tasks (description, status, created_by) VALUES ('Tâche libre pour le test', 'LIBRE', ?)";
            stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setInt(1, userIdBenef);
            stmtInsert.executeUpdate();
            stmtInsert.close();

            // Récupérer l'ID de la tâche
            selectQuery = "SELECT id FROM tasks WHERE description = 'Tâche libre pour le test'";
            stmtSelect = connection.prepareStatement(selectQuery);
            ResultSet rs = stmtSelect.executeQuery();
            rs.next();
            int taskId = rs.getInt("id");
            stmtSelect.close();

            // Accepter la tâche
            Scanner scanner = new Scanner(taskId + "\n");
            Volunteer.accepterTache(connection, scanner, userIdBenev);

            // Vérifier que la tâche a été acceptée
            String checkQuery = "SELECT status FROM tasks WHERE id = ?";
            PreparedStatement stmtCheck = connection.prepareStatement(checkQuery);
            stmtCheck.setInt(1, taskId);
            ResultSet rsCheck = stmtCheck.executeQuery();
            rsCheck.next();
            assertEquals("ACCEPTEE", rsCheck.getString("status"), "La tâche n'a pas été acceptée correctement.");

            rsCheck.close();
            stmtCheck.close();

            // Nettoyer la base de données
            String deleteQuery = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.setInt(1, taskId);
            stmtDelete.executeUpdate();
            deleteQuery = "DELETE FROM users WHERE username = 'benef_test'";
            stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.executeUpdate();
            stmtDelete.close();
            deleteQuery = "DELETE FROM users WHERE username = 'benev_test'";
            stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.executeUpdate();
            stmtDelete.close();
        } catch (SQLException e) {
            fail("Erreur lors du test d'acceptation de tâche : " + e.getMessage());
        }
    }
}
