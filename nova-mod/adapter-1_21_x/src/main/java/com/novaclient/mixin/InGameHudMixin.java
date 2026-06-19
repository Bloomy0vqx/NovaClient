package com.novaclient.mixin;

import com.novaclient.NovaClient;
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
        if (NovaClient.getInstance() != null && NovaClient.getInstance().getHudRenderer() != null) {
            float tickDelta = tickCounter.getTickDelta(false);
            NovaClient.getInstance().getHudRenderer().render(context, tickDelta);
        }
    }
}
