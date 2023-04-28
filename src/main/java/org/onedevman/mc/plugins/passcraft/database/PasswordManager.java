package org.onedevman.mc.plugins.passcraft.database;

import org.onedevman.mc.plugins.passcraft.database.DatabaseManager;

import java.sql.SQLException;

public class PasswordManager {

    public final DatabaseManager dbmanager;

    public final String dbtable;

    public final String usercolumn;
    public final String passwordcolumn;

    //

    public PasswordManager(DatabaseManager dbmanager, String dbtable, String usercolumn, String passwordcolumn) throws SQLException {
        this.dbmanager = dbmanager;

        this.dbtable = dbtable;

        this.usercolumn = usercolumn;
        this.passwordcolumn = passwordcolumn;

        //

        this.initTableIfNotExists();
    }

    //

    private void initTableIfNotExists() throws SQLException {
        if(!this.dbmanager.tableExists(this.dbtable)) {
            this.dbmanager.createTable(
                this.dbtable,
                this.usercolumn + " STRING NOT NULL," +
                this.passwordcolumn + " STRING NOT NULL," +
                "PRIMARY KEY (" + this.usercolumn + ")"
            );
        }

        this.dbmanager.addColumnIfNotExists(this.dbtable, this.usercolumn, "STRING");
        this.dbmanager.addColumnIfNotExists(this.dbtable, this.passwordcolumn, "STRING");
    }

    //

    public String getPassword(String user) throws SQLException {
        initTableIfNotExists();

        DatabaseManager.Result result = this.dbmanager.execute(
            DatabaseManager.RequestType.QUERY,
            "SELECT " + this.passwordcolumn + " FROM " + this.dbtable + " WHERE " + this.usercolumn + " = \"" + user + "\""
        );

        if(result == null)
            return null;

        if(!result.set().next()) {
            result.closer().close();
            return null;
        }

        String password = result.set().getString(1);
        result.closer().close();

        return password == null ? null : password.length() == 0 ? null : password.equalsIgnoreCase("null") ? null : password;
    }

    public boolean hasPassword(String user) throws SQLException {
        initTableIfNotExists();

        return this.getPassword(user) != null;
    }

    public boolean checkPassword(String user, String password) throws SQLException {
        initTableIfNotExists();

        String stored_password = this.getPassword(user);
        return password.equals(stored_password);
    }

    public boolean setPassword(String user, String password) throws SQLException {
        initTableIfNotExists();

        String request = "INSERT INTO " + this.dbtable + " (" + this.usercolumn + ", " + this.passwordcolumn + ") VALUES (\"" + user + "\", \"" + password + "\") "
                       + "ON CONFLICT (" + this.usercolumn + ") DO UPDATE SET " + this.passwordcolumn + "=\"" + password + "\"";

        DatabaseManager.Result result = this.dbmanager.execute(DatabaseManager.RequestType.UPDATE, request);

        if(result == null)
            return false;

        boolean success_state = result.rows() > 0;
        result.closer().close();

        return success_state;
    }

    public boolean deletePasswod(String user) throws SQLException {
        initTableIfNotExists();

        String request = "DELETE FROM " + this.dbtable + " WHERE " + this.usercolumn + "=\"" + user + "\"";

        DatabaseManager.Result result = this.dbmanager.execute(DatabaseManager.RequestType.UPDATE, request);

        if(result == null)
            return false;

        boolean success_state = result.rows() > 0;
        result.closer().close();

        return success_state;
    }

}
