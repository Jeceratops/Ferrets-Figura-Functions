package com.ferret.fff.mixin.docs;

import com.ferret.fff.figura.FFFAPI;
import org.figuramc.figura.lua.docs.FiguraGlobalsDocs;
import org.figuramc.figura.lua.docs.LuaFieldDoc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = FiguraGlobalsDocs.class, remap = false)
public class FiguraGlobalsDocsMixin {
    @Unique
    @LuaFieldDoc("globals.FFF")
    public FFFAPI FFF;
}
