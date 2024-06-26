package me.ichun.mods.deathcounter.common;

import com.mojang.logging.LogUtils;
import me.ichun.mods.deathcounter.client.ConfigClient;
import me.ichun.mods.deathcounter.client.EventHandlerClient;
import me.ichun.mods.deathcounter.common.core.Config;
import me.ichun.mods.deathcounter.common.core.DeathHandler;
import org.slf4j.Logger;

public abstract class DeathCounter
{
    public static final String MOD_ID = "deathcounter";
    public static final String MOD_NAME = "Death Counter";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static DeathCounter modProxy;

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static EventHandlerClient eventHandlerClient;

    public static Config config;

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static ConfigClient configClient;

    public static DeathHandler deathHandler;

    public enum MessageType
    {
        NONE,
        SHORT,
        LONG
    }

    public enum BroadcastType
    {
        NONE,
        SELF,
        ALL
    }
}
