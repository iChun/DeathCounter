package me.ichun.mods.deathcounter.loader.fabric;

import me.ichun.mods.deathcounter.api.fabric.DeathCounterEvents;
import me.ichun.mods.deathcounter.common.core.DeathHandler;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class DeathHandlerFabric extends DeathHandler
{
    public DeathHandlerFabric()
    {
        ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, damageAmount) -> {
            super.onLivingDeath(player, damageSource);
            return true;
        });

        ServerLifecycleEvents.SERVER_STARTING.register(super::onServerAboutToStart);
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) -> onRegisterCommands(dispatcher));
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
