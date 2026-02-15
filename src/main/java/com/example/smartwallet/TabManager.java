package com.example.smartwallet;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Simple helper to hold a global reference to the main TabPane or BorderPane so controllers can switch views reliably.
 */
public class TabManager {
    private static TabPane tabPane;
    private static BorderPane root;

    public static void setTabPane(TabPane tp) {
        tabPane = tp;
    }

    public static TabPane getTabPane() {
        return tabPane;
    }

    public static void setRoot(BorderPane bp) {
        root = bp;
    }

    public static BorderPane getRoot() {
        return root;
    }

    public static boolean selectTabByText(String text) {
        if (tabPane == null) return false;
        for (Tab t : tabPane.getTabs()) {
            if (text.equals(t.getText()) || (t.getId() != null && text.equals(t.getId()))) {
                tabPane.getSelectionModel().select(t);
                return true;
            }
        }
        return false;
    }
    public static boolean showView(String fxmlPath, String tabText) {
        System.out.println("TabManager.showView called: fxml=" + fxmlPath + ", tabText=" + tabText);
        // Try to select existing tab
        if (tabPane != null) {
            System.out.println("TabManager: tabPane found, tabs=" + tabPane.getTabs().size());
            for (Tab t : tabPane.getTabs()) {
                if (tabText != null && (tabText.equals(t.getText()) || (t.getId() != null && tabText.equals(t.getId())))) {
                    System.out.println("TabManager: selecting tab '" + tabText + "'\n");
                    tabPane.getSelectionModel().select(t);
                    return true;
                }
            }
            System.out.println("TabManager: tabText not found in TabPane");
        } else {
            System.out.println("TabManager: tabPane is null");
        }

        // If no tab, try to load into root BorderPane
        if (root != null) {
            System.out.println("TabManager: root BorderPane found, loading FXML...");
            try {
                FXMLLoader loader = new FXMLLoader(TabManager.class.getResource(fxmlPath));
                Parent content = loader.load();
                root.setCenter(content);
                root.requestLayout();
                System.out.println("TabManager: view loaded and setCenter()");
                return true;
            } catch (IOException e) {
                System.out.println("TabManager: failed to load FXML: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("TabManager: root is null");
        }

        return false;
    }
}
