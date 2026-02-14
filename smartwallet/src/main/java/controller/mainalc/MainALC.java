package controller.mainalc;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import tests.services.MainFXML;

public class MainALC {

    @FXML
    private ImageView imgLogo;

    @FXML
    private ImageView imgLocation;

    @FXML
    private ImageView imgAssurance;

    @FXML
    private ImageView imgCredit;

    @FXML
    public void initialize() {
        // Charger les images
        imgLogo.setImage(new Image(getClass().getResourceAsStream("/icons/logoservices.png")));
        imgLocation.setImage(new Image(getClass().getResourceAsStream("/icons/location.png")));
        imgAssurance.setImage(new Image(getClass().getResourceAsStream("/icons/assurance.png")));
        imgCredit.setImage(new Image(getClass().getResourceAsStream("/icons/credit.png")));

        // Appliquer le cercle pour le logo
        double radius = Math.min(imgLogo.getFitWidth(), imgLogo.getFitHeight()) / 2;
        Circle logoClip = new Circle(radius, radius, radius);
        imgLogo.setClip(logoClip);

        // Appliquer coins arrondis pour les autres icônes
        makeRounded(imgLocation, 20);
        makeRounded(imgAssurance, 20);
        makeRounded(imgCredit, 20);

        // Ajouter événements click
        imgLocation.setOnMouseClicked(this::handleLocationClick);
        imgAssurance.setOnMouseClicked(this::handleAssuranceClick);
        imgCredit.setOnMouseClicked(this::handleCreditClick);
    }

    private void makeRounded(ImageView imageView, double arc) {
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(arc);
        clip.setArcHeight(arc);
        imageView.setClip(clip);
    }

    private void handleLocationClick(MouseEvent event) {
        MainFXML.showAjouterService();
    }

    private void handleAssuranceClick(MouseEvent event) {
        MainFXML.showAjouterAssurance();
    }

    private void handleCreditClick(MouseEvent event) {
        MainFXML.showAjouterCredit();
    }
}
