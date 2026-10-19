package com.ferret.fff.figura;

import org.figuramc.figura.avatar.Avatar;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class FFFStates {

    private static final Map<Avatar, FFFState> BY_AVATAR =
            Collections.synchronizedMap(new WeakHashMap<>());

    private FFFStates() {}

    public static FFFState get(Avatar avatar) {
        return BY_AVATAR.computeIfAbsent(avatar, key -> new FFFState());
    }
}
