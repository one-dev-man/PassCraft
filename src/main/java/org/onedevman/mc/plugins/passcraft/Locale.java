package org.onedevman.mc.plugins.passcraft;

public class Locale {

    public static String PLUGIN_NAME = "PassCraft";

    public static String PLUGIN_PREFIX = "§8[§7Pass§cCraft§8]§7";

    //

    public static class CONFIGPATHS {
        public static class AUTHENTICATION {
            public static final String __ROOT__ = "authentication";

            public static class INTERFACE {
                public static final String __ROOT__ = AUTHENTICATION.__ROOT__ + ".chest-interface";

                public static final String ENABLED = __ROOT__ + ".enabled";
            }

            public static class PREMIUM {
                public static final String __ROOT__ = AUTHENTICATION.__ROOT__ + ".premium";

                public static final String ENABLED = __ROOT__ + ".enabled";
            }

            public static class DISCORD {
                public static final String __ROOT__ = AUTHENTICATION.__ROOT__ + ".discord";

                public static final String ENABLED = __ROOT__ + ".enabled";

                public static class BOT {
                    public static final String __ROOT__ = DISCORD.__ROOT__ + ".bot";

                    public static final String TOKEN = __ROOT__ + ".token";

                    public static final String REQUESTS_TIMEOUT_DELAY = __ROOT__ + ".requests-timeout-delay";
                }
            }

            public static final String UNREGISTERED_KICK_DELAY = __ROOT__ + ".unregistered-kick-delay";

            public static final String RECONNECTION_DELAY = __ROOT__ + ".reconnection-delay";

            public static final String AUTHENTICATION_TIMEOUT_KICK_DELAY = __ROOT__ + ".authentication-timeout-kick-delay";
        }

        //

        public static class DATABASE {
            public static final String __ROOT__ = "database";

            public static final String SYSTEM = __ROOT__ + ".system";

            public static class SQLITE {
                public static final String __ROOT__ = DATABASE.__ROOT__ + ".sqlite";

                public static final String PATH = __ROOT__ + ".path";
            }

            public static class MYSQL {
                public static final String __ROOT__ = DATABASE.__ROOT__ + ".mysql";

                public static final String HOSTNAME = __ROOT__ + ".hostname";

                public static final String PORT = __ROOT__ + ".port";

                public static final String DATABASE_NAME = __ROOT__ + ".database-name";

                public static final String USERNAME = __ROOT__ + ".username";

                public static final String PASSWORD = __ROOT__ + ".password";
            }

            public static class TABLES {
                public static final String __ROOT__ = DATABASE.__ROOT__ + ".tables";

                public static class PASSWORDS {
                    public static final String __ROOT__ = TABLES.__ROOT__ + ".passwords";

                    public static final String NAME = __ROOT__ + ".name";

                    public static class COLUMNS {
                        public static final String __ROOT__ = PASSWORDS.__ROOT__ + ".columns";

                        public static final String USER = __ROOT__ + ".user";

                        public static final String PASSWORD = __ROOT__ + ".password";
                    }
                }

                public static class DISCORDS {
                    public static final String __ROOT__ = TABLES.__ROOT__ + ".discords";

                    public static final String NAME = __ROOT__ + ".name";

                    public static class COLUMNS {
                        public static final String __ROOT__ = DISCORDS.__ROOT__ + ".columns";

                        public static final String USER = __ROOT__ + ".user";

                        public static final String DISCORD = __ROOT__ + ".discord";
                    }
                }
            }
        }

        //

        public static class MESSAGES {
            public static final String __ROOT__ = "messages";

            public static final String MUST_AUTHENTICATE_NOTIFY = __ROOT__ + ".must-authenticate-notify";

            public static final String INVALID_PASSWORD_FORMAT = __ROOT__ + ".invalid-password-format";

            public static final String NOT_PASSWORD_REGISTERED = __ROOT__ + ".not-password-registered";
            public static final String DISCORD_NOT_LINKED = __ROOT__ + ".discord-not-linked";
            public static final String DISCORD_USER_NOT_FOUND = __ROOT__ + ".discord-user-not-found";

            public static final String ALREADY_PASSWORD_REGISTERED = __ROOT__ + ".already-password-registered";
            public static final String DISCORD_ALREADY_LINKED = __ROOT__ + ".discord-already-linked";
            public static final String DISCORD_ALREADY_USED = __ROOT__ + ".discord-already-used";

            public static final String REGISTER_SUCCESS = __ROOT__ + ".register-success";
            public static final String REGISTER_FAILED = __ROOT__ + ".register-failed";

            public static final String DISCORD_LINKING_SUCCESS = __ROOT__ + ".discord-linking-success";
            public static final String DISCORD_UNLINKING_SUCCESS = __ROOT__ + ".discord-unlinking-success";

            public static final String DISCORD_LINKING_REFUSED = __ROOT__ + ".discord-linking-refused";

            public static final String DISCORD_LINKING_FAILED = __ROOT__ + ".discord-linking-failed";
            public static final String DISCORD_UNLINKING_FAILED = __ROOT__ + ".discord-unlinking-failed";

            public static final String DISCORD_REQUEST = __ROOT__ + ".discord-request";
            public static final String DISCORD_REQUEST_TIMED_OUT = __ROOT__ + ".discord-request-timed-out";

            public static final String UNREGISTER_SUCCESS = __ROOT__ + ".unregister-success";
            public static final String UNREGISTER_FAILED = __ROOT__ + ".unregister-failed";
            public static final String UNREGISTER_KICK = __ROOT__ + ".unregister-kick";

            public static final String LOGIN_SUCCESS = __ROOT__ + ".login-success";
            public static final String ALREADY_LOGGED = __ROOT__ + ".already-logged";
            public static final String LOGIN_FAILED = __ROOT__ + ".login-failed";

            public static final String WRONG_PASSWORD = __ROOT__ + ".wrong-password";

            public static final String DISCORD_AUTH_DISABLED = __ROOT__ + ".discord-auth-disabled";

            public static final String AUTHENTICATION_TIMED_OUT_KICK = __ROOT__ + ".authentication-timed-out-kick";

            public static final String LOGIN_REQUIRED = __ROOT__ + ".login-required";
            public static final String REGISTRATION_REQUIRED = __ROOT__ + ".registration-required";
        }

        //

        public static final String CONFIG_TOKENS = "config-tokens";
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
