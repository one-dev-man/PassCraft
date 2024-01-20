package mc.passcraft.chestinterface.components;

import org.bukkit.inventory.ItemStack;
import mc.passcraft.chestinterface.events.ChestInterfaceEvent;
import mc.passcraft.chestinterface.events.ChestInterfaceEventManager;

public abstract class ChestIconInterface<EventType extends ChestInterfaceEvent> extends ChestInterfaceEventManager<EventType> {

    public abstract ItemStack toBukkit();

}
