package me.ichun.mods.deathcounter.loader.neoforge;

import me.ichun.mods.deathcounter.api.neoforge.AddPlayerDeathStatEvent;
import me.ichun.mods.deathcounter.common.core.DeathHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class DeathHandlerNeoForge extends DeathHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeathEvent(LivingDeathEvent event)
    {
        super.onLivingDeath(event.getEntity(), event.getSource());
    }

    @Override
    public boolean postAddPlayerDeathStatEvent(ServerPlayer player, DamageSource source)
    {
        return NeoForge.EVENT_BUS.post(new AddPlayerDeathStatEvent(player, source)).isCanceled();
    }
}
