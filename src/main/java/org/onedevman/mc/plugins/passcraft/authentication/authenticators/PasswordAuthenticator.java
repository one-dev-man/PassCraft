package org.onedevman.mc.plugins.passcraft.authentication.authenticators;

import org.bukkit.entity.Player;
import org.onedevman.mc.plugins.passcraft.authentication.AuthManager;
import org.onedevman.mc.plugins.passcraft.authentication.types.AuthCallback;
import org.onedevman.mc.plugins.passcraft.authentication.types.AuthQueue;
import org.onedevman.mc.plugins.passcraft.authentication.types.AuthResult;
import org.onedevman.mc.plugins.passcraft.database.DatabaseManager;
import org.onedevman.mc.plugins.passcraft.database.PasswordManager;
import org.onedevman.mc.plugins.passcraft.utils.synchronization.Async;

import java.sql.SQLException;
import java.util.function.Consumer;

public class PasswordAuthenticator {

    private final AuthQueue authqueue;

    public final PasswordManager passwordManager;

    private final Consumer<Player> validateAuthentication;

    //

    public PasswordAuthenticator(
        AuthQueue authqueue,
        Consumer<Player> validate_authentication,
        DatabaseManager dbmanager,
        String dbtable,
        String usercolumn,
        String passwordcolumn
    ) throws SQLException {
        this.validateAuthentication = validate_authentication;
        this.authqueue = authqueue;
        this.passwordManager = new PasswordManager(dbmanager, dbtable, usercolumn, passwordcolumn);
    }

    //

    public boolean isFormatValid(String password) {
        return password.length() >= 4 && !password.contains(" ");
    }

    public boolean isRegistered(Player player) throws SQLException {
        return this.passwordManager.hasPassword(player.getUniqueId().toString());
    }

    //

    public void login(Player player, String password, AuthCallback callback) {
        login(player, password, callback, false);
    }

    public void login(Player player, String password, AuthCallback callback, boolean force) {
        Async.call(() -> this._login(player, password, callback, force));
    }

    private void _login(Player player, String password, AuthCallback callback, boolean force) {
        try {
            if(AuthManager.isLogged(this.authqueue, player) && !force) {
                callback.call(AuthResult.ALREADY_LOGGED);
                return;
            }

            if(!this.isRegistered(player)) {
                callback.call(AuthResult.NOT_REGISTERED);
                return;
            }

            if(!this.isFormatValid(password)) {
                callback.call(AuthResult.INVALID_PASSWORD_FORMAT);
                return;
            }

            String uuid = player.getUniqueId().toString();

            String stored_password = this.passwordManager.getPassword(uuid);

            if(!password.equals(stored_password)) {
                callback.call(AuthResult.WRONG_PASSWORD);
                return;
            }

            this.validateAuthentication.accept(player);

            callback.call(AuthResult.SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            callback.call(AuthResult.FAILED);
        }
    }

    //

    public void register(Player player, String password, AuthCallback callback) {
        Async.call(() -> this._register(player, password, callback));
    }

    private void _register(Player player, String password, AuthCallback callback) {
        try {
            if(!this.isFormatValid(password)) {
                callback.call(AuthResult.INVALID_PASSWORD_FORMAT);
                return;
            }

            if(this.isRegistered(player)) {
                callback.call(AuthResult.ALREADY_REGISTERED);
                return;
            }

            String uuid = player.getUniqueId().toString();

            this.passwordManager.setPassword(uuid, password);

            callback.call(AuthResult.SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            callback.call(AuthResult.FAILED);
        }
    }

    //

    public void unregister(Player player, String password, AuthCallback callback) {
        unregister(player, password, callback, false);
    }

    public void unregister(Player player, String password, AuthCallback callback, boolean force) {
        Async.call(() -> this._unregister(player, password, callback, force));
    }

    private void _unregister(Player player, String password, AuthCallback callback, boolean force) {
        try {
            if(!force && !this.isFormatValid(password)) {
                callback.call(AuthResult.INVALID_PASSWORD_FORMAT);
                return;
            }

            String uuid = player.getUniqueId().toString();

            String stored_password = this.passwordManager.getPassword(uuid);

            if(!force && stored_password == null) {
                callback.call(AuthResult.NOT_REGISTERED);
                return;
            }

            if(!force && !stored_password.equals(password)) {
                callback.call(AuthResult.WRONG_PASSWORD);
                return;
            }

            this.passwordManager.deletePasswod(uuid);

            callback.call(AuthResult.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(AuthResult.FAILED);
        }
    }

    //

}
