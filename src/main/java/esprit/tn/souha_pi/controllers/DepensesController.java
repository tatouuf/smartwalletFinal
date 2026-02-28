package esprit.tn.souha_pi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DepensesController {

    @FXML
    private TextField montantField;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dateDepenseField;

    @FXML
    private ComboBox<?> categorieCombo;

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;

    @FXML
    private ComboBox<?> filterCategorieCombo;

    @FXML
    private ComboBox<?> filterMoisCombo;

    @FXML
    private ComboBox<?> filterAnneeCombo;

    @FXML
    private ListView<?> depensesList;

    @FXML
    private Label totalDepensesLabel;

    @FXML
    public void initialize() {
        // TODO: Initialiser la vue des dépenses
    }
}