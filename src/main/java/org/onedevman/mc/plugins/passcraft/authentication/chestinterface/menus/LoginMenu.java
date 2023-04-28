package org.onedevman.mc.plugins.passcraft.authentication.chestinterface.menus;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.onedevman.mc.plugins.passcraft.Locale;
import org.onedevman.mc.plugins.passcraft.authentication.AuthManager;
import org.onedevman.mc.plugins.passcraft.authentication.chestinterface.types.*;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.ChestMenu;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations.ChestMenuConfig;
import org.onedevman.mc.plugins.passcraft.utils.game.Notify;

import java.sql.SQLException;

public class LoginMenu extends ChestMenu {

    private final LoginMenu _this = this;

    private final AuthManager authmanager;

    public final AuthButtonRecord buttons;
    public final AuthInputRecord inputs;

    //

    public LoginMenu(AuthManager authmanager) {
        super(new ChestMenuConfig("§lLogin Menu", 3, false));

        //

        this.authmanager = authmanager;

        this.buttons = AuthButtonRecord.of(
            new PasswordButton(),
            new DiscordButton()
        );

        this.buttons.password().config.setName("§a§lLogin§r §7with a password");
        this.buttons.password().onClick(event -> {
            if(event.entity() instanceof Player player) {
                try {
                    if (!this.authmanager.authenticators.password().isRegistered(player))
                        Notify.chat(
                            player,
                            this.authmanager.configParser.getString(Locale.CONFIGPATHS.MESSAGES.REGISTRATION_REQUIRED),
                            Sound.BLOCK_NOTE_BLOCK_BASS
                        );
                    else
                        player.openInventory(_this.inputs.password().toBukkit());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.buttons.discord().config.setName("§a§lLogin§r §7with your §5§lDiscord§r §7account");
        this.buttons.discord().onClick(event -> {
            if(event.entity() instanceof Player player) {
                player.closeInventory();
                player.performCommand("login discord");
            }
        });

        this.setIcon(1, 3, this.buttons.discord());
        this.setIcon(1, 5, this.buttons.password());

        //

        this.inputs = AuthInputRecord.of(new PasswordInput(), null);
        this.inputs.password().onSubmit(event -> {
            if(event.entity() instanceof Player player) {
                player.performCommand("login password " + event.text());
                player.closeInventory();
            }
        });
    }

    //

}
