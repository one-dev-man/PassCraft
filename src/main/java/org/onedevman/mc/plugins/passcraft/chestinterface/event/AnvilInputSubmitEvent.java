package org.onedevman.mc.plugins.passcraft.chestinterface.event;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.AnvilInput;

public class AnvilInputSubmitEvent extends AnvilInputEvent {

    private final String _text;

    //

    public AnvilInputSubmitEvent(Event bukkit_event, HumanEntity entity, Inventory inventory, AnvilInput anvil_input, String text) {
        super(bukkit_event, entity, inventory, anvil_input);

        this._text = text;
    }

    //

    public String text() { return this._text; }

}
