package org.onedevman.mc.plugins.passcraft.chestinterface.bukkit;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.onedevman.mc.plugins.passcraft.chestinterface.event.ChestInterfaceEvent;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.ChestInterface;

public class BukkitChestInterfaceIdentifier implements InventoryHolder {

    private final ChestInterface<? extends ChestInterfaceEvent> _chest_interface;

    //

    public BukkitChestInterfaceIdentifier(ChestInterface<? extends ChestInterfaceEvent> chest_interface) {
        this._chest_interface = chest_interface;
    }

    //

    public ChestInterface<? extends ChestInterfaceEvent> chestInterface() { return this._chest_interface; }

    //

    @Override
    public Inventory getInventory() {
        return this.chestInterface().toBukkit();
    }

}
