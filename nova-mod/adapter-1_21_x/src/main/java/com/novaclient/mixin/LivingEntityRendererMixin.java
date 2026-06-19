package com.novaclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "shouldRenderName(Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void novaclient$alwaysShowPlayerName(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ClientPlayerEntity) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && mc.player.getUuid().equals(entity.getUuid())) {
                return;
            }
            cir.setReturnValue(true);
        }
    }
}
