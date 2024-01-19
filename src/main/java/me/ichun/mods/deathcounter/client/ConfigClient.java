package me.ichun.mods.deathcounter.client;

import me.ichun.mods.deathcounter.common.core.Config;

public abstract class ConfigClient
{
    public Config.ConfigWrapper<Boolean> hideDeathCounterMessages;

    protected static class Reference
    {
        public static final String HIDE_DEATH_COUNTER_MESSAGES_COMMENT = "Enable this and death counter messages will not show up in chat.";
    }
}
