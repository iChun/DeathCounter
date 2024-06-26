package me.ichun.mods.deathcounter.loader.fabric;

import me.ichun.mods.deathcounter.api.fabric.DeathCounterEvents;
import me.ichun.mods.deathcounter.common.core.DeathHandler;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class DeathHandlerFabric extends DeathHandler
{
    public DeathHandlerFabric()
    {
        ServerPlayerEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            super.onLivingDeath(entity, damageSource);
            return true;
        });
    }

    @Override
    public boolean postAddPlayerDeathStatEvent(ServerPlayer player, DamageSource source)
    {
        return DeathCounterEvents.PLAYER_DEATH.invoker().onPlayerDeath(player, source);
    }
}
