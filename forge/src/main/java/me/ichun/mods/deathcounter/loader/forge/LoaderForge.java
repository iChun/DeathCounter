package me.ichun.mods.deathcounter.loader.forge;

import me.ichun.mods.deathcounter.client.ConfigClient;
import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.core.Config;
import me.ichun.mods.deathcounter.loader.forge.client.EventHandlerClientForge;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DeathCounter.MOD_ID)
public class LoaderForge extends DeathCounter
{
    public LoaderForge(FMLJavaModLoadingContext context)
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config(), context);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> initClient(context));

        MinecraftForge.EVENT_BUS.register(DeathCounter.deathHandler = new DeathHandlerForge());
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient(FMLJavaModLoadingContext context)
    {
        //register config
        configClient = iChunUtil.d().registerConfig(new ConfigClient(), context);

        MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClientForge());

        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new WorkspaceConfigs(screen, MOD_ID)));
    }
}
