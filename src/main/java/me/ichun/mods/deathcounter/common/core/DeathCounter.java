package me.ichun.mods.deathcounter.common.core;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Mod(modid = "deathcounter", name="DeathCounter",
		version = DeathCounter.version,
		acceptableRemoteVersions = "*",
        dependencies = "required-after:forge@[13.19.0.2141,)",
        acceptedMinecraftVersions = "[1.12,1.13)"
)
public class DeathCounter
{

	public static final String version = "1.0.0";
	public static File saveDir;

	public static HashMap<String, Integer> deathCounter = new HashMap<>();

	public static ArrayList<String> ranking = new ArrayList<>();

	public static int message;
	public static int leaderboardCount;
	public static int outputToTextFile;
	public static int singleSession;

	@Instance("deathcounter")
	public static DeathCounter instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		message = config.getInt("message", "deathcounter", 2, 0, 2, "Death Count Messages?\n0 = Disable\n1 = Short message\n2 = Long message");
		leaderboardCount = config.getInt("leaderboardCount", "deathcounter", 5, 0, 20, "Number of names to show in the leaderboards");
		singleSession = config.getInt("singleSession", "deathcounter", 0, 0, 1, "Do not save deaths in save folder?\n0 = No\n1 = Yes");
		outputToTextFile = config.getInt("outputToTextFile", "deathcounter", 1, 0, 1, "Output deaths to text file in save folder?\n(This is overridden to 0 by the singleSession config)\n0 = No\n1 = Yes");

		if(config.hasChanged())
		{
			config.save();
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		ICommandManager manager = event.getServer().getCommandManager();
		if(manager instanceof CommandHandler)
		{
			CommandHandler handler = (CommandHandler)manager;
			handler.registerCommand(new CommandDeathCounter());
		}
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event)
	{
		deathCounter.clear();
		ranking.clear();

		loadDeaths(DimensionManager.getWorld(0));
		sortRanking();
	}

	public void loadDeaths(WorldServer world)
	{
		File dir = new File(world.getChunkSaveLocation(), "deathCounter");
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		saveDir = dir;
		File[] files = dir.listFiles();
		for(File file : files)
		{
			if(file.getName().endsWith(".dat"))
			{
				String user = file.getName().substring(0, file.getName().length() - 4);
				try
				{
					NBTTagCompound tag = CompressedStreamTools.readCompressed(new FileInputStream(file));
					deathCounter.put(user, tag.getInteger("deaths"));
				}
				catch(EOFException e)
				{
					console("File for " + user + " is corrupted. Flushing.", Level.WARN);
				}
				catch(IOException e)
				{
					console("Failed to read file for " + user + ". Flushing.", Level.WARN);
				}
			}
		}
	}

	public void sortRanking()
	{
		for(Map.Entry<String, Integer> e : deathCounter.entrySet())
		{
			ranking.remove(e.getKey());
			if(e.getValue() > 0)
			{
				for(int i = 0; i < ranking.size(); i++)
				{
					if(getDeathCount(ranking.get(i)) <= e.getValue())
					{
						ranking.add(i, e.getKey());
						break;
					}
				}
				if(!ranking.contains(e.getKey()))
				{
					ranking.add(e.getKey());
				}
			}
		}
		if(singleSession != 1 && outputToTextFile == 1)
		{
			Properties s = new Properties();
			File text = new File(saveDir, "deaths.txt");

			if(text == null || text.isDirectory())
			{
				return;
			}

			for(Map.Entry<String, Integer> e : deathCounter.entrySet())
			{
				s.setProperty(e.getKey(), Integer.toString(e.getValue()));
			}

			try(FileOutputStream fos = new FileOutputStream(text))
			{
				if(!text.exists()) text.createNewFile();
				s.store(fos, null);
			}
			catch(IOException e)
			{
				console("Error writing deaths.txt", Level.WARN);
			}
		}
	}

	public void addDeath(EntityPlayer player)
	{
		int deaths = getDeathCount(player.getName()) + 1;
		deathCounter.put(player.getName(), deaths);
		sortRanking();

		if(singleSession == 1) return;
		File file = new File(saveDir, player.getName() + ".dat");
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("deaths", deaths);

		try(FileOutputStream fos = new FileOutputStream(file))
		{
			CompressedStreamTools.writeCompressed(tag, fos);
		}
		catch(IOException ioexception)
		{
			console("Failed to save death count for " + player.getName(), Level.WARN);
		}

	}

	public boolean clearDeath(String s)
	{
		if(s == null)
		{
			boolean success = true;
			File[] files = saveDir.listFiles();
			for(File file : files)
			{
				success = file.delete();
			}
			deathCounter.clear();
			ranking.clear();
			return success;
		}
		else
		{
			File file = new File(saveDir, s + ".dat");
			if(file.exists())
			{
				boolean success = file.delete();
				deathCounter.remove(s);
				sortRanking();
				return success;
			}
			else
			{
				return false;
			}
		}
	}

	public int getDeathCount(String s)
	{
		try
		{
			return deathCounter.get(s);
		}
		catch(NullPointerException e)
		{
			return 0;
		}
	}

	public int getDisplayedRank(String s)
	{
		if(ranking.contains(s))
		{
			for(int i = 0; i < ranking.size(); i++)
			{
				if(ranking.get(i).equals(s))
				{
					int rank = i;
					int deaths = getDeathCount(s);
					while(i > 0 && getDeathCount(ranking.get(--i)) == deaths)
					{
						rank--;
					}
					return rank + 1;
				}
			}
		}
		return ranking.size() + 1;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDeath(LivingDeathEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(event.getEntityLiving().getName()) != null)
		{
			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
			addDeath(player);
			if(message > 0)
			{
				if(message == 1)
				{
					player.sendStatusMessage(new TextComponentTranslation("dc.message.deathAndRank", getDeathCount(player.getName()), getDisplayedRank(player.getName())), false);
				}
				if(message == 2)
				{
					player.sendStatusMessage(new TextComponentTranslation("dc.message.death", getDeathCount(player.getName())), false);
					player.sendStatusMessage(new TextComponentTranslation("dc.message.rank", getDisplayedRank(player.getName())), false);
				}
			}
		}
	}

	@SubscribeEvent
	public void onChatEvent(ServerChatEvent event)
	{
		if(event.getMessage().toLowerCase().toLowerCase().startsWith("!dc") || event.getMessage().toLowerCase().toLowerCase().startsWith("!deathcounter"))
		{
			CommandDeathCounter.broadcastLeaderboard(event.getUsername());
			event.setCanceled(true);
		}
	}

	@SuppressWarnings("deprecation")
	public static void console(String s, Level logLevel)
	{
		FMLLog.log("DeathCounter", logLevel, "%s", s);
	}
}
