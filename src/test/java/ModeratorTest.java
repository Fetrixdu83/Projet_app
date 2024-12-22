import org.junit.jupiter.api.BeforeAll;
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
            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setString(1, "benef_test");
            stmtInsert.setString(2, "password");
            stmtInsert.setString(3, "BENEFICIAIRE");
            stmtInsert.executeUpdate();

            // Récupérer l'ID des utilisateurs insérés
            String selectQuery = "SELECT id FROM users WHERE username = ?";
            PreparedStatement stmtSelect = connection.prepareStatement(selectQuery);
            stmtSelect.setString(1, "benef_test");
            ResultSet rsid = stmtSelect.executeQuery();
            assertTrue(rsid.next(), "Aucun utilisateur trouvé pour le test.");
            int userId = rsid.getInt("id");


            rsid.close();
            stmtSelect.close();


            // Ajouter une tâche en attente
            insertQuery = "INSERT INTO tasks (description, status, created_by) VALUES ('Tâche en attente pour le test', 'EN_ATTENTE_VALIDATION', ?)";
            stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setInt(1, userId);
            stmtInsert.executeUpdate();
            stmtInsert.close();

            // Récupérer l'ID de la tâche
            selectQuery = "SELECT id FROM tasks WHERE description = 'Tâche en attente pour le test'";
            stmtSelect = connection.prepareStatement(selectQuery);
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
            deleteQuery = "DELETE FROM users WHERE username = 'benef_test'";
            stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.executeUpdate();
            stmtDelete.close();
            stmtDelete.close();
        } catch (SQLException e) {
            fail("Erreur lors du test de validation de tâche : " + e.getMessage());
        }
    }
}
