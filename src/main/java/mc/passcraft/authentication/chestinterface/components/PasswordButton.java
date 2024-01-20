package mc.passcraft.authentication.chestinterface.components;

import org.bukkit.Material;
import mc.passcraft.chestinterface.components.ChestIcon;

public class PasswordButton extends ChestIcon {

    public PasswordButton() {
        super(Material.PAPER, "");
        this.config.setEnchanted(true);
    }

}
