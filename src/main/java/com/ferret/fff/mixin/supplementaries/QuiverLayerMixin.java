package com.ferret.fff.mixin.supplementaries;

import com.ferret.fff.figura.FFFState;
import com.ferret.fff.figura.FFFStates;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import com.ferret.fff.CompatVisibility;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Makes Supplementaries quiver toggleable in Figura
@Mixin(targets = "net.mehvahdjukaar.supplementaries.client.renderers.entities.layers.QuiverLayer")
public abstract class QuiverLayerMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;" + "ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"), cancellable = true)
    private void fff$checkVisibility(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity entity,
                                      float limbSwing, float limbSwingAmount, float partialTick,
                                      float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        Avatar avatar = AvatarManager.getAvatar(entity);
        if (avatar != null) {
            FFFState state = FFFStates.get(avatar);

            state.setNativeQuiverActive(true);

            if (!state.isSuppQuiverVisible()) {
                ci.cancel();
            }
        }
    }
}
