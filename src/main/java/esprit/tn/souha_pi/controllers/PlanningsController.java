package esprit.tn.souha_pi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class PlanningsController {

    @FXML
    private TextField nomField;

    @FXML
    private ComboBox<?> typeCombo;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<?> statutCombo;

    @FXML
    private TextField revenuPrevuField;

    @FXML
    private TextField epargnePrevueField;

    @FXML
    private TextField pourcentageEpargneField;

    @FXML
    private ComboBox<?> moisCombo;

    @FXML
    private ComboBox<?> anneeCombo;

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;

    @FXML
    private ListView<?> planningsList;

    @FXML
    private Label totalPlanningsLabel;

    @FXML
    public void initialize() {
        // TODO: Initialiser la vue des plannings
    }
}