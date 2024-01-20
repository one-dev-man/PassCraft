package mc.passcraft.chestinterface.components;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import mc.passcraft.chestinterface.components.configurations.ChestIconConfig;
import mc.passcraft.chestinterface.events.ChestIconClickEvent;
import mc.passcraft.chestinterface.events.ChestIconEvent;
import mc.passcraft.chestinterface.events.ChestInterfaceEventListener;
import mc.passcraft.types.events.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChestIcon extends ChestIconInterface<ChestIconEvent> {

    public final ChestIconConfig config;

    //

    public ChestIcon(String material, String name) {
        this(Material.getMaterial(material), name, 1, new ArrayList<>(), false);
    }

    public ChestIcon(Material material, String name) {
        this(material, name, 1, new ArrayList<>(), false);
    }

    public ChestIcon(String material, String name, int amount) {
        this(Material.getMaterial(material), name, amount, new ArrayList<>(), false);
    }

    public ChestIcon(Material material, String name, int amount) {
        this(material, name, amount, new ArrayList<>(), false);
    }

    public ChestIcon(String material, String name, int amount, String description) {
        this(Material.getMaterial(material), name, amount, List.of(description.split("\n")), false);
    }

    public ChestIcon(String material, String name, int amount, List<String> description) {
        this(Material.getMaterial(material), name, amount, description, false);
    }

    public ChestIcon(Material material, String name, int amount, String description) {
        this(material, name, amount, List.of(description.split("\n")), false);
    }

    public ChestIcon(Material material, String name, int amount, List<String> description) {
        this(material, name, amount, description, false);
    }

    public ChestIcon(String material, String name, int amount, String description, boolean enchanted) {
        this(Material.getMaterial(material), name, amount, List.of(description.split("\n")), enchanted);
    }

    public ChestIcon(String material, String name, int amount, List<String> description, boolean enchanted) {
        this(Material.getMaterial(material), name, amount, description, enchanted);
    }

    public ChestIcon(Material material, String name, int amount, String description, boolean enchanted) {
        this(material, name, amount, List.of(description.split("\n")), enchanted);
    }

    public ChestIcon(Material material, String name, int amount, List<String> description, boolean enchanted) {
        this(new ChestIconConfig(material, name, amount, description, enchanted));
    }

    public ChestIcon(ChestIconConfig config) {
        this.config = config;
    }

    //

    public ChestInterfaceEventListener onClick(Consumer<ChestIconClickEvent> callback) {
        ChestInterfaceEventListener result = new ChestInterfaceEventListener() {
            @EventHandler
            public void onClick(ChestIconClickEvent event) {
                callback.accept(event);
            }
        };

        this.addListener(result);

        return result;
    }

    //

    public ItemStack toBukkit() {
        ItemStack result = new ItemStack(this.config.material(), this.config.amount());

        ItemMeta meta = result.getItemMeta();

        if(meta != null) {
            meta.setDisplayName("§r"+this.config.name());

            //

            List<String> final_description = new ArrayList<>(this.config.description());
            int final_description_line_count = final_description.size();

            for(int i = 0; i < final_description_line_count; ++i)
                final_description.set(i, "§7"+final_description.get(i));

            meta.setLore(final_description);

            //

            if(this.config.enchanted()) {
                meta.addEnchant(Enchantment.VANISHING_CURSE, 0, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            //

            result.setItemMeta(meta);
        }

        return result;
    }

}
