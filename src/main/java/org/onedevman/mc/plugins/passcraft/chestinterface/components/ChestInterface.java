package org.onedevman.mc.plugins.passcraft.chestinterface.components;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.onedevman.mc.plugins.passcraft.chestinterface.bukkit.BukkitChestInterfaceIdentifier;
import org.onedevman.mc.plugins.passcraft.chestinterface.event.ChestInterfaceEvent;
import org.onedevman.mc.plugins.passcraft.chestinterface.event.ChestInterfaceEventManager;

public abstract class ChestInterface<EventType extends ChestInterfaceEvent> extends ChestInterfaceEventManager<EventType> {

    public static ChestInterface<?> getAssociatedTo(Inventory inventory) {
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
