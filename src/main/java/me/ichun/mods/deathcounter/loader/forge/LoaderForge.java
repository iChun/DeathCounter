package me.ichun.mods.deathcounter.loader.forge;

import me.ichun.mods.deathcounter.client.ConfigClient;
import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.core.Config;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(DeathCounter.MOD_ID)
public class LoaderForge extends DeathCounter
{
    public LoaderForge()
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::setupClientConfig);

        MinecraftForge.EVENT_BUS.register(DeathCounter.deathHandler = new DeathHandlerForge());
    }

    private void setupClientConfig()
    {
        //register config
        configClient = iChunUtil.d().registerConfig(new ConfigClient());
    }
}
