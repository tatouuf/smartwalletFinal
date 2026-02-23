package esprit.tn.souha_pi.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BudgetController {

    @FXML
    private ComboBox<?> categorieCombo;

    @FXML
    private TextField montantMaxField;

    @FXML
    private TextField descriptionField;

    @FXML
    private ComboBox<?> moisCombo;

    @FXML
    private ComboBox<?> anneeCombo;

    @FXML
    private ProgressBar budgetProgressBar;

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;

    @FXML
    private ListView<?> budgetsList;

    @FXML
    private Label totalBudgetsLabel;

    @FXML
    public void initialize() {
        // TODO: Initialiser la vue des budgets
    }
}