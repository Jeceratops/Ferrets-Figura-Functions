package com.ferret.fff.mixin.moonlight;

import com.ferret.fff.CompatVisibility;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.moonlight.api.item.IThirdPersonSpecialItemRenderer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Curse you ammendments...
@Mixin(value = PlayerItemInHandLayer.class, priority = 500)
public abstract class PlayerItemInHandLayerCompatMixin<T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel>
        extends ItemInHandLayer<T, M> {

    protected PlayerItemInHandLayerCompatMixin(RenderLayerParent<T, M> renderLayerParent, ItemInHandRenderer itemInHandRenderer) {
        super(renderLayerParent, itemInHandRenderer);
    }

    @Inject(method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;"
                    + "Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;"
                    + "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    private void fff$routeHeldItem(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (stack.isEmpty()) {
            return;
        }
        IThirdPersonSpecialItemRenderer provider = IThirdPersonSpecialItemRenderer.get(stack.getItem());
        if (provider == null) {
            return;
        }
        Boolean enabled = CompatVisibility.amendmentsEnabled(entity, provider.getClass().getName());
        if (enabled == null) {
            return;
        }
        if (enabled) {
            return;
        }
        super.renderArmWithItem(entity, stack, displayContext, arm, poseStack, buffer, light);
        ci.cancel();
    }
}
