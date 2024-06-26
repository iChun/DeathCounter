package me.ichun.mods.deathcounter.loader.neoforge;

import me.ichun.mods.deathcounter.client.ConfigClient;
import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.core.Config;
import me.ichun.mods.deathcounter.loader.neoforge.client.EventHandlerClientNeoForge;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.common.NeoForge;

@Mod(DeathCounter.MOD_ID)
public class LoaderNeoForge extends DeathCounter
{
    public LoaderNeoForge(IEventBus modEventBus)
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config(), modEventBus);

        if(FMLEnvironment.dist.isClient())
        {
            setupClient(modEventBus);
        }

        NeoForge.EVENT_BUS.register(DeathCounter.deathHandler = new DeathHandlerNeoForge());
    }

    @OnlyIn(Dist.CLIENT)
    private void setupClient(IEventBus modEventBus)
    {
        //register config
        configClient = iChunUtil.d().registerConfig(new ConfigClient(), modEventBus);

        NeoForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClientNeoForge());

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new WorkspaceConfigs(screen)));
    }
}
