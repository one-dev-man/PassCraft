package org.onedevman.mc.plugins.passcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.onedevman.mc.plugins.passcraft.utils.game.Notify;
import org.onedevman.mc.plugins.passcraft.utils.game.Vanisher;

import java.util.ArrayList;
import java.util.List;

public class UnvanishCommand implements CommandExecutor {

    public static final String LABEL = "unvanish";

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
        public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
            if(!label.equalsIgnoreCase(LABEL))
                return null;

            List<String> result = new ArrayList<>();

            if(args.length > 0) {
                if(args.length < 2)
                    result.add("force");

                if(args.length < 3 && (args.length < 2 || args[0].equalsIgnoreCase("force"))) {
                    for(Player p : Bukkit.getServer().getOnlinePlayers())
                        result.add(p.getName());
                }
            }

            return result;
        }

    }

    //

    private final TabCompleter _completer = new CommandCompleter(this);

    private final Vanisher _vanisher;

    //

    public UnvanishCommand(Vanisher vanisher) {
        this._vanisher = vanisher;
    }

    //

    public TabCompleter completer() {return this._completer; }

    public Vanisher vanisher() { return this._vanisher; }

    //

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!label.equalsIgnoreCase(LABEL))
            return false;

        if(!(sender instanceof Player player)) {
            sender.sendMessage("This command isn't available in the server console.");
            return true;
        }

        if(!sender.hasPermission("passcraft.commands.unvanish")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        Player other = args.length > 0 ? args.length > 1 ? Bukkit.getPlayer(args[1]) : Bukkit.getPlayer(args[0]) : null;

        if(other != null && !sender.hasPermission("passcraft.commands.vanish.other")) {
            sender.sendMessage("§cYou don't have permission to use this command on another player.");
            return true;
        }

        if(
            (args.length > 0 && !args[0].equalsIgnoreCase("force") && other == null)
            || (args.length > 1 && args[0].equalsIgnoreCase("force") && other == null)
        ) {
            Notify.chat(player, "§cPlayer §b" + args[0] + "§c not found.", Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return true;
        }
        else if(args.length > 1 && !args[0].equalsIgnoreCase("force")) {
            Notify.chat(player, "§cSyntax error : first argument must be §5force§c or a player name.", Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return true;
        }

        Player dest = other == null ? player : other;

        boolean force_unvanish = args.length > 0 && args[0].equalsIgnoreCase("force");

        if(!this.vanisher().vanished(dest) && !force_unvanish) {
            Notify.chat(player, "§eIt seems that " + (dest == player ? "you're" : "§b" + dest.getName() + "§e is") + " not vanished. Please use §6/vanish§e to turn it on or use §6/unvanish §5force§e to force it.", Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return true;
        }

        if(force_unvanish)
            this.vanisher().vanish(dest, true);

        this.vanisher().unvanish(dest, force_unvanish);

        Notify.chat(dest, "§aOh, hello there", Sound.ENTITY_ENDERMAN_TELEPORT);
        if(dest == other)
            Notify.chat(player, "§aPlayer §b" + dest.getName() + "§a" + (force_unvanish ? " forcefully " : " ") + "unvanished.", Sound.ENTITY_ENDERMAN_TELEPORT);

        return true;
    }

}
