package mc.passcraft.authentication;

import mc.passcraft.Locale;
import mc.passcraft.utils.resources.configuration.ConfigGetter;
import mc.passcraft.utils.resources.configuration.ConfigParser;

public class AuthManagerConfigGetter extends ConfigGetter {

    public class PremiumAuthenticatorConfigGetter extends ConfigGetter {
        private PremiumAuthenticatorConfigGetter(ConfigParser configParser) { super(configParser); }

        public boolean isPremiumAuthenticationEnabled() {
            return this.configParser.getBoolean(Locale.PluginConfig.AUTHENTICATION__PREMIUM__ENABLED);
        }

    }

    //

    public class PasswordAuthenticatorConfigGetter extends ConfigGetter {
        private PasswordAuthenticatorConfigGetter(ConfigParser configParser) { super(configParser); }

        public String dbtable() {
            return this.configParser.getString(Locale.PluginConfig.DATABASE__TABLES__PASSWORDS__NAME);
        }

        public String usercolumn() {
            return this.configParser.getString(Locale.PluginConfig.DATABASE__TABLES__PASSWORDS__COLUMNS__USER);
        }
        public String passwordcolumn() {
            return this.configParser.getString(Locale.PluginConfig.DATABASE__TABLES__PASSWORDS__COLUMNS__PASSWORD);
        }

    }

    //

    public class DiscordAuthenticatorConfigGetter extends ConfigGetter {
        private DiscordAuthenticatorConfigGetter(ConfigParser configParser) { super(configParser); }

        public String dbtable() {
            return this.configParser.getString(Locale.PluginConfig.DATABASE__TABLES__DISCORDS__NAME);
        }

        public String usercolumn() {
            return this.configParser.getString(Locale.PluginConfig.DATABASE__TABLES__DISCORDS__COLUMNS__USER);
        }
        public String discordcolumn() {
            return this.configParser.getString(Locale.PluginConfig.DATABASE__TABLES__DISCORDS__COLUMNS__DISCORD);
        }

        //

        public boolean enabled() { return this.configParser.getBoolean(Locale.PluginConfig.AUTHENTICATION__DISCORD__ENABLED); }

        public String bottoken() { return this.configParser.getString(Locale.PluginConfig.AUTHENTICATION__DISCORD__BOT__TOKEN); }

        public int requestTimeoutDelay() { return this.configParser.getInt(Locale.PluginConfig.AUTHENTICATION__DISCORD__BOT__REQUESTS_TIMEOUT_DELAY); }

    }
    //

    public class MessagesConfigGetter extends ConfigGetter {
        private MessagesConfigGetter(ConfigParser configParser) { super(configParser); }

        public String mustAuthenticateNotification() { return this.configParser.getString(Locale.PluginConfig.MESSAGES__MUST_AUTHENTICATE_NOTIFICATION); }

        public String authenticationTimedOutKick() { return this.configParser.getString(Locale.PluginConfig.MESSAGES__AUTHENTICATION_TIMED_OUT_KICK); }

    }

    //

    private final PremiumAuthenticatorConfigGetter premium;
    private final PasswordAuthenticatorConfigGetter password;
    private final DiscordAuthenticatorConfigGetter discord;
//    private final MessagesConfigGetter messages;

    //

    public AuthManagerConfigGetter(ConfigParser configParser) {
        super(configParser);

        this.premium = new PremiumAuthenticatorConfigGetter(configParser);
        this.password = new PasswordAuthenticatorConfigGetter(configParser);
        this.discord = new DiscordAuthenticatorConfigGetter(configParser);
//        this.messages = new MessagesConfigGetter(configParser);
    }

    //

    public ConfigParser configParser() { return this.configParser; }

    public PremiumAuthenticatorConfigGetter premium() { return this.premium; }
    public PasswordAuthenticatorConfigGetter password() { return this.password; }
    public DiscordAuthenticatorConfigGetter discord() { return this.discord; }
//    public MessagesConfigGetter messages() { return this.messages; }

    //

    public boolean interfaceEnabled() {
        return this.configParser.getBoolean(Locale.PluginConfig.AUTHENTICATION__INTERFACE__ENABLED);
    }

    public int reconnectionDelay() {
        return this.configParser.getInt(Locale.PluginConfig.AUTHENTICATION__RECONNECTION_DELAY);
    }

    public int timeoutKickDelay() {
        return this.configParser.getInt(Locale.PluginConfig.AUTHENTICATION__TIMEOUT_KICK_DELAY);
    }
    public int unregisteredKickDelay() {
        return this.configParser.getInt(Locale.PluginConfig.AUTHENTICATION__UNREGISTERED_KICK_DELAY);
    }

}
