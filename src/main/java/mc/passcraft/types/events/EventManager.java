package mc.passcraft.types.events;

import mc.passcraft.types.Pair;
import mc.passcraft.utils.reflection.MethodsUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class EventManager<
    EventType extends Event,
    EventListenerType extends EventListener
> implements EventManagerInterface<EventType, EventListenerType> {

    private final List<EventListenerType> listeners = new ArrayList<>();

    //

//    public EventManager() {}

    //

    public List<EventListenerType> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    public void addListener(EventListenerType listener) {
        this.listeners.add(listener);
    }

    public void removeListener(EventListenerType listener) {
        this.listeners.remove(listener);
    }

    public void removeAllListeners() {
        int listener_count = this.listeners.size();
        if(listener_count > 0) listeners.subList(0, listener_count).clear();
    }

    //

    public boolean call(EventType event) {
        EventHandlerPriority[] priorities = EventHandlerPriority.values();

        Map<EventHandlerPriority, List<Pair<EventListenerType, Method>>> prioritized_event_handlers = new HashMap<>();

        for(EventHandlerPriority priority : priorities)
            prioritized_event_handlers.put(priority, new ArrayList<>());

        for(EventListenerType listener : this.listeners) {
            MethodsUtil.filter(listener, (event_handler) -> {
                if(!event_handler.isAnnotationPresent(EventHandler.class))
                    return false;

                EventHandler event_handler_props = event_handler.getAnnotation(EventHandler.class);

                if(event_handler.getParameterCount() > 0 && event_handler.getParameters()[0].getType().equals(event.getClass()))
                    prioritized_event_handlers.get(event_handler_props.priority()).add(Pair.of(listener, event_handler));

                return false;
            });
        }

        EventListenerType listener;
        Method event_handler;

        for(EventHandlerPriority priority : priorities) {
            List<Pair<EventListenerType, Method>> event_handler_pairs = prioritized_event_handlers.get(priority);

            for(Pair<EventListenerType, Method> event_handler_pair : event_handler_pairs) {
                listener = event_handler_pair.first();
                event_handler = event_handler_pair.last();

                EventHandler event_handler_props = event_handler.getAnnotation(EventHandler.class);

                if(!event.cancelled() || event_handler_props.ignoreCancelled()) {
                    try {
                        MethodsUtil.invoke(listener, event_handler, event);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return event.cancelled();
    }

    public <E> boolean call(E event) {
        return this.call((EventType) event);
    }

}
