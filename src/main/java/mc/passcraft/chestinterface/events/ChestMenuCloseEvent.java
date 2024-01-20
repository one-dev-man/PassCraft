package mc.passcraft.chestinterface.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import mc.passcraft.chestinterface.components.ChestMenu;

public class ChestMenuCloseEvent extends ChestMenuEvent {

    public ChestMenuCloseEvent(Event bukkit_event, HumanEntity entity, Inventory inventory, ChestMenu menu) {
        super(bukkit_event, entity, inventory, menu);
    }

    //

    @Override
    public InventoryCloseEvent bukkitEvent() { return (InventoryCloseEvent) super.bukkitEvent(); }

}
