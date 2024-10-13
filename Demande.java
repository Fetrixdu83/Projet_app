// Classe représentant une Demande d'Aide
public class Demande{
    private String description;
    private Patient patient;
    private Benevole benevole;
    private int status;

    public Demande(String description, Patient patient) {
        this.description = description;
        this.patient = patient;
        this.benevole = null; // Initialement, aucun bénévole n'a répondu
    }

    // Méthode pour qu'un bénévole accepte la demande
    public void accepterDemande(Benevole benevole) {
        this.benevole = benevole;
    }

    


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Benevole getBenevole() {
        return benevole;
    }

    public void setBenevole(Benevole benevole) {
        this.benevole = benevole;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    

}