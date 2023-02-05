package me.ichun.mods.deathcounter.loader.fabric;

import me.ichun.mods.deathcounter.api.fabric.DeathCounterEvents;
import me.ichun.mods.deathcounter.common.core.DeathHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class DeathHandlerFabric extends DeathHandler
{
    public DeathHandlerFabric()
    {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            super.onLivingDeath(entity, damageSource);
            return true;
        });

        ServerLifecycleEvents.SERVER_STARTING.register(super::onServerAboutToStart);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> onRegisterCommands(dispatcher));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> onServerStopping());
    }

    @Override
    public boolean postAddPlayerDeathStatEvent(ServerPlayer player, DamageSource source)
    {
        return DeathCounterEvents.PLAYER_DEATH.invoker().onPlayerDeath(player, source);
    }

    @Override
    public boolean isFakePlayer(ServerPlayer player)
    {
        return player.connection == null;
    }
}
