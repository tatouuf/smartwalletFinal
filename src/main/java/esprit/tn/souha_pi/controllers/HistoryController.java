package esprit.tn.souha_pi.controllers;


import esprit.tn.souha_pi.entities.Transaction;
import esprit.tn.souha_pi.services.TransactionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoryController {

    @FXML
    private TableView<Transaction> table;

    @FXML
    private TableColumn<Transaction,String> colType;
    @FXML
    private TableColumn<Transaction,Double> colAmount;
    @FXML
    private TableColumn<Transaction,String> colTarget;
    @FXML
    private TableColumn<Transaction,String> colDate;

    TransactionService service=new TransactionService();

    @FXML
    public void initialize(){

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colTarget.setCellValueFactory(new PropertyValueFactory<>("target"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        table.setItems(FXCollections.observableArrayList(service.getAll(1)));
    }
}
