package mc.passcraft.authentication;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.Plugin;
import mc.passcraft.Locale;
import mc.passcraft.PluginMain;
import mc.passcraft.authentication.types.AuthQueue;
import mc.passcraft.authentication.types.AuthenticatorRecord;
import mc.passcraft.authentication.types.JoinMessageRegistry;
import mc.passcraft.authentication.types.RecentlyDisconnectedCache;
import mc.passcraft.utils.game.Notify;
import mc.passcraft.utils.game.Vanisher;
import mc.passcraft.utils.synchronization.Sync;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class AuthEventListener implements Listener {

    private final Plugin pluginInstance;

    private final AuthManagerConfigGetter configGetter;

    private final List<Command> allowedCommandsOnLogin;
    private final AuthenticatorRecord authenticators;
    private final AuthQueue authqueue;
    private final Consumer<Player> validateAuthentication;
    private final RecentlyDisconnectedCache recentlyDisconnectedCache;

    //

    private int _authenticationTimeoutKickDelay;

    private boolean usesAuthInterface;

    //

    public final JoinMessageRegistry joinMessageRegistry = new JoinMessageRegistry();

    //

    public AuthEventListener(
        Plugin plugin_instance,
        AuthManagerConfigGetter configGetter,
        List<Command> allowed_commands_on_login,
        AuthQueue authqueue,
        Consumer<Player> validate_authentication,
        RecentlyDisconnectedCache recently_disconnected_cache,
        AuthenticatorRecord authenticators
    ) {
        this.pluginInstance = plugin_instance;

        this.configGetter = configGetter;

        this.allowedCommandsOnLogin = allowed_commands_on_login;

        this.authqueue = authqueue;
        this.validateAuthentication = validate_authentication;
        this.recentlyDisconnectedCache = recently_disconnected_cache;

        this.authenticators = authenticators;
    }

    //

    public void start(int authentication_timeout_kick_delay, boolean uses_authentication_interface) {
        this._authenticationTimeoutKickDelay = authentication_timeout_kick_delay;
        this.usesAuthInterface = uses_authentication_interface;

        Bukkit.getServer().getPluginManager().registerEvents(this, this.pluginInstance);
    }

    public void stop() {

    }

    //

    public int authenticationTimeoutKickDelay() {
        return this._authenticationTimeoutKickDelay;
    }

    //

    public void notifyPlayerToLogin(Player player) {
        this.notifyPlayerToLogin(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
    }

    public void notifyPlayerToLogin(Player player, Sound notification_sound) {
        Notify.chat(player, PluginMain.configParser().getString(Locale.PluginConfig.MESSAGES__MUST_AUTHENTICATE_NOTIFICATION), notification_sound);
    }

    //

    public boolean isCommandAllowedOnLogin(String label) {
        boolean result = false;

        int i = 0;
        int command_count = this.allowedCommandsOnLogin.size();
        while(i < command_count && !result) {
            Command command = this.allowedCommandsOnLogin.get(i);
            result = command.getLabel().equalsIgnoreCase(label) || command.getAliases().contains(label);
            ++i;
        }

        return result;
    }

    //

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) throws InvocationTargetException, IllegalAccessException {
        Player player = event.getPlayer();

        if(!AuthManager.isLogged(this.authqueue, player)) {
            this.joinMessageRegistry.store(player, event.getJoinMessage());
            event.setJoinMessage(null);

            Vanisher.get().vanish(player, true);

            Sync.timeout(
                () -> {
                    player.setAllowFlight(true);
                    player.setFlying(true);

                    if(this.recentlyDisconnectedCache.contains(player)) {
                        this.recentlyDisconnectedCache.withdraw(player);
                        this.validateAuthentication.accept(player);
                    }
                    else
                        this.notifyPlayerToLogin(player);
                },
                3L
            );
        }

        Sync.timeout(() -> {
            if(!AuthManager.isLogged(this.authqueue, player))
                player.kickPlayer(PluginMain.configParser().getString(Locale.PluginConfig.MESSAGES__AUTHENTICATION_TIMED_OUT_KICK));
        }, this.authenticationTimeoutKickDelay() * 20L);
    }

    //

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) throws SQLException {
        Player player = event.getPlayer();

        if(!event.getFrom().getBlock().getLocation().equals(event.getTo().getBlock().getLocation())) {
            if(!AuthManager.isLogged(this.authqueue, player)) {
                this.notifyPlayerToLogin(player, Sound.ENTITY_ENDERMAN_TELEPORT);

                try {
                    if (this.usesAuthInterface) {
                        if (this.authenticators.password().isRegistered(player) || this.authenticators.discord().isLinked(player))
                            player.performCommand("login");
                        else
                            player.performCommand("register");
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                event.setCancelled(true);
            }
        }
    }

    //

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPerformCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if(!AuthManager.isLogged(this.authqueue, player)) {
            String label = event.getMessage().split(" ")[0].substring(1);
            if(!this.isCommandAllowedOnLogin(label)) {
                this.notifyPlayerToLogin(player, null);
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTabComplete(TabCompleteEvent event) {
        if(!(event.getSender() instanceof Player player)) return;

        if(!AuthManager.isLogged(this.authqueue, player)) {
            String label = event.getBuffer().split(" ")[0].substring(1);

            if(!this.isCommandAllowedOnLogin(label))
                event.setCancelled(true);
        }
    }

    //

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQUit(PlayerQuitEvent event) throws SQLException {
        Player player = event.getPlayer();

        if(AuthManager.isLogged(this.authqueue, player)) {
            if(this.authenticators.password().isRegistered(player))
                this.recentlyDisconnectedCache.store(player, null);

            event.setQuitMessage(null);
        }

        this.authqueue.remove(player);
        this.joinMessageRegistry.withdraw(player);
    }

}
