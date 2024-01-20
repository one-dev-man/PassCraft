package mc.passcraft.authentication.chestinterface.menus;

import org.bukkit.entity.Player;
import mc.passcraft.authentication.AuthManager;
import mc.passcraft.authentication.chestinterface.components.*;
import mc.passcraft.chestinterface.components.ChestMenu;
import mc.passcraft.chestinterface.components.configurations.ChestMenuConfig;

public class UnregisterMenu extends ChestMenu {

    private final UnregisterMenu _this = this;

    private final AuthManager authmanager;

    public final AuthButtonRecord buttons;
    public final AuthInputRecord inputs;

    //

    public UnregisterMenu(AuthManager authmanager) {
        super(new ChestMenuConfig("§lUnregister Menu", 3, false));

        //

        this.authmanager = authmanager;

        this.buttons = AuthButtonRecord.of(
                new PasswordButton(),
                new DiscordButton()
        );

        this.buttons.password().config.setName("§6§lUnregister§r §7your password");
        this.buttons.password().onClick(event -> {
            if(event.entity() instanceof Player player) {
                player.openInventory(_this.inputs.password().toBukkit());
            }
        });

        this.buttons.discord().config.setName("§b§lUnlink§r §7your §5§lDiscord§r §7account");
        this.buttons.discord().onClick(event -> {
            if(event.entity() instanceof Player player) {
                player.closeInventory();
                player.performCommand("unregister discord");
            }
        });

        this.setIcon(1, 3, this.buttons.discord());
        this.setIcon(1, 5, this.buttons.password());

        //

        this.inputs = AuthInputRecord.of(new PasswordInput(), null);
        this.inputs.password().onSubmit(event -> {
            if(event.entity() instanceof Player player) {
                player.performCommand("unregister password " + event.text());
                player.closeInventory();
            }
        });
    }

}
