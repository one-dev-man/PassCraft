package mc.passcraft.commands.authentication;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import mc.passcraft.Locale;
import mc.passcraft.PluginMain;
import mc.passcraft.authentication.AuthManager;
import mc.passcraft.authentication.types.AuthResult;
import mc.passcraft.utils.game.Notify;
import mc.passcraft.utils.game.Vanisher;
import mc.passcraft.utils.synchronization.Sync;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;

public class UnregisterCommand extends AuthenticationCommand {

    public static final String LABEL = "unregister";

    //

    public UnregisterCommand(AuthManager authmanager) {
        super(authmanager);
    }

    //

    @Override
    public String label() { return UnregisterCommand.LABEL; }

    //

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, @NotNull String[] args) {
        if(!label.equalsIgnoreCase(LABEL))
            return false;

        if(!(sender instanceof Player player)) {
            sender.sendMessage("This command isn't available in the server console.");
            return true;
        }

        if(!sender.hasPermission("passcraft.commands.register")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if(args.length == 0 && this.authmanager.usesAuthInterface()) {
            try {
                this.authmanager.authInterface.openUnregister(player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

        if(args.length >= 2 && args[0].equalsIgnoreCase("password")) {
            this.authmanager.authenticators.password().unregister(player, args[1], result -> {
                try {
                    this.notifyByResult(
                            player, result,
                            Map.of(AuthResult.SUCCESS, Locale.PluginConfig.MESSAGES__UNREGISTER_SUCCESS),
                            Map.of(
                                    AuthResult.NOT_REGISTERED, Locale.PluginConfig.MESSAGES__NOT_PASSWORD_REGISTERED,
                                    AuthResult.INVALID_PASSWORD_FORMAT, Locale.PluginConfig.MESSAGES__INVALID_PASSWORD_FORMAT,
                                    AuthResult.WRONG_PASSWORD, Locale.PluginConfig.MESSAGES__WRONG_PASSWORD,
                                    AuthResult.FAILED, Locale.PluginConfig.MESSAGES__UNREGISTER_FAILED
                            ),
                            notify_case -> PluginMain.configParser().getString(notify_case)
                    );
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                if (result.equals(AuthResult.SUCCESS)) {
                    Vanisher.get().vanish(player, true);

                    Sync.timeout(
                        () -> player.kickPlayer(PluginMain.configParser().getString(Locale.PluginConfig.MESSAGES__UNREGISTER_KICK)),
                        PluginMain.authmanager().configGetter.unregisteredKickDelay() * 20L
                    );
                }
            });
        }
        else if(args.length == 1 && args[0].equalsIgnoreCase("discord")) {
            try {
                if(!this.authmanager.authenticators.password().isRegistered(player)) {
                    Notify.chatError(player, PluginMain.configParser().getString(Locale.PluginConfig.MESSAGES__REGISTRATION_REQUIRED));
                    return true;
                }

                this.authmanager.authenticators.discord().unlink(player, result -> {
                    try {
                        this.notifyByResult(
                            player, result,
                            Map.of(AuthResult.SUCCESS, Locale.PluginConfig.MESSAGES__DISCORD_UNLINKING_SUCCESS),
                            Map.of(
                                AuthResult.DISCORD_AUTH_DISABLED, Locale.PluginConfig.MESSAGES__DISCORD_AUTHENTICATION_DISABLED,
                                AuthResult.NOT_LOGGED, Locale.PluginConfig.MESSAGES__LOGIN_REQUIRED,
                                AuthResult.NOT_LINKED, Locale.PluginConfig.MESSAGES__DISCORD_NOT_LINKED,
                                AuthResult.FAILED, Locale.PluginConfig.MESSAGES__DISCORD_UNLINKING_FAILED
                            ),
                            notify_case -> PluginMain.configParser().getString(notify_case)
                        );
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else
            return false;

        return true;
    }

}
