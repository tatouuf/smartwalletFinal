package esprit.tn.souha_pi.controllers;


import esprit.tn.souha_pi.controllers.loan.LoanDetailsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class WalletLayoutController {

    @FXML
    private StackPane contentArea;

    public static WalletLayoutController instance;

    @FXML
    public void initialize(){
        instance = this;

        // attendre que la scène soit prête
        javafx.application.Platform.runLater(() -> {
            loadPage("wallet/dashboard.fxml");
        });
    }
    public void goCards(){
        loadPage("wallet/cards.fxml");
    }


    public void loadPage(String page){
        try{
            String path = "/fxml/" + page;

            FXMLLoader loader = new FXMLLoader(
                    WalletLayoutController.class.getResource(path)
            );

            if(loader.getLocation() == null){
                System.out.println("FXML INTROUVABLE: " + path);
                return;
            }

            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // demande un prêt
    public void goRequestLoan(){
        loadPage("loan/request.fxml");
    }

    public void openLoanDetails(int loanId){
        try{

            FXMLLoader loader = new FXMLLoader(
                    WalletLayoutController.class.getResource("/fxml/loan/loandetails.fxml")
            );

            Parent view = loader.load();

            // on récupère le controller
            LoanDetailsController controller = loader.getController();
            controller.loadLoan(loanId);

            contentArea.getChildren().setAll(view);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // voir mes prêts
    public void goMyLoans(){
        loadPage("loan/myloans.fxml");
    }
    public void goDashboard(){ loadPage("wallet/dashboard.fxml"); }
    public void goLoanRequests(){ loadPage("loan/requests.fxml"); }
    public void goSend(){ loadPage("wallet/send.fxml"); }
    public void goReceive(){ loadPage("wallet/receive.fxml"); }
    public void goHistory(){ loadPage("wallet/history.fxml"); }
}
