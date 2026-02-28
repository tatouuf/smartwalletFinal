package esprit.tn.souha_pi.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    private static EventBus instance;
    private final Map<String, List<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    private EventBus() {}

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public void subscribe(String eventType, Consumer<Object> callback) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(callback);

        // Vérifier le cache
        if (eventCache.containsKey(eventType)) {
            Object cachedData = eventCache.remove(eventType);
            callback.accept(cachedData);
        }
    }

    public void publish(String eventType, Object data) {
        List<Consumer<Object>> callbacks = subscribers.get(eventType);
        if (callbacks != null && !callbacks.isEmpty()) {
            for (Consumer<Object> callback : callbacks) {
                callback.accept(data);
            }
        } else {
            // Mettre en cache pour les futurs abonnés
            eventCache.put(eventType, data);
        }
    }

    public void clearCache() {
        eventCache.clear();
    }
}