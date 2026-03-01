package esprit.tn.souha_pi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

import static com.almasb.fxgl.app.GameApplication.launch;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Load FXML
        URL fxml = getClass().getResource("/fxml/admin/layout/wallet_layout.fxml");

        if (fxml == null) {
            System.out.println("FXML NOT FOUND");
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxml);
        Scene scene = new Scene(loader.load(), 1100, 700);

        // Load CSS
        URL css = getClass().getResource("/css/theme.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS NOT FOUND");
        }

        stage.setTitle("Souha Wallet");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
