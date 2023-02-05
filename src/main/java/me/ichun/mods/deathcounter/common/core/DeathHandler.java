package me.ichun.mods.deathcounter.common.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.command.CommandDeathCounter;
import me.ichun.mods.deathcounter.mixin.LevelStorageAccessAccessorMixin;
import me.ichun.mods.deathcounter.mixin.MinecraftServerAccessorMixin;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public abstract class DeathHandler
{
    private static final Map<Path, WatchServiceThread> WATCH_SERVICES = Collections.synchronizedMap(new HashMap<>()); //Taken from iChunUtil, thanks past iChun!
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private TreeMap<String, Integer> deaths = new TreeMap<>(Comparator.naturalOrder());
    private TreeMap<Integer, TreeSet<String>> ranking = new TreeMap<>(Comparator.reverseOrder());
    private Path currentDeathsFile = null;
    private long timestamp = 0L;
    private boolean writing = false;

    public void onLivingDeath(LivingEntity living, DamageSource source)
    {
        if(!living.getCommandSenderWorld().isClientSide() &&
                living instanceof ServerPlayer &&
                !isFakePlayer((ServerPlayer)living) &&
                !postAddPlayerDeathStatEvent((ServerPlayer)living, source)
        )
        {
            addDeath((ServerPlayer)living);
        }
    }

    public void onServerAboutToStart(MinecraftServer server)
    {
        if(!DeathCounter.config.singleSession.get())
        {
            Path worldSavePath = ((LevelStorageAccessAccessorMixin)((MinecraftServerAccessorMixin)server).getStorageSource()).getLevelDirectory().path();
            currentDeathsFile = worldSavePath.resolve("deaths.json");
            synchronized(WATCH_SERVICES)
            {
                WatchServiceThread watchServiceThread = WATCH_SERVICES.computeIfAbsent(currentDeathsFile.getParent(), k -> {
                    WatchServiceThread thread = new WatchServiceThread(currentDeathsFile.getParent(), fileName -> {
                        long lastChange = 0L;
                        try
                        {
                            lastChange = Files.getLastModifiedTime(currentDeathsFile).toMillis();
                        }
                        catch(IOException ignored)
                        {
                        }
                        if(currentDeathsFile != null && !DeathCounter.config.singleSession.get() && !writing && timestamp != lastChange)
                        {
                            server.execute(this::loadDeaths);
                        }
                    });
                    thread.start();
                    return thread;
                });
                watchServiceThread.addFileToWatch("deaths.json");
            }
            loadDeaths();
        }
    }

    public void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        CommandDeathCounter.register(dispatcher);
    }

    public void onServerStopping()
    {
        if(currentDeathsFile != null)
        {
            terminateWatchServices();
        }
        currentDeathsFile = null;
        deaths.clear();
        ranking.clear();
    }

    /**
     * @param message message from server
     * @return true if the message is ours
     */
    public boolean isMessageOurs(Component message)
    {
        return message instanceof MutableComponent && message.getContents() instanceof TranslatableContents && (((TranslatableContents)message.getContents()).getKey().startsWith("message.deathcounter.") || ((TranslatableContents)message.getContents()).getKey().startsWith("commands.deathcounter.leaderboard"));
    }

    private static void terminateWatchServices()
    {
        synchronized(WATCH_SERVICES)
        {
            WATCH_SERVICES.forEach((k, v) -> v.stopThread());
            WATCH_SERVICES.clear();
        }
    }

    public abstract boolean postAddPlayerDeathStatEvent(ServerPlayer player, DamageSource source); //Return true = do not add death

    public void addDeath(ServerPlayer player)
    {
        int playerDeaths = deaths.compute(player.getName().getString(), (k, v) -> v == null ? 1 : v + 1);
        saveAndUpdateDeaths(); //this updates the rank as well.
        int rank = getRank(player.getName().getString());

        switch(DeathCounter.config.messageType.get())
        {
            case SHORT -> player.sendSystemMessage(Component.translatable("message.deathcounter.deathAndRank", playerDeaths, rank)); //sendMessage
            case LONG ->
            {
                player.sendSystemMessage(Component.translatable("message.deathcounter.death", playerDeaths), false); //sendMessage
                player.sendSystemMessage(Component.translatable("message.deathcounter.rank", rank), false); //sendMessage
            }
            case NONE -> {}
        }

        switch(DeathCounter.config.broadcastOnDeath.get())
        {
            case SELF -> CommandDeathCounter.broadcastLeaderboard(Collections.singleton(player), null, DeathCounter.config.leaderboardCount.get());
            case ALL -> CommandDeathCounter.broadcastLeaderboard(player.getServer().getPlayerList().getPlayers(), null,DeathCounter.config.leaderboardCount.get());
            case NONE -> {}
        }
    }

    public int getDeaths(String name)
    {
        return deaths.getOrDefault(name, 0);
    }

    public int getDeaths(ServerPlayer player)
    {
        return getDeaths(player.getName().getString());
    }

    public void setDeaths(String name, int i)
    {
        if("all".equalsIgnoreCase(name))
        {
            if(i <= 0)
            {
                deaths.clear();
            }
            else
            {
                TreeMap<String, Integer> newDeaths = new TreeMap<>(Comparator.naturalOrder());
                newDeaths.putAll(deaths.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e-> i)));
                deaths = newDeaths;
            }
        }
        else
        {
            if(i <= 0)
            {
                deaths.remove(name);
            }
            else
            {
                deaths.put(name, i);
            }
        }
        saveAndUpdateDeaths();
    }

    public int transferDeaths(String from, String to)
    {
        if(from == null || to == null || from.isEmpty() || to.isEmpty() || !deaths.containsKey(from))
        {
            return -1;
        }
        deaths.put(to, deaths.get(from));
        deaths.remove(from);
        saveAndUpdateDeaths();
        return deaths.get(to);
    }

    public void saveAndUpdateDeaths()
    {
        if(currentDeathsFile == null)
        {
            DeathCounter.LOGGER.error("Trying to save deaths before we're ready");
            return;
        }
        if(writing)
        {
            DeathCounter.LOGGER.error("Tried to save deaths whilst saving deaths... what?");
            return;
        }
        if(!DeathCounter.config.singleSession.get()) //single session. don't save deaths.
        {
            writing = true;
            try
            {
                Files.write(currentDeathsFile, gson.toJson(deaths).getBytes(StandardCharsets.UTF_8));
                timestamp = Files.getLastModifiedTime(currentDeathsFile).toMillis();
            }
            catch(IOException e)
            {
                DeathCounter.LOGGER.error("Error writing deaths.json: " + currentDeathsFile.toString());
                e.printStackTrace();
            }
            writing = false;
        }

        calculateRank();
    }

    public void loadDeaths()
    {
        if(DeathCounter.config.singleSession.get()) //single session. don't load deaths.
        {
            return;
        }
        if(currentDeathsFile == null)
        {
            DeathCounter.LOGGER.error("Trying to load deaths before we're ready");
            return;
        }
        if(!currentDeathsFile.toFile().exists())
        {
            DeathCounter.LOGGER.info("Deaths not found, presuming new world: " + currentDeathsFile.toString());
            return;
        }
        else if(currentDeathsFile.toFile().isDirectory())
        {
            DeathCounter.LOGGER.error("Deaths is a directory: " + currentDeathsFile.toString());
            return;
        }
        if(writing)
        {
            DeathCounter.LOGGER.error("Trying to read file whilst it is being written: " + currentDeathsFile.toString());
            return;
        }

        try
        {
            byte[] bytes = Files.readAllBytes(currentDeathsFile);
            deaths = gson.fromJson(new String(bytes), new TypeToken<TreeMap<String, Integer>>() {}.getType());
            timestamp = Files.getLastModifiedTime(currentDeathsFile).toMillis();
        }
        catch(IOException | JsonSyntaxException e)
        {
            DeathCounter.LOGGER.error("Error reading deaths.json: " + currentDeathsFile.toString());
            e.printStackTrace();
        }
        calculateRank();
    }

    public void calculateRank()
    {
        ranking.clear();
        for(Map.Entry<String, Integer> e : deaths.entrySet())
        {
            TreeSet<String> names = ranking.computeIfAbsent(e.getValue(), k -> new TreeSet<>(Comparator.naturalOrder()));
            names.add(e.getKey());
        }
    }

    public int getRank(String name)
    {
        int rank = 1;
        for(Map.Entry<Integer, TreeSet<String>> e : ranking.entrySet())
        {
            if(e.getValue().contains(name))
            {
                return rank;
            }
            rank += e.getValue().size();
        }
        return -1;
    }

    public TreeMap<Integer, TreeSet<String>> getRankings()
    {
        return ranking;
    }

    public abstract boolean isFakePlayer(ServerPlayer player);
}
