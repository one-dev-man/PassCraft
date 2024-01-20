package mc.passcraft.chestinterface.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import mc.passcraft.chestinterface.components.AnvilInput;

public class AnvilInputClickEvent extends AnvilInputEvent {

    private final ItemStack _clicked_item;
    private final int _slot;
    private final ClickType _mouse_click;

    //

    public AnvilInputClickEvent(Event bukkit_event, HumanEntity entity, Inventory inventory, AnvilInput anvil_input, ItemStack clicked_item, int slot, ClickType mouse_click) {
        super(bukkit_event, entity, inventory, anvil_input);

        this._clicked_item = clicked_item;
        this._slot = slot;
        this._mouse_click = mouse_click;
    }

    //

    public ItemStack clickedItem() { return this._clicked_item; }

    public int slot() { return this._slot; }

    public ClickType mouseClick() { return this._mouse_click; }

}
