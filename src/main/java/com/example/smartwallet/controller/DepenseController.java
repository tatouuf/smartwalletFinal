package com.example.smartwallet.controller;

import com.example.smartwallet.model.Depense;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;

public class DepenseController {

    @FXML
    private TableView<Depense> tableDepenses;

    @FXML
    public void initialize() {
        // Activer sélection multiple
        tableDepenses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Méthode liée à un bouton (ex: Supprimer plusieurs dépenses)
     */
    @FXML
    private void actionSurSelection() {

        ObservableList<Depense> selection =
                tableDepenses.getSelectionModel().getSelectedItems();

        if (selection == null || selection.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner au moins une dépense.");
            alert.showAndWait();

            return;
        }

        // Exemple traitement
        for (Depense d : selection) {
            System.out.println("ID: " + d.getId() + " | Montant: " + d.getMontant());

            // 👉 Ici tu peux appeler ton DAO
            // depenseDAO.supprimer(d.getId());
        }

        // Rafraîchir la table après traitement
        tableDepenses.refresh();

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Succès");
        success.setHeaderText(null);
        success.setContentText("Action effectuée sur les dépenses sélectionnées.");
        success.showAndWait();
    }
}