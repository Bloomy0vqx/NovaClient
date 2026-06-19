package com.novaclient.mixin;

import com.novaclient.core.NovaCore;
import com.novaclient.core.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class KeyboardInputMixin {

    private boolean novaclient$wasRightShiftDown = false;
    private boolean novaclient$wasHomeDown = false;
    private boolean novaclient$wasRightCtrlDown = false;

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void novaclient$onKeyInput(CallbackInfo ci) {
        MinecraftClient mc = (MinecraftClient) (Object) this;

        try {
            long window = mc.getWindow().getHandle();

            boolean rightShiftDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
            if (rightShiftDown && !novaclient$wasRightShiftDown) {
                if (NovaCore.getInstance().isInitialized()) {
                    mc.send(() -> {
                        NovaCore.getInstance().toggleClickGui();
                        if (NovaCore.getInstance().isClickGuiOpen()) {
                            ClickGuiScreenWrapper wrapper = new ClickGuiScreenWrapper();
                            mc.setScreen(wrapper);
                        } else {
                            if (mc.currentScreen instanceof ClickGuiScreenWrapper) {
                                mc.setScreen(null);
                            }
                        }
                    });
                }
            }
            novaclient$wasRightShiftDown = rightShiftDown;

            boolean homeDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_HOME) == GLFW.GLFW_PRESS;
            if (homeDown && !novaclient$wasHomeDown) {
                if (NovaCore.getInstance().isInitialized() && NovaCore.getInstance().getHudEditor() != null) {
                    NovaCore.getInstance().getHudEditor().toggle();
                }
            }
            novaclient$wasHomeDown = homeDown;

            boolean rightCtrlDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
            if (rightCtrlDown && !novaclient$wasRightCtrlDown) {
                if (NovaCore.getInstance().isInitialized()) {
                    mc.send(() -> {
                        ModuleManager.getInstance().getModules().forEach(m -> {
                            if (m.isEnabled()) m.disable();
                        });
                    });
                }
            }
            novaclient$wasRightCtrlDown = rightCtrlDown;

        } catch (Exception e) {
            System.err.println("[Nova Client] Key input error: " + e.getMessage());
        }
    }
}
