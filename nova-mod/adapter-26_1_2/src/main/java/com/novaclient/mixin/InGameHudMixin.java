package com.novaclient.mixin;

import com.novaclient.adapter.Adapter26_1_2Fabric;
import com.novaclient.core.NovaCore;
import com.novaclient.core.ui.hud.HudManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void novaclient$onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!NovaCore.getInstance().isInitialized()) return;

        try {
            Adapter26_1_2Fabric.setCurrentDrawContext(context);

            float tickDelta = tickCounter.getTickDelta(false);
            HudManager.getInstance().renderAll(tickDelta);

            if (NovaCore.getInstance().getHudEditor() != null && NovaCore.getInstance().getHudEditor().isOpen()) {
                MinecraftClient mc = MinecraftClient.getInstance();
                double mouseX = mc.mouse.getX() * (double) mc.getWindow().getScaledWidth() / (double) mc.getWindow().getFramebufferWidth();
                double mouseY = mc.mouse.getY() * (double) mc.getWindow().getScaledHeight() / (double) mc.getWindow().getFramebufferHeight();
                NovaCore.getInstance().getHudEditor().render((float) mouseX, (float) mouseY, tickDelta);
            }
        } catch (Exception e) {
            System.err.println("[Nova Client] HUD render error: " + e.getMessage());
        }
    }
}
