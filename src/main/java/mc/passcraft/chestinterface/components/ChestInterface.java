package mc.passcraft.chestinterface.components;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import mc.passcraft.chestinterface.bukkit.BukkitChestInterfaceIdentifier;
import mc.passcraft.chestinterface.events.ChestInterfaceEvent;
import mc.passcraft.chestinterface.events.ChestInterfaceEventManager;

public abstract class ChestInterface<EventType extends ChestInterfaceEvent> extends ChestInterfaceEventManager<EventType> {

    public static ChestInterface<? extends ChestInterfaceEvent> getAssociatedTo(Inventory inventory) {
        InventoryHolder inventory_holder = inventory.getHolder();

        if(inventory_holder == null || !(inventory_holder instanceof BukkitChestInterfaceIdentifier chest_interface_identifier))
            return null;

        return chest_interface_identifier.chestInterface();
    }

    //

    public static void playInteractionSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
    }

    //

    public abstract Inventory toBukkit();

}