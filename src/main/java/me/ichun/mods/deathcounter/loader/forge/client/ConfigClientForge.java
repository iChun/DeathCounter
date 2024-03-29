package me.ichun.mods.deathcounter.loader.forge.client;

import me.ichun.mods.deathcounter.client.ConfigClient;
import me.ichun.mods.deathcounter.common.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigClientForge extends ConfigClient
{
    public ConfigClientForge(ForgeConfigSpec.Builder builder)
    {
        builder.comment("General settings").push("general");

        final ForgeConfigSpec.BooleanValue cHideDeathCounterMessages = builder.comment(Reference.HIDE_DEATH_COUNTER_MESSAGES_COMMENT)
                .translation("config.deathcounter.prop.hideDeathCounterMessages.desc")
                .define("hideDeathCounterMessages", false);
        hideDeathCounterMessages = new Config.ConfigWrapper<>(cHideDeathCounterMessages::get, cHideDeathCounterMessages::set, cHideDeathCounterMessages::save);

        builder.pop();
    }
}
