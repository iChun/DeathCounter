package me.ichun.mods.deathcounter.common.core;

import me.ichun.mods.deathcounter.common.DeathCounter;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Config
{
    public ConfigWrapper<DeathCounter.MessageType> messageType;
    public ConfigWrapper<DeathCounter.BroadcastType> broadcastOnDeath;
    public ConfigWrapper<Integer> leaderboardCount;
    public ConfigWrapper<Boolean> singleSession;
    public ConfigWrapper<Integer> commandPermissionLevel;

    protected static class Reference
    {
        public static final String MESSAGE_TYPE_COMMENT = "What kind of message should we send players when they die?\nAccepts: NONE, SHORT, LONG";
        public static final String BROADCAST_ON_DEATH_COMMENT = "Should we broadcast the leaderboard to the player when they die?\nAccepts: NONE, SELF, ALL";
        public static final String LEADERBOARD_COUNT_COMMENT = "Number of names to show in the leaderboard";
        public static final String SINGLE_SESSION_COMMENT = "Do not persist deaths across sessions? Turning this on disables saving deaths to server folder.";
        public static final String COMMAND_PERMISSION_LEVEL_COMMENT = "Permission level required to use the op commands for Death Counter";
    }

    public static class ConfigWrapper<T>
    {
        public final Supplier<T> getter;
        public final Consumer<T> setter;
        public final Runnable saver;

        public ConfigWrapper(Supplier<T> getter, Consumer<T> setter) {
            this.getter = getter;
            this.setter = setter;
            this.saver = null;
        }

        public ConfigWrapper(Supplier<T> getter, Consumer<T> setter, Runnable saver) {
            this.getter = getter;
            this.setter = setter;
            this.saver = saver;
        }

        public T get()
        {
            return getter.get();
        }

        public void set(T obj)
        {
            setter.accept(obj);
        }

        public void save()
        {
            if(saver != null)
            {
                saver.run();
            }
        }
    }
}
