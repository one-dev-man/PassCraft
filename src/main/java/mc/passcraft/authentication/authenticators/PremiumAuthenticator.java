package mc.passcraft.authentication.authenticators;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import mc.passcraft.PluginMain;
import mc.passcraft.apis.MojangAPI;
import mc.passcraft.authentication.types.AuthQueue;
import mc.passcraft.nms.NMS;

import java.util.List;

public class PremiumAuthenticator {

    private final PremiumAuthenticator _this = this;

    //

    private final AuthQueue default_authqueue;
//    private final RecentlyDisconnectedCache recently_disconnected_cache;

    //

    private boolean _enabled = false;

    public final AuthQueue authqueue = new AuthQueue();

    //

    private final PacketAdapter packet_listener = new PacketAdapter(
        PluginMain.instance(),
        ListenerPriority.HIGHEST,
        List.of(
            PacketType.Handshake.Client.SET_PROTOCOL,
            PacketType.Login.Client.START,
            PacketType.Login.Server.ENCRYPTION_BEGIN
        )
    ) {
        @Override
        public void onPacketReceiving(PacketEvent event) {
            if(event.getPacketType().equals(PacketType.Login.Client.START)) {
                try {
                    Player player = event.getPlayer();
                    String player_name = event.getPacket().getStrings().readSafely(0);
//                    String player_ip = Players.getIp(player);
//                    String recently_disconnected_cache_key = RecentlyDisconnectedCache.Formatter.formatFrom(player_name, player_ip);
//
//                    if(_this.recently_disconnected_cache.cache.contains(recently_disconnected_cache_key)) {
//                        _this.recently_disconnected_cache.cache.withdraw(recently_disconnected_cache_key);
//                        return;
//                    }

                    if(_this.enabled() && MojangAPI.user.isPremium(player_name)) {
                        PluginMain.logger().info("Premium account authentication for `" + player_name + "`.");

                        _this.authqueue.add(player);
                        NMS.getNativeServer().setUsesAuthentication(true);

                        return;
                    }


                    _this.default_authqueue.add(player);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if(event.getPacketType().equals(PacketType.Login.Server.ENCRYPTION_BEGIN)) {
                try {
                    Player player = event.getPlayer();
                    _this.authqueue.remove(player);

                    if(_this.authqueue.size() == 0)
                        NMS.getNativeServer().setUsesAuthentication(false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    //

//    public PremiumAuthenticator(AuthQueue default_authqueue, RecentlyDisconnectedCache recently_disconnected_cache) {
//        this.default_authqueue = default_authqueue;
//        this.recently_disconnected_cache = recently_disconnected_cache;
//    }
    public PremiumAuthenticator(AuthQueue default_authqueue) {
        this.default_authqueue = default_authqueue;
    }

    //

    public boolean enabled() { return this._enabled; }

    public void setEnabled(boolean enabled) { this._enabled = enabled;}

    //

    public void start() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this.packet_listener);
    }

    public void stop() {
        ProtocolLibrary.getProtocolManager().removePacketListener(this.packet_listener);
    }

}
