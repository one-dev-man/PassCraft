package mc.passcraft;

import mc.passcraft.utils.resources.configuration.ConfigParser;

import java.util.List;

public class Locale {

    public static final String PLUGIN_NAME = "PassCraft";

    public static final String PLUGIN_PREFIX = "§8[§7Pass§cCraft§8]§7";

    //

    public enum PluginConfig implements ConfigParser.Path {
        AUTHENTICATION(
            null,
            List.of(
                "╔═════════════════════════╗",
                "║ PASSCRAFT CONFIGURATION ║",
                "╚═════════════════════════╝",
                "", "|", ""
            )
        ),
            AUTHENTICATION__INTERFACE,
                AUTHENTICATION__INTERFACE__ENABLED(true),

            AUTHENTICATION__PREMIUM,
                AUTHENTICATION__PREMIUM__ENABLED(false),

            AUTHENTICATION__DISCORD,
                AUTHENTICATION__DISCORD__ENABLED(true),

                AUTHENTICATION__DISCORD__BOT,
                    AUTHENTICATION__DISCORD__BOT__TOKEN(""),
                    AUTHENTICATION__DISCORD__BOT__REQUESTS_TIMEOUT_DELAY(120, List.of("in seconds"), true),

            AUTHENTICATION__UNREGISTERED_KICK_DELAY(2, List.of("in seconds"), true),
            AUTHENTICATION__RECONNECTION_DELAY(45, List.of("in seconds"), true),
            AUTHENTICATION__TIMEOUT_KICK_DELAY(120, List.of("in seconds"), true),

        DATABASE,
            DATABASE__SYSTEM("sqlite", List.of("If you want to use MySQL, change it by `mysql` and set the MySQL configuration section properly."), true),

            DATABASE__SQLITE,
                DATABASE__SQLITE__PATH("%datafolder%/passcraft.db"),

            DATABASE__MYSQL,
                DATABASE__MYSQL__HOSTNAME(""),
                DATABASE__MYSQL__PORT(3306),
                DATABASE__MYSQL__DATABASE_NAME(""),
                DATABASE__MYSQL__USERNAME(""),
                DATABASE__MYSQL__PASSWORD(""),

            DATABASE__TABLES,
                DATABASE__TABLES__PASSWORDS,
                    DATABASE__TABLES__PASSWORDS__NAME("passcraft"),

                    DATABASE__TABLES__PASSWORDS__COLUMNS,
                        DATABASE__TABLES__PASSWORDS__COLUMNS__USER("user", List.of("PRIMARY"), true),
                        DATABASE__TABLES__PASSWORDS__COLUMNS__PASSWORD("password"),

                DATABASE__TABLES__DISCORDS,
                    DATABASE__TABLES__DISCORDS__NAME("passcraft"),

                    DATABASE__TABLES__DISCORDS__COLUMNS,
                        DATABASE__TABLES__DISCORDS__COLUMNS__USER("user", List.of("PRIMARY"), true),
                        DATABASE__TABLES__DISCORDS__COLUMNS__DISCORD("discord"),
        MESSAGES,
            MESSAGES__MUST_AUTHENTICATE_NOTIFICATION("%prefix% Please sign in before joining the server. Use §e/login§7 or §e/register§7 if you're not registered yet."),

            MESSAGES__INVALID_PASSWORD_FORMAT("%prefix% Invalid password : your password must contains at least 4 characters and no space."),

            MESSAGES__NOT_PASSWORD_REGISTERED("%prefix% Your account is not registered with a password on the server."),
            MESSAGES__DISCORD_NOT_LINKED("%prefix% Your account is not linked to a discord account on the server."),
            MESSAGES__DISCORD_USER_NOT_FOUND("%prefix% The specified discord account hasn't been found."),

            MESSAGES__ALREADY_PASSWORD_REGISTERED("%prefix% §cYour account is already registered with a password on the server."),
            MESSAGES__DISCORD_ALREADY_LINKED("%prefix% §cYour account is already linked to a discord account on the server."),
            MESSAGES__DISCORD_ALREADY_USED("%prefix% This discord account is already used by another player."),

            MESSAGES__REGISTER_SUCCESS("%prefix% §aYou're successfully registered !"),
            MESSAGES__REGISTER_FAILED("%prefix% §cAn error happened while registering your account. Please, contact an operator to fix the issue."),

            MESSAGES__DISCORD_LINKING_SUCCESS("%prefix% §aYour discord account has been successfully linked !"),
            MESSAGES__DISCORD_UNLINKING_SUCCESS("%prefix% §aYour discord account has been successfully unlinked !"),

            MESSAGES__DISCORD_LINKING_REFUSED("%prefix% §cLinking request rejected."),

            MESSAGES__DISCORD_LINKING_FAILED("%prefix% §cAn error happened while linking your discord account. Please, contact an administrator to solve the issue."),
            MESSAGES__DISCORD_UNLINKING_FAILED("%prefix% §cAn error happened while unlinking your discord account. Please, contact an administrator to solve the issue."),

            MESSAGES__DISCORD_REQUEST("%prefix% Now you will recieve a message from §e%discord-bot%§7 on discord."),
            MESSAGES__DISCORD_REQUEST_TIMED_OUT("%prefix% Discord request timed out."),

            MESSAGES__UNREGISTER_SUCCESS("%prefix% §aYou're successfully unregistered ! You'll be kicked in §e%#authentication.unregistered-kick-delay%§a seconds."),
            MESSAGES__UNREGISTER_FAILED("%prefix% §cAn error happened while unregistering your account. Please, contact an operator to fix the issue."),
            MESSAGES__UNREGISTER_KICK("§cYou must reconnect and register."),

            MESSAGES__LOGIN_SUCCESS("%prefix% §aYou're successfully logged in !"),
            MESSAGES__ALREADY_LOGGED("%prefix% You're already logged."),
            MESSAGES__LOGIN_FAILED("%prefix% §cAn error happened while logging in your account. Please, contact an operator to fix the issue."),

            MESSAGES__WRONG_PASSWORD("%prefix% Wrong password."),

            MESSAGES__DISCORD_AUTHENTICATION_DISABLED("%prefix% Discord authentication is currently disabled."),

            MESSAGES__AUTHENTICATION_TIMED_OUT_KICK("§cAuthentication timed out : too long to connect."),

            MESSAGES__LOGIN_REQUIRED("%prefix% §cYou need to be logged to perform this action."),
            MESSAGES__REGISTRATION_REQUIRED("%prefix% §cYou need to be registered to perform this action."),

        CONFIG_TOKENS,
            CONFIG_TOKENS__PREFIX(
                "§8[§7Pass§cCraft§8]§7",
                List.of(
                    "datafolder - native token",
                    "discord-bot - contextual token"
                )
            )
        ;

        private final String path;
        private final Object defaultValue;
        private final List<String> comments;
        private final boolean inlineComments;

        PluginConfig() { this(null); }
        <T> PluginConfig(T defaultValue) { this(defaultValue, null); }
        <T> PluginConfig(T defaultValue, List<String> comments) { this(defaultValue, comments, false); }

        <T> PluginConfig(T defaultValue, List<String> comments, boolean inlineComments) {
            this.path = this.name().replaceAll("__", ".").replaceAll("_", "-").toLowerCase();
            this.defaultValue = defaultValue;
            this.comments = comments;
            this.inlineComments = inlineComments;
        }

        public String getPath() { return this.path; }
        public <T> T defaultValue() { return (T) this.defaultValue; }
        public List<String> comments() { return this.comments; }
        public boolean areInlineComments() { return this.inlineComments; }
    }

    //

    public static class TEXTURES {
        public static class ICONS {
            public static class HEADS {
                public static final String DISCORD = "https://textures.minecraft.net/texture/739ee7154979b3f87735a1c8ac087814b7928d0576a2695ba01ed61631942045";
            }
        }
    }

}
