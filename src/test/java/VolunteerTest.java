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
            String insertQuery = "INSERT INTO tasks (description, status, created_by) VALUES ('Tâche libre pour le test', 'LIBRE', ?)";
            PreparedStatement stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setInt(1, 1);
            stmtInsert.executeUpdate();
            stmtInsert.close();

            // Récupérer l'ID de la tâche
            String selectQuery = "SELECT id FROM tasks WHERE description = 'Tâche libre pour le test'";
            PreparedStatement stmtSelect = connection.prepareStatement(selectQuery);
            ResultSet rs = stmtSelect.executeQuery();
            rs.next();
            int taskId = rs.getInt("id");
            stmtSelect.close();

            // Accepter la tâche
            Scanner scanner = new Scanner(taskId + "\n");
            Volunteer.accepterTache(connection, scanner, 1);

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
            stmtDelete.close();
        } catch (SQLException e) {
            fail("Erreur lors du test d'acceptation de tâche : " + e.getMessage());
        }
    }
}
