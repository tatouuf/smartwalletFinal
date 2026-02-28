package controller.acceuilservice;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class AcceuilServiceClient {

    // ================== FXML ELEMENTS ==================
    @FXML
    private ImageView imgService;
    @FXML
    private ImageView imgAssurance;
    @FXML
    private ImageView imgCredit;
    @FXML
    private ImageView imgLogo;
    @FXML
    private Button btnRetourMain;

    // ================== HOST SERVICES ==================
    private HostServices hostServices;

    // Setter pour HostServices
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
        System.out.println("✅ HostServices injecté dans AcceuilServiceClient");
    }

    // ================== INITIALIZE ==================
    @FXML
    public void initialize() {
        try {
            // Chargement des images avec vérification null
            if (imgLogo != null) {
                Image logoImg = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream("/icons/logoservices.png"),
                        "Logo image not found"
                ));
                imgLogo.setImage(logoImg);
            }

            if (imgService != null) {
                Image serviceImg = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream("/icons/location.png"),
                        "Location image not found"
                ));
                imgService.setImage(serviceImg);
            }

            if (imgAssurance != null) {
                Image assuranceImg = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream("/icons/assurance.png"),
                        "Assurance image not found"
                ));
                imgAssurance.setImage(assuranceImg);
            }

            if (imgCredit != null) {
                Image creditImg = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream("/icons/credit.png"),
                        "Credit image not found"
                ));
                imgCredit.setImage(creditImg);
            }

            System.out.println("✅ Images chargées avec succès");

        } catch (NullPointerException e) {
            System.err.println("❌ Image non trouvée: " + e.getMessage());
            showAlert("Erreur de chargement", "Certaines images n'ont pas pu être chargées.");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des images: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les images.");
        }
    }

    // ================== BUTTON ACTIONS ==================

    /**
     * Ouvre la page d'affichage des services
     */
    @FXML
    private void showAfficherService() {
        openFXMLWithHostServices("/services/AfficherServiceClient.fxml", "Afficher Tous les Services");
    }

    /**
     * Ouvre la page d'affichage des assurances
     */
    @FXML
    private void showAfficherAssurance() {
        openFXMLWithHostServices("/assurance/AfficherAssuranceClient.fxml", "Afficher Toutes les Assurances");
    }

    /**
     * Ouvre la page d'affichage des crédits
     */
    @FXML
    private void showAfficherCredit() {
        openFXMLWithHostServices("/credit/AfficherCreditClient.fxml", "Afficher Tous les Crédits");
    }

    /**
     * Retourne à la page principale
     */
    @FXML
    private void retourMain() {
        try {
            // Charger le FXML de la page principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainALC/MainALC.fxml"));
            Parent root = loader.load();

            // Récupérer le Stage actuel
            Stage stage = null;
            if (btnRetourMain != null && btnRetourMain.getScene() != null) {
                stage = (Stage) btnRetourMain.getScene().getWindow();
            } else if (imgLogo != null && imgLogo.getScene() != null) {
                stage = (Stage) imgLogo.getScene().getWindow();
            }

            if (stage != null) {
                // Réutiliser le stage existant
                stage.setScene(new Scene(root, 900, 500));
                stage.setTitle("Main ALC");
                stage.centerOnScreen();
            } else {
                // Créer un nouveau stage si aucun n'existe
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root, 900, 500));
                newStage.setTitle("Main ALC");
                newStage.centerOnScreen();
                newStage.show();
            }

            System.out.println("✅ Retour à la page principale effectué");

        } catch (NullPointerException e) {
            System.err.println("❌ FXML non trouvé: /mainALC/MainALC.fxml");
            showAlert("Erreur", "Fichier de la page principale introuvable.");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du retour à la page principale: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page principale.");
        }
    }

    // ================== UTILS ==================

    /**
     * Ouvre un fichier FXML dans la même fenêtre et passe HostServices
     * @param fxmlPath Le chemin vers le fichier FXML
     * @param title Le titre de la fenêtre
     */
    private void openFXMLWithHostServices(String fxmlPath, String title) {
        try {
            // Vérifier que le chemin n'est pas null
            if (fxmlPath == null || fxmlPath.isEmpty()) {
                throw new IllegalArgumentException("Le chemin FXML ne peut pas être vide");
            }

            // Charger le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new NullPointerException("FXML non trouvé: " + fxmlPath);
            }

            Parent root = loader.load();

            // Passer HostServices au contrôleur si la méthode existe
            Object controller = loader.getController();
            if (controller != null && hostServices != null) {
                try {
                    // Essayer d'appeler setHostServices par réflexion
                    java.lang.reflect.Method method = controller.getClass().getMethod("setHostServices", HostServices.class);
                    method.invoke(controller, hostServices);
                    System.out.println("✅ HostServices passé au contrôleur: " + controller.getClass().getSimpleName());
                } catch (NoSuchMethodException e) {
                    // La méthode n'existe pas, ce n'est pas grave
                    System.out.println("ℹ️ Le contrôleur n'a pas de méthode setHostServices");
                } catch (Exception e) {
                    System.err.println("⚠️ Erreur lors du passage de HostServices: " + e.getMessage());
                }
            }

            // Récupérer le Stage actuel
            Stage stage = null;
            if (imgLogo != null && imgLogo.getScene() != null) {
                stage = (Stage) imgLogo.getScene().getWindow();
            } else if (imgService != null && imgService.getScene() != null) {
                stage = (Stage) imgService.getScene().getWindow();
            }

            if (stage != null) {
                stage.setScene(new Scene(root, 900, 500));
                stage.setTitle(title);
                stage.centerOnScreen();
                System.out.println("✅ Page ouverte: " + title);
            } else {
                // Si aucun stage n'est trouvé, ouvrir dans une nouvelle fenêtre
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root, 900, 500));
                newStage.setTitle(title);
                newStage.centerOnScreen();
                newStage.show();
                System.out.println("✅ Nouvelle fenêtre ouverte: " + title);
            }

        } catch (NullPointerException e) {
            System.err.println("❌ FXML non trouvé: " + fxmlPath);
            showAlert("Erreur", "Fichier introuvable: " + fxmlPath);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Chemin FXML invalide: " + e.getMessage());
            showAlert("Erreur", "Chemin d'accès invalide");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ouverture de " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir " + title);
        }
    }

    /**
     * Affiche une boîte de dialogue d'erreur
     * @param title Le titre de l'alerte
     * @param message Le message à afficher
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte de dialogue d'information
     * @param title Le titre de l'alerte
     * @param message Le message à afficher
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}