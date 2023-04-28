package org.onedevman.mc.plugins.passcraft.authentication.chestinterface.types;

public record AuthInputRecord(PasswordInput password, DiscordTagInput discordtag) {

    public static AuthInputRecord of(PasswordInput password, DiscordTagInput discordtag) {
        return new AuthInputRecord(password, discordtag);
    }

}
