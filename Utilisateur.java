import java.util.ArrayList;
import java.util.List;

// Classe représentant un Utilisateur général (peut être un bénévole ou un patient)
public class Utilisateur {
    private String nom;
    private String email;
    private String role; // "patient" ou "benevole"

    public Utilisateur(String nom, String email, String role) {
        this.nom = nom;
        this.email = email;
        this.role = role;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
