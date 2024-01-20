package mc.passcraft.authentication;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import mc.passcraft.PluginMain;
import mc.passcraft.authentication.authenticators.DiscordAuthenticator;
import mc.passcraft.authentication.authenticators.PasswordAuthenticator;
import mc.passcraft.authentication.authenticators.PremiumAuthenticator;
import mc.passcraft.authentication.chestinterface.AuthInterface;
import mc.passcraft.authentication.events.SessionServiceProxyAuthenticatePlayerEvent;
import mc.passcraft.authentication.events.SessionServiceProxyEventListener;
import mc.passcraft.authentication.types.AuthQueue;
import mc.passcraft.authentication.types.AuthenticatorRecord;
import mc.passcraft.authentication.types.RecentlyDisconnectedCache;
import mc.passcraft.database.DatabaseManager;
import mc.passcraft.nms.NMS;
import mc.passcraft.types.events.EventHandler;
import mc.passcraft.utils.Players;
import mc.passcraft.utils.game.Vanisher;
import mc.passcraft.utils.resources.configuration.ConfigParser;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthManager {

    public static boolean isLogged(AuthQueue authqueue, Player player) {
        return !authqueue.contains(player);
    }

    //

    public final Plugin pluginInstance;

    public final AuthManagerConfigGetter configGetter;

    public final DatabaseManager dbmanager;

    public final AuthQueue authqueue = new AuthQueue();
    public final RecentlyDisconnectedCache recentlyDisconnectedCache;

    public final AuthenticatorRecord authenticators;
    private final SessionServiceProxy sessionServiceProxy;

    public final AuthEventListener authEventListener;

    public final AuthInterface authInterface;
    private boolean _usesAuthInterface;

    //

    public AuthManager(
        Plugin plugin_instance,
        ConfigParser config_parser,
        DatabaseManager dbmanager,
        List<Command> allowed_commands_on_login
    ) throws SQLException, InvocationTargetException, IllegalAccessException {
        this.pluginInstance = plugin_instance;
        this.configGetter = new AuthManagerConfigGetter(config_parser);
        this.dbmanager = dbmanager;

        this.recentlyDisconnectedCache = new RecentlyDisconnectedCache(0);

        this.authenticators = AuthenticatorRecord.of(
            new PremiumAuthenticator(this.authqueue),

            new PasswordAuthenticator(
                this.authqueue,
                this::validatePlayerAuthentication,
                this.dbmanager,
                this.configGetter.password().dbtable(),
                this.configGetter.password().usercolumn(),
                this.configGetter.password().passwordcolumn()
            ),

            new DiscordAuthenticator(
                this.authqueue,
                this::validatePlayerAuthentication,
                this.dbmanager,
                this.configGetter.discord().dbtable(),
                this.configGetter.discord().usercolumn(),
                this.configGetter.discord().discordcolumn()
            )
        );

        this.sessionServiceProxy = SessionServiceProxy.create(NMS.NativeServer.Services.getSessionService());

        this.sessionServiceProxy.addListener(new SessionServiceProxyEventListener() {
            @EventHandler
            public void onPlayerAuthenticate(SessionServiceProxyAuthenticatePlayerEvent event) {
                GameProfile profile = event.getProfile();

                String username = event.getUsername();
                UUID uuid = profile == null ? Players.getOfflineUUID(username) : profile.getId();

                GameProfile resultProfile = new GameProfile(uuid, username);

                if (profile != null) {
                    for (Map.Entry<String, Property> entry : profile.getProperties().entries()) {
                        resultProfile.getProperties().put(entry.getKey(), entry.getValue());
                    }
                }

                PluginMain.logger().info(resultProfile.toString());

                event.setProfile(resultProfile);
            }
        });

        //

        this.authInterface = new AuthInterface(this);

        //

        this.authEventListener = new AuthEventListener(
            this.pluginInstance,
            this.configGetter,
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

    public void start() throws InterruptedException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        this._usesAuthInterface = this.configGetter.interfaceEnabled();

        //

        this.recentlyDisconnectedCache.delay(this.configGetter.reconnectionDelay() * 1000L);

//        this.authenticators.premium().setEnabled(this.configParser.getBoolean(Locale.CONFIGPATHS.AUTHENTICATION.PREMIUM.ENABLED));
//        this.authenticators.premium().start();

        //

        if(this.configGetter.discord().enabled()) {
            this.authenticators.discord().setRequestTimeoutDelay(this.configGetter.discord().requestTimeoutDelay());
            this.authenticators.discord().start(this.configGetter.discord().bottoken());
        }
        else
            PluginMain.logger().info("Discord authenticator is disabled.");

        this.authEventListener.start(this.configGetter.timeoutKickDelay(), this.usesAuthInterface());

        //

        // /!\ NMS MinecraftSessionService is an important service used to authenticate players.
        // Replacing this value, especially using the Unsafe tool is critical.
        // The original service should be stored during all the plugin activity and restored when disabled.
        NMS.NativeServer.Services.setSessionService(this.sessionServiceProxy);
    }

    //

    public void stop() throws InterruptedException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
//        this.authenticators.premium().stop();
        this.authenticators.discord().stop();

        this.authEventListener.stop();

        //

        // Original NMS MinecraftSessionService should be restored to avoid some issues,
        // especially when reloading the server.
        NMS.NativeServer.Services.setSessionService(this.sessionServiceProxy.getOriginalSessionService());
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
