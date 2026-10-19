package com.ferret.fff.mixin;

import com.ferret.fff.figura.FFFState;
import com.ferret.fff.figura.FFFStates;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// God Dammit I want to stop placing down my lantern randomly D:
@Mixin(MultiPlayerGameMode.class)
public abstract class OffhandPlaceLockMixin {

    @Inject(method = "useItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"), cancellable = true)
    private void fff$blockOffhandUseItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (fff$isOffhandLocked(player, hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"), cancellable = true)
    private void fff$blockOffhandUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (fff$isOffhandLocked(player, hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Unique
    private static boolean fff$isOffhandLocked(Player player, InteractionHand hand) {
        if (hand != InteractionHand.OFF_HAND || player == null) {
            return false;
        }

        Avatar avatar = AvatarManager.getAvatar(player);
        if (avatar == null) {
            return false;
        }

        FFFState state = FFFStates.get(avatar);
        if (state == null) {
            return false;
        }

        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (stack.isEmpty()) {
            return false;
        }

        var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId != null && state.isOffhandPlaceLocked(itemId.toString());
    }
}
