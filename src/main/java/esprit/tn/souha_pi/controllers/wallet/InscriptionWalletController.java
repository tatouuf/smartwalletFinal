package esprit.tn.souha_pi.controllers.wallet;

<<<<<<< HEAD
import entities.User;
import esprit.tn.souha_pi.controllers.WalletLayoutController;
=======
import esprit.tn.souha_pi.controllers.WalletLayoutController;
import esprit.tn.souha_pi.entities.User;
import esprit.tn.souha_pi.services.UserService;
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
<<<<<<< HEAD
import services.ServiceUser;
=======
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InscriptionWalletController implements Initializable {

    // ======================= COMPOSANTS EXISTANTS =======================
    @FXML private Label etapeLabel;
    @FXML private ProgressBar progressBar;
    @FXML private VBox etape1, etape2, etape3, etape4, etape5, etape6, etape7;
    @FXML private TextField nomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private ComboBox<String> nationaliteCombo;
    @FXML private ComboBox<String> paysCombo;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private PasswordField adminCodeField;
    @FXML private Label erreurEtape1;
    @FXML private Label pieceIdentiteNom;
    @FXML private Label selfieNom;
    @FXML private Label justificatifNom;
    @FXML private PasswordField pinField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> questionCombo;
    @FXML private TextField reponseField;
    @FXML private CheckBox twoFactorCheck;
    @FXML private ToggleGroup typeCompteGroup;
    @FXML private TextField nomCarteField;
    @FXML private ToggleGroup couleurGroupe;
    @FXML private Slider plafondSlider;
    @FXML private Label plafondValue;
    @FXML private TextField montantField;
    @FXML private ComboBox<String> paiementCombo;
    @FXML private ComboBox<String> deviseCombo;
    @FXML private Label infoMinimumLabel;
    @FXML private Label recapNom;
    @FXML private Label recapEmail;
    @FXML private Label recapFormule;
    @FXML private Label recapCarte;
    @FXML private Label recapMontant;
    @FXML private CheckBox acceptCGU;
    @FXML private CheckBox acceptInfos;

    // ======================= NOUVEAUX CHAMPS POUR LES INFOS UTILISATEUR =======================
    private String userNom;
    private String userPrenom;
    private String userEmail;
    private String userTelephone;
    private String userPassword;
    private User currentUser;

    // ======================= VARIABLES =======================
    private int etapeActuelle = 1;
    private final int TOTAL_ETAPES = 7;
    private File pieceIdentiteFile;
    private File selfieFile;
    private File justificatifFile;

    // ======================= SERVICES =======================
<<<<<<< HEAD
    private ServiceUser userService = new ServiceUser();
=======
    private UserService userService = new UserService();
>>>>>>> 25810eff966ac1c5ab947b24304a065e2ce44cca

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Récupérer l'utilisateur connecté
        currentUser = WalletLayoutController.instance.getCurrentUser();

        if (currentUser != null) {
            // Pré-remplir les champs avec les données de l'utilisateur
            preRemplirChamps();
        }

        initialiserComboBox();
        configurerSlider();
        afficherEtape(1);
    }

    /**
     * Pré-remplir les champs avec les données de l'utilisateur connecté
     */
    private void preRemplirChamps() {
        // Séparer le nom complet en nom et prénom
        String fullname = currentUser.getFullname();
        String[] parts = fullname.split(" ", 2);
        String prenom = parts.length > 1 ? parts[1] : "";
        String nom = parts[0];

        // Remplir les champs de l'étape 1
        nomField.setText(nom);
        // Note: le champ prénom n'existe pas dans votre FXML, vous devrez peut-être l'ajouter
        emailField.setText(currentUser.getEmail());
        telephoneField.setText(currentUser.getTelephone());

        // Stocker les valeurs pour utilisation ultérieure
        userNom = nom;
        userPrenom = prenom;
        userEmail = currentUser.getEmail();
        userTelephone = currentUser.getTelephone();
        userPassword = currentUser.getPassword();
    }

    private void initialiserComboBox() {
        nationaliteCombo.getItems().addAll("Tunisienne", "Française", "Marocaine", "Algérienne", "Autre");
        paysCombo.getItems().addAll("Tunisie", "France", "Maroc", "Algérie", "Autre");
        questionCombo.getItems().addAll(
                "Nom de votre premier animal ?",
                "Ville de naissance ?",
                "Nom de jeune fille de votre mère ?",
                "Marque de votre première voiture ?"
        );
        paiementCombo.getItems().addAll("Carte bancaire", "Virement", "Espèces");
        deviseCombo.getItems().addAll("TND (Dinar Tunisien)", "EUR (Euro)", "USD (Dollar)");
        deviseCombo.setValue("TND (Dinar Tunisien)");
    }

    private void configurerSlider() {
        plafondSlider.valueProperty().addListener((obs, old, newVal) -> {
            plafondValue.setText(String.format("%.0f TND", newVal));
        });
    }

    private void afficherEtape(int numero) {
        // Cacher toutes les étapes
        etape1.setVisible(false); etape1.setManaged(false);
        etape2.setVisible(false); etape2.setManaged(false);
        etape3.setVisible(false); etape3.setManaged(false);
        etape4.setVisible(false); etape4.setManaged(false);
        etape5.setVisible(false); etape5.setManaged(false);
        etape6.setVisible(false); etape6.setManaged(false);
        etape7.setVisible(false); etape7.setManaged(false);

        switch(numero) {
            case 1: etape1.setVisible(true); etape1.setManaged(true); break;
            case 2: etape2.setVisible(true); etape2.setManaged(true); break;
            case 3: etape3.setVisible(true); etape3.setManaged(true); break;
            case 4: etape4.setVisible(true); etape4.setManaged(true); break;
            case 5: etape5.setVisible(true); etape5.setManaged(true); break;
            case 6: etape6.setVisible(true); etape6.setManaged(true); break;
            case 7:
                etape7.setVisible(true); etape7.setManaged(true);
                mettreAJourRecapitulatif();
                break;
        }

        etapeActuelle = numero;
        etapeLabel.setText(numero + "/" + TOTAL_ETAPES);
        progressBar.setProgress((double) numero / TOTAL_ETAPES);
    }

    @FXML private void allerEtape1() { afficherEtape(1); }
    @FXML private void allerEtape2() { if (validerEtape1()) afficherEtape(2); }
    @FXML private void allerEtape3() { if (validerEtape2()) afficherEtape(3); }
    @FXML private void allerEtape4() { if (validerEtape3()) afficherEtape(4); }
    @FXML private void allerEtape5() { if (validerEtape4()) afficherEtape(5); }
    @FXML private void allerEtape6() { if (validerEtape5()) afficherEtape(6); }
    @FXML private void allerEtape7() { if (validerEtape6()) afficherEtape(7); }

    private boolean validerEtape1() {
        if (nomField.getText().trim().isEmpty()) {
            erreurEtape1.setText("❌ Veuillez saisir votre nom complet");
            return false;
        }
        if (dateNaissancePicker.getValue() == null) {
            erreurEtape1.setText("❌ Veuillez saisir votre date de naissance");
            return false;
        }
        if (dateNaissancePicker.getValue().isAfter(LocalDate.now().minusYears(18))) {
            erreurEtape1.setText("❌ Vous devez avoir au moins 18 ans");
            return false;
        }
        if (nationaliteCombo.getValue() == null) {
            erreurEtape1.setText("❌ Veuillez sélectionner votre nationalité");
            return false;
        }
        if (paysCombo.getValue() == null) {
            erreurEtape1.setText("❌ Veuillez sélectionner votre pays de résidence");
            return false;
        }
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            erreurEtape1.setText("❌ Email invalide");
            return false;
        }
        if (telephoneField.getText().trim().isEmpty()) {
            erreurEtape1.setText("❌ Veuillez saisir votre numéro de téléphone");
            return false;
        }

        // Vérifier si l'email correspond à l'utilisateur connecté
        if (currentUser != null && !emailField.getText().trim().equals(currentUser.getEmail())) {
            erreurEtape1.setText("❌ L'email ne correspond pas à votre compte");
            return false;
        }

        // Vérifier si le téléphone correspond à l'utilisateur connecté
        if (currentUser != null && !telephoneField.getText().trim().equals(currentUser.getTelephone())) {
            erreurEtape1.setText("❌ Le téléphone ne correspond pas à votre compte");
            return false;
        }

        return true;
    }

    private boolean validerEtape2() {
        if (pieceIdentiteFile == null) {
            showAlert("Document manquant", "Veuillez ajouter votre pièce d'identité");
            return false;
        }
        if (selfieFile == null) {
            showAlert("Document manquant", "Veuillez prendre un selfie");
            return false;
        }
        if (justificatifFile == null) {
            showAlert("Document manquant", "Veuillez ajouter votre justificatif de domicile");
            return false;
        }
        return true;
    }

    private boolean validerEtape3() {
        if (pinField.getText().trim().length() < 4) {
            showAlert("Code PIN invalide", "Le code PIN doit contenir au moins 4 chiffres");
            return false;
        }
        if (passwordField.getText().trim().length() < 8) {
            showAlert("Mot de passe invalide", "Le mot de passe doit contenir au moins 8 caractères");
            return false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas");
            return false;
        }

        // Vérifier si le mot de passe correspond à l'utilisateur connecté
        if (currentUser != null && !passwordField.getText().equals(currentUser.getPassword())) {
            showAlert("Erreur", "Le mot de passe ne correspond pas à votre compte");
            return false;
        }

        if (questionCombo.getValue() == null || reponseField.getText().trim().isEmpty()) {
            showAlert("Question secrète", "Veuillez choisir une question et y répondre");
            return false;
        }
        return true;
    }

    private boolean validerEtape4() {
        if (typeCompteGroup.getSelectedToggle() == null) {
            showAlert("Type de compte", "Veuillez choisir un type de compte");
            return false;
        }
        return true;
    }

    private boolean validerEtape5() {
        if (nomCarteField.getText().trim().isEmpty()) {
            showAlert("Nom sur la carte", "Veuillez saisir le nom à graver sur la carte");
            return false;
        }
        if (couleurGroupe.getSelectedToggle() == null) {
            showAlert("Couleur", "Veuillez choisir une couleur pour la carte");
            return false;
        }
        return true;
    }

    private boolean validerEtape6() {
        if (montantField.getText().trim().isEmpty()) {
            showAlert("Montant", "Veuillez saisir un montant");
            return false;
        }
        try {
            double montant = Double.parseDouble(montantField.getText());
            RadioButton selected = (RadioButton) typeCompteGroup.getSelectedToggle();
            String typeCompte = selected.getText();

            if (typeCompte.contains("STANDARD") && montant < 10) {
                showAlert("Montant insuffisant", "Le montant minimum pour un wallet standard est de 10 TND");
                return false;
            }
            if (typeCompte.contains("VIRTUELLE") && montant < 20) {
                showAlert("Montant insuffisant", "Le montant minimum pour une carte virtuelle est de 20 TND");
                return false;
            }
            if (typeCompte.contains("PHYSIQUE") && montant < 50) {
                showAlert("Montant insuffisant", "Le montant minimum pour une carte physique est de 50 TND");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Montant invalide", "Veuillez saisir un montant valide");
            return false;
        }

        if (paiementCombo.getValue() == null) {
            showAlert("Mode de paiement", "Veuillez choisir un mode de paiement");
            return false;
        }
        return true;
    }

    @FXML private void ajouterPieceIdentite() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une pièce d'identité");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            pieceIdentiteFile = file;
            pieceIdentiteNom.setText(file.getName());
        }
    }

    @FXML private void prendreSelfie() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un selfie");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            selfieFile = file;
            selfieNom.setText(file.getName());
        }
    }

    @FXML private void ajouterJustificatif() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un justificatif de domicile");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            justificatifFile = file;
            justificatifNom.setText(file.getName());
        }
    }

    private void mettreAJourRecapitulatif() {
        recapNom.setText(nomField.getText());
        recapEmail.setText(emailField.getText());

        RadioButton selected = (RadioButton) typeCompteGroup.getSelectedToggle();
        if (selected != null) {
            recapFormule.setText(selected.getText());
        }

        String nomCarte = nomCarteField.getText().toUpperCase();
        RadioButton couleur = (RadioButton) couleurGroupe.getSelectedToggle();
        String couleurTexte = couleur != null ? couleur.getText() : "Noir";
        recapCarte.setText(nomCarte + " - " + couleurTexte + " - " + plafondValue.getText());

        String montant = montantField.getText();
        String paiement = paiementCombo.getValue() != null ? paiementCombo.getValue() : "Non spécifié";
        recapMontant.setText(montant + " TND par " + paiement);
    }

    // ======================= VALIDATION FINALE =======================
    @FXML
    private void validerInscription() {
        if (!acceptCGU.isSelected() || !acceptInfos.isSelected()) {
            showAlert("Acceptation requise", "Veuillez accepter les conditions d'utilisation");
            return;
        }

        try {
            // Récupérer les données du formulaire
            String nomComplet = nomField.getText().trim();
            String[] parts = nomComplet.split(" ", 2);
            String prenom = parts.length > 1 ? parts[1] : "";
            String nom = parts[0];

            String email = emailField.getText().trim();
            String telephone = telephoneField.getText().trim();
            String password = passwordField.getText();

            // Vérifier que l'utilisateur est bien connecté
            if (currentUser == null) {
                showAlert("Erreur", "Aucun utilisateur connecté");
                return;
            }

            // Vérifier que les données correspondent à l'utilisateur connecté
            if (!email.equals(currentUser.getEmail()) || !telephone.equals(currentUser.getTelephone())) {
                showAlert("Erreur", "Les informations ne correspondent pas à votre compte");
                return;
            }

            // Récupérer le montant du dépôt
            double montantDepot = Double.parseDouble(montantField.getText());

            // Créer le wallet
            User updatedUser = userService.creerWallet(currentUser.getId(), montantDepot);

            // Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wallet créé");
            alert.setHeaderText("✅ Félicitations !");
            alert.setContentText(
                    "Votre wallet a été créé avec succès.\n\n" +
                            "💰 Solde initial: " + montantDepot + " TND\n" +
                            "📧 Un email de confirmation a été envoyé à " + email
            );
            alert.showAndWait();

            // Rediriger vers le dashboard
            goToDashboard();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Montant invalide: " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
            e.printStackTrace();
        }
    }

    private void goToDashboard() {
        WalletLayoutController.instance.goDashboard();
    }

    private void reinitialiserFormulaire() {
        nomField.clear();
        dateNaissancePicker.setValue(null);
        nationaliteCombo.setValue(null);
        paysCombo.setValue(null);
        emailField.clear();
        telephoneField.clear();
        adminCodeField.clear();

        pieceIdentiteFile = null;
        selfieFile = null;
        justificatifFile = null;
        pieceIdentiteNom.setText("Aucun fichier");
        selfieNom.setText("Pas de photo");
        justificatifNom.setText("Aucun fichier");

        pinField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        questionCombo.setValue(null);
        reponseField.clear();
        twoFactorCheck.setSelected(false);

        typeCompteGroup.selectToggle(null);

        nomCarteField.clear();
        if (couleurGroupe.getToggles() != null && !couleurGroupe.getToggles().isEmpty()) {
            couleurGroupe.selectToggle(couleurGroupe.getToggles().get(0));
        }
        plafondSlider.setValue(500);

        montantField.clear();
        paiementCombo.setValue(null);
        deviseCombo.setValue("TND (Dinar Tunisien)");

        acceptCGU.setSelected(false);
        acceptInfos.setSelected(false);
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}