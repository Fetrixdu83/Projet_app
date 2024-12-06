class Task {
    enum Status { LIBRE, ACCEPTEE, FINALISEE, EN_ATTENTE_VALIDATION }
    private String description;
    private Status status;
    private String assignedTo;

    public Task(String description) {
        this.description = description;
        this.status = Status.LIBRE;
        this.assignedTo = null;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void assignTo(String user) {
        this.assignedTo = user;
        this.status = Status.ACCEPTEE;
    }

    @Override
    public String toString() {
        return String.format("Tâche: %s | Statut: %s | Assigné à: %s",
                description, status, assignedTo != null ? assignedTo : "Personne");
    }
}

class User {
    enum Role { BENEVOLE, MODERATEUR, BENEFICIAIRE }
    private String username;
    private Role role;

    public User(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return String.format("Utilisateur: %s | Rôle: %s", username, role);
    }
}

