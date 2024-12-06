import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class TaskManagementApp {
    public static void main(String[] args) {
        final String DB_URL = "jdbc:mysql://srv-bdens.insa-toulouse.fr:3306/projet_gei_018";
        final String DB_USER = "projet_gei_018";
        final String DB_PASSWORD = "ahLah8ie";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){

            System.out.println("Connexion à la base de données réussie.");

            while (true) {
                System.out.println("\n=== Bienvenue ===");
                System.out.println("1. Se connecter");
                System.out.println("2. Créer un compte");
                System.out.println("3. Quitter");
                System.out.print("Choisissez une option : ");
                    Scanner scanner = new Scanner(System.in);
                    String choix = scanner.nextLine();
                    if (choix.equals("1")) {
                        int userId = login(connection, scanner);
                        if (userId != -1) {
                            String role = getUserRole(connection, userId);
                            switch (role) {
                                case "BENEVOLE":
                                    Volunteer.showVolunteerMenu(connection, scanner, userId);
                                    break;
                                case "MODERATEUR":
                                    Moderator.showModeratorMenu(connection, scanner, userId);
                                    break;
                                case "BENEFICIAIRE":
                                    Beneficiary.showBeneficiaryMenu(connection, scanner, userId);
                                    break;
                                default:
                                    System.out.println("Rôle non reconnu.");
                            }
                        }
                    } else if (choix.equals("2")) {
                        createAccount(connection, scanner);
                    } else if (choix.equals("3")) {
                        System.out.println("Au revoir !");
                        break;
                    } else {
                        System.out.println("Option invalide. Veuillez réessayer.");
                    }
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
    }
}

    public static int login(Connection connection, Scanner scanner) {
        try {
            System.out.print("Entrez votre nom d'utilisateur : ");
            String username = scanner.nextLine();

            System.out.print("Entrez votre mot de passe : ");
            String password = scanner.nextLine();

            String query = "SELECT id, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String role = rs.getString("role");
                System.out.println("Connexion réussie ! Bienvenue, " + username + ". Votre rôle est : " + role);
                rs.close();
                stmt.close();
                return userId;
            } else {
                System.out.println("Nom d'utilisateur ou mot de passe incorrect.");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion : " + e.getMessage());
        }
        return -1;
    }

    public static String getUserRole(Connection connection, int userId) {
        try {
            String query = "SELECT role FROM users WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                rs.close();
                stmt.close();
                return role;
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du rôle : " + e.getMessage());
        }
        return null;
    }

    public static void createAccount(Connection connection, Scanner scanner) {
        try {
            System.out.println("Entrez '0' à tout moment pour retourner au menu principal.");

            System.out.print("Entrez votre nom d'utilisateur : ");
            String username = scanner.nextLine();
            if (username.equals("0")) {
                System.out.println("Retour au menu principal");
                return;
            }


            System.out.print("Choisissez un mot de passe : ");
            String password = scanner.nextLine();
            if (password.equals("0")) {
                System.out.println("Retour au menu principal");
                return;
            }

            System.out.print("Quel est votre rôle ? (Benevole, Moderateur, Beneficiaire) : ");
            String role = scanner.nextLine().toUpperCase();
            if (role.equals("0")) {
                System.out.println("Retour au menu principal");
                return;
            }

            if (!role.equals("BENEVOLE") && !role.equals("MODERATEUR") && !role.equals("BENEFICIAIRE")) {
                System.out.println("Rôle invalide. Veuillez recommencer.");
                return;
            }

            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Compte créé avec succès !");
            } else {
                System.out.println("Échec de la création du compte.");
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création du compte : " + e.getMessage());
        }
    }










}
