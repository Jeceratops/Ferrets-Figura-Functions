package com.ferret.fff;

import com.evandev.zipline.duck.ZiplinePlayerDuck;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;

public final class ZiplineCompat {

    private static final TagKey<Item> ATTACHMENT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("zipline", "attachment"));

    private static boolean resolved;
    private static boolean loaded;

    private ZiplineCompat() {}

    public static boolean isTryingToZip(Player player) {
        return player != null && player.isUsingItem() && player.getUseItem().is(ATTACHMENT);
    }

    public static boolean isZipping(Player player) {
        if (player == null || !resolve()) {
            return false;
        }
        try {
            return player instanceof ZiplinePlayerDuck duck && duck.zipline$isActuallyUsing();
        } catch (RuntimeException | LinkageError e) {
            return false;
        }
    }

    private static boolean resolve() {
        if (resolved) {
            return loaded;
        }
        resolved = true;
        ModList modList = ModList.get();
        loaded = modList != null && modList.isLoaded("zipline");
        return loaded;
    }
}
