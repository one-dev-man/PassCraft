package org.onedevman.mc.plugins.passcraft.authentication.chestinterface.types;

import org.bukkit.entity.Player;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.AnvilInput;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations.AnvilInputConfig;

import java.util.List;

public class DiscordTagInput extends AnvilInput {

    public DiscordTagInput() {
        super(new AnvilInputConfig(
            "§lDiscord tag input",
            List.of(
                "Enter your discord in the text field",
                "and click on the output item."
            ),
            "",
            false
        ));
    }

}
