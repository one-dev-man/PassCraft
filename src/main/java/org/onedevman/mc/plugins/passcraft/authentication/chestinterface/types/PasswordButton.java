package org.onedevman.mc.plugins.passcraft.authentication.chestinterface.types;

import org.bukkit.Material;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.ChestIcon;

public class PasswordButton extends ChestIcon {

    public PasswordButton() {
        super(Material.PAPER, "");
        this.config.setEnchanted(true);
    }

}
