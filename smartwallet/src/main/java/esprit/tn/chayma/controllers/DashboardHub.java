package esprit.tn.chayma.controllers;

public class DashboardHub {
    private static DashboardDepensController controller;

    public static synchronized void register(DashboardDepensController c) {
        controller = c;
    }

    public static synchronized void unregister(DashboardDepensController c) {
        if (controller == c) controller = null;
    }

    public static synchronized void notifyRefresh() {
        if (controller != null) {
            controller.refreshData();
        }
    }
}

