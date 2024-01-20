package mc.passcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import mc.passcraft.Locale;
import mc.passcraft.PluginMain;
import mc.passcraft.types.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public class PassCraftCommand extends AbstractCommand {

    public static final String LABEL = "passcraft";

    //

    private final TabCompleter completer = (@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) -> {
        List<String> result = new ArrayList<>();

        if(args.length <= 1) {
            result.add("reload");
            result.add("config");
        }
        else if(args[0].equalsIgnoreCase("config")) {
            if(args.length == 2) {
                result.add("get");
                result.add("set");
            }
            else if(args.length == 3) {
                for(String config_key : PluginMain.configParser().config().getKeys(true))
                    result.add(config_key.startsWith(".") ? config_key.substring(1) : config_key);
            }
        }

        return result;
    };

    //

    public PassCraftCommand() {}

    //

    @Override
    public String label() { return PassCraftCommand.LABEL; }

    @Override
    public TabCompleter completer() {return this.completer; }

    //

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, @NotNull String[] args) {
        if(!label.equalsIgnoreCase(LABEL)) return false;

        if(!sender.hasPermission("passcraft.commands.passcraft-config")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if(args.length < 1) return false;

        if(args[0].equalsIgnoreCase("reload")) {
            PluginMain.reload();
            sender.sendMessage(Locale.PLUGIN_PREFIX + " §aPlugin reloaded !");
        }
        else if(args[0].equalsIgnoreCase("config") && args.length >= 3) {
            if(args[1].equalsIgnoreCase("get") && args.length == 3) {
                String value = PluginMain.configParser().get(args[0]);

                if(value == null)
                    sender.sendMessage(Locale.PLUGIN_PREFIX + " This key isn't defined.");
                else
                    sender.sendMessage(Locale.PLUGIN_PREFIX + " " + value);
            }
            else if(args[1].equalsIgnoreCase("set") && args.length == 4) {
                String key = args[2];
                String value = args[3];

                try {
                    PluginMain.config().set(key, value);
                    PluginMain.saveConfiguration();

                    sender.sendMessage(Locale.PLUGIN_PREFIX + " §aKey §e" + key + "§a successfully set to §e" + value + "§a in plugin configuration.");
                } catch (Exception e) {
                    sender.sendMessage(Locale.PLUGIN_PREFIX + " §cAn error happened while trying to set §e" + key + "§c to §e" + value + "§c in plugin configuration.");
                }
            }
            else
                return false;
        }
        else
            return false;

        return true;
    }

}
