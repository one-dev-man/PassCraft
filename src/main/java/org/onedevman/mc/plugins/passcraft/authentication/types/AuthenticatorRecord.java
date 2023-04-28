package org.onedevman.mc.plugins.passcraft.authentication.types;

import org.onedevman.mc.plugins.passcraft.authentication.authenticators.DiscordAuthenticator;
import org.onedevman.mc.plugins.passcraft.authentication.authenticators.PasswordAuthenticator;
import org.onedevman.mc.plugins.passcraft.authentication.authenticators.PremiumAuthenticator;

public record AuthenticatorRecord(
        PremiumAuthenticator premium,
        PasswordAuthenticator password,
        DiscordAuthenticator discord
) {

    public static AuthenticatorRecord of(PremiumAuthenticator premium, PasswordAuthenticator password, DiscordAuthenticator discord) {
        return new AuthenticatorRecord(premium, password, discord);
    }

}
