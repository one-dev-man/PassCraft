package org.onedevman.mc.plugins.passcraft.chestinterface.event;

import org.onedevman.mc.plugins.passcraft.utils.reflection.MethodsUtil;
import org.onedevman.mc.plugins.passcraft.types.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ChestInterfaceEventManager<T extends ChestInterfaceEvent> {

    private final List<ChestInterfaceEventListener> listeners = new ArrayList<>();

    //

    public  ChestInterfaceEventManager() {
        ChestInterfaceEventPriority[] priorities = ChestInterfaceEventPriority.values();
    }

    //

    public List<ChestInterfaceEventListener> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    public void addListener(ChestInterfaceEventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ChestInterfaceEventListener listener) {
        this.listeners.remove(listener);
    }

    public void removeAllListeners() {
        int listener_count = this.listeners.size();

        for(int i = listener_count-1; i > -1; --i)
            listeners.remove(i);
    }

    //

    public boolean call(T event) throws InvocationTargetException, IllegalAccessException {
        ChestInterfaceEventPriority[] priorities = ChestInterfaceEventPriority.values();

        int listener_count = this.listeners.size();

        Map<ChestInterfaceEventPriority, List<Pair<ChestInterfaceEventListener, Method>>> prioritized_event_handlers = new HashMap<>();

        for(ChestInterfaceEventPriority priority : priorities)
            prioritized_event_handlers.put(priority, new ArrayList<>());

        for(ChestInterfaceEventListener listener : this.listeners) {
            MethodsUtil.filter(listener, (event_handler) -> {
                if(!event_handler.isAnnotationPresent(ChestInterfaceEventHandler.class))
                    return false;

                ChestInterfaceEventHandler event_handler_props = event_handler.getAnnotation(ChestInterfaceEventHandler.class);

                if(event_handler.getParameterCount() > 0 && event_handler.getParameters()[0].getType().equals(event.getClass()))
                    prioritized_event_handlers.get(event_handler_props.priority()).add(Pair.of(listener, event_handler));

                return false;
            });
        }

        ChestInterfaceEventListener listener;
        Method event_handler;

        for(ChestInterfaceEventPriority priority : priorities) {
            List<Pair<ChestInterfaceEventListener, Method>> event_handler_pairs = prioritized_event_handlers.get(priority);

            for(Pair<ChestInterfaceEventListener, Method> event_handler_pair : event_handler_pairs) {
                listener = event_handler_pair.first();
                event_handler = event_handler_pair.last();

                ChestInterfaceEventHandler event_handler_props = event_handler.getAnnotation(ChestInterfaceEventHandler.class);

                if(!event.cancelled() || event_handler_props.ignoreCancelled())
                    MethodsUtil.invoke(listener, event_handler, event);
            }
        }

        return event.cancelled();
    }

    public <E> boolean call(E event) throws InvocationTargetException, IllegalAccessException {
        return this.call((T) event);
    }

}
