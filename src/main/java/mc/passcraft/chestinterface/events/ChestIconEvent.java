package mc.passcraft.chestinterface.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import mc.passcraft.chestinterface.components.ChestIcon;
import mc.passcraft.chestinterface.components.ChestInterface;

public class ChestIconEvent extends ChestInterfaceEvent {

    private final ChestIcon _icon;
    private final ItemStack _item;

    //

    public ChestIconEvent(Event bukkit_event, HumanEntity player, Inventory inventory, ChestInterface<?> chest_interface, ChestIcon icon, ItemStack item) {
        super(bukkit_event, player, inventory, chest_interface);

        this._icon = icon;
        this._item = item;
    }

    //

    public InventoryEvent bukkitEvent() { return (InventoryEvent) super.bukkitEvent(); }

    //

    public ChestIcon icon() { return this._icon; }

    public ItemStack item() { return this._item; }
}
