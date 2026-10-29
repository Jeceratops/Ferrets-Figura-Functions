package com.ferret.fff.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.figuramc.figura.model.rendering.texture.RenderTypes;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class EmissiveOldRenderType {
    private static final String ENUM_NAME = "EMISSIVE_OLD";
    private static final Map<ResourceLocation, RenderType> CACHE = new ConcurrentHashMap<>();
    private static final RenderStateShard.TransparencyStateShard EMISSIVE_TRANSPARENCY =
            new RenderStateShard.TransparencyStateShard("emissive_old_transparency", () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ONE);
            }, () -> {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            });

    private EmissiveOldRenderType() {}

    public static RenderType get(ResourceLocation texture) {
        return CACHE.computeIfAbsent(texture, EmissiveOldRenderType::create);
    }

    private static RenderType create(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(EmissiveOldShader::get))
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(EMISSIVE_TRANSPARENCY)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(false);
        return RenderType.create("emissive_old", DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS, 256, true, false, state);
    }

    public static synchronized void registerEnumValue() {
        try {
            Field valuesField = RenderTypes.class.getDeclaredField("$VALUES");
            RenderTypes[] values = RenderTypes.values();
            for (RenderTypes value : values) {
                if (ENUM_NAME.equals(value.name())) return;
            }
            RenderTypes emissiveOld = (RenderTypes) UnsafeAccess.allocate(RenderTypes.class);
            UnsafeAccess.putObject(emissiveOld, Enum.class.getDeclaredField("name"), ENUM_NAME);
            UnsafeAccess.putInt(emissiveOld, Enum.class.getDeclaredField("ordinal"), values.length);
            UnsafeAccess.putObject(emissiveOld, RenderTypes.class.getDeclaredField("func"),
                    (Function<ResourceLocation, RenderType>) EmissiveOldRenderType::get);
            UnsafeAccess.putBoolean(emissiveOld, RenderTypes.class.getDeclaredField("texture"), true);
            UnsafeAccess.putBoolean(emissiveOld, RenderTypes.class.getDeclaredField("offset"), true);
            RenderTypes[] extended = Arrays.copyOf(values, values.length + 1);
            extended[values.length] = emissiveOld;
            UnsafeAccess.putStaticObject(valuesField, extended);
        } catch (Throwable t) {
            System.err.println("[ferrets_figura_functions] Failed to add Figura EMISSIVE_OLD render type");
            t.printStackTrace();
        }
    }

    private static final class UnsafeAccess {
        private static final sun.misc.Unsafe UNSAFE;
        static {
            try {
                Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                UNSAFE = (sun.misc.Unsafe) field.get(null);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
        static Object allocate(Class<?> type) throws InstantiationException { return UNSAFE.allocateInstance(type); }
        static void putObject(Object target, Field field, Object value) { UNSAFE.putObject(target, UNSAFE.objectFieldOffset(field), value); }
        static void putInt(Object target, Field field, int value) { UNSAFE.putInt(target, UNSAFE.objectFieldOffset(field), value); }
        static void putBoolean(Object target, Field field, boolean value) { UNSAFE.putBoolean(target, UNSAFE.objectFieldOffset(field), value); }
        static void putStaticObject(Field field, Object value) { UNSAFE.putObject(UNSAFE.staticFieldBase(field), UNSAFE.staticFieldOffset(field), value); }
    }
}
