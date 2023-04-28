package org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;
import org.onedevman.mc.plugins.passcraft.utils.reflection.MethodsUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class TexturedChestIconConfig extends ChestIconConfig {

    private String texture_url;

    //

    public TexturedChestIconConfig(String texture_url, String name, int amount, List<String> description, boolean enchanted) {
        super(Material.PLAYER_HEAD, name, amount, description, enchanted);

        this.setTextureUrl(texture_url);
    }

    //

    public String textureUrl() { return this.texture_url; }

    public String setTextureUrl(String texture_url) { return this.texture_url = texture_url; }

    //

    public void applyTexture(SkullMeta meta) throws InvocationTargetException, IllegalAccessException {
        GameProfile texture_profile = new GameProfile(UUID.randomUUID(), null);

        String final_texture_property_data = "{\"textures\":{\"SKIN\":{\"url\":\"" + this.textureUrl() + "\"}}}";

        Property textures_property = new Property(
            "textures",
            new String(Base64.getEncoder().encode(final_texture_property_data.getBytes()))
        );

        texture_profile.getProperties().put("textures", textures_property);

        MethodsUtil.invoke(meta, "setProfile", texture_profile);
    }

}