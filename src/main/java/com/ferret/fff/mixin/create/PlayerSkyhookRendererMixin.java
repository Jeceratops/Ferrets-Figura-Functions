package com.ferret.fff.mixin.create;

import com.ferret.fff.CompatVisibility;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Ported from JigCompats by jess (soup587) - https://github.com/soup587/JigCompats
@Mixin(targets = "com.simibubi.create.foundation.render.PlayerSkyhookRenderer")
public abstract class PlayerSkyhookRendererMixin {

    @Inject(method = "afterSetupAnim(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/client/model/HumanoidModel;)V",
            at = @At("HEAD"), cancellable = true)
    private static void fff$hideSkyhookAnimation(Player player, HumanoidModel<?> model, CallbackInfo ci) {
        if (player != null && !CompatVisibility.isCreateSkyhookAnimationEnabled(player)) {
            ci.cancel();
        }
    }
}
