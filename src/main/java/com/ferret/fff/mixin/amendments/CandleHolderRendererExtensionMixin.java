package com.ferret.fff.mixin.amendments;

import com.ferret.fff.CompatVisibility;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


//Candle Holder Holding. Moonlight shenanigans overides vanilla render, needed to be fixed.
@Mixin(targets = "net.mehvahdjukaar.amendments.client.renderers.CandleHolderRendererExtension")
public abstract class CandleHolderRendererExtensionMixin {

    @Inject(method = "renderThirdPersonItem(Lnet/minecraft/client/model/EntityModel;" +
                    "Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;" +
                    "Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    private void fff$hideCandleHolder(EntityModel<?> parentModel, LivingEntity entity, ItemStack stack, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (!CompatVisibility.isAmmdCandleHolderEnabled(entity)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFirstPersonItem(Lnet/minecraft/client/player/AbstractClientPlayer;" +
                    "Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;" +
                    "Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "FFFFLnet/minecraft/client/renderer/MultiBufferSource;I" +
                    "Lnet/minecraft/client/renderer/ItemInHandRenderer;)Z",
            at = @At("HEAD"), cancellable = true)
    private void fff$hideCandleHolderFirstPerson(AbstractClientPlayer player, ItemStack stack, InteractionHand hand,
                                                   HumanoidArm arm, PoseStack poseStack, float partialTicks, float pitch,
                                                   float attackAnim, float equipAnim, MultiBufferSource buffer, int light,
                                                   ItemInHandRenderer renderer, CallbackInfoReturnable<Boolean> cir) {
        if (!CompatVisibility.isAmmdCandleHolderEnabled(player)) {
            cir.setReturnValue(false);
        }
    }
}
