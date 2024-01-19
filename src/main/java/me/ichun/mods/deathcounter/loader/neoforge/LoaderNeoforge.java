package me.ichun.mods.deathcounter.loader.neoforge;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.loader.neoforge.client.ConfigClientNeoforge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;

@Mod(DeathCounter.MOD_ID)
public class LoaderNeoforge extends DeathCounter
{
    public LoaderNeoforge(IEventBus modEventBus)
    {
        modProxy = this;

        //build the config
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        config = new ConfigNeoforge(configBuilder);
        //register the config. This loads the config for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());

        if(FMLEnvironment.dist.isClient())
        {
            setupClientConfig();
        }

        NeoForge.EVENT_BUS.register(DeathCounter.deathHandler = new DeathHandlerNeoforge());
    }

    private void setupClientConfig()
    {
        //build the config
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        configClient = new ConfigClientNeoforge(configBuilder);
        //register the config. This loads the config for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, configBuilder.build());
    }
}
