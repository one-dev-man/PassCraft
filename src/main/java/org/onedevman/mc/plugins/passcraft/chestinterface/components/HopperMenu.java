package org.onedevman.mc.plugins.passcraft.chestinterface.components;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.onedevman.mc.plugins.passcraft.chestinterface.bukkit.BukkitChestInterfaceIdentifier;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations.HopperMenuConfig;

public class HopperMenu extends ChestMenu {

    public static final int MAX_HOPPER_INVENTORY_SLOT_COUNT = 5;

    public final HopperMenuConfig config;

    //

    public HopperMenu(String title) {
        this(new HopperMenuConfig(title));
    }

    public HopperMenu(HopperMenuConfig config) {
        super(config);
        this.config = config;
    }

    //

    @Override
    public Inventory toBukkit() {
        Inventory inventory = Bukkit.createInventory(new BukkitChestInterfaceIdentifier(this), InventoryType.HOPPER, this.config.title());

        for(int i = 0; i < HopperMenu.MAX_HOPPER_INVENTORY_SLOT_COUNT; ++i)
            inventory.setItem(i, this.getIcon(i).toBukkit());

        return inventory;
    }

}
