package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Permet de notifier tous les écrans abonnés qu'une modification de données (ajout, modification, suppression)
 * a eu lieu, afin qu'ils se rafraîchissent automatiquement.
 */
public class DataChangeNotifier {
    private static final List<Runnable> listeners = new ArrayList<>();

    public static void addListener(Runnable listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    public static void notifyDataChanged() {
        listeners.forEach(Runnable::run);
    }
}