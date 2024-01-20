package mc.passcraft.authentication.chestinterface.components;

import java.util.List;

import mc.passcraft.chestinterface.components.AnvilInput;
import mc.passcraft.chestinterface.components.configurations.AnvilInputConfig;

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
