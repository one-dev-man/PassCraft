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
import org.onedevman.mc.plugins.passcraft.authentication.types.AuthResult;
import org.onedevman.mc.plugins.passcraft.authentication.AuthManager;
import org.onedevman.mc.plugins.passcraft.utils.game.Notify;
import org.onedevman.mc.plugins.passcraft.utils.resources.ConfigParser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegisterCommand implements CommandExecutor {

    public static final String LABEL = "register";

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

    private final TabCompleter _completer = new RegisterCommand.CommandCompleter(this);

    public final AuthManager authmanager;

    //

    public RegisterCommand(AuthManager authentication_manager) {
        this.authmanager = authentication_manager;
    }

    //

    public TabCompleter completer() {return this._completer; }

    //

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, @NotNull String[] args) {
        try {
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
                    this.authmanager.authInterface.openRegister(player);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return true;
            }

            if(args.length < 2)
                return false;

            if(args[0].equalsIgnoreCase("password")) {
                this.authmanager.authenticators.password().register(player, args[1], result -> {
                    String notify_message = null;
                    Sound notify_sound;

                    if (result.equals(AuthResult.SUCCESS)) {
                        notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.REGISTER_SUCCESS);
                        notify_sound = Sound.BLOCK_ENCHANTMENT_TABLE_USE;
                    }
                    else {
                        notify_sound = Sound.BLOCK_NOTE_BLOCK_BASS;

                        if(result.equals(AuthResult.ALREADY_REGISTERED))
                            notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.ALREADY_PASSWORD_REGISTERED);
                        else if(result.equals(AuthResult.INVALID_PASSWORD_FORMAT))
                            notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.INVALID_PASSWORD_FORMAT);
                        if(result.equals(AuthResult.FAILED))
                            notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.REGISTER_FAILED);
                    }

                    Notify.chat(player, notify_message, notify_sound);
                });
            }
            else if(args[0].equalsIgnoreCase("discord")) {
                if(this.authmanager.authenticators.discord().isLinked(player)) {
                    Notify.chat(
                        player,
                        PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_ALREADY_LINKED),
                        Sound.BLOCK_NOTE_BLOCK_BASS
                    );

                    return true;
                }

                String discord_tag = args[1];

                try {
                    if(!this.authmanager.authenticators.password().isRegistered(player)) {
                        Notify.chat(
                            player,
                            PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.REGISTRATION_REQUIRED),
                            Sound.BLOCK_NOTE_BLOCK_BASS
                        );

                        return true;
                    }

                    this.authmanager.authenticators.discord().link(
                        player,
                        discord_tag,
                        () -> Notify.chat(
                            player,
                            ConfigParser.ContextualToken.DISCORD_BOT.parse(
                                PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_REQUEST),
                                this.authmanager.authenticators.discord().botTag()
                            ),
                            Sound.ENTITY_EXPERIENCE_ORB_PICKUP
                        ), result -> {
                            String notify_message = null;
                            Sound notify_sound;

                            if (result.equals(AuthResult.SUCCESS)) {
                                notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_LINKING_SUCCESS);
                                notify_sound = Sound.BLOCK_ENCHANTMENT_TABLE_USE;
                            }
                            else {
                                notify_sound = Sound.BLOCK_NOTE_BLOCK_BASS;

                                if(result.equals(AuthResult.DISCORD_AUTH_DISABLED))
                                    notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_AUTH_DISABLED);
                                else if(result.equals(AuthResult.NOT_LOGGED))
                                    notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.LOGIN_REQUIRED);
                                else if(result.equals(AuthResult.ALREADY_LINKED))
                                    notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_ALREADY_LINKED);
                                else if(result.equals(AuthResult.TAG_ALREADY_USED))
                                    notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_ALREADY_USED);
                                else if(result.equals(AuthResult.DISCORD_USER_NOT_FOUND))
                                    notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_USER_NOT_FOUND);
                                else if(result.equals(AuthResult.TIMED_OUT))
                                    notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_REQUEST_TIMED_OUT);
                                else if(result.equals(AuthResult.REJECTED))
                                    notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_LINKING_REFUSED);
                                else if(result.equals(AuthResult.FAILED))
                                    notify_message = PluginMain.configParser().getString(Locale.CONFIGPATHS.MESSAGES.DISCORD_LINKING_FAILED);
                            }

                            Notify.chat(player, notify_message, notify_sound);
                        }
                    );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else
                return false;

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("§cAn internal error happened. Please contact an administrator to solve the issue.");
            return true;
        }
    }

}
