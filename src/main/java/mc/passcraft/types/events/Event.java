package mc.passcraft.types.events;

public abstract class Event {

    private final boolean cancellable;

    private boolean cancelled = false;

    //

    public Event() {
        this(false);
    }

    public Event(boolean cancellable) {
        this.cancellable = cancellable;
    }

    //

    public boolean cancellable() { return this.cancellable; }

    public boolean cancelled() { return this.cancelled; }

    public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
