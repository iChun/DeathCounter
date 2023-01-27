package me.ichun.mods.deathcounter.loader.forge;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigForge extends Config
{
    public ConfigForge(ForgeConfigSpec.Builder builder)
    {
        builder.comment("General settings").push("general");

        final ForgeConfigSpec.EnumValue<DeathCounter.MessageType> cMessageType = builder.comment("What kind of message should we send players when they die?\nAccepts: NONE, SHORT, LONG")
                .translation("config.deathcounter.prop.messageType.desc")
                .defineEnum("messageType", DeathCounter.MessageType.LONG);
        messageType = new ConfigWrapper<>(cMessageType::get, cMessageType::set, cMessageType::save);

        final ForgeConfigSpec.EnumValue<DeathCounter.BroadcastType> cBroadcastOnDeath = builder.comment("Should we broadcast the leaderboard to the player when they die?\nAccepts: NONE, SELF, ALL")
                .translation("config.deathcounter.prop.broadcastOnDeath.desc")
                .defineEnum("broadcastOnDeath", DeathCounter.BroadcastType.NONE);
        broadcastOnDeath = new ConfigWrapper<>(cBroadcastOnDeath::get, cBroadcastOnDeath::set, cBroadcastOnDeath::save);

        final ForgeConfigSpec.IntValue cLeaderboardCount = builder.comment("Number of names to show in the leaderboard")
                .translation("config.deathcounter.prop.leaderboardCount.desc")
                .defineInRange("leaderboardCount", 5, 1, 50);
        leaderboardCount = new ConfigWrapper<>(cLeaderboardCount::get, cLeaderboardCount::set, cLeaderboardCount::save);

        final ForgeConfigSpec.BooleanValue cSingleSession = builder.comment("Do not persist deaths across sessions? Turning this on disables saving deaths to server folder.")
                .translation("config.deathcounter.prop.singleSession.desc")
                .define("singleSession", false);
        singleSession = new ConfigWrapper<>(cSingleSession::get, cSingleSession::set, cSingleSession::save);

        final ForgeConfigSpec.IntValue cCommandPermissionLevel = builder.comment("Permission level required to use the op commands for Death Counter")
                .translation("config.deathcounter.prop.commandPermissionLevel.desc")
                .defineInRange("commandPermissionLevel", 1, 0, 4);
        commandPermissionLevel = new ConfigWrapper<>(cCommandPermissionLevel::get, cCommandPermissionLevel::set, cCommandPermissionLevel::save);

        builder.pop();
    }

}
