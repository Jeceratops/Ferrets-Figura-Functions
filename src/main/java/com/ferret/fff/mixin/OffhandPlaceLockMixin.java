package com.ferret.fff.mixin;

import com.ferret.fff.OffhandPlacementGuard;
import com.ferret.fff.figura.FFFState;
import com.ferret.fff.figura.FFFStates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Old method fucked up offgrid... whoops
@Mixin(BlockItem.class)
public abstract class OffhandPlaceLockMixin {

    @Inject(method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At("HEAD"), cancellable = true)
    private void fff$blockOffhandPlacement(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (context.getHand() != InteractionHand.OFF_HAND) return;
        if (!fff$isOffhandLocked(context.getPlayer(), context.getItemInHand())) return;
        OffhandPlacementGuard.block(InteractionHand.OFF_HAND);
        cir.setReturnValue(InteractionResult.FAIL);
    }

    @Unique
    private static boolean fff$isOffhandLocked(Player player, ItemStack stack) {
        if (player == null || stack.isEmpty()) return false;

        Avatar avatar = AvatarManager.getAvatar(player);
        if (avatar == null) return false;

        FFFState state = FFFStates.get(avatar);
        if (state == null) return false;

        var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId != null && state.isOffhandPlaceLocked(itemId.toString());
    }
}
