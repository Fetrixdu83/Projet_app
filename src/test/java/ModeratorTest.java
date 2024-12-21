import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ModeratorTest {
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
    void testValiderTache() {
        try {
            // Ajouter une tâche en attente
            String insertQuery = "INSERT INTO tasks (description, status, created_by) VALUES (?, 'EN_ATTENTE_VALIDATION', ?)";
            PreparedStatement stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setString(1, "Tâche en attente pour le test");
            stmtInsert.setInt(2, 1);
            stmtInsert.executeUpdate();
            stmtInsert.close();

            // Récupérer l'ID de la tâche
            String selectQuery = "SELECT id FROM tasks WHERE description = 'Tâche en attente pour le test'";
            PreparedStatement stmtSelect = connection.prepareStatement(selectQuery);
            ResultSet rs = stmtSelect.executeQuery();
            rs.next();
            int taskId = rs.getInt("id");
            stmtSelect.close();

            // Valider la tâche
            Scanner scanner = new Scanner(taskId + "\n");
            Moderator.validerTache(connection, scanner);

            // Vérifier que la tâche a été validée
            String checkQuery = "SELECT status FROM tasks WHERE id = ?";
            PreparedStatement stmtCheck = connection.prepareStatement(checkQuery);
            stmtCheck.setInt(1, taskId);
            ResultSet rsCheck = stmtCheck.executeQuery();
            rsCheck.next();
            assertEquals("LIBRE", rsCheck.getString("status"), "La tâche n'a pas été validée correctement.");

            rsCheck.close();
            stmtCheck.close();

            // Nettoyer la base de données
            String deleteQuery = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.setInt(1, taskId);
            stmtDelete.executeUpdate();
            stmtDelete.close();
        } catch (SQLException e) {
            fail("Erreur lors du test de validation de tâche : " + e.getMessage());
        }
    }
}
