package mc.passcraft.authentication.chestinterface.components;

public record AuthButtonRecord(PasswordButton password, DiscordButton discord) {

    public static AuthButtonRecord of(PasswordButton password, DiscordButton discord) {
        return new AuthButtonRecord(password, discord);
    }

}
