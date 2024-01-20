package mc.passcraft.types;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public abstract class AbstractCommand implements CommandExecutor {

    public abstract String label();

    public abstract TabCompleter completer();

    //

    public abstract boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args);

}
