import java.util.ArrayList;
import java.util.List;

// Classe représentant un Patient (ou utilisateur ayant besoin d'aide)
public class Patient extends Utilisateur {
    private List<Demande> demandes;

    public Patient(String nom, String email) {
        super(nom, email, "patient");
        this.demandes = new ArrayList<>();
    }

    // Méthode pour créer une demande d'aide
    public void creerDemande(Demande demande) {
        demandes.add(demande);
    }

    // Getters et Setters
}
