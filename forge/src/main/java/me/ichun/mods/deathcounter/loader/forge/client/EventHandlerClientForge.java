package me.ichun.mods.deathcounter.loader.forge.client;

import me.ichun.mods.deathcounter.client.EventHandlerClient;
import me.ichun.mods.ichunutil.loader.forge.event.client.ClientSystemChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerClientForge extends EventHandlerClient
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
