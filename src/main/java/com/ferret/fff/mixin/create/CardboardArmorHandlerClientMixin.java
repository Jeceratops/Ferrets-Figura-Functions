package com.ferret.fff.mixin.create;

import com.ferret.fff.CompatVisibility;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Ported from JigCompats by jess (soup587) - https://github.com/soup587/JigCompats
@Mixin(targets = "com.simibubi.create.content.equipment.armor.CardboardArmorHandlerClient")
public abstract class CardboardArmorHandlerClientMixin {

    @Inject(method = "playerRendersAsBoxWhenSneaking(Lnet/neoforged/neoforge/client/event/RenderPlayerEvent$Pre;)V",
            at = @At("HEAD"), cancellable = true)
    private static void fff$hideCardboardBox(RenderPlayerEvent.Pre event, CallbackInfo ci) {
        Player player = event.getEntity();
        if (player != null && !CompatVisibility.isCreateCardboardBoxEnabled(player)) {
            ci.cancel();
        }
    }
}
