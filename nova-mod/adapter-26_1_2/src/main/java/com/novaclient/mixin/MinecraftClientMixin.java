package com.novaclient.mixin;

import com.novaclient.core.NovaCore;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void novaclient$onSetScreen(Screen screen, CallbackInfo ci) {
        if (!NovaCore.getInstance().isInitialized()) return;

        MinecraftClient mc = (MinecraftClient) (Object) this;

        if (screen == null && mc.currentScreen != null) {
            com.novaclient.core.module.ModuleManager.getInstance().getModules().forEach(m -> {
                if (m.isEnabled()) m.onScreenClose();
            });
        }
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void novaclient$onStart(CallbackInfo ci) {
        System.out.println("[Nova Client] MinecraftClient.run() detected - mod active");
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void novaclient$onClose(CallbackInfo ci) {
        if (NovaCore.getInstance().isInitialized()) {
            NovaCore.getInstance().shutdown();
        }
    }
}
