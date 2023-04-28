package org.onedevman.mc.plugins.passcraft.authentication.chestinterface;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.onedevman.mc.plugins.passcraft.Locale;
import org.onedevman.mc.plugins.passcraft.authentication.AuthManager;
import org.onedevman.mc.plugins.passcraft.authentication.chestinterface.menus.LoginMenu;
import org.onedevman.mc.plugins.passcraft.authentication.chestinterface.menus.RegisterMenu;
import org.onedevman.mc.plugins.passcraft.authentication.chestinterface.menus.UnregisterMenu;
import org.onedevman.mc.plugins.passcraft.utils.game.Notify;

import java.sql.SQLException;

public class AuthInterface {

    private final AuthInterface _this = this;

    public final AuthManager authmanager;

    private final LoginMenu login_menu;
    private final RegisterMenu register_menu;
    private final UnregisterMenu unregister_menu;

    //

    public AuthInterface(AuthManager auth_manager) {
        this.authmanager = auth_manager;

        //

        this.login_menu = new LoginMenu(this.authmanager);
        this.register_menu = new RegisterMenu(this.authmanager);
        this.unregister_menu = new UnregisterMenu(this.authmanager);
    }

    //

    public void openLogin(Player player) throws SQLException {
        if(!this.authmanager.authenticators.password().isRegistered(player)) {
            Notify.chat(
                player,
                this.authmanager.configParser.getString(Locale.CONFIGPATHS.MESSAGES.NOT_PASSWORD_REGISTERED),
                Sound.BLOCK_NOTE_BLOCK_BASS
            );
            return;
        }

        if(this.authmanager.authenticators.discord().isLinked(player))
            player.openInventory(this.login_menu.toBukkit());
        else
            player.openInventory(this.login_menu.inputs.password().toBukkit());
    }

    public void openRegister(Player player) throws SQLException {
        if(this.authmanager.authenticators.password().isRegistered(player))
            player.openInventory(this.register_menu.toBukkit());
        else
            player.openInventory(this.register_menu.inputs.password().toBukkit());
    }

    public void openUnregister(Player player) throws SQLException {
        if(!AuthManager.isLogged(this.authmanager.authqueue, player)) {
            Notify.chat(
                player,
                this.authmanager.configParser.getString(Locale.CONFIGPATHS.MESSAGES.LOGIN_REQUIRED),
                Sound.BLOCK_NOTE_BLOCK_BASS
            );
            return;
        }

        if(this.authmanager.authenticators.discord().isLinked(player))
            player.openInventory(this.unregister_menu.toBukkit());
        else
            player.openInventory(this.unregister_menu.inputs.password().toBukkit());
    }
}
