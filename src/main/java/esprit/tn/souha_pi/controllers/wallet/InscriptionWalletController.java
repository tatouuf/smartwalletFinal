package esprit.tn.souha_pi.controllers.wallet;

import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.WalletService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import services.ServiceUser;
import tests.MainFxml;
import utils.Session;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class InscriptionWalletController {

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

    @FXML private Label pieceIdentiteNom, selfieNom, justificatifNom;

    @FXML private PasswordField pinField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> questionCombo;
    @FXML private TextField reponseField;
    @FXML private CheckBox twoFactorCheck;

    @FXML private RadioButton standardRadio;
    @FXML private RadioButton virtuelleRadio;
    @FXML private RadioButton physiqueRadio;
    @FXML private RadioButton premiumRadio;
    @FXML private RadioButton entrepriseRadio;

    @FXML private TextField nomCarteField;
    @FXML private RadioButton couleurNoir, couleurBlanc, couleurBleu, couleurRouge, couleurOr;
    @FXML private CheckBox sansContact, etranger, retrait, decouvert;
    @FXML private Slider plafondSlider;
    @FXML private Label plafondValue;

    @FXML private TextField montantField;
    @FXML private ComboBox<String> paiementCombo;
    @FXML private ComboBox<String> deviseCombo;
    @FXML private Label infoMinimumLabel;

    @FXML private Label recapNom, recapEmail, recapFormule, recapCarte, recapMontant;
    @FXML private CheckBox acceptCGU, acceptInfos;

    private int etapeCourante = 1;

    // Services
    private WalletService walletService = new WalletService();
    private ServiceUser userService = new ServiceUser();
    private int userId;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur d'inscription wallet...");

        // Récupérer l'ID de l'utilisateur connecté
        if (Session.getCurrentUser() != null) {
            userId = Session.getCurrentUser().getId();
            nomField.setText(Session.getCurrentUser().getNom() + " " + Session.getCurrentUser().getPrenom());
            emailField.setText(Session.getCurrentUser().getEmail());
            telephoneField.setText(Session.getCurrentUser().getTelephone());
        }

        // Initialisation des combobox
        nationaliteCombo.getItems().addAll("Tunisienne", "Française", "Algérienne", "Marocaine", "Autre");
        paysCombo.getItems().addAll("Tunisie", "France", "Algérie", "Maroc", "Autre");

        questionCombo.getItems().addAll(
                "Nom de votre premier animal ?",
                "Ville de naissance ?",
                "Nom de jeune fille de votre mère ?",
                "Nom de votre premier professeur ?"
        );

        paiementCombo.getItems().addAll("Carte bancaire", "Virement", "Espèces (agence)");
        deviseCombo.getItems().addAll("TND", "EUR", "USD");

        // Groupe de radios pour les formules
        ToggleGroup groupeFormules = new ToggleGroup();
        standardRadio.setToggleGroup(groupeFormules);
        virtuelleRadio.setToggleGroup(groupeFormules);
        physiqueRadio.setToggleGroup(groupeFormules);
        premiumRadio.setToggleGroup(groupeFormules);
        entrepriseRadio.setToggleGroup(groupeFormules);
        standardRadio.setSelected(true); // Sélection par défaut

        // Groupe de radios pour les couleurs
        ToggleGroup groupeCouleurs = new ToggleGroup();
        couleurNoir.setToggleGroup(groupeCouleurs);
        couleurBlanc.setToggleGroup(groupeCouleurs);
        couleurBleu.setToggleGroup(groupeCouleurs);
        couleurRouge.setToggleGroup(groupeCouleurs);
        couleurOr.setToggleGroup(groupeCouleurs);
        couleurBleu.setSelected(true); // Sélection par défaut

        // Slider listener
        plafondSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                plafondValue.setText(newVal.intValue() + " TND")
        );

        System.out.println("Initialisation terminée");
    }

    @FXML
    private void allerEtape2() {
        if (validerEtape1()) {
            changerEtape(2);
        }
    }

    @FXML
    private void allerEtape1() {
        changerEtape(1);
    }

    @FXML
    private void allerEtape3() {
        if (validerEtape2()) {
            changerEtape(3);
        }
    }

    @FXML
    private void allerEtape4() {
        if (validerEtape3()) {
            changerEtape(4);
        }
    }

    @FXML
    private void allerEtape5() {
        if (validerEtape4()) {
            changerEtape(5);
        }
    }

    @FXML
    private void allerEtape6() {
        if (validerEtape5()) {
            changerEtape(6);
        }
    }

    @FXML
    private void allerEtape7() {
        if (validerEtape6()) {
            mettreAJourRecap();
            changerEtape(7);
        }
    }

    private void changerEtape(int nouvelleEtape) {
        // Cacher toutes les étapes
        etape1.setVisible(false); etape1.setManaged(false);
        etape2.setVisible(false); etape2.setManaged(false);
        etape3.setVisible(false); etape3.setManaged(false);
        etape4.setVisible(false); etape4.setManaged(false);
        etape5.setVisible(false); etape5.setManaged(false);
        etape6.setVisible(false); etape6.setManaged(false);
        etape7.setVisible(false); etape7.setManaged(false);

        // Afficher la nouvelle étape
        switch(nouvelleEtape) {
            case 1:
                etape1.setVisible(true);
                etape1.setManaged(true);
                break;
            case 2:
                etape2.setVisible(true);
                etape2.setManaged(true);
                break;
            case 3:
                etape3.setVisible(true);
                etape3.setManaged(true);
                break;
            case 4:
                etape4.setVisible(true);
                etape4.setManaged(true);
                break;
            case 5:
                etape5.setVisible(true);
                etape5.setManaged(true);
                break;
            case 6:
                etape6.setVisible(true);
                etape6.setManaged(true);
                break;
            case 7:
                etape7.setVisible(true);
                etape7.setManaged(true);
                break;
        }

        etapeCourante = nouvelleEtape;
        etapeLabel.setText(nouvelleEtape + "/7");
        progressBar.setProgress((nouvelleEtape - 1) / 7.0);
    }

    private boolean validerEtape1() {
        if (nomField.getText().trim().isEmpty()) {
            erreurEtape1.setText("Veuillez saisir votre nom complet");
            return false;
        }
        if (dateNaissancePicker.getValue() == null) {
            erreurEtape1.setText("Veuillez saisir votre date de naissance");
            return false;
        }
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            erreurEtape1.setText("Email invalide");
            return false;
        }
        if (telephoneField.getText().trim().isEmpty()) {
            erreurEtape1.setText("Veuillez saisir votre téléphone");
            return false;
        }
        erreurEtape1.setText("");
        return true;
    }

    private boolean validerEtape2() {
        // Vérifier que les documents sont uploadés
        if (pieceIdentiteNom.getText().equals("Aucun fichier")) {
            showAlert("Erreur", "Veuillez ajouter votre pièce d'identité");
            return false;
        }
        if (selfieNom.getText().equals("Pas de photo")) {
            showAlert("Erreur", "Veuillez prendre un selfie");
            return false;
        }
        if (justificatifNom.getText().equals("Aucun fichier")) {
            showAlert("Erreur", "Veuillez ajouter votre justificatif de domicile");
            return false;
        }
        return true;
    }

    private boolean validerEtape3() {
        if (pinField.getText().trim().isEmpty() || pinField.getText().length() < 4) {
            showAlert("Erreur", "Code PIN invalide (minimum 4 chiffres)");
            return false;
        }
        if (passwordField.getText().trim().isEmpty() || passwordField.getText().length() < 8) {
            showAlert("Erreur", "Mot de passe invalide (minimum 8 caractères)");
            return false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas");
            return false;
        }
        if (questionCombo.getValue() == null || reponseField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez choisir une question secrète et y répondre");
            return false;
        }
        return true;
    }

    private boolean validerEtape4() {
        // Vérifier qu'une formule est sélectionnée
        return true; // Une radio est toujours sélectionnée par défaut
    }

    private boolean validerEtape5() {
        if (nomCarteField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir le nom à graver sur la carte");
            return false;
        }
        return true;
    }

    private boolean validerEtape6() {
        if (montantField.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir un montant");
            return false;
        }
        try {
            double montant = Double.parseDouble(montantField.getText());
            if (montant <= 0) {
                showAlert("Erreur", "Le montant doit être positif");
                return false;
            }

            // Vérifier le montant minimum selon le type de wallet
            String typeWallet = getTypeWallet();
            double montantMinimum = getMontantMinimum(typeWallet);

            if (montant < montantMinimum) {
                showAlert("Erreur", "Le montant minimum pour " + typeWallet + " est de " + montantMinimum + " DT");
                return false;
            }

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Montant invalide");
            return false;
        }
        if (paiementCombo.getValue() == null) {
            showAlert("Erreur", "Veuillez choisir un mode de paiement");
            return false;
        }
        return true;
    }

    private void mettreAJourRecap() {
        recapNom.setText("Nom: " + nomField.getText());
        recapEmail.setText("Email: " + emailField.getText());

        String formule = getTypeWallet();
        recapFormule.setText("Formule: " + formule);

        recapMontant.setText("Montant initial: " + montantField.getText() + " TND");
    }

    private String getTypeWallet() {
        if (standardRadio.isSelected()) return "Standard";
        if (virtuelleRadio.isSelected()) return "Carte Virtuelle";
        if (physiqueRadio.isSelected()) return "Carte Physique";
        if (premiumRadio.isSelected()) return "Premium";
        if (entrepriseRadio.isSelected()) return "Entreprise";
        return "Standard";
    }

    private double getMontantMinimum(String typeWallet) {
        switch(typeWallet) {
            case "Standard": return 10;
            case "Carte Virtuelle": return 20;
            case "Carte Physique": return 50;
            case "Premium": return 100;
            case "Entreprise": return 200;
            default: return 10;
        }
    }

    @FXML
    private void ajouterPieceIdentite() {
        pieceIdentiteNom.setText("piece_identite.jpg");
        showInfo("Document ajouté", "Pièce d'identité ajoutée avec succès");
    }

    @FXML
    private void prendreSelfie() {
        selfieNom.setText("selfie.jpg");
        showInfo("Selfie pris", "Selfie enregistré avec succès");
    }

    @FXML
    private void ajouterJustificatif() {
        justificatifNom.setText("justificatif.pdf");
        showInfo("Document ajouté", "Justificatif de domicile ajouté avec succès");
    }

    @FXML
    private void validerInscription() {
        if (!acceptCGU.isSelected() || !acceptInfos.isSelected()) {
            showAlert("Validation", "Veuillez accepter les conditions pour continuer");
            return;
        }

        try {
            // Vérifier que l'utilisateur est connecté
            if (userId == 0) {
                showAlert("Erreur", "Vous devez être connecté pour créer un wallet");
                return;
            }

            // Récupérer les données du formulaire
            double montantInitial = Double.parseDouble(montantField.getText());
            String typeWallet = getTypeWallet();

            // Vérifier si l'utilisateur a déjà un wallet
            Wallet existingWallet = walletService.getByUserId(userId);
            if (existingWallet != null) {
                String status = existingWallet.getStatus();

                if ("PENDING".equals(status)) {
                    showInfo("Demande en cours",
                            "Vous avez déjà une demande de wallet en attente d'approbation.\n" +
                                    "Veuillez patienter jusqu'à ce qu'un administrateur la traite.\n" +
                                    "Vous recevrez une notification dès que votre wallet sera activé.");
                    retourAccueil();
                    return;

                } else if ("ACTIF".equals(status)) {
                    showInfo("Wallet existant",
                            "Vous possédez déjà un wallet actif.\n" +
                                    "Solde actuel: " + existingWallet.getBalance() + " DT\n" +
                                    "Type: " + existingWallet.getType() + "\n\n" +
                                    "Vous pouvez le consulter dans votre espace personnel.");
                    retourAccueil();
                    return;

                } else if ("REJECTED".equals(status)) {
                    // Si wallet rejeté, on permet une nouvelle demande
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Nouvelle demande");
                    confirm.setHeaderText("Votre précédente demande a été rejetée");
                    confirm.setContentText("Souhaitez-vous soumettre une nouvelle demande ?");

                    if (confirm.showAndWait().get() != ButtonType.OK) {
                        retourAccueil();
                        return;
                    }
                }
            }

            // Créer le wallet avec statut PENDING
            walletService.creerWallet(userId, montantInitial, typeWallet);

            // Message de confirmation détaillé
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("✅ Demande envoyée avec succès");
            success.setHeaderText("Votre demande de wallet a été enregistrée");

            // Contenu du message avec mise en forme
            String message = String.format(
                    "┌─────────────────────────────────────┐\n" +
                            "│  RÉCAPITULATIF DE VOTRE DEMANDE     │\n" +
                            "├─────────────────────────────────────┤\n" +
                            "│  • Type de wallet : %-20s │\n" +
                            "│  • Montant initial : %-20.2f DT │\n" +
                            "│  • Statut : EN ATTENTE D'APPROBATION │\n" +
                            "├─────────────────────────────────────┤\n" +
                            "│  PROCHAINES ÉTAPES :                 │\n" +
                            "│  1. Un administrateur examinera      │\n" +
                            "│     votre demande                     │\n" +
                            "│  2. Vous recevrez une notification    │\n" +
                            "│     dès validation                    │\n" +
                            "│  3. Vous pourrez ensuite utiliser     │\n" +
                            "│     votre wallet                      │\n" +
                            "├─────────────────────────────────────┤\n" +
                            "│  ⌛ Délai moyen de traitement :      │\n" +
                            "│     24-48 heures                     │\n" +
                            "└─────────────────────────────────────┘",
                    typeWallet, montantInitial
            );

            // Ajouter des conseils selon le type de wallet
            if ("Carte Physique".equals(typeWallet) || "Premium".equals(typeWallet)) {
                message += "\n\n📦 Pour les cartes physiques :\n" +
                        "• Vous serez notifié dès l'activation\n" +
                        "• La carte sera livrée sous 5-7 jours ouvrés";
            }

            success.setContentText(message);
            success.showAndWait();

            // Retourner à l'accueil
            retourAccueil();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Montant invalide");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la création du wallet: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retourAccueil() {
        MainFxml.getInstance().showWalletHome();
    }
}