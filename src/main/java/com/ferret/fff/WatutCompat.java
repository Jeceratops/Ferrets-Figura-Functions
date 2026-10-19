package com.ferret.fff;

import com.corosus.watut.PlayerStatus;
import com.corosus.watut.PlayerStatusManager;
import com.corosus.watut.WatutMod;
import com.ferret.fff.figura.FFFState;
import com.ferret.fff.figura.FFFStates;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;

import java.util.UUID;

public final class WatutCompat {

    private static boolean resolved;
    private static boolean loaded;
    private static boolean headCaptured;
    private static float headXRot;
    private static float headYRot;
    private static float headZRot;

    private WatutCompat() {}

    public static boolean isLoaded() {
        resolve();
        return loaded;
    }

    public static boolean idleAnimationWouldPlay(Player player) {
        PlayerStatus status = status(player);
        return status != null && status.isIdle();
    }

    public static boolean typingAnimationWouldPlay(Player player) {
        PlayerStatus status = status(player);
        return status != null && status.getPlayerChatState() == PlayerStatus.PlayerChatState.CHAT_TYPING;
    }

    public static boolean typingTextBoxWouldRender(Player player) {
        PlayerStatus status = status(player);
        if (status == null || status.getPlayerGuiState() != PlayerStatus.PlayerGuiState.CHAT_SCREEN) {
            return false;
        }
        PlayerStatus.PlayerChatState chat = status.getPlayerChatState();
        return chat == PlayerStatus.PlayerChatState.CHAT_FOCUSED
                || chat == PlayerStatus.PlayerChatState.CHAT_TYPING;
    }

    public static boolean idleAnimationSuppressed(Entity entity) {
        FFFState state = state(AvatarManager.getAvatar(entity));
        return state != null && !state.watutIdleAnimation();
    }

    public static boolean typingAnimationSuppressed(Entity entity) {
        FFFState state = state(AvatarManager.getAvatar(entity));
        return state != null && !state.watutTypingAnimation();
    }

    public static boolean typingAnimationSuppressedByUuid(UUID uuid) {
        FFFState state = state(AvatarManager.getAvatarForPlayer(uuid));
        return state != null && !state.watutTypingAnimation();
    }

    public static boolean typingTextBoxSuppressed(Player player) {
        FFFState state = state(AvatarManager.getAvatar(player));
        if (state == null || state.watutTypingTextBox()) {
            return false;
        }
        PlayerStatus status = status(player);
        return status != null && status.getPlayerGuiState() == PlayerStatus.PlayerGuiState.CHAT_SCREEN;
    }

    public static void clearGuiVisual(Player player) {
        PlayerStatus status = status(player);
        if (status == null) {
            return;
        }
        Particle particle = status.getParticle();
        if (particle != null) {
            particle.remove();
        }
        status.setParticle(null);
    }

    public static void clearIdleVisual(Player player) {
        PlayerStatus status = status(player);
        if (status == null) {
            return;
        }
        Particle particle = status.getParticleIdle();
        if (particle != null) {
            particle.remove();
        }
        status.setParticleIdle(null);
    }

    public static void captureHead(Object model) {
        headCaptured = false;
        if (model instanceof PlayerModel<?> pm) {
            headXRot = pm.head.xRot;
            headYRot = pm.head.yRot;
            headZRot = pm.head.zRot;
            headCaptured = true;
        }
    }

    public static void restoreHead(Object model, Entity entity) {
        boolean captured = headCaptured;
        headCaptured = false;
        if (!captured || !(model instanceof PlayerModel<?> pm) || !keepNaturalHead(entity)) {
            return;
        }
        pm.head.xRot = headXRot;
        pm.head.yRot = headYRot;
        pm.head.zRot = headZRot;
        pm.hat.xRot = headXRot;
        pm.hat.yRot = headYRot;
        pm.hat.zRot = headZRot;
    }

    private static boolean keepNaturalHead(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        FFFState state = state(AvatarManager.getAvatar(player));
        if (state == null) {
            return false;
        }
        PlayerStatus status = status(player);
        if (status == null) {
            return false;
        }

        if (!state.watutIdleAnimation() && status.isIdle()) {
            return true;
        }

        return !state.watutTypingTextBox() && status.getPlayerGuiState() == PlayerStatus.PlayerGuiState.CHAT_SCREEN;
    }

    private static FFFState state(Avatar avatar) {
        return avatar == null ? null : FFFStates.get(avatar);
    }

    private static PlayerStatus status(Player player) {
        if (player == null || !resolve()) {
            return null;
        }
        try {
            PlayerStatusManager manager = WatutMod.getPlayerStatusManagerClient();
            if (manager == null) {
                return null;
            }
            return manager.getStatus(player);
        } catch (RuntimeException | LinkageError e) {
            return null;
        }
    }

    private static boolean resolve() {
        if (resolved) {
            return loaded;
        }
        resolved = true;
        ModList modList = ModList.get();
        loaded = modList != null && modList.isLoaded("watut");
        return loaded;
    }
}
