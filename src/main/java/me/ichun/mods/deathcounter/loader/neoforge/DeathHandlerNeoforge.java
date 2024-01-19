package me.ichun.mods.deathcounter.loader.neoforge;

import me.ichun.mods.deathcounter.api.neoforge.AddPlayerDeathStatEvent;
import me.ichun.mods.deathcounter.common.core.DeathHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.Locale;

public class DeathHandlerNeoforge extends DeathHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeathEvent(LivingDeathEvent event)
    {
        super.onLivingDeath(event.getEntity(), event.getSource());
    }

    @SubscribeEvent
    public void onServerAboutToStartEvent(ServerStartingEvent event)
    {
        super.onServerAboutToStart(event.getServer());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        super.onRegisterCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStoppingEvent(ServerStoppingEvent event)
    {
        super.onServerStopping();
    }

    @Override
    public boolean postAddPlayerDeathStatEvent(ServerPlayer player, DamageSource source)
    {
        return NeoForge.EVENT_BUS.post(new AddPlayerDeathStatEvent(player, source)).isCanceled();
    }

    @Override
    public boolean isFakePlayer(ServerPlayer player)
    {
        return player.connection == null || player.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("fakeplayer");
    }
}
