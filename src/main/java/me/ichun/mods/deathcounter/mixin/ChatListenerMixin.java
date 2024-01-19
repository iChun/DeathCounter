package me.ichun.mods.deathcounter.mixin;

import me.ichun.mods.deathcounter.common.DeathCounter;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatListener.class)
public abstract class ChatListenerMixin
{
    @Inject(method = "handleSystemMessage", at = @At("HEAD"), cancellable = true)
    public void deathcounter_handleSystemMessage(Component message, boolean isOverlay, CallbackInfo ci)
    {
        if(DeathCounter.configClient.hideDeathCounterMessages.get() && DeathCounter.deathHandler.isMessageOurs(message))
        {
            ci.cancel();
        }
    }
}
