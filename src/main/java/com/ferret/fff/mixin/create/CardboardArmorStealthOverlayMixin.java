package com.ferret.fff.mixin.create;

import com.ferret.fff.CompatVisibility;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Ported from JigCompats by jess (soup587) - https://github.com/soup587/JigCompats
@Mixin(targets = "com.simibubi.create.content.equipment.armor.CardboardArmorStealthOverlay")
public abstract class CardboardArmorStealthOverlayMixin {

    @Inject(method = "renderHelmetOverlay(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;IIF)V",
            at = @At("HEAD"), cancellable = true)
    private void fff$hideCardboardOverlay(ItemStack stack, Player player, int width, int height, float partialTick,
                                          CallbackInfo ci) {
        if (player != null && !CompatVisibility.isCreateCardboardBoxEnabled(player)) {
            ci.cancel();
        }
    }
}
