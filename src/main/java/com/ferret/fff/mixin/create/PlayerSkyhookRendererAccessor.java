package com.ferret.fff.mixin.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;

// Ported from JigCompats by jess (soup587) - https://github.com/soup587/JigCompats
@Mixin(targets = "com.simibubi.create.foundation.render.PlayerSkyhookRenderer")
public interface PlayerSkyhookRendererAccessor {

    @Accessor("hangingPlayers")
    static Set<UUID> getHangingPlayers() {
        throw new AssertionError("Untransformed accessor");
    }
}
