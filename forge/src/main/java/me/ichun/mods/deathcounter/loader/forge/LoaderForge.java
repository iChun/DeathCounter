package me.ichun.mods.deathcounter.loader.forge;

import me.ichun.mods.deathcounter.client.ConfigClient;
import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.core.Config;
import me.ichun.mods.deathcounter.loader.forge.client.EventHandlerClientForge;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(DeathCounter.MOD_ID)
public class LoaderForge extends DeathCounter
{
    public LoaderForge()
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::setupClient);

        MinecraftForge.EVENT_BUS.register(DeathCounter.deathHandler = new DeathHandlerForge());
    }

    @OnlyIn(Dist.CLIENT)
    private void setupClient()
    {
        //register config
        configClient = iChunUtil.d().registerConfig(new ConfigClient());

        MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClientForge());

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((mc, screen) -> new WorkspaceConfigs(screen)));
    }
}
