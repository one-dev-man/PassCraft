package mc.passcraft.utils.game.vanisher;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import mc.passcraft.types.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum VanisherPropertyAffector {
//    CanPickupItem(false, true) {
//        @Override
//        protected Object affection(Player player) {
//            boolean result = player.getCanPickupItems();
//            player.setCanPickupItems(false);
//            return result;
//        }
//
//        @Override
//        protected void disaffection(Player player, Object value, boolean forced) {
//            player.setCanPickupItems((boolean) value);
//        }
//    },

    Collidable(false, true) {
        @Override
        protected Object affection(Player player) {
            boolean result = player.isCollidable();
            player.setCollidable(false);
            return result;
        }

        @Override
        protected void disaffection(Player player, Object value, boolean forced) {
            player.setCollidable((boolean) value);
        }
    },

    Flying(true, false, 2L) {
        @Override
        protected Object affection(Player player) {
            Pair<Boolean, Boolean> result = Pair.of(player.getAllowFlight(), player.isFlying());
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlying(result.last());
            return result;
        }

        @Override
        protected void disaffection(Player player, Object value, boolean forced) {
            Pair<Boolean, Boolean> _value = (Pair<Boolean, Boolean>) value;

            player.setFlying(_value.last());
            player.setAllowFlight(_value.first());

            if(
                forced
                && ( player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE) )
            )
                player.setAllowFlight(false);
        }
    },

    Invulnerable() {
        @Override
        protected Object affection(Player player) {
            boolean result = player.isInvulnerable();
            player.setInvulnerable(true);
            return result;
        }

        @Override
        protected void disaffection(Player player, Object value, boolean forced) {
            player.setInvulnerable(forced ? false : (boolean) value);
        }
    },

    Invisible() {
        @Override
        protected Object affection(Player player) {
            boolean result = player.isInvisible();
            player.setInvisible(true);
            return result;
        }

        @Override
        protected void disaffection(Player player, Object value, boolean forced) {
            if(forced)
                player.setInvisible(false);
            else
                player.setInvisible((Boolean) value);
        }
    },

    Health() {
        @Override
        protected Object affection(Player player) {
            double result = player.getHealth();
            player.setHealth(20.0);
            return result;
        }

        @Override
        protected void disaffection(Player player, Object value, boolean forced) {
            player.setHealth((double) value);
        }
    },

    FoodLevel() {
        @Override
        protected Object affection(Player player) {
            int result = player.getFoodLevel();
            player.setFoodLevel(20);
            return result;
        }

        @Override
        protected void disaffection(Player player, Object value, boolean forced) {
            player.setFoodLevel((int) value);
        }
    },

    Exp() {
        @Override
        protected Object affection(Player player) {
            float result = player.getExp();
            player.setExp(0);
            return result;
        }

        @Override
        protected void disaffection(Player player, Object value, boolean forced) {
            player.setExp((float) value);
        }
    };

    //

    private final Map<String, Object> affected_players = new HashMap<>();

    public final boolean playerUpdateSensitive;

    public final boolean phantomModeOnly;

    public final long differed;

    //

    VanisherPropertyAffector() {
        this(false);
    }

    VanisherPropertyAffector(boolean player_update_sensitive) {
        this(player_update_sensitive, false);
    }

    VanisherPropertyAffector(boolean player_update_sensitive, boolean phantom_mode_only) {
        this(player_update_sensitive, phantom_mode_only, 0L);
    }

    VanisherPropertyAffector(boolean player_update_sensitive, boolean phantom_mode_only, long differed) {
        this.playerUpdateSensitive = player_update_sensitive;
        this.phantomModeOnly = phantom_mode_only;
        this.differed = differed;
    }

    //

    protected abstract Object affection(Player player);

    protected abstract void disaffection(Player player, Object value, boolean forced);

    //

    public void affect(String uuid) {
        affect(Bukkit.getPlayer(UUID.fromString(uuid)));
    }

    public void affect(Player player) {
        String uuid = player.getUniqueId().toString();

        Object value = this.affection(player);

        if(!this.affected_players.containsKey(uuid))
            this.affected_players.put(uuid, value);
    }

    public void disaffect(String uuid) {
        disaffect(Bukkit.getPlayer(UUID.fromString(uuid)));
    }

    public void disaffect(Player player) {
        this.disaffect(player, false);
    }

    public void disaffect(Player player, boolean forced) {
        String uuid = player.getUniqueId().toString();

        if(this.affected_players.containsKey(uuid))
            disaffection(player, this.affected_players.remove(uuid), forced);
    }

}
