package com.ferret.fff;

import net.minecraft.world.InteractionHand;

public final class OffhandPlacementGuard {

    private static boolean blocked;
    private static InteractionHand hand;

    private OffhandPlacementGuard() {}

    public static void reset() {
        blocked = false;
        hand = null;
    }

    public static void block(InteractionHand hand) {
        blocked = true;
        OffhandPlacementGuard.hand = hand;
    }

    public static boolean isBlocked() {
        return blocked;
    }

    public static boolean blocks(InteractionHand hand) {
        return blocked && OffhandPlacementGuard.hand == hand;
    }
}
