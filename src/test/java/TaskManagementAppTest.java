import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagementAppTest {
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
    void testGetUserRole_ValidUserId() {
        try {
            // Insérer un utilisateur fictif dans la base de données pour le test
            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setString(1, "testuser");
            stmtInsert.setString(2, "password");
            stmtInsert.setString(3, "BENEFICIAIRE");
            stmtInsert.executeUpdate();
            stmtInsert.close();

            // Récupérer l'ID de l'utilisateur inséré
            String selectQuery = "SELECT id FROM users WHERE username = ?";
            PreparedStatement stmtSelect = connection.prepareStatement(selectQuery);
            stmtSelect.setString(1, "testuser");
            ResultSet rs = stmtSelect.executeQuery();

            assertTrue(rs.next(), "Aucun utilisateur trouvé pour le test.");

            int userId = rs.getInt("id");
            rs.close();
            stmtSelect.close();

            // Tester la fonction
            String role = TaskManagementApp.getUserRole(connection, userId);

            assertEquals("BENEFICIAIRE", role, "Le rôle récupéré ne correspond pas au rôle attendu.");

            // Nettoyer la base de données après le test
            String deleteQuery = "DELETE FROM users WHERE username = ?";
            PreparedStatement stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.setString(1, "testuser");
            stmtDelete.executeUpdate();
            stmtDelete.close();
        } catch (SQLException e) {
            fail("Erreur lors du test de récupération du rôle utilisateur : " + e.getMessage());
        }
    }

    @Test
    void testGetUserRole_InvalidUserId() {
        String role = TaskManagementApp.getUserRole(connection, -1);
        assertNull(role, "Le rôle devrait être null pour un ID utilisateur invalide.");
    }

    @Test
    void testCreateAccount_ValidInput() {
        try {
            Scanner scanner = new Scanner("newuser\npassword123\nBENEFICIAIRE\n");

            TaskManagementApp.createAccount(connection, scanner);

            // Vérifier que l'utilisateur a été ajouté
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement stmtCheck = connection.prepareStatement(query);
            stmtCheck.setString(1, "newuser");
            ResultSet rs = stmtCheck.executeQuery();

            assertTrue(rs.next(), "Aucune donnée retournée pour la vérification.");
            assertEquals(1, rs.getInt(1), "L'utilisateur n'a pas été correctement créé.");
            rs.close();
            stmtCheck.close();

            // Nettoyer la base de données après le test
            String deleteQuery = "DELETE FROM users WHERE username = ?";
            PreparedStatement stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.setString(1, "newuser");
            stmtDelete.executeUpdate();
            stmtDelete.close();
        } catch (SQLException e) {
            fail("Erreur lors du test de création de compte : " + e.getMessage());
        }
    }

    @Test
    void testLogin_ValidCredentials() {
        try {
            // Insérer un utilisateur fictif dans la base de données pour le test
            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmtInsert = connection.prepareStatement(insertQuery);
            stmtInsert.setString(1, "loginuser");
            stmtInsert.setString(2, "password123");
            stmtInsert.setString(3, "BENEVOLE");
            stmtInsert.executeUpdate();
            stmtInsert.close();

            Scanner scanner = new Scanner("loginuser\npassword123\n");
            int userId = TaskManagementApp.login(connection, scanner);

            assertTrue(userId > 0, "L'ID utilisateur devrait être valide pour des identifiants corrects.");

            // Nettoyer la base de données après le test
            String deleteQuery = "DELETE FROM users WHERE username = ?";
            PreparedStatement stmtDelete = connection.prepareStatement(deleteQuery);
            stmtDelete.setString(1, "loginuser");
            stmtDelete.executeUpdate();
            stmtDelete.close();
        } catch (SQLException e) {
            fail("Erreur lors du test de connexion : " + e.getMessage());
        }
    }

    @Test
    void testLogin_InvalidCredentials() {
        Scanner scanner = new Scanner("wronguser\nwrongpassword\n");
        int userId = TaskManagementApp.login(connection, scanner);
        assertEquals(-1, userId, "La connexion aurait dû échouer pour des identifiants incorrects.");
    }
}
