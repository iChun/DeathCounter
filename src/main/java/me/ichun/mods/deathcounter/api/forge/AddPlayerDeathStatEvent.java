package me.ichun.mods.deathcounter.api.forge;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * AddPlayerDeathStatEvent is fired when DeathCounter receives
 * a LivingDeathEvent matching a player and would like to add
 * a stat to the player's death count. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the stat is not increased.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class AddPlayerDeathStatEvent extends PlayerEvent
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
