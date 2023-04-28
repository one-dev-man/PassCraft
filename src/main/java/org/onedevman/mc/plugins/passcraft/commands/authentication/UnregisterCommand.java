package org.onedevman.mc.plugins.passcraft.commands.authentication;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.onedevman.mc.plugins.passcraft.Locale;
import org.onedevman.mc.plugins.passcraft.PluginMain;
import org.onedevman.mc.plugins.passcraft.authentication.AuthManager;
import org.onedevman.mc.plugins.passcraft.authentication.types.AuthResult;
import org.onedevman.mc.plugins.passcraft.utils.game.Notify;
import org.onedevman.mc.plugins.passcraft.utils.game.Vanisher;
import org.onedevman.mc.plugins.passcraft.utils.synchronization.Sync;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnregisterCommand implements CommandExecutor {

    public static final String LABEL = "unregister";

    //

    private static class CommandCompleter implements TabCompleter {

        private final CommandExecutor _command;

        //

        public CommandCompleter(CommandExecutor command) {
            this._command = command;
        }

        //

        public CommandExecutor command() { return this._command; }

        //

        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
            List<String> result = new ArrayList<>();

            if(args.length <= 1) {
                result.add("password");
                result.add("discord");
            }

            return result;
        }

    }

    //

    private final TabCompleter _completer = new UnregisterCommand.CommandCompleter(this);

    public final AuthManager authmanager;

    //

    public UnregisterCommand(AuthManager authentication_manager) {
        this.authmanager = authentication_manager;
    }

    //

    public TabCompleter completer() {return this._completer; }

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
                String notify_message = null;
                Sound notify_sound;

                if (result.equals(AuthResult.SUCCESS)) {
                    notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.UNREGISTER_SUCCESS);
                    notify_sound = Sound.BLOCK_ENCHANTMENT_TABLE_USE;

                    Vanisher.get().vanish(player, true);

                    Sync.timeout(
                        () -> player.kickPlayer(PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.UNREGISTER_KICK)),
                        PluginMain.configParser().getLong(Locale.CONFIGPATHS.AUTHENTICATION.UNREGISTERED_KICK_DELAY) * 20L
                    );
                } else {
                    notify_sound = Sound.BLOCK_NOTE_BLOCK_BASS;

                    if(result.equals(AuthResult.NOT_REGISTERED))
                        notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.NOT_PASSWORD_REGISTERED);
                    else if(result.equals(AuthResult.INVALID_PASSWORD_FORMAT))
                        notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.INVALID_PASSWORD_FORMAT);
                    else if(result.equals(AuthResult.WRONG_PASSWORD))
                        notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.WRONG_PASSWORD);
                    else if(result.equals(AuthResult.FAILED))
                        notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.UNREGISTER_FAILED);
                }

                Notify.chat(player, notify_message, notify_sound);
            });
        }
        else if(args.length == 1 && args[0].equalsIgnoreCase("discord")) {
            try {
                if(!this.authmanager.authenticators.password().isRegistered(player)) {
                    Notify.chat(
                        player,
                        PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.REGISTRATION_REQUIRED),
                        Sound.BLOCK_NOTE_BLOCK_BASS
                    );

                    return true;
                }

                this.authmanager.authenticators.discord().unlink(player, result -> {
                    String notify_message = null;
                    Sound notify_sound;

                    if (result.equals(AuthResult.SUCCESS)) {
                        notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_UNLINKING_SUCCESS);
                        notify_sound = Sound.BLOCK_ENCHANTMENT_TABLE_USE;
                    }
                    else {
                        notify_sound = Sound.BLOCK_NOTE_BLOCK_BASS;

                        if(result.equals(AuthResult.DISCORD_AUTH_DISABLED))
                            notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_AUTH_DISABLED);
                        else if(result.equals(AuthResult.NOT_LOGGED))
                            notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.LOGIN_REQUIRED);
                        else if(result.equals(AuthResult.NOT_LINKED))
                            notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_NOT_LINKED);
                        else if(result.equals(AuthResult.FAILED))
                            notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_UNLINKING_FAILED);
                    }

                    Notify.chat(player, notify_message, notify_sound);
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
