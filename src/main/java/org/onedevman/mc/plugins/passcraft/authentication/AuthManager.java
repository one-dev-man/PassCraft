package org.onedevman.mc.plugins.passcraft.authentication;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.onedevman.mc.plugins.passcraft.Locale;
import org.onedevman.mc.plugins.passcraft.PluginMain;
import org.onedevman.mc.plugins.passcraft.authentication.authenticators.DiscordAuthenticator;
import org.onedevman.mc.plugins.passcraft.authentication.authenticators.PasswordAuthenticator;
import org.onedevman.mc.plugins.passcraft.authentication.authenticators.PremiumAuthenticator;
import org.onedevman.mc.plugins.passcraft.authentication.chestinterface.AuthInterface;
import org.onedevman.mc.plugins.passcraft.authentication.types.AuthQueue;
import org.onedevman.mc.plugins.passcraft.authentication.types.AuthenticatorRecord;
import org.onedevman.mc.plugins.passcraft.authentication.types.RecentlyDisconnectedCache;
import org.onedevman.mc.plugins.passcraft.database.DatabaseManager;
import org.onedevman.mc.plugins.passcraft.utils.game.Vanisher;
import org.onedevman.mc.plugins.passcraft.utils.resources.ConfigParser;

import java.sql.SQLException;
import java.util.List;

public class AuthManager {

    public static boolean isLogged(AuthQueue authqueue, Player player) {
        return !authqueue.contains(player);
    }

    //

    public final Plugin pluginInstance;

    public final ConfigParser configParser;

    public final DatabaseManager dbmanager;

    public final AuthQueue authqueue = new AuthQueue();
    public final RecentlyDisconnectedCache recentlyDisconnectedCache;

    public final AuthenticatorRecord authenticators;

    public final AuthEventListener authEventListener;

    public final AuthInterface authInterface;
    private boolean _usesAuthInterface;

    //

    public AuthManager(
        Plugin plugin_instance,
        ConfigParser config_parser,
        DatabaseManager dbmanager,
        List<Command> allowed_commands_on_login
    ) throws SQLException {
        this.pluginInstance = plugin_instance;
        this.configParser = config_parser;
        this.dbmanager = dbmanager;

        this.recentlyDisconnectedCache = new RecentlyDisconnectedCache(0);

        this.authenticators = AuthenticatorRecord.of(
            new PremiumAuthenticator(this.authqueue),
            new PasswordAuthenticator(
                this.authqueue,
                this::validatePlayerAuthentication,
                this.dbmanager,
                this.configParser.getString(Locale.CONFIGPATHS.DATABASE.TABLES.PASSWORDS.NAME),
                this.configParser.getString(Locale.CONFIGPATHS.DATABASE.TABLES.PASSWORDS.COLUMNS.USER),
                this.configParser.getString(Locale.CONFIGPATHS.DATABASE.TABLES.PASSWORDS.COLUMNS.PASSWORD)
            ),
            new DiscordAuthenticator(
                this.authqueue,
                this::validatePlayerAuthentication,
                this.dbmanager,
                this.configParser.getString(Locale.CONFIGPATHS.DATABASE.TABLES.DISCORDS.NAME),
                this.configParser.getString(Locale.CONFIGPATHS.DATABASE.TABLES.DISCORDS.COLUMNS.USER),
                this.configParser.getString(Locale.CONFIGPATHS.DATABASE.TABLES.DISCORDS.COLUMNS.DISCORD)
            )
        );

        //

        this.authInterface = new AuthInterface(this);

        //

        this.authEventListener = new AuthEventListener(
            this.pluginInstance,
            this.configParser,
            allowed_commands_on_login,
            this.authqueue,
            this::validatePlayerAuthentication,
            this.recentlyDisconnectedCache,
            this.authenticators
        );
    }

    //

    public boolean usesAuthInterface() { return this._usesAuthInterface; }

    //

    public void start() throws InterruptedException {
        this._usesAuthInterface = this.configParser.getBoolean(Locale.CONFIGPATHS.AUTHENTICATION.INTERFACE.ENABLED);

        //

        this.recentlyDisconnectedCache.delay(this.configParser.getInt(Locale.CONFIGPATHS.AUTHENTICATION.RECONNECTION_DELAY) * 1000L);

        this.authenticators.premium().setEnabled(this.configParser.getBoolean(Locale.CONFIGPATHS.AUTHENTICATION.PREMIUM.ENABLED));
        this.authenticators.premium().start();

        //

        if(PluginMain.configParser().getBoolean(Locale.CONFIGPATHS.AUTHENTICATION.DISCORD.ENABLED)) {
            this.authenticators.discord().setRequestTimeoutDelay(this.configParser.getInt(Locale.CONFIGPATHS.AUTHENTICATION.DISCORD.BOT.REQUESTS_TIMEOUT_DELAY));
            this.authenticators.discord().start(this.configParser.getString(Locale.CONFIGPATHS.AUTHENTICATION.DISCORD.BOT.TOKEN));
        }
        else
            PluginMain.logger().info("Discord authenticator is disabled.");

        this.authEventListener.start(
            this.configParser.getInt(Locale.CONFIGPATHS.AUTHENTICATION.AUTHENTICATION_TIMEOUT_KICK_DELAY),
            this.usesAuthInterface()
        );
    }

    //

    public void stop() throws InterruptedException {
        this.authenticators.premium().stop();
        this.authenticators.discord().stop();

        this.authEventListener.stop();
    }

    //

    private void validatePlayerAuthentication(Player player) {
        this.authqueue.remove(player);

        String associated_join_message = this.authEventListener.joinMessageRegistry.withdraw(player);
        if(associated_join_message != null)
            Bukkit.getServer().broadcastMessage(associated_join_message);

        Vanisher.get().unvanish(player, true);
    }

    //

}
