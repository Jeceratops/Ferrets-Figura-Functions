package com.ferret.fff.mixin;

import com.ferret.fff.render.EmissiveOldRenderType;
import org.figuramc.figura.model.rendering.texture.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTypes.class)
public abstract class RenderTypesMixin {
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void fff$addEmissiveOld(CallbackInfo ci) {
        EmissiveOldRenderType.registerEnumValue();
    }
}
