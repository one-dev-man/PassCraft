package mc.passcraft.types.events;

public enum EventHandlerPriority {

    LOWEST(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    HIGHEST(4);

    //

    public final int priority;

    //

    EventHandlerPriority(int priority) {
        this.priority = priority;
    }

}
