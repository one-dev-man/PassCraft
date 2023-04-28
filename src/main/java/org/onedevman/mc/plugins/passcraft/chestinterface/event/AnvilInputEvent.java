package org.onedevman.mc.plugins.passcraft.chestinterface.event;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.AnvilInput;

public class AnvilInputEvent extends ChestInterfaceEvent {

    private final AnvilInput _anvil_input;

    //

    public AnvilInputEvent(Event bukkit_event, HumanEntity entity, Inventory inventory, AnvilInput anvil_input) {
        super(bukkit_event, entity, inventory, anvil_input);

        this._anvil_input = anvil_input;
    }

    //

    public AnvilInput anvilInput() { return this._anvil_input; }

}
