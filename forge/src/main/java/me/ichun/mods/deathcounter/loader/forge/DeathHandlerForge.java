package me.ichun.mods.deathcounter.loader.forge;

import me.ichun.mods.deathcounter.api.forge.AddPlayerDeathStatEvent;
import me.ichun.mods.deathcounter.common.core.DeathHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathHandlerForge extends DeathHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeathEvent(LivingDeathEvent event)
    {
        super.onLivingDeath(event.getEntity(), event.getSource());
    }

    @Override
    public boolean postAddPlayerDeathStatEvent(ServerPlayer player, DamageSource source)
    {
        return MinecraftForge.EVENT_BUS.post(new AddPlayerDeathStatEvent(player, source));
    }
}
