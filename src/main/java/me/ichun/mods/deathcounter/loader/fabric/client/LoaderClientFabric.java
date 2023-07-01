package me.ichun.mods.deathcounter.loader.fabric.client;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.lortseam.completeconfig.data.Config;
import net.fabricmc.api.ClientModInitializer;

public class LoaderClientFabric
        implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        //register config
        ConfigClientFabric configClientFabric = new ConfigClientFabric();
        DeathCounter.configClient = configClientFabric;
        configClientFabric.configInstance = new Config(DeathCounter.MOD_ID, new String[]{ DeathCounter.MOD_ID + "-client"}, configClientFabric);
        configClientFabric.configInstance.load();
        Runtime.getRuntime().addShutdownHook(new Thread(configClientFabric.configInstance::save));
    }
}
