package me.ichun.mods.deathcounter.loader.forge;

import me.ichun.mods.deathcounter.api.forge.AddPlayerDeathStatEvent;
import me.ichun.mods.deathcounter.common.core.DeathHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathHandlerForge extends DeathHandler
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
        return MinecraftForge.EVENT_BUS.post(new AddPlayerDeathStatEvent(player, source));
    }

    @Override
    public boolean isFakePlayer(ServerPlayer player)
    {
        return player instanceof FakePlayer || player.connection == null; // || player.getName().getUnformattedComponentText().toLowerCase().startsWith("fakeplayer") || player.getName().getUnformattedComponentText().toLowerCase().startsWith("[minecraft]");
    }
}
