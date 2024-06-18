package me.ichun.mods.deathcounter.loader.fabric;

import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.core.Config;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.fabricmc.api.ModInitializer;

public class LoaderFabric extends DeathCounter
    implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config());

        DeathCounter.deathHandler = new DeathHandlerFabric();
    }
}
