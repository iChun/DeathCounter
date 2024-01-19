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
        broadcastOnDeath = new ConfigWrapper<>(() -> GENERAL.broadcastOnDeath, v -> GENERAL.broadcastOnDeath = v);
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

        @ConfigEntry(nameKey = "prop.messageType.name", descriptionKey = "prop.messageType.desc", comment = Reference.MESSAGE_TYPE_COMMENT)
        public DeathCounter.MessageType messageType = DeathCounter.MessageType.LONG;

        @ConfigEntry(nameKey = "prop.broadcastOnDeath.name", descriptionKey = "prop.broadcastOnDeath.desc", comment = Reference.BROADCAST_ON_DEATH_COMMENT)
        public DeathCounter.BroadcastType broadcastOnDeath = DeathCounter.BroadcastType.NONE;

        @ConfigEntry(nameKey = "prop.leaderboardCount.name", descriptionKey = "prop.leaderboardCount.desc", comment = Reference.LEADERBOARD_COUNT_COMMENT)
        @ConfigEntry.BoundedInteger(min = 1, max = 50)
        public int leaderboardCount = 5;

        @ConfigEntry(nameKey = "prop.singleSession.name", descriptionKey = "prop.singleSession.desc", comment = Reference.SINGLE_SESSION_COMMENT)
        public boolean singleSession = false;

        @ConfigEntry(nameKey = "prop.commandPermissionLevel.name", descriptionKey = "prop.commandPermissionLevel.desc", comment = Reference.COMMAND_PERMISSION_LEVEL_COMMENT)
        @ConfigEntry.BoundedInteger(min = 0, max = 4)
        public int commandPermissionLevel = 1;
    }
}
