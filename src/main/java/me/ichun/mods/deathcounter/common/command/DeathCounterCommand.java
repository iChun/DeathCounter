package me.ichun.mods.deathcounter.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.ichun.mods.deathcounter.common.DeathCounter;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.*;

public class DeathCounterCommand
{
    private static final DynamicCommandExceptionType TRANSFER_FAIL = new DynamicCommandExceptionType((name) -> Component.translatable("commands.deathcounter.transfer.fail", name));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralCommandNode<CommandSourceStack> command =
                dispatcher.register(Commands.literal("dc")
                        .executes((source) -> {
                            Entity ent = source.getSource().getEntity();
                            if(ent != null)
                            {
                                broadcastLeaderboard(Collections.singleton(ent), null, DeathCounter.config.leaderboardCount.get());
                            }
                            else
                            {
                                broadcastLeaderboard(Collections.emptyList(), source.getSource(), DeathCounter.config.leaderboardCount.get());
                            }
                            return 0;
                        })
                        .then(Commands.literal("get")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(commandContext.getSource().getServer().getPlayerList().getPlayerNamesArray(), suggestionsBuilder))
                                        .executes((source) -> {
                                            String name = StringArgumentType.getString(source, "name");
                                            int deaths = DeathCounter.deathHandler.getDeaths(name);
                                            int rank = DeathCounter.deathHandler.getRank(name);
                                            if(deaths > 0)
                                            {
                                                source.getSource().sendSuccess(Component.translatable("commands.deathcounter.get", name, deaths, rank), false);
                                            }
                                            else
                                            {
                                                source.getSource().sendSuccess(Component.translatable("commands.deathcounter.get.none", name), false);
                                            }
                                            return 0;
                                        })))
                        .then(Commands.literal("set").requires((p) -> p.hasPermission(DeathCounter.config.commandPermissionLevel.get()))
                                .then(Commands.argument("name/\"all\"", StringArgumentType.word())
                                        .suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(commandContext.getSource().getServer().getPlayerList().getPlayerNamesArray(), suggestionsBuilder))
                                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                .executes((source) -> {
                                                    int deaths = IntegerArgumentType.getInteger(source, "value");
                                                    DeathCounter.deathHandler.setDeaths(StringArgumentType.getString(source, "name/\"all\""), deaths);
                                                    return deaths;
                                                }))))
                        .then(Commands.literal("broadcast").requires((p) -> p.hasPermission(DeathCounter.config.commandPermissionLevel.get()))
                                .executes((source) -> {
                                    //send to all
                                    source.getSource().sendSuccess(Component.translatable("commands.deathcounter.leaderboard.broadcasted"), true);
                                    broadcastLeaderboard(source.getSource().getServer().getPlayerList().getPlayers(), null, DeathCounter.config.leaderboardCount.get());
                                    return 0;
                                })
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes((source) -> {
                                            //send to specific
                                            source.getSource().sendSuccess(Component.translatable("commands.deathcounter.leaderboard.broadcasted"), true);
                                            broadcastLeaderboard(EntityArgument.getPlayers(source, "targets"), null, DeathCounter.config.leaderboardCount.get());
                                            return 0;
                                        })
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes((source) -> {
                                                    //broadcast specific count
                                                    source.getSource().sendSuccess(Component.translatable("commands.deathcounter.leaderboard.broadcasted"), true);
                                                    broadcastLeaderboard(EntityArgument.getPlayers(source, "targets"), null, IntegerArgumentType.getInteger(source, "count"));
                                                    return 0;
                                                })))
                                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                        .executes((source) -> {
                                            //broadcast specific count
                                            source.getSource().sendSuccess(Component.translatable("commands.deathcounter.leaderboard.broadcasted"), true);
                                            broadcastLeaderboard(source.getSource().getServer().getPlayerList().getPlayers(), null, IntegerArgumentType.getInteger(source, "count"));
                                            return 0;
                                        })))
                        .then(Commands.literal("transfer").requires((p) -> p.hasPermission(DeathCounter.config.commandPermissionLevel.get()))
                                .then(Commands.argument("from", StringArgumentType.word())
                                        .suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(commandContext.getSource().getServer().getPlayerList().getPlayerNamesArray(), suggestionsBuilder))
                                        .then(Commands.argument("to", StringArgumentType.word())
                                                .suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(commandContext.getSource().getServer().getPlayerList().getPlayerNamesArray(), suggestionsBuilder))
                                                .executes((source) -> {
                                                    //transfer
                                                    String from = StringArgumentType.getString(source, "from");
                                                    String to = StringArgumentType.getString(source, "to");
                                                    int deaths = DeathCounter.deathHandler.transferDeaths(from, to);
                                                    if(deaths > 0)
                                                    {
                                                        source.getSource().sendSuccess(Component.translatable("commands.deathcounter.transfer", deaths, from, to), true);
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
                .redirect(command));
    }

    public static void broadcastLeaderboard(Collection<? extends Entity> entities, CommandSourceStack source, final int count)
    {
        if(source != null) //query from server
        {
            source.sendSuccess(Component.translatable("commands.deathcounter.leaderboard"), false);
            if(DeathCounter.deathHandler.getRankings().isEmpty())
            {
                source.sendSuccess(Component.translatable("commands.deathcounter.leaderboard.none"), false);
            }
            else
            {
                int done = 0;
                int rank = 1;
                for(Map.Entry<Integer, TreeSet<String>> e : DeathCounter.deathHandler.getRankings().entrySet())
                {
                    TreeSet<String> set = e.getValue();
                    for(String s : set)
                    {
                        source.sendSuccess(setStyleForRank(Component.translatable("commands.deathcounter.leaderboard.copy", "   " + rank + " - " + s + " (" + e.getKey() + ")"), rank), false); //setStyle
                        if(++done >= count) break;
                    }
                    if(done >= count) break;
                    rank += e.getValue().size();
                }
            }
        }
        else
        {
            entities.stream().filter(e->e instanceof ServerPlayer).forEach(e -> {
                ServerPlayer player = (ServerPlayer)e;
                player.sendSystemMessage(Component.translatable("commands.deathcounter.leaderboard")); //sendSystemMessage
                if(DeathCounter.deathHandler.getRankings().isEmpty())
                {
                    player.sendSystemMessage(Component.translatable("commands.deathcounter.leaderboard.none")); //sendSystemMessage
                }
                else
                {
                    int done = 0;
                    int rank = 1;
                    for(Map.Entry<Integer, TreeSet<String>> e1 : DeathCounter.deathHandler.getRankings().entrySet())
                    {
                        TreeSet<String> set = e1.getValue();
                        for(String s : set)
                        {
                            if(done++ < count || s.equalsIgnoreCase(player.getName().getString()))
                            {
                                player.sendSystemMessage(setStyleForRank(Component.translatable("commands.deathcounter.leaderboard.copy", (s.equalsIgnoreCase(player.getName().getString()) ? "-> " : "   ") + rank + " - " + s + " (" + e1.getKey() + ")"), rank)); //sendMessage
                            }
                        }
                        rank += e1.getValue().size();
                    }
                }
            });
        }
    }

    private static Component setStyleForRank(MutableComponent text, int i)
    {
        switch(i)
        {
            case 1: return text.withStyle(ChatFormatting.YELLOW);
            case 2: return text.withStyle(ChatFormatting.GRAY);
            case 3: return text.withStyle(ChatFormatting.DARK_RED);
            default: return text;
        }
    }
}
