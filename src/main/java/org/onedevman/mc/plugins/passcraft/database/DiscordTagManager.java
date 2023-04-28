package org.onedevman.mc.plugins.passcraft.database;

import java.sql.SQLException;

public class DiscordTagManager {

    public final DatabaseManager dbmanager;

    public final String dbtable;

    public final String usercolumn;
    public final String discordcolumn;

    //

    public DiscordTagManager(DatabaseManager dbmanager, String dbtable, String usercolumn, String discordcolumn) throws SQLException {
        this.dbmanager = dbmanager;

        this.dbtable = dbtable;

        this.usercolumn = usercolumn;
        this.discordcolumn = discordcolumn;

        //

        this.initTableIfNotExists();
    }

    //

    private void initTableIfNotExists() throws SQLException {
        if(!this.dbmanager.tableExists(this.dbtable)) {
            this.dbmanager.createTable(
                this.dbtable,
                "" + this.usercolumn + " STRING NOT NULL," +
                "" + this.discordcolumn + " STRING NOT NULL," +
                "PRIMARY KEY (" + this.usercolumn + ", " + this.discordcolumn + ")"
            );
        }

        this.dbmanager.addColumnIfNotExists(this.dbtable, this.usercolumn, "STRING");
        this.dbmanager.addColumnIfNotExists(this.dbtable, this.discordcolumn, "STRING");
    }

    //

    public boolean hasTag(String user) throws SQLException {
        return this.getTag(user) != null;
    }

    //

    public String getTag(String user) throws SQLException {
        this.initTableIfNotExists();

        DatabaseManager.Result result = this.dbmanager.execute(
            DatabaseManager.RequestType.QUERY,
            "SELECT " + this.discordcolumn + " FROM " + this.dbtable + " WHERE " + this.usercolumn + " = \"" + user + "\""
        );

        if(result == null)
            return null;

        if(!result.set().next()) {
            result.closer().close();
            return null;
        }

        String tag = result.set().getString(1);
        result.closer().close();

        return tag == null ? null : tag.length() == 0 ? null : tag.equalsIgnoreCase("null") ? null : tag;
    }

    //

    public boolean setTag(String user, String discord_tag) throws SQLException {
        this.initTableIfNotExists();

        DatabaseManager.Result user_row_exists_resuslt = this.dbmanager.execute(
            DatabaseManager.RequestType.QUERY,
            "SELECT " + this.usercolumn + " FROM " + this.dbtable + " WHERE " + this.usercolumn + " = \"" + user + "\""
        );

        if(user_row_exists_resuslt == null)
            return false;

        boolean user_row_exists = user_row_exists_resuslt.set().next();
        user_row_exists_resuslt.closer().close();

        //

        String sql_request;
        if(user_row_exists)
            sql_request = "UPDATE " + this.dbtable + " SET " + this.discordcolumn + "=\"" + discord_tag + "\"";
        else
            sql_request = "INSERT INTO " + this.dbtable + " (" + this.usercolumn + ", " + this.discordcolumn + ") VALUES (\"" + user + "\", \"" + discord_tag + "\")";

        DatabaseManager.Result result = this.dbmanager.execute(DatabaseManager.RequestType.UPDATE, sql_request);

        if(result == null)
            return false;

        boolean success_state = result.rows() > 0;
        result.closer().close();

        return success_state;
    }

    //

    public boolean deleteTag(String user) throws SQLException {
        return this.setTag(user, "");
    }

    //

    public boolean isTagUsed(String discord_tag) throws SQLException {
        this.initTableIfNotExists();

        DatabaseManager.Result result = this.dbmanager.execute(
            DatabaseManager.RequestType.QUERY,
            "SELECT " + this.discordcolumn + " FROM " + this.dbtable + " WHERE " + this.discordcolumn + " = \"" + discord_tag + "\""
        );

        if(result == null)
            return false;

        boolean r = result.set().next();
        result.closer().close();

        return r;
    }

    //

}
