package org.onedevman.mc.plugins.passcraft.database;

import org.onedevman.mc.plugins.passcraft.types.Trio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseManager {

    public static class ConnectionCloser {

        private final Connection connection;

        public ConnectionCloser(Connection connection) {
            this.connection = connection;
        }

        public void close() throws SQLException { this.connection.close(); }

    }

    //

    public enum System {
        MYSQL,
        SQLITE;

        public static final System DEFAULT = SQLITE;
    }

    public enum RequestType {
        UPDATE,
        QUERY;
    }

    //

    public static class Result {

        private final Trio<Integer, ResultSet, ConnectionCloser> trio;

        //

        private Result(Integer rows, ResultSet result_set, ConnectionCloser closer) {
            trio = Trio.of(rows, result_set, closer);
        }

        //

        public int rows() { return this.trio.one(); }

        public ResultSet set() { return this.trio.two(); }

        public ConnectionCloser closer() { return this.trio.three(); }

        //

        public static Result of(Integer rows, ResultSet result_set, ConnectionCloser closer) {
            return new Result(rows, result_set, closer);
        }

    }

    //

    public static String MYSQL_URL(String hostname, int port, String dbname) {
        return "jdbc:mysql://" + hostname + ":" + port + "/" + dbname;
    }

    public static String SQLITE_URL(String dbpath) {
        return "jdbc:sqlite:" + dbpath;
    }

    //

    public final System dbtype;

    private final String _hostname;
    private final int _port;

    private final String _username;
    private final String _password;

    private final String _dbname;

    private final Path _dbpath;

    //

    private DatabaseManager(System dbtype, String hostname, int port, String dbname, String username, String password, Path dbpath) {
        this.dbtype = dbtype;

        this._hostname = hostname;
        this._port = port;
        this._dbname = dbname;
        this._username = username;
        this._password = password;

        this._dbpath = dbpath;
    }

    //

    public String hostname() { return this._hostname; }

    public int port() { return this._port; }

    public String username() { return this._username; }

    public String password() { return this._password; }

    public String dbname() {return this._dbname; }

    public Path dbpath() { return this._dbpath; }

    //

    private synchronized Connection connect() throws SQLException {
        String url = "";

        if(this.dbtype.equals(System.MYSQL))
            url = DatabaseManager.MYSQL_URL(this.hostname(), this.port(), this.dbname());
        else if (this.dbtype.equals(System.SQLITE))
            url = DatabaseManager.SQLITE_URL(this.dbpath().toString());

        return DriverManager.getConnection(url);
    }

    //

    public synchronized Result execute(RequestType request_type, String request) throws SQLException {
        Connection connection = this.connect();
        Statement statement = connection.createStatement();

        int rows = -1;
        ResultSet result = null;
        try {
            if (request_type.equals(RequestType.UPDATE))
                rows = statement.executeUpdate(request);
            else if (request_type.equals(RequestType.QUERY))
                result = statement.executeQuery(request);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return Result.of(rows, result, new ConnectionCloser(connection));
    }

    //

    public synchronized boolean tableExists(String table) throws SQLException {
        Connection connection = this.connect();

        ResultSet result = connection.getMetaData().getTables(null, null, table, null);

        connection.close();

        return result.next();
    }

    //
    public synchronized List<String> tables() throws SQLException {
        Connection connection = this.connect();

        ResultSet result = connection.getMetaData().getTables(null, null, "%", null);

        List<String> tables = new ArrayList<>();

        while(result.next())
            tables.add(result.getString("TABLE_NAME"));

        connection.close();

        return Collections.unmodifiableList(tables);
    }

    //

    public synchronized void createTable(String table, String inits) throws SQLException {
        createTable(table, inits, false);
    }

    public synchronized void createTable(String table, String inits, boolean erase) throws SQLException {
        if(this.tableExists(table) && erase)
            this.deleteTable(table);

        if(!this.tableExists(table) || erase)
            this.execute(RequestType.UPDATE, "CREATE TABLE " + table + " (" + inits + ")");
    }

    //

    public synchronized boolean deleteTable(String table) throws SQLException {
        if(!this.tableExists(table))
            return false;

        this.execute(RequestType.UPDATE, "DROP TABLE " + table + "");

        return true;
    }

    @Deprecated
    public synchronized boolean dropTable(String table) throws SQLException {
        return this.deleteTable(table);
    }

    //

    public synchronized List<Trio<String, String, Integer>> columns(String table) throws SQLException {
        Connection connection = this.connect();

        ResultSet result = connection.getMetaData().getColumns(null, null, table, null);

        List<Trio<String, String, Integer>> columns = new ArrayList<>();

        while(result.next())
            columns.add(Trio.of(result.getString("COLUMN_NAME"), result.getString("TYPE_NAME"), result.getInt("COLUMN_SIZE")));

        connection.close();

        return columns;
    }

    //

    public synchronized boolean columnExists(String table, String column) throws SQLException {
        List<Trio<String, String, Integer>> columns = this.columns(table);

        boolean column_found = false;

        int i = 0;
        while(i < columns.size() && !column_found) {
            Trio<String, String, Integer> column_i = columns.get(i);
            column_found = column_i.one().equalsIgnoreCase(column);
            ++i;
        }

        return column_found;
    }

    //

    public synchronized void addColumnIfNotExists(String table, String column, String type) throws SQLException {
        if(this.columnExists(table, column)) return;

        this.execute(
            RequestType.UPDATE,
            "ALTER TABLE " + table + " ADD " + column + " " + type
        ).closer().close();
    }

    //

    public static DatabaseManager forMySQL(String hostname, int port, String dbname, String username, String password) {
        return new DatabaseManager(System.MYSQL, hostname, port, dbname, username, password, null);
    }

    public static DatabaseManager forSQLite(String dbpath) throws IOException {
        return forSQLite(Path.of(dbpath));
    }

    public static DatabaseManager forSQLite(Path dbpath) throws IOException {
        if(!Files.exists(dbpath))
            Files.createFile(dbpath);

        return new DatabaseManager(System.SQLITE, null, 0, null, null, null, dbpath);
    }

}
