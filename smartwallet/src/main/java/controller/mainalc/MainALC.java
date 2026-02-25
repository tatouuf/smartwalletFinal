package controller.mainalc;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tests.MainFxml;

public class MainALC {

    @FXML
    private ImageView imgLogo;

    // ================= INITIALIZE =================
    @FXML
    public void initialize() {
        try {
            imgLogo.setImage(new Image(
                    getClass().getResourceAsStream("/icons/logoservices.png")
            ));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Logo non trouvé : /icons/logoservices.png");
        }
    }

    // ================= USER ACCESS =================
    @FXML
    private void userseract() {
        // ouvre la page service client
        MainFxml.getInstance().showServiceClient();
    }

    // ================= ADMIN ACCESS =================
    @FXML
    private void buttonadminser() {
        // ouvre la page service admin
        MainFxml.getInstance().showServiceAdmin();
    }
}