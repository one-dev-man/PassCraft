package org.onedevman.mc.plugins.passcraft.commands.db;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.onedevman.mc.plugins.passcraft.PluginMain;
import org.onedevman.mc.plugins.passcraft.database.DatabaseManager;
import org.onedevman.mc.plugins.passcraft.utils.Arrays;
import org.onedevman.mc.plugins.passcraft.utils.synchronization.Async;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PassCraftDBCommand implements CommandExecutor {

    public static final String LABEL = "passcraft-db";

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
            List<String> result = new ArrayList<>();

            if(args.length <= 1) {
                result.add("update");
                result.add("query");
            }

            return result;
        }

    }

    //

    private final TabCompleter _completer = new CommandCompleter(this);

    public final Plugin pluginInstance;

    public final DatabaseManager dbmanager;

    //

    public PassCraftDBCommand(Plugin plugin_instance, DatabaseManager dbmanager) {
        this.pluginInstance = plugin_instance;
        this.dbmanager = dbmanager;
    }

    //

    public TabCompleter completer() {return this._completer; }

    //

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!label.equalsIgnoreCase(LABEL))
            return false;

        if(!sender.hasPermission("passcraft.commands.passcraft-db.update") && !sender.hasPermission("passcraft.commands.passcraft-db.query")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if(args.length < 2)
            return false;

        String request_type_name = args[0].toUpperCase();

        boolean request_type_valid = false;

        DatabaseManager.RequestType[] request_types = DatabaseManager.RequestType.values();

        int request_type_i = 0;
        while(request_type_i < request_types.length && !request_type_valid) {
            request_type_valid = request_types[request_type_i].name().equals(request_type_name);
            ++request_type_i;
        }

        if(!request_type_valid)
            return false;

        DatabaseManager.RequestType request_type = DatabaseManager.RequestType.valueOf(request_type_name);

        if(request_type == null)
            return false;

        String request = Arrays.join(args, " ", 1, args.length);

        Async.call(() -> {
            try {
                DatabaseManager.Result result = this.dbmanager.execute(request_type, request);

                if(request_type.equals(DatabaseManager.RequestType.QUERY) && result.rows() == -1) {
                    ResultSet result_set = result.set();

                    if(result_set == null) {
                        result.closer().close();
                        throw new SQLException("An error append during the request execution (see logs).");
                    }

                    PluginMain.logger().info("§8[§7Pass§cCraft§7-DB - §aLOG§8]§r Result for the following request :");
                    PluginMain.logger().info("§8[§7Pass§cCraft§7-DB - §aLOG§8]§r | `" + request + "`");

                    while(result_set.next()) {
                        int column_count = result_set.getMetaData().getColumnCount();

                        PluginMain.logger().info("§8[§7Pass§cCraft§7-DB - §aLOG§8]§r Row n°" + result_set.getRow() + ":");

                        for(int i = 1; i < column_count + 1; ++i) {
                            PluginMain.logger().info(
                                String.format(
                                    "§8[§7Pass§cCraft§7-DB - §aLOG§8]§r | [from `%s`] (%s) `%s` : `%s`",
                                    result_set.getMetaData().getTableName(i),
                                    result_set.getMetaData().getColumnTypeName(i),
                                    result_set.getMetaData().getColumnName(i),
                                    result_set.getObject(i)
                                )
                            );
                        }
                    }
                }

                result.closer().close();

                sender.sendMessage("§8[§7Pass§cCraft§7-DB - §aSUCCESS§8]§r Request successfully executed.");
                sender.sendMessage("§8[§7Pass§cCraft§7-DB - §aSUCCESS§8]§r §eResult printed in the console.");
            } catch (SQLException e) {
                sender.sendMessage("§8[§7Pass§cCraft§7-DB - §cERROR§8]§r " + e.getLocalizedMessage());
                PluginMain.logger().warning(e.getLocalizedMessage());
                e.printStackTrace(System.out);
            }
        });

        return true;
    }

}
