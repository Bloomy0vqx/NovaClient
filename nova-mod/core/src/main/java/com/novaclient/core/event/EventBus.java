package com.novaclient.core.event;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private static final EventBus INSTANCE = new EventBus();
    private final Map<Class<? extends Event>, List<ListenerEntry>> listenerMap = new ConcurrentHashMap<>();

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public void register(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            EventListener annotation = method.getAnnotation(EventListener.class);
            if (annotation == null) continue;
            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) continue;
            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) params[0];
            method.setAccessible(true);
            listenerMap.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>())
                .add(new ListenerEntry(obj, method, annotation.priority()));
        }
    }

    public void unregister(Object obj) {
        for (List<ListenerEntry> entries : listenerMap.values()) {
            entries.removeIf(e -> e.instance == obj);
        }
    }

    public <T extends Event> T fire(T event) {
        List<ListenerEntry> entries = listenerMap.get(event.getClass());
        if (entries != null) {
            for (ListenerEntry entry : entries) {
                try {
                    entry.method.invoke(entry.instance, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return event;
    }

    private static class ListenerEntry {
        final Object instance;
        final Method method;
        final Event.Priority priority;

        ListenerEntry(Object instance, Method method, Event.Priority priority) {
            this.instance = instance;
            this.method = method;
            this.priority = priority;
        }
    }
}
