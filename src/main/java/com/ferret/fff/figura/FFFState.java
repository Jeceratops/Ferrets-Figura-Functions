package com.ferret.fff.figura;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class FFFState {

    private boolean suppQuiverVisible = true;
    private boolean nativeQuiverActive = false;
    private boolean ammdLantern = true;
    private boolean ammdTorch = true;
    private boolean ammdCandleHolder = true;
    private boolean watutIdleAnimation = true;
    private boolean watutTypingAnimation = true;
    private boolean watutTypingTextBox = true;
    private boolean createSkyhookAnimation = true;
    private boolean createCardboardBox = true;

    public boolean suppQuiverVisible() {
        return this.suppQuiverVisible;
    }

    public boolean isNativeQuiverActive() {
        return this.nativeQuiverActive;
    }

    public void setNativeQuiverActive(boolean active) {
        this.nativeQuiverActive = active;
    }

    public void resetNativeQuiver() {
        this.nativeQuiverActive = false;
    }

    void setSuppQuiverVisible(boolean state) {
        this.suppQuiverVisible = state;
    }

    public boolean ammdLantern() {
        return this.ammdLantern;
    }

    public boolean ammdTorch() {
        return this.ammdTorch;
    }

    public boolean ammdCandleHolder() {
        return this.ammdCandleHolder;
    }

    void setAmmdLantern(boolean state) {
        this.ammdLantern = state;
    }

    void setAmmdTorch(boolean state) {
        this.ammdTorch = state;
    }

    void setAmmdCandleHolder(boolean state) {
        this.ammdCandleHolder = state;
    }

    public boolean watutIdleAnimation() {
        return this.watutIdleAnimation;
    }

    void setWatutIdleAnimation(boolean state) {
        this.watutIdleAnimation = state;
    }

    public boolean watutTypingAnimation() {
        return this.watutTypingAnimation;
    }

    void setWatutTypingAnimation(boolean state) {
        this.watutTypingAnimation = state;
    }

    public boolean watutTypingTextBox() {
        return this.watutTypingTextBox;
    }

    void setWatutTypingTextBox(boolean state) {
        this.watutTypingTextBox = state;
    }

    public boolean createSkyhookAnimation() {
        return this.createSkyhookAnimation;
    }

    void setCreateSkyhookAnimation(boolean state) {
        this.createSkyhookAnimation = state;
    }

    public boolean createCardboardBox() {
        return this.createCardboardBox;
    }

    void setCreateCardboardBox(boolean state) {
        this.createCardboardBox = state;
    }

    private final Set<String> offhandPlaceLock = new HashSet<>();

    public boolean isOffhandPlaceLocked(String itemId) {
        return this.offhandPlaceLock.contains(itemId);
    }

    void setOffhandPlaceLock(Collection<String> itemIds) {
        this.offhandPlaceLock.clear();
        if (itemIds != null) {
            this.offhandPlaceLock.addAll(itemIds);
        }
    }
}
