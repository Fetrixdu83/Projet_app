import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class BeneficiaryTest {
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
    void testPosterNouvelleTache() {
        try {
            Scanner scanner = new Scanner("Nouvelle tâche de test\n");
            Beneficiary.posterNouvelleTache(connection, scanner, 1);

            // Vérifier que la tâche a été ajoutée
            String query = "SELECT COUNT(*) FROM tasks WHERE description = 'Nouvelle tâche de test' AND created_by = ?";
            PreparedStatement stmtCheck = connection.prepareStatement(query);
            stmtCheck.setInt(1, 1);
            ResultSet rs = stmtCheck.executeQuery();

            assertTrue(rs.next(), "Aucune donnée retournée pour la vérification.");
            assertEquals(1, rs.getInt(1), "La tâche n'a pas été correctement créée.");

            rs.close();
            stmtCheck.close();

            // Nettoyer la base de données
            String deleteQuery = "DELETE FROM tasks WHERE description = 'Nouvelle tâche de test'";
            PreparedStatement stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.executeUpdate();
            stmtDelete.close();
        } catch (SQLException e) {
            fail("Erreur lors du test de création de tâche : " + e.getMessage());
        }
    }

    @Test
    void testVoirTachesPostees() {
        try {
            // Ajouter une tâche pour le test
            String insertQuery = "INSERT INTO tasks (description, status, created_by) VALUES (?, 'EN_ATTENTE_VALIDATION', ?)";
            PreparedStatement stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setString(1, "Tâche postée pour le test");
            stmtInsert.setInt(2, 1);
            stmtInsert.executeUpdate();
            stmtInsert.close();

            // Vérifier que la méthode affiche les tâches
            Beneficiary.voirTachesPostees(connection, 1);

            // Nettoyer la base de données
            String deleteQuery = "DELETE FROM tasks WHERE description = 'Tâche postée pour le test'";
            PreparedStatement stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.executeUpdate();
            stmtDelete.close();
        } catch (SQLException e) {
            fail("Erreur lors du test de visualisation des tâches postées : " + e.getMessage());
        }
    }
}
