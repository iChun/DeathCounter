package me.ichun.mods.deathcounter.common.core;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandDeathCounter extends CommandBase
{
	@Override
	public String getName()
	{
		return "dc";
	}

	@Override
	public String getUsage(ICommandSender par1ICommandSender)
	{
		return "/" + this.getName() + " (leaderboard/reset) [all/user]";
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.singletonList("deathcounter");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException
	{
		if(args.length > 0)
		{
			if("leaderboard".toLowerCase().startsWith(args[0]))
			{
				if(args.length >= 2)
				{
					if(args[1].equalsIgnoreCase("all"))
					{
						broadcastLeaderboard(null);
					}
					else
					{
						broadcastLeaderboard(args[1]);
					}
				}
				else
				{
					broadcastLeaderboard(commandSender.getName());
				}
			}
			else if("reset".toLowerCase().startsWith(args[0]))
			{
				if(args.length >= 2)
				{
					if(args[1].equalsIgnoreCase("all"))
					{
						notifyCommandListener(commandSender, this, "dc.command.resetAll");
						DeathCounter.console(commandSender.getName() + ": Resetting deaths for all players", Level.INFO);
					}
					else
					{
						if(DeathCounter.instance.clearDeath(args[1]))
						{
							notifyCommandListener(commandSender, this, "dc.command.resetPlayer");
							DeathCounter.console(commandSender.getName() + ": Resetting deaths for " + args[1], Level.INFO);
						}
						else
						{
							commandSender.sendMessage(new TextComponentTranslation("dc.command.noDeathsPlayer", args[1]));
						}
					}
				}
				else
				{
					throw new WrongUsageException("/" + this.getName() + " reset [all/user]");
				}
			}
			else
			{
				throw new WrongUsageException("/" + this.getName() + " (leaderboard/reset) [all/user]");
			}
		}
		else
		{
			throw new WrongUsageException("/" + this.getName() + " (leaderboard/reset) [all/user]");
		}
	}

	public static void broadcastLeaderboard(String s)
	{
		ArrayList<EntityPlayer> playersToNotify = new ArrayList<EntityPlayer>();
		if(s != null)
		{
			if(!s.equals("Server"))
			{
				if(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(s) == null)
				{
					return;
				}
				playersToNotify.add(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(s));
			}
		}
		else
		{
			List players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
			for(Object o : players)
			{
				EntityPlayer player = (EntityPlayer)o;
				playersToNotify.add(player);
			}
		}
		if(s != null && s.equals("Server"))
		{
			if(DeathCounter.ranking.isEmpty())
			{
				DeathCounter.console("No deaths have occured on this server yet.", Level.INFO);
			}
			else
			{
				DeathCounter.console("Death Leaderboard:", Level.INFO);
				for(int i = 1; i <= DeathCounter.leaderboardCount && i - 1 < DeathCounter.ranking.size(); i++)
				{
					DeathCounter.console("   " + DeathCounter.instance.getDisplayedRank(DeathCounter.ranking.get(i - 1)) + "  " + DeathCounter.ranking.get(i - 1) + " (" + DeathCounter.instance.getDeathCount(DeathCounter.ranking.get(i - 1)) + (DeathCounter.instance.getDeathCount(DeathCounter.ranking.get(i - 1)) == 1 ? " Death)" : " Deaths)"), Level.INFO);
				}
			}
		}
		else
		{
			for(EntityPlayer player: playersToNotify)
			{
				if(DeathCounter.ranking.isEmpty())
				{
					player.sendStatusMessage(new TextComponentTranslation("dc.command.noDeaths"), false);
				}
				else
				{
					boolean isPlayer = false;
					player.sendStatusMessage(new TextComponentTranslation("dc.command.leaderboard"), false);
					for(int i = 1; i <= DeathCounter.leaderboardCount && i - 1 < DeathCounter.ranking.size(); i++)
					{
						String playerName = DeathCounter.ranking.get(i - 1);
						if(playerName.equals(player.getName()))
						{
							isPlayer = true;
						}
						int rank = DeathCounter.instance.getDisplayedRank(playerName);
						player.sendStatusMessage(new TextComponentString((rank == 1 ? "\u00A7e" : rank == 2 ? "\u00A77" : rank == 3 ? "\u00A74" : "") + (playerName.equals(player.getName()) ? " ->" : "    ") + rank + "  " + DeathCounter.ranking.get(i - 1) + " (" + DeathCounter.instance.getDeathCount(DeathCounter.ranking.get(i - 1)) + (DeathCounter.instance.getDeathCount(DeathCounter.ranking.get(i - 1)) == 1 ? " Death)" : " Deaths)")), false);
					}
					if(!isPlayer)
					{
						int rank = DeathCounter.instance.getDisplayedRank(player.getName());
						player.sendStatusMessage(new TextComponentString(" ->" + rank + (rank >= 10 ? " " : "  ") + player.getName() + " (" + DeathCounter.instance.getDeathCount(player.getName()) + (DeathCounter.instance.getDeathCount(player.getName()) == 1 ? " Death)" : " Deaths)")), false);
					}
				}
			}
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		if(args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "reset", "leaderboard");
		}
		else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getOnlinePlayerNames());
		}
		return null;
	}

}
