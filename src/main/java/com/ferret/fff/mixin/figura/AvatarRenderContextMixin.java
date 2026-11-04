package com.ferret.fff.mixin.figura;

import com.ferret.fff.exposure.ExposureCompat;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.figuramc.figura.avatar.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Avatar.class, remap = false)
public class AvatarRenderContextMixin {

    @ModifyExpressionValue(
            method = "renderEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/figuramc/figura/model/rendering/EntityRenderMode;name()Ljava/lang/String;",
                    remap = false))
    private String fff$renderContext(String original) {
        return ExposureCompat.renderContext(original);
    }

    @ModifyExpressionValue(
            method = "postRenderEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/figuramc/figura/model/rendering/EntityRenderMode;name()Ljava/lang/String;",
                    remap = false))
    private String fff$postRenderContext(String original) {
        return ExposureCompat.renderContext(original);
    }
}
