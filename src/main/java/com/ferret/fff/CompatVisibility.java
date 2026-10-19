package com.ferret.fff;

import com.ferret.fff.figura.FFFState;
import com.ferret.fff.figura.FFFStates;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.lua.api.vanilla_model.VanillaPart;
import org.figuramc.figura.utils.RenderUtils;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class CompatVisibility {

    private CompatVisibility() {}

    private static final Map<LivingEntity, Boolean> NATIVE_QUIVER_ACTIVE =
            Collections.synchronizedMap(new WeakHashMap<>());

    private static FFFState state(LivingEntity entity) {
        Avatar avatar = AvatarManager.getAvatar(entity);
        return avatar == null ? null : FFFStates.get(avatar);
    }

    public static boolean isSuppQuiverVisible(LivingEntity entity) {
        FFFState state = state(entity);
        return state == null || state.suppQuiverVisible();
    }

    public static void setNativeQuiverActive(LivingEntity entity, boolean active) {
        if (entity != null) {
            NATIVE_QUIVER_ACTIVE.put(entity, active);
        }
    }

    public static boolean isQuiverEquipped(LivingEntity entity) {
        if (entity == null) return false;
        return NATIVE_QUIVER_ACTIVE.getOrDefault(entity, false);
    }

    public static boolean isAmmdLanternEnabled(LivingEntity entity) {
        FFFState state = state(entity);
        return state == null || state.ammdLantern();
    }

    public static boolean isAmmdTorchEnabled(LivingEntity entity) {
        FFFState state = state(entity);
        return state == null || state.ammdTorch();
    }

    public static boolean isAmmdCandleHolderEnabled(LivingEntity entity) {
        FFFState state = state(entity);
        return state == null || state.ammdCandleHolder();
    }

    public static boolean isCreateSkyhookAnimationEnabled(LivingEntity entity) {
        FFFState state = state(entity);
        return state == null || state.createSkyhookAnimation();
    }

    public static boolean isCreateCardboardBoxEnabled(LivingEntity entity) {
        FFFState state = state(entity);
        return state == null || state.createCardboardBox();
    }

    public static boolean shouldHideCreateBacktank(LivingEntity entity) {
        if (entity == null) {
            return false;
        }

        Avatar avatar = AvatarManager.getAvatar(entity);
        if (avatar == null) {
            return false;
        }

        VanillaPart chest = RenderUtils.partFromSlot(avatar, EquipmentSlot.CHEST);
        return chest != null && !chest.checkVisible();
    }

    public static Boolean amendmentsEnabled(LivingEntity entity, String rendererClassName) {
        if (rendererClassName == null) {
            return null;
        }
        if (rendererClassName.endsWith("LanternRendererExtension")) {
            return isAmmdLanternEnabled(entity);
        }
        if (rendererClassName.endsWith("TorchRendererExtension")) {
            return isAmmdTorchEnabled(entity);
        }
        if (rendererClassName.endsWith("CandleHolderRendererExtension")) {
            return isAmmdCandleHolderEnabled(entity);
        }
        return null;
    }
}
