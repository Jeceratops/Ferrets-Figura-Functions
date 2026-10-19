package com.ferret.fff.mixin.watut;

import com.ferret.fff.WatutCompat;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

// Lets an avatar opt out of WATUT's visuals
@Mixin(targets = "com.corosus.watut.PlayerStatusManagerClient")
public abstract class PlayerStatusManagerClientMixin {

    private static final String SETUP_ROTATIONS = "setupRotationsHook(Lnet/minecraft/client/model/EntityModel;" + "Lnet/minecraft/world/entity/Entity;FFFFF)V";

    @Inject(method = SETUP_ROTATIONS, at = @At("HEAD"))
    private void fff$captureHead(EntityModel<?> model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        WatutCompat.captureHead(model);
    }

    @Inject(method = SETUP_ROTATIONS, at = @At("TAIL"))
    private void fff$restoreHead(EntityModel<?> model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        WatutCompat.restoreHead(model, entity);
    }

    @ModifyExpressionValue(method = SETUP_ROTATIONS,
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/corosus/watut/config/ConfigClient;showPlayerAnimation_Typing:Z"))
    private boolean fff$typingAnimation(boolean original, @Local(argsOnly = true) Entity entity) {
        return original && !WatutCompat.typingAnimationSuppressed(entity);
    }

    @ModifyExpressionValue(method = "setPoseTarget(Ljava/util/UUID;Z)V",
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/corosus/watut/config/ConfigClient;showPlayerAnimation_Typing:Z"))
    private boolean fff$typingAnimationPose(boolean original, @Local(argsOnly = true) UUID uuid) {
        return original && !WatutCompat.typingAnimationSuppressedByUuid(uuid);
    }

    @ModifyExpressionValue(method = "tickOtherPlayerClient(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/corosus/watut/config/ConfigClient;showPlayerActiveChatGui:Z"))
    private boolean fff$typingTextBox(boolean original, @Local(argsOnly = true) Player player) {
        return original && !WatutCompat.typingTextBoxSuppressed(player);
    }

    @ModifyExpressionValue(method = "tickOtherPlayerClient(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/corosus/watut/config/ConfigClient;showIdleStatesInPlayerAboveHead:Z"))
    private boolean fff$idleParticle(boolean original, @Local(argsOnly = true) Player player) {
        return original && !WatutCompat.idleAnimationSuppressed(player);
    }

    @Inject(method = "tickOtherPlayerClient(Lnet/minecraft/world/entity/player/Player;)V", at = @At("TAIL"))
    private void fff$clearSuppressedParticles(Player player, CallbackInfo ci) {
        if (WatutCompat.typingTextBoxSuppressed(player)) {
            WatutCompat.clearGuiVisual(player);
        }
        if (WatutCompat.idleAnimationSuppressed(player)) {
            WatutCompat.clearIdleVisual(player);
        }
    }
}
