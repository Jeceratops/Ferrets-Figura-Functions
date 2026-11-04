package com.ferret.fff.mixin.figura;

import com.ferret.fff.exposure.ExposureCompat;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.figuramc.figura.model.rendering.ImmediateAvatarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ImmediateAvatarRenderer.class, remap = false)
public class ImmediateRendererRenderContextMixin {

    @ModifyExpressionValue(
            method = "renderPart",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/figuramc/figura/model/rendering/EntityRenderMode;name()Ljava/lang/String;",
                    remap = false))
    private String fff$partRenderContext(String original) {
        return ExposureCompat.renderContext(original);
    }
}
