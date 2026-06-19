package com.novaclient.mixin;

import com.novaclient.core.NovaCore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Method;

@Mixin(targets = "net.minecraft.client.MinecraftClient")
public class KeyboardInputMixin {

    private boolean novaclient$wasRightShiftDown = false;
    private boolean novaclient$wasHomeDown = false;

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void novaclient$onKeyInput(CallbackInfo ci) {
        if (!NovaCore.getInstance().isInitialized()) return;

        try {
            Object mc = Class.forName("net.minecraft.client.MinecraftClient")
                .getMethod("getInstance").invoke(null);
            if (mc == null) return;

            java.lang.reflect.Field currentScreenField = mc.getClass().getDeclaredField("currentScreen");
            currentScreenField.setAccessible(true);
            if (currentScreenField.get(mc) != null) return;

            Method getWindow = mc.getClass().getMethod("getWindow");
            Object window = getWindow.invoke(mc);
            Method getHandle = window.getClass().getMethod("getHandle");
            long handle = (long) getHandle.invoke(window);

            Class<?> glfwClass = Class.forName("org.lwjgl.glfw.GLFW");
            Method glfwGetKey = glfwClass.getMethod("glfwGetKey", long.class, int.class);

            boolean rightShiftDown = (int) glfwGetKey.invoke(null, handle, 344) == 1;
            if (rightShiftDown && !novaclient$wasRightShiftDown) {
                if (NovaCore.getInstance().getClickGui() != null) {
                    NovaCore.getInstance().toggleClickGui();
                }
            }
            novaclient$wasRightShiftDown = rightShiftDown;

            if (NovaCore.getInstance().isClickGuiOpen()) {
                boolean escDown = (int) glfwGetKey.invoke(null, handle, 256) == 1;
                if (escDown) {
                    mc.getClass().getMethod("send", Runnable.class).invoke(mc, (Runnable) () -> {
                        NovaCore.getInstance().getClickGui().close();
                    });
                }
            }

            boolean homeDown = (int) glfwGetKey.invoke(null, handle, 268) == 1;
            if (homeDown && !novaclient$wasHomeDown) {
                if (NovaCore.getInstance().getHudEditor() != null) {
                    NovaCore.getInstance().getHudEditor().toggle();
                }
            }
            novaclient$wasHomeDown = homeDown;
        } catch (Exception e) {
            System.err.println("[Nova Client] Key input error: " + e.getMessage());
        }
    }
}
