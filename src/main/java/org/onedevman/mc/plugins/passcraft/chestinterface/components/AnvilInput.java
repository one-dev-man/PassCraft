package org.onedevman.mc.plugins.passcraft.chestinterface.components;

import com.comphenix.packetwrapper.WrapperPlayClientItemName;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.onedevman.mc.plugins.passcraft.PluginMain;
import org.onedevman.mc.plugins.passcraft.chestinterface.bukkit.BukkitChestInterfaceIdentifier;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations.AnvilInputConfig;
import org.onedevman.mc.plugins.passcraft.chestinterface.event.*;
import org.onedevman.mc.plugins.passcraft.types.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AnvilInput extends ChestInterface<AnvilInputEvent> {

    public static final int DEFAULT_ANVIL_FIRST_INPUT_SLOT = 0;
    public static final int DEFAULT_ANVIL_SECOND_INPUT_SLOT = 1;
    public static final int DEFAULT_ANVIL_OUTPUT_SLOT = 2;

    //

    private final AnvilInput _this = this;

    public final AnvilInputConfig config;

    private final PacketAdapter INPUT_PACKET_INTERCEPTOR = new PacketAdapter(
            PluginMain.instance(),
            ListenerPriority.HIGHEST,
            List.of(
                    PacketType.Play.Client.ITEM_NAME
            )
    ) {
        @Override
        public void onPacketReceiving(PacketEvent event) {
            String player_uuid = event.getPlayer().getUniqueId().toString();
            if(_this.processing_input_players.containsKey(player_uuid)) {
                Pair<Inventory, String> pair = _this.processing_input_players.remove(player_uuid);

                if(event.getPlayer().getOpenInventory().getTopInventory().equals(pair.first())) {
                    WrapperPlayClientItemName packet = new WrapperPlayClientItemName(event.getPacket());

                    _this.processing_input_players.put(player_uuid, Pair.of(pair.first(), packet.getItemName()));
                }
            }
        }
    };

    //

    private final Map<String, Pair<Inventory, String>> processing_input_players = new HashMap<>();

    //

    public AnvilInput(String title) {
        this(title, List.of(), "");
    }

    public AnvilInput(String title, boolean silent) {
        this(title, List.of(), "", silent);
    }

    public AnvilInput(String title, String description) {
        this(title, List.of(description.split("\n")), "");
    }

    public AnvilInput(String title, String description, boolean silent) {
        this(title, List.of(description.split("\n")), "", silent);
    }

    public AnvilInput(String title, List<String> description) {
        this(title, description, "");
    }

    public AnvilInput(String title, List<String> description, boolean silent) {
        this(title, description, "", silent);
    }

    public AnvilInput(String title, String description, String default_value) {
        this(title, List.of(description.split("\n")), default_value);
    }
    public AnvilInput(String title, String description, String default_value, boolean silent) {
        this(title, List.of(description.split("\n")), default_value, silent);
    }

    public AnvilInput(String title, List<String> description, String default_value) {
        this(title, description, default_value, false);
    }

    public AnvilInput(String title, List<String> description, String default_value, boolean silent) {
        this(new AnvilInputConfig(title, description, default_value, silent));
    }

    public AnvilInput(AnvilInputConfig config) {
        this.config = config;

        this.config.setInputIcon(new ChestIcon(Material.NAME_TAG, this.config.defaultValue(), 1, this.config.description()));

        //

        if(!ProtocolLibrary.getProtocolManager().getPacketListeners().contains(this.INPUT_PACKET_INTERCEPTOR))
            ProtocolLibrary.getProtocolManager().addPacketListener(this.INPUT_PACKET_INTERCEPTOR);

        this.addListener(new ChestInterfaceEventListener() {
            @ChestInterfaceEventHandler
            public void onOpen(AnvilInputOpenEvent event) {
                _this.processing_input_players.put(event.entity().getUniqueId().toString(), Pair.of(event.inventory(), ""));
            }

            @ChestInterfaceEventHandler
            public void onClose(AnvilInputCloseEvent event) {
                if(event.entity() instanceof Player && !_this.config.silent())
                    playInteractionSound((Player) event.entity());

                _this.processing_input_players.remove(event.entity().getUniqueId().toString());
            }

            @ChestInterfaceEventHandler(priority = ChestInterfaceEventPriority.HIGHEST, ignoreCancelled = true)
            public void onClick(AnvilInputClickEvent event) throws InvocationTargetException, IllegalAccessException {
                if(event.entity() instanceof Player player) {
                    player.setExp(player.getExp());

                    if(event.slot() != AnvilInput.DEFAULT_ANVIL_OUTPUT_SLOT) return;

                    if(!_this.config.silent())
                        playInteractionSound(player);

                    Pair<Inventory, String> pair = _this.processing_input_players.get(event.entity().getUniqueId().toString());

                    if(_this.processing_input_players.keySet().size() == 0)
                        ProtocolLibrary.getProtocolManager().removePacketListener(_this.INPUT_PACKET_INTERCEPTOR);

                    _this.call(new AnvilInputSubmitEvent(null, event.entity(), event.inventory(), _this, pair.last()));
                }
            }
        });
    }

    //

    public ChestInterfaceEventListener onSubmit(Consumer<AnvilInputSubmitEvent> callback) {
        ChestInterfaceEventListener result = new ChestInterfaceEventListener() {
            @ChestInterfaceEventHandler(priority = ChestInterfaceEventPriority.LOWEST, ignoreCancelled = true)
            public void onSubmit(AnvilInputSubmitEvent event) {
                callback.accept(event);
            }
        };

        this.addListener(result);

        return result;
    }

    //

    @Override
    public Inventory toBukkit() {
        Inventory inventory = Bukkit.createInventory(new BukkitChestInterfaceIdentifier(this), InventoryType.ANVIL, this.config.title());

        inventory.setItem(0, this.config.inputIcon().toBukkit());

        return inventory;
    }

}
