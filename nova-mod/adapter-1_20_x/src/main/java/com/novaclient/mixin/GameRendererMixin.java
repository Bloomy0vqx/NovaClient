package com.novaclient.mixin;

import com.novaclient.core.NovaCore;
import com.novaclient.core.event.EventBus;
import com.novaclient.core.event.Render2DEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Method;

@Mixin(targets = "net.minecraft.client.render.GameRenderer")
public class GameRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void novaclient$onRender(float tickDelta, long startTime, boolean tickCounter, CallbackInfo ci) {
        if (!NovaCore.getInstance().isInitialized()) return;

        try {
            Object mc = getMinecraftClient();
            if (mc == null) return;

            if (hasCurrentScreen(mc)) return;

            double mouseX = getMouseX(mc);
            double mouseY = getMouseY(mc);

            Render2DEvent event = new Render2DEvent(tickDelta);
            EventBus.getInstance().fire(event);

            NovaCore.getInstance().getHudEditor().render((float) mouseX, (float) mouseY, tickDelta);

            if (NovaCore.getInstance().isClickGuiOpen() && NovaCore.getInstance().getClickGui() != null) {
                NovaCore.getInstance().getClickGui().render((float) mouseX, (float) mouseY, tickDelta);
            }
        } catch (Exception e) {
            System.err.println("[Nova Client] Render error: " + e.getMessage());
        }
    }

    private Object getMinecraftClient() {
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.MinecraftClient");
            Method getInstance = mcClass.getMethod("getInstance");
            return getInstance.invoke(null);
        } catch (Exception e) { return null; }
    }

    private boolean hasCurrentScreen(Object mc) {
        try {
            java.lang.reflect.Field f = mc.getClass().getDeclaredField("currentScreen");
            f.setAccessible(true);
            return f.get(mc) != null;
        } catch (Exception e) { return false; }
    }

    private double getMouseX(Object mc) {
        try {
            java.lang.reflect.Field mouseField = mc.getClass().getDeclaredField("mouse");
            mouseField.setAccessible(true);
            Object mouse = mouseField.get(mc);
            Method getX = mouse.getClass().getMethod("getX");
            double rawX = (double) getX.invoke(mouse);

            Method getWindow = mc.getClass().getMethod("getWindow");
            Object window = getWindow.invoke(mc);
            Method getScaledWidth = window.getClass().getMethod("getScaledWidth");
            int scaledWidth = (int) getScaledWidth.invoke(window);
            Method getFramebufferWidth = window.getClass().getMethod("getFramebufferWidth");
            int fbWidth = (int) getFramebufferWidth.invoke(window);

            return rawX * (double) scaledWidth / (double) fbWidth;
        } catch (Exception e) { return 0; }
    }

    private double getMouseY(Object mc) {
        try {
            java.lang.reflect.Field mouseField = mc.getClass().getDeclaredField("mouse");
            mouseField.setAccessible(true);
            Object mouse = mouseField.get(mc);
            Method getY = mouse.getClass().getMethod("getY");
            double rawY = (double) getY.invoke(mouse);

            Method getWindow = mc.getClass().getMethod("getWindow");
            Object window = getWindow.invoke(mc);
            Method getScaledHeight = window.getClass().getMethod("getScaledHeight");
            int scaledHeight = (int) getScaledHeight.invoke(window);
            Method getFramebufferHeight = window.getClass().getMethod("getFramebufferHeight");
            int fbHeight = (int) getFramebufferHeight.invoke(window);

            return rawY * (double) scaledHeight / (double) fbHeight;
        } catch (Exception e) { return 0; }
    }
}
