package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.services.BankCardService;
import esprit.tn.souha_pi.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
<<<<<<< HEAD
import utils.Session;

=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import java.util.UUID;

public class CardAddController {

    @FXML private TextField holderField;
    @FXML private TextField numberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;
    @FXML private ChoiceBox<String> typeChoice;

    private BankCardService cardService = new BankCardService();

    @FXML
    public void initialize() {
        typeChoice.getItems().addAll("Visa", "Mastercard", "Visa Electron", "Maestro");
        typeChoice.setValue("Visa");
    }

    @FXML
    private void save() {
        String holder = holderField.getText().trim();
        String number = numberField.getText().trim().replace(" ", "");
        String expiry = expiryField.getText().trim();
        String cvv = cvvField.getText().trim();
        String type = typeChoice.getValue();

        // Validations
        if (holder.isEmpty() || number.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            DialogUtil.error("Erreur", "Tous les champs sont obligatoires");
            return;
        }

        if (number.length() != 16 || !number.matches("\\d+")) {
            DialogUtil.error("Erreur", "Le numéro de carte doit contenir 16 chiffres");
            return;
        }

        if (cvv.length() != 3 || !cvv.matches("\\d+")) {
            DialogUtil.error("Erreur", "Le CVV doit contenir 3 chiffres");
            return;
        }

        // Générer un RIB unique
        String rib = genererRIB();
<<<<<<< HEAD
        entities.User currentUser = Session.getCurrentUser();

        // Récupérer l'utilisateur connecté
        entities.User  currentUserId =Session.getCurrentUser();

        BankCard card = new BankCard(
                currentUserId.getId(),
=======

        // Récupérer l'utilisateur connecté
        int currentUserId = WalletLayoutController.instance.getCurrentUser().getId();

        BankCard card = new BankCard(
                currentUserId,
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
                holder,
                number,
                expiry,
                cvv,
                type,
                rib
        );

        try {
            cardService.add(card);
            DialogUtil.success("Succès", "Carte ajoutée avec succès\nRIB: " + rib);
            annuler();
        } catch (RuntimeException e) {
            DialogUtil.error("Erreur", e.getMessage());
        }
    }

    private String genererRIB() {
        // Format RIB Tunisien: TN59 + 20 chiffres
        String timestamp = String.valueOf(System.currentTimeMillis());
        String chiffres = timestamp.replaceAll("[^0-9]", "");
        while (chiffres.length() < 20) {
            chiffres = "0" + chiffres;
        }
        if (chiffres.length() > 20) {
            chiffres = chiffres.substring(0, 20);
        }
        return "TN59" + chiffres;
    }

    @FXML
    private void annuler() {
        WalletLayoutController.instance.goCards();
    }
}