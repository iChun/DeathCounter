package me.ichun.mods.deathcounter.common.core;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import org.jetbrains.annotations.NotNull;

public class Config extends ConfigBase
{
    public DeathCounter.MessageType messageType = DeathCounter.MessageType.LONG;

    public DeathCounter.BroadcastType broadcastOnDeath = DeathCounter.BroadcastType.NONE;

    @Prop(min = 1, max = 50)
    public int leaderboardCount = 5;

    public boolean singleSession = false;

    @Prop(min = 0, max = 4)
    public int commandPermissionLevel = 1;

    @NotNull
    @Override
    public String getModId()
    {
        return DeathCounter.MOD_ID;
    }

    @NotNull
    @Override
    public String getConfigName()
    {
        return DeathCounter.MOD_NAME;
    }
}
