package me.ichun.mods.deathcounter.loader.fabric.client;

import me.ichun.mods.deathcounter.client.EventHandlerClient;
import me.ichun.mods.ichunutil.loader.fabric.event.client.FabricClientEvents;

public class EventHandlerClientFabric extends EventHandlerClient
{
    public EventHandlerClientFabric()
    {
        FabricClientEvents.ALLOW_GAME.register(((message, overlay) -> !disableMessageReceived(message, overlay))); //Inverse, return FALSE to cancel even
    }
}
