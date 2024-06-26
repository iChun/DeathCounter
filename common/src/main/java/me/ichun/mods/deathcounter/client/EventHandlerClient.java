package me.ichun.mods.deathcounter.client;

import me.ichun.mods.deathcounter.common.DeathCounter;
import net.minecraft.network.chat.Component;

public abstract class EventHandlerClient
{
    public boolean disableMessageReceived(Component message, boolean isOverlay)
    {
        return DeathCounter.configClient.hideDeathCounterMessages && DeathCounter.deathHandler.isMessageOurs(message);
    }
}
