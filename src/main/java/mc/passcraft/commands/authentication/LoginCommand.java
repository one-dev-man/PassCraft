package mc.passcraft.commands.authentication;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import mc.passcraft.Locale;
import mc.passcraft.PluginMain;
import mc.passcraft.authentication.AuthManager;
import mc.passcraft.authentication.types.AuthResult;
import mc.passcraft.utils.game.Notify;
import mc.passcraft.utils.resources.configuration.ConfigParser;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;

public class LoginCommand extends AuthenticationCommand {

    public static final String LABEL = "login";

    //

    public LoginCommand(AuthManager authmanager) {
        super(authmanager);
    }

    //

    @Override
    public String label() { return LoginCommand.LABEL; }

    //

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, @NotNull String[] args) {
        if(!label.equalsIgnoreCase(LABEL))
            return false;

        if(!(sender instanceof Player player)) {
            sender.sendMessage("This command isn't available in the server console.");
            return true;
        }

        if(!sender.hasPermission("passcraft.commands.login")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if(AuthManager.isLogged(this.authmanager.authqueue, player)) {
            Notify.chat(
                player,
                PluginMain.configParser().getString(Locale.PluginConfig.MESSAGES__ALREADY_LOGGED),
                Sound.BLOCK_NOTE_BLOCK_BASS
            );

            return true;
        }


        if(args.length == 0 && this.authmanager.usesAuthInterface()) {
            try {
                this.authmanager.authInterface.openLogin(player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

        if(args.length >= 2 && args[0].equalsIgnoreCase("password")) {
            this.authmanager.authenticators.password().login(player, args[1], result -> {
                try {
                    this.notifyByResult(
                        player, result,
                        Map.of(AuthResult.SUCCESS, Locale.PluginConfig.MESSAGES__LOGIN_SUCCESS),
                        Map.of(
                            AuthResult.ALREADY_LOGGED, Locale.PluginConfig.MESSAGES__ALREADY_LOGGED,
                            AuthResult.INVALID_PASSWORD_FORMAT, Locale.PluginConfig.MESSAGES__INVALID_PASSWORD_FORMAT,
                            AuthResult.WRONG_PASSWORD, Locale.PluginConfig.MESSAGES__WRONG_PASSWORD,
                            AuthResult.NOT_REGISTERED, Locale.PluginConfig.MESSAGES__REGISTRATION_REQUIRED,
                            AuthResult.FAILED, Locale.PluginConfig.MESSAGES__LOGIN_FAILED
                        ),
                        notify_case -> PluginMain.configParser().getString(notify_case)
                    );
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else if(args.length == 1 && args[0].equalsIgnoreCase("discord")) {
            try {
                if(!this.authmanager.authenticators.password().isRegistered(player)) {
                    Notify.chatError(player, PluginMain.configParser().getString(Locale.PluginConfig.MESSAGES__REGISTRATION_REQUIRED));
                    return true;
                }

                this.authmanager.authenticators.discord().login(player, () -> Notify.chatSuccess(
                    player,
                    ConfigParser.ContextualToken.DISCORD_BOT.parse(
                        PluginMain.configParser().getString(Locale.PluginConfig.MESSAGES__DISCORD_REQUEST),
                        this.authmanager.authenticators.discord().botTag()
                    )
                ), result -> {
                    try {
                        this.notifyByResult(
                            player, result,
                            Map.of(AuthResult.SUCCESS, Locale.PluginConfig.MESSAGES__LOGIN_SUCCESS),
                            Map.of(
                                AuthResult.DISCORD_AUTH_DISABLED, Locale.PluginConfig.MESSAGES__DISCORD_AUTHENTICATION_DISABLED,
                                AuthResult.NOT_LINKED, Locale.PluginConfig.MESSAGES__DISCORD_NOT_LINKED,
                                AuthResult.ALREADY_LOGGED, Locale.PluginConfig.MESSAGES__ALREADY_LOGGED,
                                AuthResult.DISCORD_USER_NOT_FOUND, Locale.PluginConfig.MESSAGES__DISCORD_USER_NOT_FOUND,
                                AuthResult.TIMED_OUT, Locale.PluginConfig.MESSAGES__DISCORD_REQUEST_TIMED_OUT,
                                AuthResult.REJECTED, Locale.PluginConfig.MESSAGES__DISCORD_LINKING_REFUSED,
                                AuthResult.FAILED, Locale.PluginConfig.MESSAGES__DISCORD_LINKING_FAILED
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
