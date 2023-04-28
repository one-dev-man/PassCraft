package org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations;

import org.onedevman.mc.plugins.passcraft.chestinterface.components.ChestIcon;

import java.util.List;

public class AnvilInputConfig {

    private String title;
    private List<String> description;
    private String default_value;
    private ChestIcon input_icon;
    private boolean silent;

    //

    public AnvilInputConfig(String title, List<String> description, String default_value, boolean silent) {
        this.setTitle(title);
        this.setDescription(description);
        this.setDefaultValue(default_value);
        this.setSilent(silent);
    }

    //

    public String title() { return this.title; }

    public List<String> description() { return this.description; }

    public String defaultValue() { return this.default_value; }

    public ChestIcon inputIcon() { return this.input_icon; }

    public boolean silent() { return this.silent; }

    //

    public String setTitle(String title) { return this.title = title; }

    public List<String> setDescription(List<String> description) { return this.description = description; }

    public String setDefaultValue(String default_value) { return this.default_value = default_value; }

    public ChestIcon setInputIcon(ChestIcon icon) { return this.input_icon = icon; }

    public boolean setSilent(boolean silent) { return this.silent = silent; }

}
