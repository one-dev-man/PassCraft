package mc.passcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import mc.passcraft.types.AbstractCommand;
import mc.passcraft.utils.game.Notify;
import mc.passcraft.utils.game.Vanisher;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand extends AbstractCommand {

    public static final String LABEL = "vanish";

    //

    private final TabCompleter completer = (CommandSender sender, Command cmd, String label, String[] args) -> {
        if(label.equalsIgnoreCase(LABEL)) {
            List<String> result = new ArrayList<>();

            if(args.length > 0) {
                if(args.length < 2)
                    result.add("phantom");

                if(args.length < 3 && (args.length < 2 || args[0].equalsIgnoreCase("phantom"))) {
                    for(Player p : Bukkit.getServer().getOnlinePlayers())
                        result.add(p.getName());
                }
            }

            return result;
        }
        else
            return null;
    };

    private final Vanisher vanisher;

    //

    public VanishCommand(Vanisher vanisher) {
        this.vanisher = vanisher;
    }

    //

    @Override
    public String label() { return VanishCommand.LABEL; }

    @Override
    public TabCompleter completer() { return this.completer; }

    public Vanisher vanisher() {return this.vanisher; }

    //

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!label.equalsIgnoreCase(LABEL))
            return false;

        if(!(sender instanceof Player player)) {
            sender.sendMessage("This command isn't available in the server console.");
            return true;
        }

        boolean phantom_mode = args.length > 0 && args[0].equalsIgnoreCase("phantom");

        if(
            !sender.hasPermission("passcraft.commands.vanish")
            || (phantom_mode && !sender.hasPermission("passcraft.commands.vanish.phantom"))
        ) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        Player other = args.length > 0 ? args.length > 1 ? Bukkit.getPlayer(args[1]) : Bukkit.getPlayer(args[0]) : null;

        if(
            other != null
            && (
                !sender.hasPermission("passcraft.commands.vanish.other")
                || (phantom_mode && !sender.hasPermission("passcraft.commands.vanish.phantom.other"))
            )
        ) {
            sender.sendMessage("§cYou don't have permission to use this command on another player.");
            return true;
        }

        if(
            (args.length > 0 && !args[0].equalsIgnoreCase("phantom") && other == null)
            || (args.length > 1 && args[0].equalsIgnoreCase("phantom") && other == null)
        ) {
            Notify.chat(player, "§cPlayer §b" + args[0] + "§c not found.", Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return true;
        }
        else if(args.length > 1 && !args[0].equalsIgnoreCase("phantom")) {
            Notify.chat(player, "§cSyntax error : first argument must be §5phantom§c or a player name.", Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return true;
        }

        Player dest = other == null ? player : other;

        if(this.vanisher().vanished(dest)) {
            boolean is_phantom = this.vanisher().phantomized(dest);
            if(phantom_mode && is_phantom) {
                Notify.chat(player, "§eIt seems that " + (dest == player ? "you're" : "§b" + dest.getName() + "§e") + " already vanished in phantom mode. Please use §6/unvanish§e to turn it off.", Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                return true;
            }
            else if(!phantom_mode && !is_phantom) {
                Notify.chat(player, "§eIt seems that " + (dest == player ? "you're" : "§b" + dest.getName() + "§e") + " already vanished. Please use §6/unvanish§e to turn it off.", Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                return true;
            }

            this.vanisher().unvanish(dest, true);
        }

        this.vanisher().vanish(dest, phantom_mode);

        Notify.chat(dest, "§aWoosh ! You " + (phantom_mode ? "become a phantom" : "disappeared") + "", Sound.ENTITY_ENDERMAN_TELEPORT);
        if(dest == other)
            Notify.chat(player, "§aPlayer §b" + dest.getName() + "§a vanished" + (phantom_mode ? " in phantom mode" : "") + ".", Sound.ENTITY_ENDERMAN_TELEPORT);

        return true;
    }

}
