package me.ichun.mods.deathcounter.loader.fabric;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.core.Config;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class ConfigFabric extends Config
        implements ConfigContainer
{
    public static General GENERAL = null;

    public ConfigFabric()
    {
        messageType = new ConfigWrapper<>(() -> GENERAL.messageType, v -> GENERAL.messageType = v);
        leaderboardCount = new ConfigWrapper<>(() -> GENERAL.leaderboardCount, v -> GENERAL.leaderboardCount = v);
        singleSession = new ConfigWrapper<>(() -> GENERAL.singleSession, v -> GENERAL.singleSession = v);
        commandPermissionLevel = new ConfigWrapper<>(() -> GENERAL.commandPermissionLevel, v -> GENERAL.commandPermissionLevel = v);
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

        @ConfigEntry(nameKey = "config.deathcounter.prop.messageType.name", descriptionKey = "config.deathcounter.prop.messageType.desc", comment = "What kind of message should we send players when they die?")
        public DeathCounter.MessageType messageType = DeathCounter.MessageType.LONG;

        @ConfigEntry(nameKey = "config.deathcounter.prop.leaderboardCount.name", descriptionKey = "config.deathcounter.prop.leaderboardCount.desc", comment = "Number of names to show in the leaderboard")
        @ConfigEntry.BoundedInteger(min = 1, max = 50)
        public int leaderboardCount = 5;

        @ConfigEntry(nameKey = "config.deathcounter.prop.singleSession.name", descriptionKey = "config.deathcounter.prop.singleSession.desc", comment = "Do not persist deaths across sessions? Turning this on disables saving deaths to server folder.")
        public boolean singleSession = false;

        @ConfigEntry(nameKey = "config.deathcounter.prop.commandPermissionLevel.name", descriptionKey = "config.deathcounter.prop.commandPermissionLevel.desc", comment = "Permission level required to use the op commands for Death Counter")
        @ConfigEntry.BoundedInteger(min = 0, max = 4)
        public int commandPermissionLevel = 1;
    }
}
