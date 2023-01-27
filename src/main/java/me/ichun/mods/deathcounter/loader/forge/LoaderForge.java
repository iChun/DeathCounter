package me.ichun.mods.deathcounter.loader.forge;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.loader.forge.client.ConfigClientForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(DeathCounter.MOD_ID)
public class LoaderForge extends DeathCounter
{
    public LoaderForge()
    {
        modProxy = this;

        //build the config
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        config = new ConfigForge(configBuilder);
        //register the config. This loads the config for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::setupClientConfig);

        MinecraftForge.EVENT_BUS.register(DeathCounter.deathHandler = new DeathHandlerForge());
    }

    private void setupClientConfig()
    {
        //build the config
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        configClient = new ConfigClientForge(configBuilder);
        //register the config. This loads the config for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, configBuilder.build());
    }
}
