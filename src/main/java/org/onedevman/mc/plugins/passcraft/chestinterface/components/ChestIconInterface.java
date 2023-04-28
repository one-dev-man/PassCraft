package org.onedevman.mc.plugins.passcraft.chestinterface.components;

import org.bukkit.inventory.ItemStack;
import org.onedevman.mc.plugins.passcraft.chestinterface.event.ChestInterfaceEvent;
import org.onedevman.mc.plugins.passcraft.chestinterface.event.ChestInterfaceEventManager;

public abstract class ChestIconInterface<EventType extends ChestInterfaceEvent> extends ChestInterfaceEventManager<EventType> {

    public abstract ItemStack toBukkit();

}
