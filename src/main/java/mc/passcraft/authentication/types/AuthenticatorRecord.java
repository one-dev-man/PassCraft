package mc.passcraft.authentication.types;

import mc.passcraft.authentication.authenticators.DiscordAuthenticator;
import mc.passcraft.authentication.authenticators.PasswordAuthenticator;
import mc.passcraft.authentication.authenticators.PremiumAuthenticator;

public record AuthenticatorRecord(
        PremiumAuthenticator premium,
        PasswordAuthenticator password,
        DiscordAuthenticator discord
) {

    public static AuthenticatorRecord of(PremiumAuthenticator premium, PasswordAuthenticator password, DiscordAuthenticator discord) {
        return new AuthenticatorRecord(premium, password, discord);
    }

}
