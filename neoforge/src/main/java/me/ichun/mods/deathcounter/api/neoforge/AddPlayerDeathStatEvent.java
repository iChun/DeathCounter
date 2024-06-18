package me.ichun.mods.deathcounter.api.neoforge;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * AddPlayerDeathStatEvent is fired when DeathCounter receives
 * a LivingDeathEvent matching a player and would like to add
 * a stat to the player's death count. <br>
 * <br>
 * This event is cancellable.<br>
 * If this event is canceled, the stat is not increased.<br>
 * <br>
 * This event does not have a result.<br>
 * <br>
 * This event is fired on the {@link NeoForge#EVENT_BUS}.
 **/
public class AddPlayerDeathStatEvent extends PlayerEvent
    implements ICancellableEvent
{
    private final DamageSource source;
    public AddPlayerDeathStatEvent(Player player, DamageSource source)
    {
        super(player);
        this.source = source;
    }

    public DamageSource getSource()
    {
        return source;
    }
}
