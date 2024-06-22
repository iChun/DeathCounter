package me.ichun.mods.deathcounter.loader.neoforge.client;

import me.ichun.mods.deathcounter.client.EventHandlerClient;
import me.ichun.mods.ichunutil.loader.neoforge.event.client.ClientSystemChatEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class EventHandlerClientNeoForge extends EventHandlerClient
{
    @SubscribeEvent
    public void onReceiveSystemChat(ClientSystemChatEvent event)
    {
        if(disableMessageReceived(event.getComponent(), event.isOverlay()))
        {
            event.setCanceled(true);
        }
    }
}
