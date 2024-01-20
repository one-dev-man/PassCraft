package mc.passcraft.chestinterface.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import mc.passcraft.PluginMain;
import mc.passcraft.chestinterface.components.AnvilInput;
import mc.passcraft.chestinterface.components.ChestIcon;
import mc.passcraft.chestinterface.components.ChestInterface;
import mc.passcraft.chestinterface.components.ChestMenu;
import mc.passcraft.chestinterface.events.*;

import java.lang.reflect.InvocationTargetException;

public class BukkitChestInterfaceEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpen(InventoryOpenEvent event) throws InvocationTargetException, IllegalAccessException { handle(event); }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent event) throws InvocationTargetException, IllegalAccessException { handle(event); }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onIconClick(InventoryClickEvent event) throws InvocationTargetException, IllegalAccessException { handle(event); }

    //

    public void handle(Event e) throws InvocationTargetException, IllegalAccessException {
        if(!(e instanceof InventoryEvent inventory_event)) return;

        Inventory inventory = inventory_event.getInventory();

        ChestInterface<? extends ChestInterfaceEvent> chest_interface = ChestInterface.getAssociatedTo(inventory);

        if(chest_interface == null) return;

        boolean cancelled = false;

        if(inventory_event instanceof InventoryOpenEvent event) {
            if(chest_interface instanceof ChestMenu menu)
                event.setCancelled(menu.call(new ChestMenuOpenEvent(event, event.getPlayer(), inventory, menu)));
            else if(chest_interface instanceof AnvilInput anvil_input)
                event.setCancelled(anvil_input.call(new AnvilInputOpenEvent(event, event.getPlayer(), inventory, anvil_input)));
        }

        //

        else if(inventory_event instanceof InventoryCloseEvent event) {
            if (chest_interface instanceof ChestMenu menu)
                cancelled = menu.call(new ChestMenuCloseEvent(event, event.getPlayer(), inventory, menu));
            else if (chest_interface instanceof AnvilInput anvil_input)
                cancelled = anvil_input.call(new AnvilInputCloseEvent(event, event.getPlayer(), inventory, anvil_input));

            if (cancelled) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(
                    PluginMain.instance(), () -> {
                        if(!chest_interface.equals(ChestInterface.getAssociatedTo(event.getPlayer().getOpenInventory().getTopInventory())))
                            event.getPlayer().openInventory(inventory);
                    },
                    1L
                );
            }
        }

        //

        else if(inventory_event instanceof InventoryClickEvent event) {
            cancelled = true;

            ChestIcon icon = null;
            ChestInterfaceEvent chest_interface_event = null;

            int slot = event.getSlot();
            ClickType mouse_click = event.getClick();
            HumanEntity entity = event.getWhoClicked();

            if(chest_interface instanceof ChestMenu menu) {
                icon = menu.getIcon(slot);
                chest_interface_event = new ChestMenuClickEvent(event, entity, inventory, menu, event.getCurrentItem(), slot, mouse_click);
            }
            else if(chest_interface instanceof AnvilInput anvil_input) {
                icon = anvil_input.config.inputIcon();
                chest_interface_event = new AnvilInputClickEvent(event, entity, inventory, anvil_input, event.getCurrentItem(), slot, mouse_click);
            }

            if (icon != null)
                cancelled = !icon.call(new ChestIconClickEvent(event, entity, inventory, chest_interface, icon, event.getCurrentItem(), slot, mouse_click));

            if(chest_interface_event != null) {
                chest_interface_event.setCancelled(!cancelled);
                cancelled = !chest_interface.call(chest_interface_event);
            }

            event.setCancelled(cancelled);
        }
    }

}
