package org.onedevman.mc.plugins.passcraft.utils.game;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.onedevman.mc.plugins.passcraft.PluginMain;
import org.onedevman.mc.plugins.passcraft.utils.game.vanisher.VanisherPropertyAffector;
import org.onedevman.mc.plugins.passcraft.types.Pair;

import java.util.*;
import java.util.function.Consumer;

public class Vanisher {

    private static final Map<String, Pair<Vanisher, Boolean>> vanishedPlayers = new HashMap<>();

    private static final Map<String, Long> temp_invulnerable_player_map = new HashMap<>();

    private static final long TEMP_INVULNERABLE_TIMEOUT_DELAY = 10;
    private static final long TEMP_INVULNERABLE_DELAY_AFTER_FIRST_DAMAGE = 2;

    //

    private static final long INIT_INSTANT = System.currentTimeMillis();

    private static final String VANISHED_PLAYER_PREFIX = "§7(§8vanished§7)§r ";

    private static Team _VANISHED_PLAYER_TEAM = null;
    private static Team VANISHED_PLAYER_TEAM() {
        String team_name = "VANISHED_PLAYER_TEAM_" + INIT_INSTANT;

        boolean needs_to_recreate = _VANISHED_PLAYER_TEAM == null;

        if(!needs_to_recreate) {
            try {
                _VANISHED_PLAYER_TEAM.addEntry("test");
                _VANISHED_PLAYER_TEAM.removeEntry("test");
            } catch(Exception e) {
                needs_to_recreate = true;
            }
        }

        if(needs_to_recreate) {
            _VANISHED_PLAYER_TEAM = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().registerNewTeam(team_name);

            _VANISHED_PLAYER_TEAM.setCanSeeFriendlyInvisibles(true);
            _VANISHED_PLAYER_TEAM.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            _VANISHED_PLAYER_TEAM.setPrefix(Vanisher.VANISHED_PLAYER_PREFIX);

            for(Map.Entry<String, Pair<Vanisher, Boolean>> entry : Vanisher.vanishedPlayers.entrySet()) {
                Player vanished_player = Bukkit.getPlayer(UUID.fromString(entry.getKey()));

                if(vanished_player != null && !entry.getValue().last()) {
                    _VANISHED_PLAYER_TEAM.removeEntry(vanished_player.getName());
                    _VANISHED_PLAYER_TEAM.addEntry(vanished_player.getName());
                }
            }
        }

        return _VANISHED_PLAYER_TEAM;
    }

    //

    private static class VanisherPlayerEventListener implements Listener {

        private final Vanisher _vanisher;

        //

        public VanisherPlayerEventListener(Vanisher vanisher) {
            this._vanisher = vanisher;
        }

        //

        public Vanisher vanisher() { return this._vanisher; }

        //

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();


            for(Map.Entry<String, Pair<Vanisher, Boolean>> entry : Vanisher.vanishedPlayers.entrySet()) {
                Player vanished_player = Bukkit.getPlayer(UUID.fromString(entry.getKey()));

                if(vanished_player != null) {
                    Pair<Vanisher, Boolean> vanish_mode_state = entry.getValue();

                    player.hidePlayer(vanish_mode_state.first().pluginInstance(), vanished_player);
                }
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onQuit(PlayerQuitEvent event) {
            String uuid = event.getPlayer().getUniqueId().toString();
            Pair<Vanisher, Boolean> vanish_mode_state = Vanisher.vanishedPlayers.get(uuid);

            if(vanish_mode_state != null)
                vanish_mode_state.first().unvanish(event.getPlayer());
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onGameModeChange(PlayerGameModeChangeEvent event) {
            Player player = event.getPlayer();

            String uuid = player.getUniqueId().toString();
            Pair<Vanisher, Boolean> vanish_mode_state = Vanisher.vanishedPlayers.get(uuid);

            if(vanish_mode_state != null)
                vanish_mode_state.first().vanishingOperation(player, false, vanish_mode_state.last(), false, VanishingOperation.VANISH);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedTargeted(EntityTargetLivingEntityEvent event) {
            if(event.getTarget() instanceof Player player) {
                String uuid = player.getUniqueId().toString();
                event.setCancelled(Vanisher.vanishedPlayers.containsKey(uuid));
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomInteractWithEntity(PlayerInteractAtEntityEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomInteractEntity(PlayerInteractEntityEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomInteract(PlayerInteractEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

//        @EventHandler(priority = EventPriority.HIGHEST)
//        public void onVanishedPhantomTakePortal(PlayerPortalEvent event) {
//            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
//        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomEmptyBucket(PlayerBucketEmptyEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomFillBucket(PlayerBucketFillEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomBucketEntity(PlayerBucketEntityEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomBreakBlock(BlockBreakEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomPlaceBlock(BlockPlaceEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomDrop(PlayerDropItemEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomItemConsume(PlayerItemConsumeEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomItemDamage(PlayerItemDamageEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomSwapHandItems(PlayerSwapHandItemsEvent event) {
            event.setCancelled(cancelVanishedActions(event.getPlayer(), true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedPhantomHit(EntityDamageByEntityEvent event) {
            if(event.getDamager() instanceof Player player)
                event.setCancelled(cancelVanishedActions(player, true));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onJustUnvanishedGetDamaged(EntityDamageEvent event) {
            if(event.getEntity() instanceof Player player) {
                String uuid = player.getUniqueId().toString();

                if(Vanisher.temp_invulnerable_player_map.containsKey(uuid)) {
                    long invulnerability_timestamp = temp_invulnerable_player_map.remove(uuid);

                    if(System.currentTimeMillis() - invulnerability_timestamp <= Vanisher.TEMP_INVULNERABLE_TIMEOUT_DELAY * 1000L) {
                        player.setInvulnerable(true);

                        Bukkit.getScheduler().runTaskLater(
                            this.vanisher().pluginInstance(),
                            () -> {
                                player.setInvulnerable(false);
                            },
                            Vanisher.TEMP_INVULNERABLE_DELAY_AFTER_FIRST_DAMAGE * 20L
                        );

                        event.setCancelled(true);
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedHealthChange(EntityRegainHealthEvent event) {
            if(event.getEntity() instanceof Player player)
                event.setCancelled(cancelVanishedActions(player, false));
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onVanishedFoodChange(FoodLevelChangeEvent event) {
            if(event.getEntity() instanceof Player player)
                event.setCancelled(cancelVanishedActions(player, false));
        }

        public boolean cancelVanishedActions(Player player, boolean phantom_mode) {
            String uuid = player.getUniqueId().toString();
            Pair<Vanisher, Boolean> vanish_mode_state = Vanisher.vanishedPlayers.get(uuid);

            if(vanish_mode_state != null)
                return !phantom_mode || vanish_mode_state.last();

            return false;
        }
    }

    //

    private static void forEachOnlinePlayer(Consumer<Player> callback) {
        for(Player online_player : Bukkit.getOnlinePlayers())
            callback.accept(online_player);
    }

    //

    private static void joinVanishedPlayerTeam(Player player) {
        joinVanishedPlayerTeam(player, false);
    }

    private static void joinVanishedPlayerTeam(Player player, boolean as_phantom) {
        if(!as_phantom) {
            Vanisher.VANISHED_PLAYER_TEAM().addEntry(player.getName());
            return;
        }

        try {
            ProtocolManager protocol_manager = ProtocolLibrary.getProtocolManager();

            PacketContainer packet = protocol_manager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            WrapperPlayServerScoreboardTeam wrapped_packet = new WrapperPlayServerScoreboardTeam(packet);

            wrapped_packet.setName(Vanisher.VANISHED_PLAYER_TEAM().getName());
            wrapped_packet.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_ADDED);
            wrapped_packet.setPlayers(List.of(player.getName()));

            protocol_manager.sendServerPacket(player, wrapped_packet.getHandle());
        } catch (Exception e) {
            PluginMain.logger().warning(e.getLocalizedMessage());
//            e.printStackTrace();
        }
    }

    //

    private static void leaveVanishedPlayerTeam(Player player) {
        try {
            Vanisher.VANISHED_PLAYER_TEAM().removeEntry(player.getName());

            ProtocolManager protocol_manager = ProtocolLibrary.getProtocolManager();

            PacketContainer packet = protocol_manager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            WrapperPlayServerScoreboardTeam wrapped_packet = new WrapperPlayServerScoreboardTeam(packet);

            wrapped_packet.setName(Vanisher.VANISHED_PLAYER_TEAM().getName());
            wrapped_packet.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_REMOVED);
            wrapped_packet.setPlayers(List.of(player.getName()));

            protocol_manager.sendServerPacket(player, wrapped_packet.getHandle());
        } catch (Exception e) {
            PluginMain.logger().warning(e.getLocalizedMessage());
//            e.printStackTrace();
        }
    }

    //

    private final Plugin _plugin_instance;

    private final BukkitTask VANISHED_PLAYER_TEAM_UPDATE_TASK;

    //

    private Vanisher(Plugin plugin_instance) {
        this._plugin_instance = plugin_instance;

        Bukkit.getServer().getPluginManager().registerEvents(new VanisherPlayerEventListener(this), plugin_instance);

        VANISHED_PLAYER_TEAM_UPDATE_TASK = Bukkit.getScheduler().runTaskTimer(this.pluginInstance(), Vanisher::VANISHED_PLAYER_TEAM, 0L, 20L);
    }

    //

    public Plugin pluginInstance() { return this._plugin_instance; }

    //

    private enum VanishingOperation {
        VANISH,
        UNVANISH
    }

    private synchronized void vanishingOperation(
        Player player,
        boolean player_update_sensitive_only,
        boolean phantom_mode,
        boolean force_unvanish,
        VanishingOperation operation
    ) {
        String uuid = player.getUniqueId().toString();

        for(VanisherPropertyAffector property_affector : VanisherPropertyAffector.values()) {
            boolean can_affect = (!player_update_sensitive_only || property_affector.playerUpdateSensitive) && (!property_affector.phantomModeOnly || phantom_mode);
            if(can_affect) {
                if(property_affector.differed < 1L) {
                    if(operation.equals(VanishingOperation.VANISH))
                        property_affector.affect(player);
                    else if (operation.equals(VanishingOperation.UNVANISH))
                        property_affector.disaffect(player, force_unvanish);
                }
                else {
                    try {
                        Bukkit.getScheduler().runTaskLater(
                            this.pluginInstance(),
                            () -> {
                                if(operation.equals(VanishingOperation.VANISH))
                                    property_affector.affect(player);
                                else if(operation.equals(VanishingOperation.UNVANISH))
                                    property_affector.disaffect(player, force_unvanish);
                            },
                            property_affector.differed
                        );
                    } catch(Exception e) {
                        PluginMain.logger().warning(e.getLocalizedMessage());
//                        e.printStackTrace();
                    }
                }
            }
        }

        if(operation.equals(VanishingOperation.VANISH)) {
            Vanisher.forEachOnlinePlayer(online_player -> {
                try {
                    if(this.vanished(online_player) && !phantom_mode) {
                        online_player.showPlayer(this.pluginInstance(), player);
                        player.showPlayer(this.pluginInstance(), online_player);
                    }
                    else {
                        online_player.hidePlayer(this.pluginInstance(), player);

                        if(!this.phantomized(online_player))
                            player.showPlayer(this.pluginInstance(), online_player);
                    }
                } catch(Exception e) {
                    PluginMain.logger().warning(e.getLocalizedMessage());
//                    e.printStackTrace();
                }
            });

            Vanisher.joinVanishedPlayerTeam(player, phantom_mode);

            Vanisher.vanishedPlayers.put(uuid, Pair.of(this, phantom_mode));
        }
        else if(operation.equals(VanishingOperation.UNVANISH)) {
            Vanisher.forEachOnlinePlayer(online_player -> {
                try {
                    online_player.showPlayer(this.pluginInstance(), player);

                    if(this.vanished(online_player))
                        player.hidePlayer(this.pluginInstance(), online_player);
                } catch(Exception e) {
                    PluginMain.logger().warning(e.getLocalizedMessage());
//                    e.printStackTrace();
                }
            });

            Vanisher.leaveVanishedPlayerTeam(player);

            Vanisher.vanishedPlayers.remove(uuid);

            Vanisher.temp_invulnerable_player_map.put(uuid, System.currentTimeMillis());
        }
    }

    //

    public void vanish(Player player) {
        vanish(player, false);
    }

    public void vanish(Player player, boolean phantom_mode) {
        vanishingOperation(player, false, phantom_mode, false, VanishingOperation.VANISH);
    }

    //

    public void unvanish(Player player) {
        this.unvanish(player, false);
    }

    public void unvanish(Player player, boolean force) {
        vanishingOperation(player, false, false, force, VanishingOperation.UNVANISH);
    }

    //

    public boolean vanished(Player player) {
        return Vanisher.vanishedPlayers.containsKey(player.getUniqueId().toString());
    }

    public boolean phantomized(Player player) {
        Pair<Vanisher, Boolean> vanish_mode_state = Vanisher.vanishedPlayers.get(player.getUniqueId().toString());

        if(vanish_mode_state == null)
            return false;

        return vanish_mode_state.last();
    }

    public void cancelTemporaryInvulnerability(Player player) {
        String uuid = player.getUniqueId().toString();
        Vanisher.temp_invulnerable_player_map.remove(uuid);
    }

    //

    public List<Player> vanishedList() {
        List<Player> result = new ArrayList<>();

        for (String vanished_uuid : Vanisher.vanishedPlayers.keySet()) {
            Player vanished_player = Bukkit.getPlayer(UUID.fromString(vanished_uuid));

            if (vanished_player != null)
                result.add(vanished_player);
        }

        return result;
    }

    //

    private static Vanisher _instance_ = null;

    public static void init(Plugin plugin_instance) {
        assert Vanisher._instance_ == null : "Vanisher already initialized.";
        Vanisher._instance_ = new Vanisher(plugin_instance);
    }

    public static Vanisher get() { return Vanisher._instance_; }

    public static void disable() {
        for(Map.Entry<String, Pair<Vanisher, Boolean>> vanished_player_entry : Vanisher.vanishedPlayers.entrySet()) {
            Player vanished_player = Bukkit.getPlayer(UUID.fromString(vanished_player_entry.getKey()));

            if(vanished_player != null) {
                try { vanished_player_entry.getValue().first().unvanish(vanished_player); }
                catch(Exception e) { }
            }
        }

        Vanisher vanisher = Vanisher.get();
        if(vanisher == null) return;

        vanisher.VANISHED_PLAYER_TEAM_UPDATE_TASK.cancel();

        Vanisher.VANISHED_PLAYER_TEAM().unregister();
    }

}