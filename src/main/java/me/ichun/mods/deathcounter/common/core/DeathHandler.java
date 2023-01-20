package me.ichun.mods.deathcounter.common.core;

import com.electronwill.nightconfig.core.file.FileWatcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import me.ichun.mods.deathcounter.api.AddPlayerDeathStatEvent;
import me.ichun.mods.deathcounter.common.DeathCounter;
import me.ichun.mods.deathcounter.common.command.DeathCounterCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = DeathCounter.MOD_ID)
public class DeathHandler
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static TreeMap<String, Integer> deaths = new TreeMap<>(Comparator.naturalOrder());
    private static TreeMap<Integer, TreeSet<String>> ranking = new TreeMap<>(Comparator.reverseOrder());
    private static Path currentDeathsFile = null;
    private static long timestamp = 0L;
    private static boolean writing = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeathEvent(LivingDeathEvent event)
    {
        if(!event.getEntity().getCommandSenderWorld().isClientSide() &&
                event.getEntity() instanceof ServerPlayer &&
                !isFakePlayer((ServerPlayer)event.getEntity()) &&
                !MinecraftForge.EVENT_BUS.post(new AddPlayerDeathStatEvent((ServerPlayer)event.getEntity(), event.getSource()))
        )
        {
            addDeath((ServerPlayer)event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onServerAboutToStartEvent(ServerStartingEvent event)
    {
        MinecraftServer server = event.getServer();
        if(!DeathCounter.config.singleSession.get())
        {
            currentDeathsFile = server.storageSource.getWorldDir().resolve("deaths.json");
            try
            {
                FileWatcher.defaultInstance().addWatch(currentDeathsFile, () -> {
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
                        server.execute(DeathHandler::loadDeaths);
                    }
                });
            }
            catch(IOException e)
            {
                DeathCounter.LOGGER.error("Error listening to deaths.json: " + currentDeathsFile.toString());
                e.printStackTrace();
            }
            loadDeaths();
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        DeathCounterCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStoppingEvent(ServerStoppingEvent event)
    {
        if(currentDeathsFile != null)
        {
            FileWatcher.defaultInstance().removeWatch(currentDeathsFile);
        }
        currentDeathsFile = null;
        deaths.clear();
        ranking.clear();
    }

    public static void addDeath(ServerPlayer player)
    {
        int playerDeaths = deaths.compute(player.getName().getString(), (k, v) -> v == null ? 1 : v + 1);
        saveAndUpdateDeaths(); //this updates the rank as well.
        int rank = getRank(player.getName().getString());

        switch(DeathCounter.config.messageType.get())
        {
            case SHORT:
            {
                player.sendSystemMessage(Component.translatable("message.deathcounter.deathAndRank", playerDeaths, rank)); //sendMessage
                break;
            }
            case LONG:
            {
                player.sendSystemMessage(Component.translatable("message.deathcounter.death", playerDeaths), false); //sendMessage
                player.sendSystemMessage(Component.translatable("message.deathcounter.rank", rank), false); //sendMessage
                break;
            }
            default:
            case NONE:
        }
    }

    public static int getDeaths(String name)
    {
        return deaths.getOrDefault(name, 0);
    }

    public static int getDeaths(ServerPlayer player)
    {
        return getDeaths(player.getName().getString());
    }

    public static void setDeaths(String name, int i)
    {
        if("all".equals(name.toLowerCase()))
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

    public static int transferDeaths(String from, String to)
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

    public static void saveAndUpdateDeaths()
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

    public static void loadDeaths()
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

    public static void calculateRank()
    {
        ranking.clear();
        for(Map.Entry<String, Integer> e : deaths.entrySet())
        {
            TreeSet<String> names = ranking.computeIfAbsent(e.getValue(), k -> new TreeSet<>(Comparator.naturalOrder()));
            names.add(e.getKey());
        }
    }

    public static int getRank(String name)
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

    public static TreeMap<Integer, TreeSet<String>> getRankings()
    {
        return ranking;
    }

    public static boolean isFakePlayer(ServerPlayer player)
    {
        return player instanceof FakePlayer || player.connection == null; // || player.getName().getUnformattedComponentText().toLowerCase().startsWith("fakeplayer") || player.getName().getUnformattedComponentText().toLowerCase().startsWith("[minecraft]");
    }
}
