package me.ichun.mods.deathcounter.loader.fabric.client;

import me.ichun.mods.deathcounter.client.ConfigClient;
import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.fabricmc.api.ClientModInitializer;

public class LoaderFabricClient
    implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        //register config
        DeathCounter.configClient = iChunUtil.d().registerConfig(new ConfigClient());
    }
}
