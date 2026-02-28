package controller.PaymentAssurance;
import entities.bankcardassur.BankCardAssur;
import services.paymentservice.PaymentService;

public class PaymentAssurance {

    private PaymentService paymentService;

    public PaymentAssurance() {
        this.paymentService = new PaymentService();
    }

    // Méthode pour lancer le paiement d'une assurance
    public void payInsurance(int userId, double amount) {
        BankCardAssur card = paymentService.getBankCardByUserId(userId);
        boolean success = paymentService.payWithPayPal(card, amount);
        if (success) {
            System.out.println("Assurance payée avec succès !");
        } else {
            System.out.println("Le paiement a échoué.");
        }
    }

    // Exemple d'utilisation
    public static void main(String[] args) {
        PaymentAssurance controller = new PaymentAssurance();
        controller.payInsurance(1, 250.0); // Utilisateur avec user_id = 1
    }
}