package esprit.tn.souha_pi.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

import java.util.Optional;

public class DialogUtil {

    // ================= SUCCESS =================
    public static void success(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ================= ERROR =================
    public static void error(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setHeaderText("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ================= CONFIRM =================
    public static boolean confirm(String title, String message){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // ================= INFO =================
    public static void info(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}