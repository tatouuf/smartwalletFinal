package entities;
import java.util.List;

public class DashboardAdmin {

    private int totalUsers;
    private int totalAmities;
    private int totalReclamations;
    private int totalTransactions;
    private double benefice;

    private List<User> listeUsers;
    private List<Reclamation> listeReclamations;
    private List<Transaction> listeTransactions;


    public DashboardAdmin() {}


    public DashboardAdmin(int totalUsers, int totalAmities, int totalReclamations, int totalTransactions,
                          double benefice, List<User> listeUsers, List<Amitie> listeAmities,
                          List<Reclamation> listeReclamations, List<Transaction> listeTransactions) {
        this.totalUsers = totalUsers;
        this.totalAmities = totalAmities;
        this.totalReclamations = totalReclamations;
        this.totalTransactions = totalTransactions;
        this.benefice = benefice;
        this.listeUsers = listeUsers;
        this.listeReclamations = listeReclamations;
        this.listeTransactions = listeTransactions;
    }

    // ✅ Getters et Setters
    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getTotalAmities() { return totalAmities; }
    public void setTotalAmities(int totalAmities) { this.totalAmities = totalAmities; }

    public int getTotalReclamations() { return totalReclamations; }
    public void setTotalReclamations(int totalReclamations) { this.totalReclamations = totalReclamations; }

    public int getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(int totalTransactions) { this.totalTransactions = totalTransactions; }

    public double getBenefice() { return benefice; }
    public void setBenefice(double benefice) { this.benefice = benefice; }

    public List<User> getListeUsers() { return listeUsers; }
    public void setListeUsers(List<User> listeUsers) { this.listeUsers = listeUsers; }


    public List<Reclamation> getListeReclamations() { return listeReclamations; }
    public void setListeReclamations(List<Reclamation> listeReclamations) { this.listeReclamations = listeReclamations; }

    public List<Transaction> getListeTransactions() { return listeTransactions; }
    public void setListeTransactions(List<Transaction> listeTransactions) { this.listeTransactions = listeTransactions; }

    @Override
    public String toString() {
        return "DashboardAdmin{" +
                "totalUsers=" + totalUsers +
                ", totalAmities=" + totalAmities +
                ", totalReclamations=" + totalReclamations +
                ", totalTransactions=" + totalTransactions +
                ", benefice=" + benefice +
                ", listeUsers=" + listeUsers +
                ", listeReclamations=" + listeReclamations +
                ", listeTransactions=" + listeTransactions +
                '}';
    }
}
