package esprit.tn.souha_pi.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus {
    private static EventBus instance;
    private final Map<String, List<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();

    private EventBus() {}

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public void subscribe(String eventType, Consumer<Object> callback) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(callback);
    }

    public void publish(String eventType, Object data) {
        List<Consumer<Object>> callbacks = subscribers.get(eventType);
        if (callbacks != null) {
            for (Consumer<Object> callback : callbacks) {
                callback.accept(data);
            }
        }
    }
}