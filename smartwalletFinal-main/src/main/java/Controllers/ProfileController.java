package Controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceUser;
import utils.PasswordUtils;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileController {

    private static final Logger LOGGER = Logger.getLogger(ProfileController.class.getName());
    private static final String PHOTO_DIR = "src/main/resources/images/profile/";
    private static final double AVATAR_RADIUS = 45.0;   // half of 90px

    // ── FXML ────────────────────────────────────────────────────────────────

    // Avatar
    @FXML private javafx.scene.layout.StackPane avatarPane;
    @FXML private ImageView profileImageView;
    @FXML private Label     initialsLabel;
    @FXML private Button    changePhotoBtn;

    // Info tab
    @FXML private TextField  nomField;
    @FXML private TextField  prenomField;
    @FXML private TextField  emailField;
    @FXML private TextField  telephoneField;
    @FXML private ComboBox<String> nationaliteCombo;
    @FXML private ComboBox<String> paysResidenceCombo;
    @FXML private Label      roleLabel;
    @FXML private Label      statusLabel;
    @FXML private Label      memberSinceLabel;
    @FXML private Button     saveInfoBtn;
    @FXML private Label      infoMessageLabel;

    // Password tab
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         pwdMessageLabel;

    // ── INTERNALS ────────────────────────────────────────────────────────────

    private final ServiceUser userService = new ServiceUser();
    private User currentUser;

    // ── INIT ─────────────────────────────────────────────────────────────────

    @FXML
    private void initialize() {
        // Apply circular clip to the ImageView in code
        // (FXML clip only works on containers, not ImageView sizing)
        Circle clip = new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS);
        profileImageView.setClip(clip);

        populateCountryLists();

        currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            loadUserData();
        }
    }

    private void loadUserData() {
        nomField.setText(nvl(currentUser.getNom()));
        prenomField.setText(nvl(currentUser.getPrenom()));
        emailField.setText(nvl(currentUser.getEmail()));
        telephoneField.setText(nvl(currentUser.getTelephone()));
        roleLabel.setText(currentUser.getRole() != null ? currentUser.getRole().name() : "—");
        statusLabel.setText(nvl(currentUser.getStatus()));

        if (currentUser.getDate_creation() != null) {
            memberSinceLabel.setText(currentUser.getDate_creation().toLocalDate().toString());
        }

        String nat  = currentUser.getNationalite();
        String pays = currentUser.getPaysResidence();
        if (nat  != null && !nat.isEmpty())  nationaliteCombo.setValue(nat);
        if (pays != null && !pays.isEmpty()) paysResidenceCombo.setValue(pays);

        loadAvatar();
    }

    // ── AVATAR ───────────────────────────────────────────────────────────────

    private void loadAvatar() {
        String imageName = currentUser.getImageName();
        if (imageName != null && !imageName.isEmpty()) {
            try {
                File imgFile = new File(PHOTO_DIR + imageName);
                if (imgFile.exists()) {
                    Image img = new Image(imgFile.toURI().toString(),
                            90, 90, false, true);
                    profileImageView.setImage(img);
                    profileImageView.setVisible(true);
                    initialsLabel.setVisible(false);
                    // reapply clip (image swap can lose it)
                    Circle clip = new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS);
                    profileImageView.setClip(clip);
                    return;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load profile image", e);
            }
        }
        // Fallback — show initials, hide photo
        initialsLabel.setText(currentUser.getInitials());
        initialsLabel.setVisible(true);
        profileImageView.setVisible(false);
    }

    // ── COUNTRY LISTS ────────────────────────────────────────────────────────

    private void populateCountryLists() {
        String[] countries = {
                "", "Afghanistan","Afrique du Sud","Albanie","Algérie","Allemagne","Angola",
                "Arabie Saoudite","Argentine","Australie","Autriche","Azerbaïdjan","Bahreïn",
                "Bangladesh","Belgique","Bénin","Bolivie","Brésil","Bulgarie","Burkina Faso",
                "Cambodge","Cameroun","Canada","Chili","Chine","Colombie","Congo","Corée du Sud",
                "Côte d'Ivoire","Croatie","Cuba","Danemark","Djibouti","Égypte",
                "Émirats arabes unis","Espagne","Éthiopie","États-Unis","Finlande","France",
                "Gabon","Ghana","Grèce","Guatemala","Guinée","Hongrie","Inde","Indonésie",
                "Irak","Iran","Irlande","Islande","Israël","Italie","Jamaïque","Japon",
                "Jordanie","Kazakhstan","Kenya","Koweït","Liban","Libye","Luxembourg",
                "Madagascar","Malaisie","Mali","Maroc","Mauritanie","Mexique","Mongolie",
                "Mozambique","Myanmar","Niger","Nigéria","Norvège","Nouvelle-Zélande","Oman",
                "Ouganda","Pakistan","Palestine","Panama","Pays-Bas","Pérou","Philippines",
                "Pologne","Portugal","Qatar","République tchèque","Roumanie","Russie","Rwanda",
                "Sénégal","Serbie","Singapour","Slovaquie","Somalie","Soudan","Sri Lanka",
                "Suède","Suisse","Syrie","Taïwan","Tanzanie","Tchad","Thaïlande","Togo",
                "Tunisie","Turquie","Ukraine","Uruguay","Venezuela","Vietnam","Yémen",
                "Zambie","Zimbabwe"
        };
        nationaliteCombo.getItems().addAll(countries);
        paysResidenceCombo.getItems().addAll(countries);
    }

    // ── SAVE INFO ────────────────────────────────────────────────────────────

    @FXML
    private void handleSaveInfo() {
        clearMessages();

        String nom       = nomField.getText().trim();
        String prenom    = prenomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String nat       = nationaliteCombo.getValue();
        String pays      = paysResidenceCombo.getValue();

        if (nom.isEmpty() || prenom.isEmpty()) {
            showInfoMessage("Le nom et le prénom sont obligatoires.", true); return;
        }
        if (nom.length() < 2 || prenom.length() < 2) {
            showInfoMessage("Nom et prénom doivent avoir au moins 2 caractères.", true); return;
        }
        if (!telephone.matches("\\d{8,15}")) {
            showInfoMessage("Numéro de téléphone invalide (8-15 chiffres).", true); return;
        }

        try {
            userService.updateProfile(
                    currentUser.getId(), nom, prenom, telephone,
                    (nat  != null && !nat.isEmpty())  ? nat  : null,
                    (pays != null && !pays.isEmpty()) ? pays : null
            );

            currentUser.setNom(nom);
            currentUser.setPrenom(prenom);
            currentUser.setTelephone(telephone);
            currentUser.setNationalite(nat);
            currentUser.setPaysResidence(pays);
            Session.setCurrentUser(currentUser);

            showInfoMessage("✅  Profil mis à jour avec succès.", false);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating profile", e);
            showInfoMessage("❌  Erreur : " + e.getMessage(), true);
        }
    }

    // ── CHANGE PASSWORD ───────────────────────────────────────────────────────

    @FXML
    private void handleChangePassword() {
        clearMessages();

        String current = currentPasswordField.getText();
        String newPwd  = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
            showPwdMessage("Veuillez remplir tous les champs.", true); return;
        }
        if (!PasswordUtils.checkPassword(current, currentUser.getPassword())) {
            showPwdMessage("Mot de passe actuel incorrect.", true); return;
        }
        if (newPwd.length() < 6) {
            showPwdMessage("Le nouveau mot de passe doit contenir au moins 6 caractères.", true); return;
        }
        if (!newPwd.equals(confirm)) {
            showPwdMessage("Les mots de passe ne correspondent pas.", true); return;
        }

        try {
            userService.updatePassword(currentUser.getEmail(), newPwd);
            currentUser.setPassword(PasswordUtils.hashPassword(newPwd));
            Session.setCurrentUser(currentUser);

            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            showPwdMessage("✅  Mot de passe modifié avec succès.", false);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error changing password", e);
            showPwdMessage("❌  Erreur : " + e.getMessage(), true);
        }
    }

    // ── CHANGE PHOTO ──────────────────────────────────────────────────────────

    @FXML
    private void handleChangePhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une photo de profil");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png","*.jpg","*.jpeg","*.gif","*.webp")
        );

        Stage stage = (Stage) changePhotoBtn.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file == null) return;

        try {
            Path destDir = Paths.get(PHOTO_DIR);
            if (!Files.exists(destDir)) Files.createDirectories(destDir);

            String ext         = file.getName().substring(file.getName().lastIndexOf('.'));
            String newFileName = "user_" + currentUser.getId() + "_" + System.currentTimeMillis() + ext;
            Path   dest        = destDir.resolve(newFileName);
            Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            userService.updateProfileImage(currentUser.getId(), newFileName);
            currentUser.setImageName(newFileName);
            Session.setCurrentUser(currentUser);

            loadAvatar();
            showInfoMessage("✅  Photo mise à jour.", false);

        } catch (IOException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error uploading photo", e);
            showInfoMessage("❌  Erreur upload : " + e.getMessage(), true);
        }
    }

    // ── CLOSE ────────────────────────────────────────────────────────────────

    @FXML
    private void handleClose() {
        ((Stage) saveInfoBtn.getScene().getWindow()).close();
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private void showInfoMessage(String msg, boolean isError) {
        if (infoMessageLabel == null) return;
        infoMessageLabel.setText(msg);
        infoMessageLabel.setStyle(isError
                ? "-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-font-weight: 600;"
                : "-fx-text-fill: #16a34a; -fx-font-size: 13px; -fx-font-weight: 600;");
        infoMessageLabel.setVisible(true);
    }

    private void showPwdMessage(String msg, boolean isError) {
        if (pwdMessageLabel == null) return;
        pwdMessageLabel.setText(msg);
        pwdMessageLabel.setStyle(isError
                ? "-fx-text-fill: #ef4444; -fx-font-size: 13px; -fx-font-weight: 600;"
                : "-fx-text-fill: #16a34a; -fx-font-size: 13px; -fx-font-weight: 600;");
        pwdMessageLabel.setVisible(true);
    }

    private void clearMessages() {
        if (infoMessageLabel != null) infoMessageLabel.setVisible(false);
        if (pwdMessageLabel  != null) pwdMessageLabel.setVisible(false);
    }

    private String nvl(String s) { return s != null ? s : ""; }
}