package me.ichun.mods.deathcounter.loader.neoforge.client;

import me.ichun.mods.deathcounter.client.ConfigClient;
import me.ichun.mods.deathcounter.common.core.Config;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigClientNeoforge extends ConfigClient
{
    public ConfigClientNeoforge(ModConfigSpec.Builder builder)
    {
        builder.comment("General settings").push("general");

        final ModConfigSpec.BooleanValue cHideDeathCounterMessages = builder.comment(Reference.HIDE_DEATH_COUNTER_MESSAGES_COMMENT)
                .translation("config.deathcounter.prop.hideDeathCounterMessages.desc")
                .define("hideDeathCounterMessages", false);
        hideDeathCounterMessages = new Config.ConfigWrapper<>(cHideDeathCounterMessages::get, cHideDeathCounterMessages::set, cHideDeathCounterMessages::save);

        builder.pop();
    }
}
