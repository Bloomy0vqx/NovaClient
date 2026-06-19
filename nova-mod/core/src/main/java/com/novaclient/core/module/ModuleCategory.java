package com.novaclient.core.module;

public enum ModuleCategory {
    PVP("PvP", "\u2694"),
    HUD("HUD", "\u25A3"),
    VISUAL("Visual", "\u2606"),
    UTILITY("Utility", "\u2699"),
    SERVER("Server", "\u25B6");

    private final String displayName;
    private final String icon;

    ModuleCategory(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
}
