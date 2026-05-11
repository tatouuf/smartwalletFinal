package esprit.tn.souha_pi.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import esprit.tn.souha_pi.entities.Equipe;
import esprit.tn.souha_pi.services.EquipeService;

public class EquipeAddController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField logoField;

    @FXML
    private TextField gameField;

    @FXML
    private TextField categorieField;

    @FXML
    private TextField coachIdField;

    private EquipeService equipeService = new EquipeService();

    @FXML
    public void addEquipe() {
        try {
            Equipe e = new Equipe();

            e.setNom(nomField.getText());
            e.setLogo(logoField.getText());
            e.setGame(gameField.getText());
            e.setCategorie(categorieField.getText());
            e.setCoachId(Integer.parseInt(coachIdField.getText()));

            equipeService.add(e);

            System.out.println("✅ Équipe ajoutée !");

            // vider les champs
            nomField.clear();
            logoField.clear();
            gameField.clear();
            categorieField.clear();
            coachIdField.clear();

        } catch (Exception ex) {
            System.out.println("❌ Erreur : " + ex.getMessage());
        }
    }
}