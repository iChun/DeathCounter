package me.ichun.mods.deathcounter.client;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class ConfigClient extends ConfigBase
{
    public boolean hideDeathCounterMessages = false;

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

    @Override
    public Type getConfigType()
    {
        return Type.CLIENT;
    }
}
