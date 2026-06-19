package com.novaclient.mixin;

import com.novaclient.core.NovaCore;
import com.novaclient.core.ui.ClickGuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Method;

@Mixin(targets = "net.minecraft.client.Mouse")
public class MouseInputMixin {

    private float novaclient$getMouseX(Object mc) throws Exception {
        Method getWindow = mc.getClass().getMethod("getWindow");
        Object window = getWindow.invoke(mc);
        Method getScaledWidth = window.getClass().getMethod("getScaledWidth");
        int sw = (int) getScaledWidth.invoke(window);
        Method getFramebufferWidth = window.getClass().getMethod("getFramebufferWidth");
        int fbW = (int) getFramebufferWidth.invoke(window);

        java.lang.reflect.Field mouseField = mc.getClass().getDeclaredField("mouse");
        mouseField.setAccessible(true);
        Object mouse = mouseField.get(mc);
        Method getX = mouse.getClass().getMethod("getX");
        double rawX = (double) getX.invoke(mouse);
        return (float) (rawX * sw / fbW);
    }

    private float novaclient$getMouseY(Object mc) throws Exception {
        Method getWindow = mc.getClass().getMethod("getWindow");
        Object window = getWindow.invoke(mc);
        Method getScaledHeight = window.getClass().getMethod("getScaledHeight");
        int sh = (int) getScaledHeight.invoke(window);
        Method getFramebufferHeight = window.getClass().getMethod("getFramebufferHeight");
        int fbH = (int) getFramebufferHeight.invoke(window);

        java.lang.reflect.Field mouseField = mc.getClass().getDeclaredField("mouse");
        mouseField.setAccessible(true);
        Object mouse = mouseField.get(mc);
        Method getY = mouse.getClass().getMethod("getY");
        double rawY = (double) getY.invoke(mouse);
        return (float) (rawY * sh / fbH);
    }

    private boolean novaclient$isCorrectWindow(Object mc, long window) throws Exception {
        Method getWindow = mc.getClass().getMethod("getWindow");
        Object windowObj = getWindow.invoke(mc);
        Method getHandle = windowObj.getClass().getMethod("getHandle");
        return (long) getHandle.invoke(windowObj) == window;
    }

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void novaclient$onMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (!NovaCore.getInstance().isInitialized()) return;
        if (!NovaCore.getInstance().isClickGuiOpen()) return;

        try {
            Object mc = Class.forName("net.minecraft.client.MinecraftClient").getMethod("getInstance").invoke(null);
            if (mc == null) return;
            if (!novaclient$isCorrectWindow(mc, window)) return;

            float mouseX = novaclient$getMouseX(mc);
            float mouseY = novaclient$getMouseY(mc);

            ClickGuiScreen gui = NovaCore.getInstance().getClickGui();
            if (action == 0) {
                gui.mouseClicked(mouseX, mouseY, button);
            } else if (action == 1) {
                gui.mouseReleased(mouseX, mouseY, button);
            }
            ci.cancel();
        } catch (Exception e) {
            System.err.println("[Nova Client] Mouse click error: " + e.getMessage());
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void novaclient$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (!NovaCore.getInstance().isInitialized()) return;
        if (!NovaCore.getInstance().isClickGuiOpen()) return;

        try {
            Object mc = Class.forName("net.minecraft.client.MinecraftClient").getMethod("getInstance").invoke(null);
            if (mc == null) return;
            if (!novaclient$isCorrectWindow(mc, window)) return;

            float mouseX = novaclient$getMouseX(mc);
            float mouseY = novaclient$getMouseY(mc);

            NovaCore.getInstance().getClickGui().mouseScrolled(mouseX, mouseY, (float) vertical);
            ci.cancel();
        } catch (Exception e) {
            System.err.println("[Nova Client] Mouse scroll error: " + e.getMessage());
        }
    }
}
