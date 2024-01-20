package mc.passcraft.authentication.chestinterface.menus;

import java.sql.SQLException;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import mc.passcraft.Locale;
import mc.passcraft.authentication.AuthManager;
import mc.passcraft.authentication.chestinterface.components.AuthButtonRecord;
import mc.passcraft.authentication.chestinterface.components.AuthInputRecord;
import mc.passcraft.authentication.chestinterface.components.DiscordButton;
import mc.passcraft.authentication.chestinterface.components.DiscordTagInput;
import mc.passcraft.authentication.chestinterface.components.PasswordButton;
import mc.passcraft.authentication.chestinterface.components.PasswordInput;
import mc.passcraft.chestinterface.components.ChestMenu;
import mc.passcraft.chestinterface.components.configurations.ChestMenuConfig;
import mc.passcraft.utils.game.Notify;

public class RegisterMenu extends ChestMenu {

    private final RegisterMenu _this = this;

    private final AuthManager authmanager;

    public final AuthButtonRecord buttons;
    public final AuthInputRecord inputs;

    //

    public RegisterMenu(AuthManager authmanager) {
        super(new ChestMenuConfig("§lRegister Menu", 3, false));

        //

        this.authmanager = authmanager;

        this.buttons = AuthButtonRecord.of(
            new PasswordButton(),
            new DiscordButton()
        );

        this.buttons.password().config.setName("§6§lRegister§r §7with a password");
        this.buttons.password().onClick(event -> {
            if(event.entity() instanceof Player player) {
                try {
                    if(this.authmanager.authenticators.password().isRegistered(player))
                        Notify.chat(
                            player,
                            this.authmanager.configGetter.configParser().getString(Locale.PluginConfig.MESSAGES__ALREADY_PASSWORD_REGISTERED),
                            Sound.BLOCK_NOTE_BLOCK_BASS
                        );
                    else
                        player.openInventory(_this.inputs.password().toBukkit());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.buttons.discord().config.setName("§b§lLink§r §7your §5§lDiscord§r §7account");
        this.buttons.discord().onClick(event -> {
            if(event.entity() instanceof Player player) {
                try {
                    if(!AuthManager.isLogged(this.authmanager.authqueue, player))
                        Notify.chat(
                            player,
                            this.authmanager.configGetter.configParser().getString(Locale.PluginConfig.MESSAGES__LOGIN_REQUIRED),
                            Sound.BLOCK_NOTE_BLOCK_BASS
                        );
                    else if(this.authmanager.authenticators.discord().isLinked(player))
                        Notify.chat(
                            player,
                            this.authmanager.configGetter.configParser().getString(Locale.PluginConfig.MESSAGES__DISCORD_ALREADY_LINKED),
                            Sound.BLOCK_NOTE_BLOCK_BASS
                        );
                    else
                        player.openInventory(_this.inputs.discordtag().toBukkit());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.setIcon(1, 3, this.buttons.discord());
        this.setIcon(1, 5, this.buttons.password());

        //

        this.inputs = AuthInputRecord.of(new PasswordInput(), new DiscordTagInput());

        this.inputs.password().onSubmit(event -> {
            if(event.entity() instanceof Player player) {
                player.performCommand("register password "+event.text());
                player.closeInventory();
            }
        });

        this.inputs.discordtag().onSubmit(event -> {
            if(event.entity() instanceof Player player) {
                player.performCommand("register discord " + event.text());
                player.closeInventory();
            }
        });
    }

    //

}
