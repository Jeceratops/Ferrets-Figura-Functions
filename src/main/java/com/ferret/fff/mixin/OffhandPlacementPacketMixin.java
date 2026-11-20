package com.ferret.fff.mixin;

import com.ferret.fff.OffhandPlacementGuard;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class OffhandPlacementPacketMixin {

    private static final String OFFGRID_USE_PACKET = "dev.tazer.offgrid.network.UseOverlay";

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void fff$cancelBlockedOffhandUse(Packet<?> packet, CallbackInfo ci) {
        if (!OffhandPlacementGuard.isBlocked()) return;

        if (packet instanceof ServerboundUseItemOnPacket use) {
            if (offhand(use.getHand())) {
                ci.cancel();
                OffhandPlacementGuard.reset();
            }
            return;
        }

        if (packet instanceof ServerboundCustomPayloadPacket custom) {
            Object payload = custom.payload();
            if (payload != null && payload.getClass().getName().equals(OFFGRID_USE_PACKET)) {
                try {
                    Object hand = payload.getClass().getMethod("hand").invoke(payload);
                    if (hand instanceof InteractionHand interactionHand && offhand(interactionHand)) {
                        ci.cancel();
                        OffhandPlacementGuard.reset();
                    }
                } catch (ReflectiveOperationException ignored) {
                    ci.cancel();
                    OffhandPlacementGuard.reset();
                }
            }
        }
    }

    private static boolean offhand(InteractionHand hand) {
        return OffhandPlacementGuard.blocks(hand) && hand == InteractionHand.OFF_HAND;
    }
}
