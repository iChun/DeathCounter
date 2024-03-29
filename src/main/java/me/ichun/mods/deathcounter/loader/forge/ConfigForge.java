package me.ichun.mods.deathcounter.loader.forge;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigForge extends Config
{
    public ConfigForge(ForgeConfigSpec.Builder builder)
    {
        builder.comment("General settings").push("general");

        final ForgeConfigSpec.EnumValue<DeathCounter.MessageType> cMessageType = builder.comment(Reference.MESSAGE_TYPE_COMMENT)
            .translation("config.deathcounter.prop.messageType.desc")
            .defineEnum("messageType", DeathCounter.MessageType.LONG);
        messageType = new ConfigWrapper<>(cMessageType::get, cMessageType::set, cMessageType::save);

        final ForgeConfigSpec.EnumValue<DeathCounter.BroadcastType> cBroadcastOnDeath = builder.comment(Reference.BROADCAST_ON_DEATH_COMMENT)
            .translation("config.deathcounter.prop.broadcastOnDeath.desc")
            .defineEnum("broadcastOnDeath", DeathCounter.BroadcastType.NONE);
        broadcastOnDeath = new ConfigWrapper<>(cBroadcastOnDeath::get, cBroadcastOnDeath::set, cBroadcastOnDeath::save);

        final ForgeConfigSpec.IntValue cLeaderboardCount = builder.comment(Reference.LEADERBOARD_COUNT_COMMENT)
            .translation("config.deathcounter.prop.leaderboardCount.desc")
            .defineInRange("leaderboardCount", 5, 1, 50);
        leaderboardCount = new ConfigWrapper<>(cLeaderboardCount::get, cLeaderboardCount::set, cLeaderboardCount::save);

        final ForgeConfigSpec.BooleanValue cSingleSession = builder.comment(Reference.SINGLE_SESSION_COMMENT)
            .translation("config.deathcounter.prop.singleSession.desc")
            .define("singleSession", false);
        singleSession = new ConfigWrapper<>(cSingleSession::get, cSingleSession::set, cSingleSession::save);

        final ForgeConfigSpec.IntValue cCommandPermissionLevel = builder.comment(Reference.COMMAND_PERMISSION_LEVEL_COMMENT)
            .translation("config.deathcounter.prop.commandPermissionLevel.desc")
            .defineInRange("commandPermissionLevel", 1, 0, 4);
        commandPermissionLevel = new ConfigWrapper<>(cCommandPermissionLevel::get, cCommandPermissionLevel::set, cCommandPermissionLevel::save);

        builder.pop();
    }

}
