package me.ichun.mods.deathcounter.loader.fabric;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.ModInitializer;

public class LoaderFabric extends DeathCounter
        implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        modProxy = this;

        //register config
        ConfigFabric configFabric = new ConfigFabric();
        config = configFabric;
        Config modConfig = new Config(MOD_ID, new String[]{}, configFabric);
        modConfig.load();
        Runtime.getRuntime().addShutdownHook(new Thread(modConfig::save));

        DeathCounter.deathHandler = new DeathHandlerFabric();
    }
}
