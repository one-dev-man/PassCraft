package mc.passcraft.types.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    EventHandlerPriority priority() default EventHandlerPriority.NORMAL;

    boolean ignoreCancelled() default false;

}
