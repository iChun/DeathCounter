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

        @ConfigEntry(nameKey = "prop.messageType.name", descriptionKey = "prop.messageType.desc", comment = "What kind of message should we send players when they die?\nAccepts: NONE, SHORT, LONG")
        public DeathCounter.MessageType messageType = DeathCounter.MessageType.LONG;

        @ConfigEntry(nameKey = "prop.broadcastOnDeath.name", descriptionKey = "prop.broadcastOnDeath.desc", comment = "Should we broadcast the leaderboard to the player when they die?\nAccepts: NONE, SELF, ALL")
        public DeathCounter.BroadcastType broadcastOnDeath = DeathCounter.BroadcastType.NONE;

        @ConfigEntry(nameKey = "prop.leaderboardCount.name", descriptionKey = "prop.leaderboardCount.desc", comment = "Number of names to show in the leaderboard")
        @ConfigEntry.BoundedInteger(min = 1, max = 50)
        public int leaderboardCount = 5;

        @ConfigEntry(nameKey = "prop.singleSession.name", descriptionKey = "prop.singleSession.desc", comment = "Do not persist deaths across sessions? Turning this on disables saving deaths to server folder.")
        public boolean singleSession = false;

        @ConfigEntry(nameKey = "prop.commandPermissionLevel.name", descriptionKey = "prop.commandPermissionLevel.desc", comment = "Permission level required to use the op commands for Death Counter")
        @ConfigEntry.BoundedInteger(min = 0, max = 4)
        public int commandPermissionLevel = 1;
    }
}
