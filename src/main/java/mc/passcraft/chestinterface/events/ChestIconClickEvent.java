package mc.passcraft.chestinterface.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import mc.passcraft.chestinterface.components.ChestIcon;
import mc.passcraft.chestinterface.components.ChestInterface;

public class ChestIconClickEvent extends ChestIconEvent {

    private final int _slot;
    private final ClickType _mouse_click;

    //

    public ChestIconClickEvent(Event bukkit_event, HumanEntity entity, Inventory inventory, ChestInterface<?> chest_interface, ChestIcon icon, ItemStack item, int slot, ClickType mouse_click) {
        super(bukkit_event, entity, inventory, chest_interface, icon, item);

        this._mouse_click = mouse_click;
        this._slot = slot;
    }

    //

    public InventoryClickEvent bukkitEvent() { return (InventoryClickEvent) super.bukkitEvent(); }

    //

    public ClickType mouseClick() { return this._mouse_click; }

    public int slot() { return this._slot; }

}
