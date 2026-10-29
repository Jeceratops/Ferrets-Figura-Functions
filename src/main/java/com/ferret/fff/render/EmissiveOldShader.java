package com.ferret.fff.render;

import com.ferret.fff.FerretsFiguraFunctions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = FerretsFiguraFunctions.MOD_ID, value = Dist.CLIENT)
public final class EmissiveOldShader {
    private static ShaderInstance instance;

    private EmissiveOldShader() {}

    public static ShaderInstance get() {
        return instance;
    }

    @SubscribeEvent
    public static void register(RegisterShadersEvent event) throws IOException {
        EmissiveOldRenderType.registerEnumValue();
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(FerretsFiguraFunctions.MOD_ID, "emissive_old"),
                DefaultVertexFormat.NEW_ENTITY), shader -> instance = shader);
    }
}
