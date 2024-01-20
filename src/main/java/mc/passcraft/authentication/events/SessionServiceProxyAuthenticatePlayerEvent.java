package mc.passcraft.authentication.events;

import mc.passcraft.types.events.Event;

import com.mojang.authlib.GameProfile;

public class SessionServiceProxyAuthenticatePlayerEvent extends Event {

    private final String username;
    private GameProfile profile;

    //

    public SessionServiceProxyAuthenticatePlayerEvent(String username, GameProfile profile) {
        super(true);

        this.username = username;
        this.profile = profile;
    }

    //

    public String getUsername() { return this.username; }

    public GameProfile getProfile() { return this.profile; }
    public void setProfile(GameProfile profile) { this.profile = profile; }

}
