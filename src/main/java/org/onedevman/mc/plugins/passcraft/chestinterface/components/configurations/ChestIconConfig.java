package org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations;

import org.bukkit.Material;

import java.util.List;

public class ChestIconConfig {

    private Material material;
    private String name;
    private int amount;
    private List<String> description;
    private boolean enchanted;

    //

    public ChestIconConfig(Material material, String name, int amount, List<String> description, boolean enchanted) {
        this.setMaterial(material);
        this.setName(name);
        this.setAmount(amount);
        this.setDescription(description);
        this.setEnchanted(enchanted);
    }

    //

    public Material material() { return this.material; }

    public String name() { return this.name; }

    public int amount() { return this.amount; }

    public List<String> description() { return this.description; }

    public boolean enchanted() { return this.enchanted; }

    //

    public Material setMaterial(Material material) { return this.material = material; }

    public String setName(String name) { return this.name = name; }

    public int setAmount(int amount) { return this.amount = amount; }

    public List<String> setDescription(List<String> description) { return this.description = description; }

    public boolean setEnchanted(boolean enchanted) { return this.enchanted = enchanted; }

}
