package org.onedevman.mc.plugins.passcraft.chestinterface.components;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.onedevman.mc.plugins.passcraft.chestinterface.bukkit.BukkitChestInterfaceIdentifier;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations.ChestMenuConfig;
import org.onedevman.mc.plugins.passcraft.chestinterface.event.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChestMenu extends ChestInterface<ChestMenuEvent> {

    private final ChestMenu _this = this;

    public final ChestMenuConfig config;

    private final Map<Integer, ChestIcon> _icons = new HashMap<>();

    //

    public ChestMenu(String title, int rows) {
        this(title, rows, false);
    }

    public ChestMenu(String title, int rows, boolean silent) {
        this(new ChestMenuConfig(title, rows, silent));
    }

    public ChestMenu(ChestMenuConfig config) {
        this.config = config;

        //

        this.addListener(new ChestInterfaceEventListener() {
            @ChestInterfaceEventHandler
            public void onClose(ChestMenuCloseEvent event) {
                if(event.entity() instanceof Player && !_this.config.silent())
                    ChestInterface.playInteractionSound((Player) event.entity());
            }

            @ChestInterfaceEventHandler
            public void onClick(ChestMenuClickEvent event) {
                if(event.entity() instanceof Player && !_this.config.silent() && _this.getIcon(event.slot()) != null)
                    ChestInterface.playInteractionSound((Player) event.entity());
            }
        });
    }

    //

    public Map<Integer, ChestIcon> icons() { return Collections.unmodifiableMap(this._icons); }

    //

    public boolean isSlotEmpty(int slot) {
        if(this._icons.containsKey(slot))
            return false;

        return this._icons.get(slot) == null;
    }

    //

    public ChestIcon getIcon(int slot) {
        if(this.isSlotEmpty(slot))
            return null;

        return this._icons.get(slot);
    }

    //

    public ChestMenu setIcon(int row, int col, ChestIcon icon) {
        return this.setIcon(9*row+col, icon);
    }

    public ChestMenu setIcon(int slot, ChestIcon icon) {
        if(!this.isSlotEmpty(slot))
            this._icons.remove(slot);

        this._icons.put(slot, icon);

        return this;
    }

    //

    public ChestMenu removeIcon(int slot) {
        this._icons.remove(slot);

        return this;
    }

    //

    @Override
    public Inventory toBukkit() {
        Inventory inventory = Bukkit.createInventory(new BukkitChestInterfaceIdentifier(this), this.config.rows() * 9, this.config.title());

        ChestIcon icon;
        for(int i = 0; i < this.config.rows()*9; ++i) {
            icon = this.getIcon(i);

            if(icon != null)
                inventory.setItem(i, icon.toBukkit());
        }

        return inventory;
    }

}
