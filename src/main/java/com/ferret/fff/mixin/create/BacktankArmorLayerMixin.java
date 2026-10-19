package com.ferret.fff.mixin.create;

import com.ferret.fff.CompatVisibility;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Makes Create's worn backtank respect Figura's vanilla armor visibility
@Mixin(targets = "com.simibubi.create.content.equipment.armor.BacktankArmorLayer")
public abstract class BacktankArmorLayerMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"), cancellable = true)
    private void fff$hideBacktankWithArmor(PoseStack poseStack, MultiBufferSource bufferSource, int light, LivingEntity entity,
                                           float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                                           float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (CompatVisibility.shouldHideCreateBacktank(entity)) {
            ci.cancel();
        }
    }
}
