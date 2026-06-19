package com.novaclient.mixin;

import com.novaclient.core.NovaCore;
import com.novaclient.core.module.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.MinecraftClient")
public class MinecraftClientMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void novaclient$onSetScreen(Object screen, CallbackInfo ci) {
        if (!NovaCore.getInstance().isInitialized()) return;

        try {
            Object mc = Class.forName("net.minecraft.client.MinecraftClient")
                .getMethod("getInstance").invoke(null);
            if (mc == null) return;

            java.lang.reflect.Field currentScreenField = mc.getClass().getDeclaredField("currentScreen");
            currentScreenField.setAccessible(true);
            Object currentScreen = currentScreenField.get(mc);

            if (screen == null && currentScreen != null) {
                ModuleManager.getInstance().getModules().forEach(m -> {
                    if (m.isEnabled()) m.onScreenClose();
                });
            }
        } catch (Exception e) {
            System.err.println("[Nova Client] Screen hook error: " + e.getMessage());
        }
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void novaclient$onStart(CallbackInfo ci) {
        System.out.println("[Nova Client] MinecraftClient.run() detected - mod should be active");
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void novaclient$onClose(CallbackInfo ci) {
        if (NovaCore.getInstance().isInitialized()) {
            NovaCore.getInstance().shutdown();
        }
    }
}
