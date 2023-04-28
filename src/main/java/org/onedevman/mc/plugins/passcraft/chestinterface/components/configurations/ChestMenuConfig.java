package org.onedevman.mc.plugins.passcraft.chestinterface.components.configurations;

public class ChestMenuConfig {

    private String title;
    private int rows;
    private boolean silent;

    //

    public ChestMenuConfig(String title, int rows, boolean silent) {
        this.setTitle(title);
        this.setRows(rows);
        this.setSilent(silent);
    }

    //

    public String title() { return this.title; }

    public int rows() { return this.rows; }

    public boolean silent() { return this.silent; }

    //

    public String setTitle(String title) { return this.title = title; }

    public int setRows(int rows) { return this.rows = rows; }

    public boolean setSilent(boolean silent) { return this.silent = silent; }

}
