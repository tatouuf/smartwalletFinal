package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.Wallet;
import esprit.tn.souha_pi.services.WalletService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label balanceLabel;

    private static DashboardController instance;
    private WalletService walletService = new WalletService();

    public DashboardController(){
        instance = this;
    }

    @FXML
    public void initialize(){
        refreshBalance();
    }

    public void refreshBalance(){
        Wallet w = walletService.getWallet();
        balanceLabel.setText(String.format("%.2f TND", w.getBalance()));
    }

    // 🔥 permet aux autres controllers de refresh
    public static void refreshStatic(){
        if(instance != null){
            instance.refreshBalance();
        }
    }
}
