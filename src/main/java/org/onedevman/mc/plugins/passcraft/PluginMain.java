package org.onedevman.mc.plugins.passcraft;

import com.google.common.base.Enums;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.onedevman.mc.plugins.passcraft.authentication.AuthManager;
import org.onedevman.mc.plugins.passcraft.chestinterface.bukkit.BukkitChestInterfaceEventListener;
import org.onedevman.mc.plugins.passcraft.commands.PassCraftCommand;
import org.onedevman.mc.plugins.passcraft.commands.UnvanishCommand;
import org.onedevman.mc.plugins.passcraft.commands.VanishCommand;
import org.onedevman.mc.plugins.passcraft.commands.authentication.LoginCommand;
import org.onedevman.mc.plugins.passcraft.commands.authentication.RegisterCommand;
import org.onedevman.mc.plugins.passcraft.commands.authentication.UnregisterCommand;
import org.onedevman.mc.plugins.passcraft.commands.db.PassCraftDBCommand;
import org.onedevman.mc.plugins.passcraft.database.DatabaseManager;
import org.onedevman.mc.plugins.passcraft.utils.Lists;
import org.onedevman.mc.plugins.passcraft.utils.PluginLogger;
import org.onedevman.mc.plugins.passcraft.utils.game.Vanisher;
import org.onedevman.mc.plugins.passcraft.utils.resources.ConfigParser;
import org.onedevman.mc.plugins.reflectcraft.ReflectCraft;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class PluginMain extends JavaPlugin {

    private static PluginMain _instance = null;
    public static PluginMain instance() {
        return _instance;
    }

    private static PluginLogger _logger;
    public static PluginLogger logger() { return _logger; }

    //

    public static DedicatedServer nativeServer() throws InvocationTargetException, IllegalAccessException {
        return ReflectCraft.getNativeServer(Bukkit.getServer());
    }

    //

    private static YamlConfiguration _config = null;
    public static YamlConfiguration config() { return _config; };

    private static ConfigParser _config_string_parser = null;
    public static ConfigParser configParser() { return _config_string_parser; };

    private static DatabaseManager _database_manager = null;
    public static DatabaseManager databaseManager() { return _database_manager; };

    private static AuthManager _authmanager = null;
    public static AuthManager authmanager() { return _authmanager; }

    //

    @Override
    public void onEnable() {
        super.onEnable();

        //

        PluginMain._instance = this;

//        PluginMain._logger = this.getLogger();
        PluginMain._logger = new PluginLogger(Locale.PLUGIN_PREFIX);

        //

        PluginMain._config_string_parser = new ConfigParser(this);
        PluginMain.loadConfiguration();

        //

        try {
            PluginMain.setupDBManager();
            //

            PluginMain._authmanager = new AuthManager(
                PluginMain.instance(),
                PluginMain.configParser(),
                PluginMain.databaseManager(),
                List.of(
                    Objects.requireNonNull(this.getCommand(LoginCommand.LABEL)),
                    Objects.requireNonNull(this.getCommand(RegisterCommand.LABEL)),
                    Objects.requireNonNull(this.getCommand(UnregisterCommand.LABEL))
                )
            );

            PluginMain.authmanager().start();
        } catch(SQLException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //

        Bukkit.getServer().getPluginManager().registerEvents(new BukkitChestInterfaceEventListener(), this);

        //

        PluginCommand cmd;



        LoginCommand login_command = new LoginCommand(PluginMain.authmanager());
        if((cmd = this.getCommand(LoginCommand.LABEL)) != null) {
            cmd.setExecutor(login_command);
            cmd.setTabCompleter(login_command.completer());
        }

        RegisterCommand register_command = new RegisterCommand(PluginMain.authmanager());
        if((cmd = this.getCommand(RegisterCommand.LABEL)) != null) {
            cmd.setExecutor(register_command);
            cmd.setTabCompleter(register_command.completer());
        }

        UnregisterCommand unregister_command = new UnregisterCommand(PluginMain.authmanager());
        if((cmd = this.getCommand(UnregisterCommand.LABEL)) != null) {
            cmd.setExecutor(unregister_command);
            cmd.setTabCompleter(unregister_command.completer());
        }

        //

        PassCraftCommand passcraft_config_command = new PassCraftCommand();
        if((cmd = this.getCommand(PassCraftCommand.LABEL)) != null) {
            cmd.setExecutor(passcraft_config_command);
            cmd.setTabCompleter(passcraft_config_command.completer());
        }

        PassCraftDBCommand passcraft_db_command = new PassCraftDBCommand(this, PluginMain.databaseManager());
        if((cmd = this.getCommand(PassCraftDBCommand.LABEL)) != null) {
            cmd.setExecutor(passcraft_db_command);
            cmd.setTabCompleter(passcraft_db_command.completer());
        }

        //

        Vanisher.init(this);

        VanishCommand vanish_command = new VanishCommand(Vanisher.get());
        if((cmd = this.getCommand(VanishCommand.LABEL)) != null) {
            cmd.setExecutor(vanish_command);
            cmd.setTabCompleter(vanish_command.completer());
        }

        UnvanishCommand unvanish_command = new UnvanishCommand(Vanisher.get());
        if((cmd = this.getCommand(UnvanishCommand.LABEL)) != null) {
            cmd.setExecutor(unvanish_command);
            cmd.setTabCompleter(unvanish_command.completer());
        }
    }

    //

    @Override
    public void onDisable() {
        try {
            PluginMain.authmanager().stop();
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

        //

        Vanisher.disable();

        //

        super.onDisable();
    }

    //

    public static void setupDBManager() throws IOException {
        String dbsystem_name = PluginMain.config().getString(Locale.CONFIGPATHS.DATABASE.SYSTEM);

        if(dbsystem_name == null)
            dbsystem_name = DatabaseManager.System.SQLITE.name();

        DatabaseManager.System dbsystem = (DatabaseManager.System) Enums.getIfPresent(DatabaseManager.System.class, dbsystem_name.toUpperCase()).orNull();

        if(dbsystem == null) {
            dbsystem = DatabaseManager.System.DEFAULT;
            PluginMain.logger().warning("Invalid database system found in plugin configuration, switching to default (" + dbsystem.name() + ").");
        }

        if(dbsystem.equals(DatabaseManager.System.SQLITE))
            PluginMain._database_manager = DatabaseManager.forSQLite(PluginMain.configParser().getString(Locale.CONFIGPATHS.DATABASE.SQLITE.PATH));
        else if(dbsystem.equals(DatabaseManager.System.MYSQL)) {
            PluginMain._database_manager = DatabaseManager.forMySQL(
                    PluginMain.config().getString(Locale.CONFIGPATHS.DATABASE.MYSQL.HOSTNAME),
                    PluginMain.config().getInt(Locale.CONFIGPATHS.DATABASE.MYSQL.PORT),
                    PluginMain.config().getString(Locale.CONFIGPATHS.DATABASE.MYSQL.DATABASE_NAME),
                    PluginMain.config().getString(Locale.CONFIGPATHS.DATABASE.MYSQL.USERNAME),
                    PluginMain.config().getString(Locale.CONFIGPATHS.DATABASE.MYSQL.PASSWORD)
            );
        }
    }

    //

    public static void reload() {
        try {
            PluginMain.loadConfiguration();
            PluginMain.setupDBManager();
            PluginMain.authmanager().stop();
            PluginMain.authmanager().start();
        } catch(InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    //

    public static void loadConfiguration() {
        try {
            String config_filename = "config.yml";
            String config_filepath = Paths.get(PluginMain.instance().getDataFolder().getPath(), config_filename).toString();

            Path config_filepath_path = Path.of(config_filepath);

            if(!Files.exists(config_filepath_path)) {
                Files.createDirectories(config_filepath_path.getParent());
                Files.createFile(config_filepath_path);
            }

            PluginMain._config = YamlConfiguration.loadConfiguration(new File(config_filepath));

            InputStream internal_config_file_input_stream = Objects.requireNonNull(PluginMain.class.getClassLoader().getResourceAsStream(config_filename));

            if(!Files.exists(config_filepath_path)) {
                FileOutputStream config_file_output_stream = new FileOutputStream(config_filepath);

                int b;
                while((b = internal_config_file_input_stream.read()) != -1)
                    config_file_output_stream.write(b);

                config_file_output_stream.close();
            }

            internal_config_file_input_stream.close();
            internal_config_file_input_stream = Objects.requireNonNull(PluginMain.class.getClassLoader().getResourceAsStream(config_filename));

            PluginMain.config().setDefaults(YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(internal_config_file_input_stream))));

            List<String> config_keys = Lists.toList(Objects.requireNonNull(PluginMain.config().getDefaults()).getKeys(true));

            String key;
            for (String configKey : config_keys) {
                key = configKey;
                PluginMain.config().set(key, PluginMain.config().get(key));
            }

            PluginMain.saveConfiguration();

            PluginMain.configParser().setConfig(PluginMain.config());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfiguration() {
        try {
            String config_filename = "config.yml";
            String config_filepath = Paths.get(PluginMain.instance().getDataFolder().getPath(), config_filename).toString();

            PluginMain.config().save(new File(config_filepath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
