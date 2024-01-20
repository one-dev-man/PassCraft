package mc.passcraft.authentication.chestinterface.components;

import java.util.List;

import mc.passcraft.chestinterface.components.AnvilInput;
import mc.passcraft.chestinterface.components.configurations.AnvilInputConfig;

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
