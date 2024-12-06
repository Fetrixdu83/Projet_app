import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Moderator{
    public static void showModeratorMenu(Connection connection, Scanner scanner, int moderatorId) {
        while (true) {
            System.out.println("\n=== Menu Modérateur ===");
            System.out.println("1. Voir les tâches en attente de validation");
            System.out.println("2. Valider une tâche");
            System.out.println("3. Retourner au menu principal");
            System.out.print("Choisissez une option : ");

            String choix = scanner.nextLine();

            if (choix.equals("1")) {
                afficherTachesEnAttente(connection);
            } else if (choix.equals("2")) {
                validerTache(connection, scanner);
            } else if (choix.equals("3")) {
                break;
            } else {
                System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    public static void afficherTachesEnAttente(Connection connection) {
        System.out.println("\n=== Tâches en Attente de Validation ===");

        String query = "SELECT id, description, created_by FROM tasks WHERE status = 'EN_ATTENTE_VALIDATION'";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("Aucune tâche en attente de validation.");
                return;
            }

            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                int createdBy = rs.getInt("created_by");

                System.out.println("ID: " + id + " | Description: " + description + " | Créée par (ID utilisateur): " + createdBy);
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des tâches en attente : " + e.getMessage());
        }
    }

    public static void validerTache(Connection connection, Scanner scanner) {
        int taskId = -1;
        try {
            System.out.print("Entrez l'ID de la tâche à valider : ");
            if (scanner.hasNextInt()) {
                taskId = scanner.nextInt();
                scanner.nextLine(); // Consomme le retour à la ligne

                String query = "UPDATE tasks SET status = 'LIBRE' WHERE id = ? AND status = 'EN_ATTENTE_VALIDATION'";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, taskId);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Tâche validée avec succès !");
                } else {
                    System.out.println("Impossible de valider cette tâche. Vérifiez qu'elle est en attente.");
                }

                stmt.close();
            }
            else{
                System.out.println("Entrée invalide. Veuillez entrer un entier.");
                scanner.nextLine(); // Consomme l'entrée incorrecte
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la validation de la tâche : " + e.getMessage());
        }
    }
}
