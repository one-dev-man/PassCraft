package org.onedevman.mc.plugins.passcraft.chestinterface.components;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations.TexturedChestIconConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TexturedChestIcon extends ChestIcon {

    public TexturedChestIconConfig config;

    //

    public TexturedChestIcon(String texture_url, String name) {
        this(texture_url, name, 1, new ArrayList<>(), false);
    }

    public TexturedChestIcon(String texture_url, String name, int amount) {
        this(texture_url, name, amount, new ArrayList<>(), false);
    }

    public TexturedChestIcon(String texture_url, String name, int amount, String description) {
        this(texture_url, name, amount, List.of(description.split("\n")), false);
    }

    public TexturedChestIcon(String texture_url, String name, int amount, List<String> description) {
        this(texture_url, name, amount, description, false);
    }

    public TexturedChestIcon(String texture_url, String name, int amount, String description, boolean enchanted) {
        this(texture_url, name, amount, List.of(description.split("\n")), enchanted);
    }

    public TexturedChestIcon(String texture_url, String name, int amount, List<String> description, boolean enchanted) {
        this(new TexturedChestIconConfig(texture_url, name, amount, description, enchanted));
    }

    public TexturedChestIcon(TexturedChestIconConfig config) {
        super(config);
        this.config = config;
    }

    //


    @Override
    public ItemStack toBukkit() {
        ItemStack item = super.toBukkit();

        if(item.hasItemMeta()) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();

            try {
                this.config.applyTexture(meta);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            item.setItemMeta(meta);
        }

        return item;
    }
}
