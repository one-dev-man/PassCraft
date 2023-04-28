package org.onedevman.mc.plugins.passcraft.chestinterface.event;

public enum ChestInterfaceEventPriority {

    LOWEST(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    HIGHEST(4);

    //

    public final int priority;

    //

    ChestInterfaceEventPriority(int priority) {
        this.priority = priority;
    }

}
