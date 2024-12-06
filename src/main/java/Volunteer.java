import java.sql.*;
import java.util.Scanner;

public class Volunteer {
    public static void showVolunteerMenu(Connection connection, Scanner scanner, int volunteerId) {
        while (true) {
            System.out.println("\n=== Menu Bénévole ===");
            System.out.println("1. Voir les tâches disponibles");
            System.out.println("2. Accepter une tâche");
            System.out.println("3. Voir les tâches acceptées");
            System.out.println("4. Annoncer qu'une tâche est terminée");
            System.out.println("5. Retourner au menu principal");
            System.out.print("Choisissez une option : ");

            String choix = scanner.nextLine();

            if (choix.equals("1")) {
                afficherTachesDisponibles(connection);
            } else if (choix.equals("2")) {
                accepterTache(connection, scanner, volunteerId);
            } else if (choix.equals("3")) {
                voirTachesAcceptees(connection, volunteerId);
            } else if (choix.equals("4")) {
                annoncerTacheFinie(connection, scanner, volunteerId);
            } else if (choix.equals("5")) {
                break;
            } else {
                System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }


    public static void afficherTachesDisponibles(Connection connection) {
        try {
            String query = "SELECT id, description, status FROM tasks WHERE status = 'LIBRE'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n=== Tâches Disponibles ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Description: " + rs.getString("description"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des tâches : " + e.getMessage());
        }
    }

    public static void accepterTache(Connection connection, Scanner scanner, int volunteerId) {
        int taskId =-1;
        try {
            System.out.print("Entrez l'ID de la tâche à accepter : ");
            if (scanner.hasNextInt()) {
                taskId = scanner.nextInt();
                scanner.nextLine();

                String query = "UPDATE tasks SET status = 'ACCEPTEE', accepted_by = ? WHERE id = ? AND status = 'LIBRE'";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, volunteerId);
                stmt.setInt(2, taskId);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Tâche acceptée avec succès !");
                } else {
                    System.out.println("Impossible d'accepter cette tâche.");
                }

                stmt.close();
            }
            else {
                System.out.println("Entrée invalide. Veuillez entrer un entier.");
                scanner.nextLine(); // Consomme l'entrée incorrecte
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'acceptation de la tâche : " + e.getMessage());
        }
    }

    public static void voirTachesAcceptees(Connection connection, int volunteerId) {
        try {
            String query = "SELECT id, description FROM tasks WHERE accepted_by = ? AND status = 'ACCEPTEE'";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, volunteerId);

            ResultSet rs = stmt.executeQuery();

            System.out.println("\n=== Mes Tâches Acceptées ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Description: " + rs.getString("description"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de vos tâches : " + e.getMessage());
        }
    }

    public static void annoncerTacheFinie(Connection connection, Scanner scanner, int volunteerId) {
        System.out.println("\n=== Annoncer une Tâche Terminée ===");

        try {
            // Récupérer les tâches acceptées par le bénévole
            String querySelect = "SELECT id, description FROM tasks WHERE accepted_by = ? AND status = 'ACCEPTEE'";
            PreparedStatement stmtSelect = connection.prepareStatement(querySelect);
            stmtSelect.setInt(1, volunteerId);

            ResultSet rs = stmtSelect.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("Vous n'avez accepté aucune tâche ou toutes vos tâches sont déjà terminées.");
                rs.close();
                stmtSelect.close();
                return;
            }

            System.out.println("Tâches Acceptées :");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Description: " + rs.getString("description"));
            }
            rs.close();
            stmtSelect.close();

            // Demander à l'utilisateur de choisir une tâche à terminer
            int taskId = -1;
            System.out.print("Entrez l'ID de la tâche que vous avez terminée : ");
            if (scanner.hasNextInt()) {
                taskId = scanner.nextInt();
                scanner.nextLine();

                // Mettre à jour le statut de la tâche dans la base de données
                String queryUpdate = "UPDATE tasks SET status = 'FINALISEE', accepted_by = NULL WHERE id = ? AND accepted_by = ?";
                PreparedStatement stmtUpdate = connection.prepareStatement(queryUpdate);
                stmtUpdate.setInt(1, taskId);
                stmtUpdate.setInt(2, volunteerId);

                int rowsUpdated = stmtUpdate.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Tâche marquée comme terminée et retirée de votre liste !");
                } else {
                    System.out.println("Impossible de terminer cette tâche. Vérifiez qu'elle est acceptée par vous.");
                }

                stmtUpdate.close();
            }
            else {
                System.out.println("Entrée invalide. Veuillez entrer un entier.");
                scanner.nextLine(); // Consomme l'entrée incorrecte
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la tâche : " + e.getMessage());
        }
    }


}
