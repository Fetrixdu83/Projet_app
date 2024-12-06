import java.sql.*;
import java.util.Scanner;

public class Beneficiary {
    public static void showBeneficiaryMenu(Connection connection, Scanner scanner, int beneficiaryId) {
        while (true) {
            System.out.println("\n=== Menu Bénéficiaire ===");
            System.out.println("1. Voir mes tâches postées");
            System.out.println("2. Poster une nouvelle tâche");
            System.out.println("3. Laisser un avis sur une tâche terminée");
            System.out.println("4. Retourner au menu principal");
            System.out.print("Choisissez une option : ");

            String choix = scanner.nextLine(); // Consomme le retour à la ligne

            if (choix.equals("1")) {
                voirTachesPostees(connection, beneficiaryId);
            } else if (choix.equals("2")) {
                posterNouvelleTache(connection, scanner, beneficiaryId);
            } else if (choix.equals("3")) {
                laisserAvis(connection, scanner, beneficiaryId);
            } else if (choix.equals("4")) {
                break;
            } else {
                System.out.println("Option invalide. Veuillez réessayer. 555 ");
            }
        }
    }
    public static void voirTachesPostees(Connection connection, int beneficiaryId) {
        System.out.println("\n=== Mes Tâches Postées ===");

        String query = "SELECT id, description, status FROM tasks WHERE created_by = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, beneficiaryId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("Vous n'avez posté aucune tâche.");
                return;
            }

            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String status = rs.getString("status");

                System.out.println("ID: " + id + " | Description: " + description + " | Statut: " + status);
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de vos tâches : " + e.getMessage());
        }
    }

    public static void posterNouvelleTache(Connection connection, Scanner scanner, int beneficiaryId) {
        try {
            System.out.print("Entrez la description de la nouvelle tâche : ");
            String description = scanner.nextLine();

            String query = "INSERT INTO tasks (description, status, created_by) VALUES (?, 'EN_ATTENTE_VALIDATION', ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, description);
            stmt.setInt(2, beneficiaryId);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Nouvelle tâche postée avec succès. Elle est en attente de validation.");
            } else {
                System.out.println("Échec de la création de la tâche.");
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création de la tâche : " + e.getMessage());
        }
    }

    public static void laisserAvis(Connection connection, Scanner scanner, int beneficiaryId) {
        System.out.println("\n=== Laisser un Avis sur une Tâche Terminée ===");

        try {
            // Récupérer les tâches finalisées postées par le bénéficiaire
            String querySelect = "SELECT id, description FROM tasks WHERE created_by = ? AND status = 'FINALISEE'";
            PreparedStatement stmtSelect = connection.prepareStatement(querySelect);
            stmtSelect.setInt(1, beneficiaryId);

            ResultSet rs = stmtSelect.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("Aucune tâche terminée pour laquelle laisser un avis.");
                rs.close();
                stmtSelect.close();
                return;
            }

            System.out.println("Tâches Terminées :");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Description: " + rs.getString("description"));
            }
            rs.close();
            stmtSelect.close();

            int taskId = -1;
            while (true) {
                System.out.print("Entrez '0' pour retourner au menu \n");
                System.out.print("Entrez l'ID de la tâche pour laquelle vous voulez laisser un avis : ");
                if (scanner.hasNextInt()) {
                    taskId = scanner.nextInt();
                    scanner.nextLine(); // Consomme le retour à la ligne
                    if(taskId == 0){
                        return;
                    }

                    // Vérifier si un avis existe déjà pour cette tâche
                    String queryCheckReview = "SELECT COUNT(*) FROM reviews WHERE task_id = ?";
                    PreparedStatement stmtCheckReview = connection.prepareStatement(queryCheckReview);
                    stmtCheckReview.setInt(1, taskId);
                    ResultSet rsCheckReview = stmtCheckReview.executeQuery();
                    rsCheckReview.next();

                    if (rsCheckReview.getInt(1) > 0) {
                        System.out.println("Un avis existe déjà pour cette tâche. Vous ne pouvez pas en laisser un autre.");
                        stmtCheckReview.close();
                        rsCheckReview.close();
                        return;
                    }

                    stmtCheckReview.close();
                    rsCheckReview.close();

                    // Vérifier si l'ID existe dans la base
                    String queryValidate = "SELECT COUNT(*) FROM tasks WHERE id = ? AND created_by = ? AND status = 'FINALISEE'";
                    PreparedStatement stmtValidate = connection.prepareStatement(queryValidate);
                    stmtValidate.setInt(1, taskId);
                    stmtValidate.setInt(2, beneficiaryId);

                    ResultSet rsValidate = stmtValidate.executeQuery();
                    rsValidate.next();
                    if (rsValidate.getInt(1) > 0) {
                        stmtValidate.close();
                        break; // ID valide
                    } else {
                        System.out.println("ID invalide ou tâche non disponible pour un avis. Veuillez réessayer.");
                        stmtValidate.close();
                    }
                } else {
                    System.out.println("Entrée invalide. Veuillez entrer un entier.");
                    scanner.nextLine(); // Consomme l'entrée incorrecte
                }
            }

            System.out.print("Entrez votre avis : ");
            String avis = scanner.nextLine();
            if(avis.equals("0")){
                return;
            }

            // Insérer l'avis dans la table reviews
            String queryInsert = "INSERT INTO reviews (task_id, user_id, comment) VALUES (?, ?, ?)";
            PreparedStatement stmtInsert = connection.prepareStatement(queryInsert);
            stmtInsert.setInt(1, taskId);
            stmtInsert.setInt(2, beneficiaryId);
            stmtInsert.setString(3, avis);

            int rowsInserted = stmtInsert.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Avis ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout de l'avis.");
            }
            stmtInsert.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'avis : " + e.getMessage());
        }
    }


}
