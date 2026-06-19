package com.novaclient.mixin;

import com.novaclient.core.NovaCore;
import com.novaclient.core.event.EventBus;
import com.novaclient.core.event.Render2DEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void novaclient$onRender(float tickDelta, long startTime, boolean tickCounter, CallbackInfo ci) {
        if (!NovaCore.getInstance().isInitialized()) return;

        try {
            Render2DEvent event = new Render2DEvent(tickDelta);
            EventBus.getInstance().fire(event);
        } catch (Exception e) {
            System.err.println("[Nova Client] Render event error: " + e.getMessage());
        }
    }
}
