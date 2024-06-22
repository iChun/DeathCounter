package me.ichun.mods.deathcounter.loader.fabric.client;

import me.ichun.mods.deathcounter.client.EventHandlerClient;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class EventHandlerClientFabric extends EventHandlerClient
{
    public EventHandlerClientFabric()
    {
        ClientReceiveMessageEvents.ALLOW_GAME.register(((message, overlay) -> !disableMessageReceived(message, overlay))); //Inverse, return FALSE to cancel even
    }
}
