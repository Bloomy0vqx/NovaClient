package com.novaclient.mixin;

import com.novaclient.NovaClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.glfw.GLFW;

@Mixin(MinecraftClient.class)
public class KeyboardInputMixin {

    @Unique
    private boolean rightShiftPressed = false;
    @Unique
    private boolean homePressed = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void novaclient$onTick(CallbackInfo ci) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        long window = client.getWindow().getHandle();
        
        NovaClient nova = NovaClient.getInstance();
        if (nova == null) return;

        boolean isRightShift = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        if (isRightShift && !rightShiftPressed) {
            rightShiftPressed = true;
            nova.toggleGui();
        } else if (!isRightShift) {
            rightShiftPressed = false;
        }

        boolean isHome = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_HOME) == GLFW.GLFW_PRESS;
        if (isHome && !homePressed) {
            homePressed = true;
            nova.setHudEditorOpen(!nova.isHudEditorOpen());
        } else if (!isHome) {
            homePressed = false;
        }
    }
}
