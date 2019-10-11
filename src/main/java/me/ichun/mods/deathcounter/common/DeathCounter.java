package me.ichun.mods.deathcounter.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DeathCounter.MOD_ID)
public class DeathCounter
{
    public static final String MOD_ID = "deathcounter";
    public static final String MOD_NAME = "Death Counter";

    public static final Logger LOGGER = LogManager.getLogger();

    public static Config config;

    public DeathCounter()
    {
        //build the config
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();

        config = new Config(configBuilder);

        //register the config. This loads the config for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());
    }

    public enum MessageType
    {
        NONE,
        SHORT,
        LONG
    }

    public class Config
    {
        public final ForgeConfigSpec.EnumValue<MessageType> messageType;
        public final ForgeConfigSpec.IntValue leaderboardCount;
        public final ForgeConfigSpec.BooleanValue singleSession;
        public final ForgeConfigSpec.IntValue commandPermissionLevel;

        public Config(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General settings").push("general");

            messageType = builder.comment("What kind of message should we send players when they die?")
                    .translation("config.deathcounter.prop.messageType.desc")
                    .defineEnum("messageType", MessageType.LONG);
            leaderboardCount = builder.comment("Number of names to show in the leaderboard")
                    .translation("config.deathcounter.prop.leaderboardCount.desc")
                    .defineInRange("leaderboardCount", 5, 1, 50);
            singleSession = builder.comment("Do not persist deaths across sessions? Turning this on disables saving deaths to server folder.")
                    .translation("config.deathcounter.prop.singleSession.desc")
                    .define("singleSession", false);
            commandPermissionLevel = builder.comment("Permission level required to use the op commands for Death Counter")
                    .translation("config.deathcounter.prop.commandPermissionLevel.desc")
                    .defineInRange("commandPermissionLevel", 1, 0, 4);

            builder.pop();
        }
    }
}
