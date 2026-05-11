package esprit.tn.chayma.controllers;

import esprit.tn.chayma.services.AdvisorService;
import esprit.tn.chayma.utils.DialogUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class AdvisorController {
    @FXML
    private ComboBox<Integer> moisCombo;
    @FXML
    private ComboBox<Integer> anneeCombo;
    @FXML
    private TextArea adviceArea;
    @FXML
    private Button analyzeBtn;
    @FXML
    private Button refreshBtn;

    private AdvisorService advisorService = new AdvisorService();

    @FXML
    public void initialize() {
        // Initialiser mois (1-12)
        for (int i = 1; i <= 12; i++) {
            moisCombo.getItems().add(i);
        }
        LocalDate today = LocalDate.now();
        moisCombo.setValue(today.getMonthValue());

        // Initialiser années
        for (int i = 2024; i <= 2028; i++) {
            anneeCombo.getItems().add(i);
        }
        anneeCombo.setValue(today.getYear());

        analyzeBtn.setOnAction(e -> onAnalyze());
        refreshBtn.setOnAction(e -> onAnalyze());
    }

    private void onAnalyze() {
        Integer mois = moisCombo.getValue();
        Integer annee = anneeCombo.getValue();

        if (mois == null || annee == null) {
            DialogUtil.error("Erreur", "Sélectionnez mois et année");
            return;
        }

        String advice = advisorService.getAdvice(0, mois, annee);
        adviceArea.setText(advice);
    }
}