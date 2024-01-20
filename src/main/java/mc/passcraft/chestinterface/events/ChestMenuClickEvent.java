package mc.passcraft.chestinterface.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import mc.passcraft.chestinterface.components.ChestMenu;

public class ChestMenuClickEvent extends ChestMenuEvent {

    private final ItemStack _clicked_item;
    private final int _slot;
    private final ClickType _mouse_click;

    //

    public ChestMenuClickEvent(Event bukkit_event, HumanEntity entity, Inventory inventory, ChestMenu menu, ItemStack clicked_item, int slot, ClickType mouse_click) {
        super(bukkit_event, entity, inventory, menu);

        this._clicked_item = clicked_item;
        this._slot = slot;
        this._mouse_click = mouse_click;
    }

    //

    public ItemStack clickedItem() { return this._clicked_item; }

    public int slot() { return this._slot; }

    public ClickType mouseClick() { return this._mouse_click; }

}
