package controller.service;

import entities.service.Services;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import services.service.ServiceServices;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AfficherService {

    @FXML
    private TableView<Services> tableServices;

    @FXML
    private TableColumn<Services, Integer> idcolomserv;
    @FXML
    private TableColumn<Services, Float> priccolomserv;
    @FXML
    private TableColumn<Services, String> desccolomserv;
    @FXML
    private TableColumn<Services, String> typecolomserv;
    @FXML
    private TableColumn<Services, String> statutcolomserv;
    @FXML
    private TableColumn<Services, Integer> idusercolomserv;
    @FXML
    private TableColumn<Services, String> localicolomsheck;
    @FXML
    private TableColumn<Services, String> adressecolomserv;
    @FXML
    private TableColumn<Services, String> typeservcolom;
    @FXML
    private TableColumn<Services, String> imagecolomserv;

    @FXML
    public void initialize() {
        // Lier les colonnes aux propriétés de Services
        idcolomserv.setCellValueFactory(new PropertyValueFactory<>("id"));
        priccolomserv.setCellValueFactory(new PropertyValueFactory<>("prix"));
        desccolomserv.setCellValueFactory(new PropertyValueFactory<>("description"));
        typecolomserv.setCellValueFactory(new PropertyValueFactory<>("type"));
        statutcolomserv.setCellValueFactory(new PropertyValueFactory<>("statutString"));
        idusercolomserv.setCellValueFactory(new PropertyValueFactory<>("userId"));
        localicolomsheck.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        adressecolomserv.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        typeservcolom.setCellValueFactory(new PropertyValueFactory<>("typeServiceString"));
        imagecolomserv.setCellValueFactory(new PropertyValueFactory<>("image"));

        // CellFactory pour afficher l’image avec taille fixe
        imagecolomserv.setCellFactory(column -> new TableCell<Services, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(100);
                imageView.setFitHeight(75);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null || imagePath.isBlank()) {
                    setGraphic(null);
                } else {
                    File file = new File(imagePath);
                    if (file.exists()) {
                        Image img = new Image(file.toURI().toString(), 100, 75, true, true);
                        imageView.setImage(img);
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Charger les services depuis la base de données
        loadServices();
    }

    public void loadServices() {
        ServiceServices ss = new ServiceServices();
        try {
            List<Services> services = ss.recupererServices();

            // S'assurer que l'image n'est jamais null
            for (Services s : services) {
                if (s.getImage() == null || s.getImage().isBlank()) {
                    s.setImage("");
                }
            }

            ObservableList<Services> list = FXCollections.observableArrayList(services);
            tableServices.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
