package org.onedevman.mc.plugins.passcraft.chestinterface.event;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.ChestInterface;

public abstract class ChestInterfaceEvent {

    private final Event _bukkit_event;
    private final HumanEntity _entity;
    private final Inventory _inventory;
    private final ChestInterface<?> _chest_interface;

    public boolean _cancelled = false;

    //

    public ChestInterfaceEvent(Event bukkit_event, HumanEntity entity, Inventory inventory, ChestInterface<?> chest_interface) {
        this._bukkit_event = bukkit_event;
        this._entity = entity;
        this._inventory = inventory;
        this._chest_interface = chest_interface;
    }

    //

    public Event bukkitEvent() { return this._bukkit_event; }

    public HumanEntity entity() { return this._entity; }

    public Inventory inventory() { return this._inventory; }

    public ChestInterface<?> chestInterface() { return this._chest_interface; }

    //

    public boolean cancelled() { return this._cancelled; }

    public void setCancelled(boolean cancel) { this._cancelled = cancel; }

}
