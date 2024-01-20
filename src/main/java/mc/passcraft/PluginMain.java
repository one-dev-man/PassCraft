package mc.passcraft;

import com.google.common.base.Enums;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import mc.passcraft.authentication.AuthManager;
import mc.passcraft.authentication.SessionServiceProxy;
import mc.passcraft.chestinterface.bukkit.BukkitChestInterfaceEventListener;
import mc.passcraft.commands.PassCraftCommand;
import mc.passcraft.commands.UnvanishCommand;
import mc.passcraft.commands.VanishCommand;
import mc.passcraft.commands.authentication.LoginCommand;
import mc.passcraft.commands.authentication.RegisterCommand;
import mc.passcraft.commands.authentication.UnregisterCommand;
import mc.passcraft.commands.db.PassCraftDBCommand;
import mc.passcraft.database.DatabaseManager;
import mc.passcraft.types.AbstractCommand;
import mc.passcraft.utils.Lists;
import mc.passcraft.utils.PluginLogger;
import mc.passcraft.utils.game.Vanisher;
import mc.passcraft.utils.resources.configuration.ConfigParser;

import java.io.File;
import java.io.IOException;
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

    private static YamlConfiguration _config = null;
    public static YamlConfiguration config() { return _config; };

    private static ConfigParser _config_string_parser = null;
    public static ConfigParser configParser() { return _config_string_parser; };

    private static DatabaseManager _database_manager = null;
    public static DatabaseManager databaseManager() { return _database_manager; };

    private static AuthManager _authmanager = null;
    public static AuthManager authmanager() { return _authmanager; }

    private static SessionServiceProxy _sessionServiceProxy = null;
    public static SessionServiceProxy sessionServiceProxy() { return _sessionServiceProxy; }

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

//            _sessionServiceProxy = SessionServiceProxy.create(NMS.NativeServer.Services.getSessionService());
//
//            PluginMain.sessionServiceProxy().setGameProfileFormatter(profile -> {
//                System.out.println(profile);
//                return profile;
//            });
//
//            NMS.NativeServer.Services.setSessionService(PluginMain.sessionServiceProxy());

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
        } catch(SQLException | IOException | InterruptedException | InvocationTargetException | IllegalAccessException |
                NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        //

        Bukkit.getServer().getPluginManager().registerEvents(new BukkitChestInterfaceEventListener(), this);

        //

        this.registerCommands(
            new LoginCommand(PluginMain.authmanager()),
            new RegisterCommand(PluginMain.authmanager()),
            new UnregisterCommand(PluginMain.authmanager()),

            //

            new PassCraftCommand(),
            new PassCraftDBCommand(this, PluginMain.databaseManager())
        );

        //

        Vanisher.init(this);

        this.registerCommands(
            new VanishCommand(Vanisher.get()),
            new UnvanishCommand(Vanisher.get())
        );
    }

    //

    @Override
    public void onDisable() {
        try {
            PluginMain.authmanager().stop();
        } catch(InterruptedException | NoSuchFieldException | ClassNotFoundException | InvocationTargetException |
                IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        //

        Vanisher.disable();

        //

        super.onDisable();
    }

    //

    public static void setupDBManager() throws IOException {
        String dbsystem_name = PluginMain.configParser().getString(Locale.PluginConfig.DATABASE__SYSTEM);

        if(dbsystem_name == null)
            dbsystem_name = DatabaseManager.System.SQLITE.name();

        DatabaseManager.System dbsystem = (DatabaseManager.System) Enums.getIfPresent(DatabaseManager.System.class, dbsystem_name.toUpperCase()).orNull();

        if(dbsystem == null) {
            dbsystem = DatabaseManager.System.DEFAULT;
            PluginMain.logger().warning("Invalid database system found in plugin configuration, switching to default (" + dbsystem.name() + ").");
        }

        if(dbsystem.equals(DatabaseManager.System.SQLITE))
            PluginMain._database_manager = DatabaseManager.forSQLite(PluginMain.configParser().getString(Locale.PluginConfig.DATABASE__SQLITE__PATH));
        else if(dbsystem.equals(DatabaseManager.System.MYSQL)) {
            PluginMain._database_manager = DatabaseManager.forMySQL(
                PluginMain.configParser().getString(Locale.PluginConfig.DATABASE__MYSQL__HOSTNAME),
                PluginMain.configParser().getInt(Locale.PluginConfig.DATABASE__MYSQL__PORT),
                PluginMain.configParser().getString(Locale.PluginConfig.DATABASE__MYSQL__DATABASE_NAME),
                PluginMain.configParser().getString(Locale.PluginConfig.DATABASE__MYSQL__USERNAME),
                PluginMain.configParser().getString(Locale.PluginConfig.DATABASE__MYSQL__PASSWORD)
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
        } catch(InterruptedException | IOException | NoSuchFieldException | ClassNotFoundException |
                InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
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

//            InputStream internal_config_file_input_stream = Objects.requireNonNull(PluginMain.class.getClassLoader().getResourceAsStream(config_filename));
//
//            if(!Files.exists(config_filepath_path)) {
//                FileOutputStream config_file_output_stream = new FileOutputStream(config_filepath);
//
//                int b;
//                while((b = internal_config_file_input_stream.read()) != -1)
//                    config_file_output_stream.write(b);
//
//                config_file_output_stream.close();
//            }
//
//            internal_config_file_input_stream.close();
//            internal_config_file_input_stream = Objects.requireNonNull(PluginMain.class.getClassLoader().getResourceAsStream(config_filename));

            YamlConfiguration defaultConfig = new YamlConfiguration();

            for(ConfigParser.Path path : Locale.PluginConfig.values()) {
                defaultConfig.set(path.getPath(), path.defaultValue());

                List<String> comments = path.comments();
                if(comments != null) {
                    if(path.areInlineComments()) defaultConfig.setInlineComments(path.getPath(), comments);
                    else defaultConfig.setComments(path.getPath(), comments);
                }
            }

//            PluginMain.config().setDefaults(YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(internal_config_file_input_stream))));
            PluginMain.config().setDefaults(defaultConfig);

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

    //

    public void registerCommands(AbstractCommand ...commands) {
        PluginCommand cmd;

        for(AbstractCommand command : commands) {
            if((cmd = this.getCommand(command.label())) != null) {
                cmd.setExecutor(command);
                cmd.setTabCompleter(command.completer());
            }
        }
    }

}
