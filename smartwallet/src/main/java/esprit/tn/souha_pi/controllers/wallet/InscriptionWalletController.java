package esprit.tn.souha_pi.controllers.wallet;

import entities.User;
import esprit.tn.souha_pi.controllers.WalletLayoutController;
import esprit.tn.souha_pi.services.WalletService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import utils.Session;

import java.net.URL;
import java.util.ResourceBundle;

public class InscriptionWalletController implements Initializable {

    @FXML private Label etapeLabel;
    @FXML private ProgressBar progressBar;

    @FXML private VBox etape3, etape4, etape6, etape7;

    @FXML private PasswordField pinField;
    @FXML private ToggleGroup typeCompteGroup;
    @FXML private TextField montantField;
    @FXML private Label recapMontant;

    private User currentUser;
    private WalletService walletService = new WalletService();

    private int etapeActuelle = 1;
    private final int TOTAL_ETAPES = 4;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        currentUser = Session.getCurrentUser();

        if(currentUser == null){
            showAlert("Session expirée", "Veuillez vous reconnecter.");
            WalletLayoutController.instance.logout();
            return;
        }

        afficherEtape(1);
    }

    private void afficherEtape(int numero){
        etape3.setVisible(false); etape3.setManaged(false);
        etape4.setVisible(false); etape4.setManaged(false);
        etape6.setVisible(false); etape6.setManaged(false);
        etape7.setVisible(false); etape7.setManaged(false);

        switch(numero){
            case 1: etape3.setVisible(true); etape3.setManaged(true); break;
            case 2: etape4.setVisible(true); etape4.setManaged(true); break;
            case 3: etape6.setVisible(true); etape6.setManaged(true); break;
            case 4:
                recapMontant.setText("Montant : " + montantField.getText() + " TND");
                etape7.setVisible(true); etape7.setManaged(true);
                break;
        }

        etapeActuelle = numero;
        etapeLabel.setText(numero + "/" + TOTAL_ETAPES);
        progressBar.setProgress((double)numero / TOTAL_ETAPES);
    }

    @FXML private void allerEtape3(){ afficherEtape(1); }
    @FXML private void allerEtape4(){ afficherEtape(2); }
    @FXML private void allerEtape6(){ afficherEtape(3); }
    @FXML private void allerEtape7(){ afficherEtape(4); }

    @FXML
    private void validerInscription(){

        try{
            double montant = Double.parseDouble(montantField.getText());

            if(montant < 10){
                showAlert("Erreur", "Le dépôt minimum est 10 TND");
                return;
            }

            if(walletService.walletExiste(currentUser.getId())){
                showAlert("Info", "Vous avez déjà un wallet !");
                WalletLayoutController.instance.goDashboard();
                return;
            }

            walletService.creerWallet(currentUser.getId(), montant);

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Succès");
            a.setHeaderText("Wallet créé");
            a.setContentText("Votre wallet est prêt !");
            a.showAndWait();

            WalletLayoutController.instance.goDashboard();

        }catch(Exception e){
            showAlert("Erreur", "Montant invalide");
        }
    }

    private void showAlert(String t,String m){
        Alert a=new Alert(Alert.AlertType.WARNING);
        a.setTitle(t);
        a.setContentText(m);
        a.showAndWait();
    }
}