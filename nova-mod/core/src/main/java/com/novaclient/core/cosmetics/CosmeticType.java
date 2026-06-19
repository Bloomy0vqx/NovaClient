package com.novaclient.core.cosmetics;

public enum CosmeticType {
    CAPE("Cape", "Back cosmetic"),
    WINGS("Wings", "Wing cosmetic"),
    HAT("Hat", "Head cosmetic"),
    CLOAK("Cloak", "Back cosmetic"),
    BANDANA("Bandana", "Head cosmetic"),
    ACCESSORY("Accessory", "Special cosmetic");

    private final String displayName;
    private final String description;

    CosmeticType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
