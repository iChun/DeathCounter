package me.ichun.mods.deathcounter.api.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

/**
 * AddPlayerDeathStatEvent is fired when DeathCounter receives
 * a LivingDeathEvent matching a player and would like to add
 * a stat to the player's death count. <br>
 * <br>
 * If any callback returns true, the stat is not increased.<br>
 **/
public class DeathCounterEvents
{
    public DeathCounterEvents(){} // No init

    public static final Event<PlayerDeath> PLAYER_DEATH = EventFactory.createArrayBacked(PlayerDeath.class, callbacks -> (player, source) -> {
        for(PlayerDeath callback : callbacks)
        {
            if(callback.onPlayerDeath(player, source))
            {
                return true;
            }
        }
        return false;
    });

    @FunctionalInterface
    public interface PlayerDeath
    {
        /**
         * Callback for when a player dies.
         * @param player The player that died
         * @param source The Damage Source that killed the player
         * @return true to cancel adding the death
         */
        boolean onPlayerDeath(ServerPlayer player, DamageSource source);
    }
}
