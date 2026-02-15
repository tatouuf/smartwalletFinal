package esprit.tn.souha_pi.controllers;

import esprit.tn.souha_pi.entities.BankCard;
import esprit.tn.souha_pi.services.BankCardService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CardAddController {

    @FXML private TextField holderField;
    @FXML private TextField numberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvvField;
    @FXML private ChoiceBox<String> typeChoice;

    private final BankCardService service = new BankCardService();

    @FXML
    public void initialize(){

        typeChoice.getItems().addAll("VISA","MASTERCARD");

        // 🔹 HOLDER : lettres seulement
        holderField.textProperty().addListener((obs,oldVal,newVal)->{
            if(!newVal.matches("[a-zA-Z ]*")){
                holderField.setText(oldVal);
            }
        });
// ===== CREDIT CARD EXPIRY FORMATTER (MM/YY) =====
        expiryField.setTextFormatter(new TextFormatter<>(change -> {

            // text after the user action
            String newText = change.getControlNewText();

            // allow empty
            if (newText.isEmpty())
                return change;

            // allow only digits and slash
            if (!newText.matches("[0-9/]*"))
                return null;

            // auto insert slash after month
            if (newText.length() == 2 && !newText.contains("/")) {
                change.setText(change.getText() + "/");
                change.setCaretPosition(3);
                change.setAnchor(3);
            }

            // prevent more than MM/YY
            if (newText.length() > 5)
                return null;

            return change;
        }));

        // 🔹 CARD NUMBER : 16 chiffres max
        numberField.textProperty().addListener((obs,oldVal,newVal)->{
            if(!newVal.matches("\\d*") || newVal.length()>16){
                numberField.setText(oldVal);
            }
        });

        // 🔹 CVV : 3 chiffres max
        cvvField.textProperty().addListener((obs,oldVal,newVal)->{
            if(!newVal.matches("\\d*") || newVal.length()>3){
                cvvField.setText(oldVal);
            }
        });

        // 🔹 EXPIRY AUTO FORMAT MM/YY

    }

    @FXML
    private void save(){

        // 🔴 VALIDATION AVANT INSERT
        String error = validate();

        if(error != null){
            showError(error);
            return;
        }

        BankCard c = new BankCard(
                1,
                holderField.getText(),
                numberField.getText(),
                expiryField.getText(),
                cvvField.getText(),
                typeChoice.getValue()
        );

        service.add(c);

        Alert a = new Alert(Alert.AlertType.INFORMATION,"Card added successfully");
        a.showAndWait();

        // reset form
        holderField.clear();
        numberField.clear();
        expiryField.clear();
        cvvField.clear();
        typeChoice.setValue(null);
    }

    // 🔥 VALIDATION COMPLETE
    private String validate(){

        // HOLDER
        if(holderField.getText().isEmpty())
            return "Card holder is required";

        // CARD NUMBER
        if(numberField.getText().length()!=16)
            return "Card number must contain 16 digits";

        // CVV
        if(cvvField.getText().length()!=3)
            return "CVV must be 3 digits";

        // TYPE
        if(typeChoice.getValue()==null)
            return "Select card type";

        // EXPIRY FORMAT
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expiry = YearMonth.parse(expiryField.getText(), formatter);

            if(expiry.isBefore(YearMonth.now()))
                return "Card expired";

        }catch(Exception e){
            return "Invalid expiry date (MM/YY)";
        }

        return null;
    }

    private void showError(String msg){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Invalid Card");
        a.setContentText(msg);
        a.showAndWait();
    }
}
