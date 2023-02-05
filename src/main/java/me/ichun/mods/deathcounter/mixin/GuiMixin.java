package me.ichun.mods.deathcounter.mixin;

import me.ichun.mods.deathcounter.common.DeathCounter;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
    public void handleChat(ChatType chatType, Component message, UUID senderId, CallbackInfo ci)
    {
        if(DeathCounter.configClient.hideDeathCounterMessages.get() && DeathCounter.deathHandler.isMessageOurs(message))
        {
            ci.cancel();
        }
    }
}
