package me.ichun.mods.deathcounter.loader.fabric.client;

import me.ichun.mods.deathcounter.client.ConfigClient;
import me.ichun.mods.deathcounter.common.core.Config;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class ConfigClientFabric extends ConfigClient
        implements ConfigContainer
{
    public static General GENERAL = null;

    public me.lortseam.completeconfig.data.Config configInstance; // the config screen builder from completeconfig does not support multiple configs, modmenu doesn't handle multiple entrypoints properly.

    public ConfigClientFabric()
    {
        hideDeathCounterMessages = new Config.ConfigWrapper<>(() -> GENERAL.hideDeathCounterMessages, v -> GENERAL.hideDeathCounterMessages = v);
    }

    @ConfigContainer.Transitive
    @ConfigEntries(includeAll = true)
    public static class General implements ConfigGroup
    {
        public General()
        {
            GENERAL = this;
        }

        @Override
        public String getComment()
        {
            return "General configs that don't fit any other category.";
        }

        @ConfigEntry(nameKey = "prop.hideDeathCounterMessages.name", descriptionKey = "prop.hideDeathCounterMessages.desc", comment = Reference.HIDE_DEATH_COUNTER_MESSAGES_COMMENT)
        public boolean hideDeathCounterMessages = false;
    }
}
