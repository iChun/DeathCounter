package me.ichun.mods.deathcounter.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.core.DeathHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.server.command.TextComponentHelper;

import java.util.*;

public class DeathCounterCommand
{
    private static final DynamicCommandExceptionType TRANSFER_FAIL = new DynamicCommandExceptionType((name) -> new TranslationTextComponent("commands.deathcounter.transfer.fail", name));

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        LiteralCommandNode<CommandSource> command =
                dispatcher.register(Commands.literal("dc")
                        .executes((source) -> {
                            Entity ent = source.getSource().getEntity();
                            if(ent != null)
                            {
                                ArrayList<Entity> ents = new ArrayList<>();
                                ents.add(ent);
                                broadcastLeaderboard(ents, null, DeathCounter.config.leaderboardCount.get());
                            }
                            else
                            {
                                broadcastLeaderboard(Collections.emptyList(), source.getSource(), DeathCounter.config.leaderboardCount.get());
                            }
                            return 0;
                        })
                        .then(Commands.literal("get")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes((source) -> {
                                            String name = StringArgumentType.getString(source, "name");
                                            int deaths = DeathHandler.getDeaths(name);
                                            int rank = DeathHandler.getRank(name);
                                            if(deaths > 0)
                                            {
                                                source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(source.getSource().getEntity(), "commands.deathcounter.get", name, deaths, rank), false);
                                            }
                                            else
                                            {
                                                source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(source.getSource().getEntity(), "commands.deathcounter.get.none", name), false);
                                            }
                                            return 0;
                                        })))
                        .then(Commands.literal("set").requires((p) -> p.hasPermissionLevel(DeathCounter.config.commandPermissionLevel.get()))
                                .then(Commands.argument("name/\"all\"", StringArgumentType.word())
                                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                .executes((source) -> {
                                                    int deaths = IntegerArgumentType.getInteger(source, "value");
                                                    DeathHandler.setDeaths(StringArgumentType.getString(source, "name/\"all\""), deaths);
                                                    return deaths;
                                                }))))
                        .then(Commands.literal("broadcast").requires((p) -> p.hasPermissionLevel(DeathCounter.config.commandPermissionLevel.get()))
                                .executes((source) -> {
                                    //send to all
                                    source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(source.getSource().getEntity(), "commands.deathcounter.leaderboard.broadcasted"), true);
                                    broadcastLeaderboard(source.getSource().getServer().getPlayerList().getPlayers(), null, DeathCounter.config.leaderboardCount.get());
                                    return 0;
                                })
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes((source) -> {
                                            //send to specific
                                            source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(source.getSource().getEntity(), "commands.deathcounter.leaderboard.broadcasted"), true);
                                            broadcastLeaderboard(EntityArgument.getPlayers(source, "targets"), null, DeathCounter.config.leaderboardCount.get());
                                            return 0;
                                        })
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes((source) -> {
                                                    //broadcast specific count
                                                    source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(source.getSource().getEntity(), "commands.deathcounter.leaderboard.broadcasted"), true);
                                                    broadcastLeaderboard(EntityArgument.getPlayers(source, "targets"), null, IntegerArgumentType.getInteger(source, "count"));
                                                    return 0;
                                                })))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                        .executes((source) -> {
                                            //broadcast specific count
                                            source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(source.getSource().getEntity(), "commands.deathcounter.leaderboard.broadcasted"), true);
                                            broadcastLeaderboard(source.getSource().getServer().getPlayerList().getPlayers(), null, IntegerArgumentType.getInteger(source, "count"));
                                            return 0;
                                        })))
                        .then(Commands.literal("transfer").requires((p) -> p.hasPermissionLevel(DeathCounter.config.commandPermissionLevel.get()))
                                .then(Commands.argument("from", StringArgumentType.word())
                                        .then(Commands.argument("to", StringArgumentType.word())
                                                .executes((source) -> {
                                                    //transfer
                                                    String from = StringArgumentType.getString(source, "from");
                                                    String to = StringArgumentType.getString(source, "to");
                                                    int deaths = DeathHandler.transferDeaths(from, to);
                                                    if(deaths > 0)
                                                    {
                                                        source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(source.getSource().getEntity(), "commands.deathcounter.transfer", deaths, from, to), true);
                                                    }
                                                    else
                                                    {
                                                        throw TRANSFER_FAIL.create(from);
                                                    }
                                                    return deaths;
                                                }))))
                );

        //register alias.
        dispatcher.register(Commands.literal("deathcounter")
                .executes((source) -> {
                    Entity ent = source.getSource().getEntity();
                    if(ent != null)
                    {
                        ArrayList<Entity> ents = new ArrayList<>();
                        ents.add(ent);
                        broadcastLeaderboard(ents, null, DeathCounter.config.leaderboardCount.get());
                    }
                    else
                    {
                        broadcastLeaderboard(Collections.emptyList(), source.getSource(), DeathCounter.config.leaderboardCount.get());
                    }
                    return 0;
                })
                .redirect(command));
    }

    public static void broadcastLeaderboard(Collection<? extends Entity> entities, CommandSource source, final int count)
    {
        if(source != null) //query from server
        {
            source.sendFeedback(TextComponentHelper.createComponentTranslation(null, "commands.deathcounter.leaderboard"), false);
            if(DeathHandler.getRankings().isEmpty())
            {
                source.sendFeedback(TextComponentHelper.createComponentTranslation(null, "commands.deathcounter.leaderboard.none"), false);
            }
            else
            {
                int done = 0;
                int rank = 1;
                for(Map.Entry<Integer, TreeSet<String>> e : DeathHandler.getRankings().entrySet())
                {
                    TreeSet<String> set = e.getValue();
                    for(String s : set)
                    {
                        source.sendFeedback(setStyleForRank(new StringTextComponent("   " + rank + " - " + s + " (" + e.getKey() + ")"), rank), false); //setStyle
                        if(++done >= count) break;
                    }
                    if(done >= count) break;
                    rank += e.getValue().size();
                }
            }
        }
        else
        {
            entities.stream().filter(e->e instanceof ServerPlayerEntity).forEach(e -> {
                ServerPlayerEntity player = (ServerPlayerEntity)e;
                player.func_241151_a_(TextComponentHelper.createComponentTranslation(player, "commands.deathcounter.leaderboard"), ChatType.CHAT, Util.DUMMY_UUID); //sendMessage
                if(DeathHandler.getRankings().isEmpty())
                {
                    player.func_241151_a_(TextComponentHelper.createComponentTranslation(player, "commands.deathcounter.leaderboard.none"), ChatType.CHAT, Util.DUMMY_UUID); //sendMessage
                }
                else
                {
                    int done = 0;
                    int rank = 1;
                    for(Map.Entry<Integer, TreeSet<String>> e1 : DeathHandler.getRankings().entrySet())
                    {
                        TreeSet<String> set = e1.getValue();
                        for(String s : set)
                        {
                            if(done++ < count || s.equalsIgnoreCase(player.getName().getUnformattedComponentText()))
                            {
                                player.func_241151_a_(setStyleForRank(new StringTextComponent((s.equalsIgnoreCase(player.getName().getUnformattedComponentText()) ? "-> " : "   ") + rank + " - " + s + " (" + e1.getKey() + ")"), rank), ChatType.CHAT, Util.DUMMY_UUID); //sendMessage
                            }
                        }
                        rank += e1.getValue().size();
                    }
                }
            });
        }
    }

    private static ITextComponent setStyleForRank(TextComponent text, int i)
    {
        switch(i)
        {
            case 1: return text.mergeStyle(TextFormatting.YELLOW);
            case 2: return text.mergeStyle(TextFormatting.GRAY);
            case 3: return text.mergeStyle(TextFormatting.DARK_RED);
            default: return text;
        }
    }
}
