package esprit.tn.chayma.utils;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Utilitaire pour afficher des notifications toast (popups temporaires)
 * Utile pour les notifications d'alerte importante (dépassement de budget, etc.)
 */
public class ToastNotification {

    public enum ToastType {
        SUCCESS("#4CAF50"),      // Vert
        WARNING("#FF9800"),      // Orange
        ERROR("#F44336"),        // Rouge
        INFO("#2196F3");         // Bleu

        public final String color;

        ToastType(String color) {
            this.color = color;
        }
    }

    /**
     * Affiche une notification toast à l'écran
     * @param title Titre de la notification
     * @param message Message de la notification
     * @param type Type de notification (détermine la couleur)
     * @param duration Durée en secondes avant disparition
     */
    public static void show(String title, String message, ToastType type, double duration) {
        Platform.runLater(() -> {
            try {
                // Créer le label avec le message
                Label messageLabel = new Label(message);
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(400);
                messageLabel.setStyle("-fx-font-size: 13; -fx-text-fill: white; -fx-padding: 5;");

                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 5 5 0 5;");

                // Créer le container
                VBox container = new VBox(5);
                container.getChildren().addAll(titleLabel, messageLabel);
                container.setStyle("-fx-background-color: " + type.color + "; " +
                        "-fx-padding: 15; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.3, 0, 3);");

                // Créer la scène
                Scene scene = new Scene(container);
                scene.setFill(Color.TRANSPARENT);

                // Créer la fenêtre
                Stage stage = new Stage();
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.setWidth(450);
                stage.setAlwaysOnTop(true);

                // Positionner en bas à droite de l'écran
                double screenWidth = Screen.getPrimary().getBounds().getWidth();
                double screenHeight = Screen.getPrimary().getBounds().getHeight();
                stage.setX(screenWidth - 470);
                stage.setY(screenHeight - 150);

                stage.show();

                // Disparition automatique avec fade-out
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), container);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setDelay(Duration.seconds(duration));
                fadeOut.setOnFinished(e -> stage.close());
                fadeOut.play();
            } catch (Exception e) {
                System.err.println("Erreur affichage toast: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // Méthodes de commodité
    public static void success(String title, String message) {
        show(title, message, ToastType.SUCCESS, 3);
    }

    public static void warning(String title, String message) {
        show(title, message, ToastType.WARNING, 4);
    }

    public static void error(String title, String message) {
        show(title, message, ToastType.ERROR, 5);
    }

    public static void info(String title, String message) {
        show(title, message, ToastType.INFO, 3);
    }

    /**
     * Obtient la largeur de l'écran principal
     */
    public static double getScreenWidth() {
        return Screen.getPrimary().getBounds().getWidth();
    }

    /**
     * Obtient la hauteur de l'écran principal
     */
    public static double getScreenHeight() {
        return Screen.getPrimary().getBounds().getHeight();
    }
}

