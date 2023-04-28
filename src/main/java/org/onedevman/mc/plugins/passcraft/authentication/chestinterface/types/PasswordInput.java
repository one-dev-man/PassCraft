package org.onedevman.mc.plugins.passcraft.authentication.chestinterface.types;

import org.onedevman.mc.plugins.passcraft.chestinterface.components.AnvilInput;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations.AnvilInputConfig;

import java.util.List;

public class PasswordInput extends AnvilInput {

    public PasswordInput() {
        super(new AnvilInputConfig(
            "§lPassword input",
            List.of(
                "Enter your password in the text field",
                "and click on the output item."
            ),
            "",
            false
        ));
    }

}
