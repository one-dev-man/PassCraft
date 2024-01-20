package mc.passcraft.authentication.authenticators;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import mc.passcraft.PluginMain;
import mc.passcraft.authentication.AuthManager;
import mc.passcraft.authentication.types.AuthCallback;
import mc.passcraft.authentication.types.AuthQueue;
import mc.passcraft.authentication.types.AuthResult;
import mc.passcraft.database.DatabaseManager;
import mc.passcraft.database.DiscordTagManager;
import mc.passcraft.utils.synchronization.Async;
import mc.passcraft.utils.synchronization.Sync;

import java.awt.*;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class DiscordAuthenticator extends ListenerAdapter {

    private interface IDefaultEmbed {
        MessageEmbed build(Object... args);
    }

    public enum DefaultEmbed implements IDefaultEmbed {
        LINKING_REQUEST() {
            public MessageEmbed build(Object... args) {
                return this.build((Player) args[0], (User) args[1]);
            }

            private MessageEmbed build(Player player, User discord_user) {
                return new EmbedBuilder()
                    .setColor(Color.decode("#ff9933")).setTitle("Linking request")
                    .setDescription("Do you want to link <@" + discord_user.getId() + "> to the minecraft player `" + player.getName() + "` ?").build();
            }
        },
        LINKING_REQUEST_TIMED_OUT() {
            public MessageEmbed build(Object... args) {
                return new EmbedBuilder().setColor(Color.decode("#ff0000")).setTitle("Linking request timed out.").build();
            }
        },
        LINKING_REQUEST_ACCEPTED() {
            public MessageEmbed build(Object... args) {
                return new EmbedBuilder().setColor(Color.decode("#00ff00")).setTitle("Linking discord request accepted.").build();
            }
        },
        LINKING_REQUEST_REJECTED() {
            public MessageEmbed build(Object... args) {
                return new EmbedBuilder().setColor(Color.decode("#ff0000")).setTitle("Linking discord request rejected.").build();
            }
        },
        LINKING_REQUEST_FAILED() {
            public MessageEmbed build(Object... args) {
                return new EmbedBuilder().setColor(Color.decode("#ff0000")).setTitle("An error happened while linking your account.").build();
            }
        },

        //

        LOGIN_REQUEST() {
            public MessageEmbed build(Object... args) {
                return this.build((Player) args[0]);
            }

            private MessageEmbed build(Player player) {
                return new EmbedBuilder()
                    .setColor(Color.decode("#ffff00")).setTitle("Login account")
                    .setDescription("Do you want to login `" + player.getName() + "` ?").build();
            }
        },
        LOGIN_REQUEST_TIMED_OUT() {
            public MessageEmbed build(Object... args) {
                return new EmbedBuilder().setColor(Color.decode("#ff0000")).setTitle("Login request timed out.").build();
            }
        },
        LOGIN_REQUEST_ACCEPTED() {
            public MessageEmbed build(Object... args) {
                return new EmbedBuilder().setColor(Color.decode("#00ff00")).setTitle("Login request accepted.").build();
            }
        },
        LOGIN_REQUEST_REJECTED() {
            public MessageEmbed build(Object... args) {
                return new EmbedBuilder().setColor(Color.decode("#ff0000")).setTitle("Login request rejected.").build();
            }
        },
        LOGIN_REQUEST_FAILED() {
            public MessageEmbed build(Object... args) {
                return new EmbedBuilder().setColor(Color.decode("#ff0000")).setTitle("An error happened while linking your account.").build();
            }
        };

        //

        DefaultEmbed() {

        }
    }

    //

    public enum Buttons {
        LOGIN_YES("login-yes", ButtonStyle.SUCCESS),
        LOGIN_NO("login-no", ButtonStyle.DANGER),

        LINKING_YES("linking-yes", ButtonStyle.SUCCESS),
        LINKING_NO("linking-no", ButtonStyle.DANGER);

        //

        public final String idprefix;

        public final ButtonStyle style;

        //

        Buttons(String idprefix, ButtonStyle style) {
            this.idprefix = idprefix;
            this.style = style;
        }

        //

        public Button create(String data, String label) {
            return Button.of(this.style, this.idprefix + ";" + data, label);
        }

        public boolean same(Button button) {
            return Objects.requireNonNull(button.getId()).startsWith(this.idprefix + ";");
        }

        public String dataFrom(Button button) {
            return Objects.requireNonNull(button.getId()).substring((this.idprefix + ";").length());
        }

    }

    //

    private record RequestData(String user, String discordtag, AuthCallback response) {

        public static RequestData of(String user, String discord_tag, AuthCallback callback) {
            return new RequestData(user, discord_tag, callback);
        }

    }

    //

    private final Map<String, RequestData> request_map = new HashMap<>();

    //

    private final DiscordAuthenticator _this = this;

    private final AuthQueue authqueue;
    private final Consumer<Player> validateAuthentication;
    public final DiscordTagManager discordTagManager;

    private String _token;

    private JDA _discord_client;

    private int _request_timeout_delay;

    private boolean _running = false;

    //

    public DiscordAuthenticator(
        AuthQueue authqueue,
        Consumer<Player> validate_authentication,
        DatabaseManager dbmanager,
        String dbtable,
        String usercolumn,
        String discordcolumn
    ) throws SQLException {
        this.authqueue = authqueue;
        this.validateAuthentication = validate_authentication;
        this.discordTagManager = new DiscordTagManager(dbmanager, dbtable, usercolumn, discordcolumn);
    }

    //

    public JDA discordClient() { return this._discord_client; }

    public String token() { return this._token; }

    public String botTag() { return this.discordClient().getSelfUser().getAsTag(); }

    public int getRequestTimeoutDelay() { return this._request_timeout_delay; }
    public void setRequestTimeoutDelay(int seconds) { this._request_timeout_delay = seconds; }

    public boolean running() { return this._running; }

    //

    public void start(String token) throws InterruptedException {
        this._token = token;

        if(this.token().length() == 0) {
            PluginMain.logger().warning("Invalid discord bot token found in plugins configuration, discord authentication will be disabled.");
            this.stop();
            return;
        }

        JDABuilder discord_client_builder = JDABuilder.createDefault(this.token());
        discord_client_builder.setIdle(false);

        EnumSet<GatewayIntent> required_intents = EnumSet.of(
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.MESSAGE_CONTENT
        );

        discord_client_builder.enableIntents(required_intents);
        discord_client_builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        PluginMain.logger().info("Starting discord authenticator.");

        try { this._discord_client = discord_client_builder.build(); }
        catch (InvalidTokenException e) {
            PluginMain.logger().warning("Invalid discord bot token found in plugins configuration, discord authentication will be disabled.");
            this.stop();
            return;
        }
        catch (Exception e) { throw e; }

        long login_timeout_delay = 5 * 20L;

        Sync.timeout(() -> {
            if(!_this.discordClient().getStatus().equals(JDA.Status.CONNECTED)) {
                PluginMain.logger().warning("The discord authentication will be disabled because the bot login timed out (" + (login_timeout_delay / 20L) + "s).");
                PluginMain.logger().warning("This issue could be due to lack of privileged gateway intents from the discord bot.");
                PluginMain.logger().warning("If this issue is effectively caused for this reasons, please check if your bot can handle all these gateway intents :");

                for (GatewayIntent required_intent : required_intents)
                    PluginMain.logger().warning(" - " + required_intent.name());

                try { _this.stop(); }
                catch (InterruptedException e) { throw new RuntimeException(e); }

                return;
            }

            this.discordClient().addEventListener(this);

            this._running = true;
        }, login_timeout_delay);
    }

    //

    public void stop() throws InterruptedException {
        this._running = false;

        PluginMain.logger().info("Stopping discord authenticator.");

        if(this.discordClient() == null) return;

        this.discordClient().shutdown();

        if (!this.discordClient().awaitShutdown(Duration.ofSeconds(5))) {
            this.discordClient().shutdownNow();
            this.discordClient().awaitShutdown();
        }
    }

    //

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        try {
            handleRequestResponses(event);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //

    private void handleRequestResponses(ButtonInteractionEvent event) throws SQLException {
        if(handleLoginRequestResponse(event)) return;
        if(handleLinkingRequestResponse(event)) return;
    }

    private boolean handleLoginRequestResponse(ButtonInteractionEvent event) {
        Button button = event.getButton();

        String request_id;

        request_id = Buttons.LOGIN_YES.same(button) ? Buttons.LOGIN_YES.dataFrom(button)
                     : Buttons.LOGIN_NO.same(button) ? Buttons.LOGIN_NO.dataFrom(button)
                     : null;

        if(request_id == null) return false;

        RequestData request_data = this.request_map.remove(request_id);

        if(request_data == null) return false;

        MessageEmbed response_embed = null;
        AuthResult response_result = null;

        if(Buttons.LOGIN_YES.same(button)) {
            response_embed = DefaultEmbed.LOGIN_REQUEST_ACCEPTED.build();
            response_result = AuthResult.SUCCESS;
        }
        else if(Buttons.LOGIN_NO.same(button)) {
            response_embed = DefaultEmbed.LOGIN_REQUEST_REJECTED.build();
            response_result = AuthResult.REJECTED;
        }

        if(response_result != null) {
            event.getMessage().delete().queue();
            event.getChannel().sendMessageEmbeds(response_embed).queue();

            request_data.response().call(response_result);

            return true;
        }

        return false;
    }

    private boolean handleLinkingRequestResponse(ButtonInteractionEvent event) throws SQLException {
        Button button = event.getButton();

        String request_id = Buttons.LINKING_YES.same(button) ? Buttons.LINKING_YES.dataFrom(button)
                            : Buttons.LINKING_NO.same(button) ? Buttons.LINKING_NO.dataFrom(button)
                            : null;

        if(request_id == null) return false;

        RequestData request_data = this.request_map.remove(request_id);

        if(request_data == null) return false;

        String player_uuid = request_data.user();
        String discord_tag = request_data.discordtag();

        if(Buttons.LINKING_YES.same(button)) {
            event.getMessage().delete().queue();

            MessageEmbed response_embed;
            AuthResult response_result;

            if(this.discordTagManager.setTag(player_uuid, discord_tag)) {
                response_embed = DefaultEmbed.LINKING_REQUEST_ACCEPTED.build();
                response_result = AuthResult.SUCCESS;
            }
            else {
                response_embed = DefaultEmbed.LINKING_REQUEST_FAILED.build();
                response_result = AuthResult.FAILED;
            }

            event.getChannel().sendMessageEmbeds(response_embed).queue();
            request_data.response().call(response_result);

            return true;
        }
        else if(Buttons.LINKING_NO.same(button)) {
            event.getMessage().delete().queue();
            event.getChannel().sendMessageEmbeds(DefaultEmbed.LINKING_REQUEST_REJECTED.build()).queue();

            request_data.response().call(AuthResult.REJECTED);

            return true;
        }

        return false;
    }

    //

    public User findUserByTag(String discord_tag) {
        if(!this.running())
            return null;

        User result = null;

        List<Guild> guilds = this.discordClient().getGuilds();

        int i = 0;
        while(i < guilds.size() && result == null) {
            Guild guild = guilds.get(i);
            List<Member> members = guild.loadMembers().get();

            int j = 0;
            while(j < members.size() && result == null) {
                Member member = members.get(j);
                if(member.getUser().getAsTag().equals(discord_tag))
                    result = member.getUser();
                ++j;
            }
            ++i;
        }

        return result;
    }

    //

    public boolean isLinked(Player player) throws SQLException {
        return this.discordTagManager.hasTag(player.getUniqueId().toString());
    }

    //

    private void launchRequestTimeout(String request_id, AuthCallback callback) {
        Sync.timeout(() -> {
            if(this.request_map.remove(request_id) == null) return;
            callback.call(AuthResult.TIMED_OUT);
        }, this.getRequestTimeoutDelay() * 20L);
    }

    //

    public void login(Player player, Runnable request_callback, AuthCallback response_callback) {
        Async.call(() -> {
            try { _this._login(player, request_callback, response_callback); }
            catch (SQLException e) { throw new RuntimeException(e); }
        });
    }

    private void _login(Player player, Runnable request_callback, AuthCallback response_callback) throws SQLException {
        final AuthCallback final_response_callback = result -> {
            if(result.equals(AuthResult.SUCCESS))
                this.validateAuthentication.accept(player);

            response_callback.call(result);
        };

        if(!this.running()) {
            response_callback.call(AuthResult.DISCORD_AUTH_DISABLED);
            return;
        }

        if(!this.isLinked(player)) {
            final_response_callback.call(AuthResult.NOT_LINKED);
            return;
        }

        String request_id = String.valueOf(System.currentTimeMillis());

        String discord_tag = this.discordTagManager.getTag(player.getUniqueId().toString());
        User discord_user = this.findUserByTag(discord_tag);

        if(discord_user == null) {
            final_response_callback.call(AuthResult.DISCORD_USER_NOT_FOUND);
            return;
        }

        PrivateChannel dm = discord_user.openPrivateChannel().complete();

        Message[] request_message_pointer = { null };
        Sync.call(() -> {
            request_message_pointer[0] = dm.sendMessageEmbeds(DefaultEmbed.LOGIN_REQUEST.build(player))
            .addActionRow(Buttons.LOGIN_YES.create(request_id, "Yes"), Buttons.LOGIN_NO.create(request_id, "No")).complete();

            this.request_map.put(request_id, RequestData.of(null, null, final_response_callback));
        });

        request_callback.run();

        this.launchRequestTimeout(request_id, result -> {
            Message request_message = request_message_pointer[0];
            if(request_message != null) {
                request_message.delete().queue();
                request_message.getChannel().sendMessageEmbeds(DefaultEmbed.LOGIN_REQUEST_TIMED_OUT.build()).queue();
            }

            final_response_callback.call(result);
        });
    }

    //

    public void link(Player player, String discord_tag, Runnable request_callback, AuthCallback response_callback) {
        Async.call(() -> {
            try { _this._link(player, discord_tag, request_callback, response_callback); }
            catch (SQLException e) { throw new RuntimeException(e); }
        });
    }

    private void _link(Player player, String discord_tag, Runnable request_callback, AuthCallback response_callback) throws SQLException {
        if(!this.running()) {
            response_callback.call(AuthResult.DISCORD_AUTH_DISABLED);
            return;
        }

        if(!AuthManager.isLogged(this.authqueue, player)) {
            response_callback.call(AuthResult.NOT_LOGGED);
            return;
        }

        String request_id = String.valueOf(System.currentTimeMillis());

        if(this.isLinked(player)) {
            response_callback.call(AuthResult.ALREADY_LINKED);
            return;
        }

        if(this.discordTagManager.isTagUsed(discord_tag)) {
            response_callback.call(AuthResult.TAG_ALREADY_USED);
            return;
        }

        User discord_user = this.findUserByTag(discord_tag);

        if(discord_user == null) {
            response_callback.call(AuthResult.DISCORD_USER_NOT_FOUND);
            return;
        }

        PrivateChannel dm = discord_user.openPrivateChannel().complete();

        Message[] request_message_pointer = { null };
        Sync.call(() -> {
            request_message_pointer[0] = dm.sendMessageEmbeds(DefaultEmbed.LINKING_REQUEST.build(player, discord_user))
                                           .addActionRow(Buttons.LINKING_YES.create(request_id, "Yes"), Buttons.LINKING_NO.create(request_id, "No")).complete();

            this.request_map.put(request_id, RequestData.of(player.getUniqueId().toString(), discord_tag, response_callback));
        });

        request_callback.run();

        this.launchRequestTimeout(request_id, result -> {
            Message request_message = request_message_pointer[0];
            if(request_message != null) {
                request_message.delete().queue();
                request_message.getChannel().sendMessageEmbeds(DefaultEmbed.LINKING_REQUEST_TIMED_OUT.build()).queue();
            }

            response_callback.call(result);
        });
    }

    //

    public void unlink(Player player, AuthCallback response_callback) {
        Async.call(() -> {
            try { this._unlink(player, response_callback); }
            catch (SQLException e) { throw new RuntimeException(e); }
        });
    }

    private void _unlink(Player player, AuthCallback response_callback) throws SQLException {
        if(!this.running()) {
            response_callback.call(AuthResult.DISCORD_AUTH_DISABLED);
            return;
        }

        if(!AuthManager.isLogged(this.authqueue, player)) {
            response_callback.call(AuthResult.NOT_LOGGED);
            return;
        }
        String player_uuid = player.getUniqueId().toString();
        String tag = this.discordTagManager.getTag(player_uuid);
        PluginMain.logger().info("||| `" + tag + "`");
        PluginMain.logger().info("||| `" + (tag == null ? "really null" : tag.getClass()) + "`");
        PluginMain.logger().info("||| " + this.discordTagManager.hasTag(player_uuid));

        if(!this.isLinked(player)) {
            response_callback.call(AuthResult.NOT_LINKED);
            return;
        }

        if(!this.discordTagManager.deleteTag(player_uuid)) {
            response_callback.call(AuthResult.FAILED);
            return;
        }

        response_callback.call(AuthResult.SUCCESS);
    }

}
