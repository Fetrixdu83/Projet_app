import java.util.ArrayList;
import java.util.List;

public class Benevole extends Utilisateur {
    String Service;
    private List<Service> servicesProposes;

    public Benevole(String nom, String email) {
        super(nom, email, "benevole");
        this.servicesProposes = new ArrayList<>();
    }

    // Méthode pour proposer une aide spontanée
    public void proposerAide(Service service) {
        servicesProposes.add(service);
    }

    // Getters et Setters
}