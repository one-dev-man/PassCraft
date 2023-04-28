package org.onedevman.mc.plugins.passcraft.chestinterface.event;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.AnvilInput;

public class AnvilInputOpenEvent extends AnvilInputEvent {

    public AnvilInputOpenEvent(Event bukkit_event, HumanEntity entity, Inventory inventory, AnvilInput anvil_input) {
        super(bukkit_event, entity, inventory, anvil_input);
    }

    //

    @Override
    public InventoryOpenEvent bukkitEvent() { return (InventoryOpenEvent) super.bukkitEvent(); }

}
