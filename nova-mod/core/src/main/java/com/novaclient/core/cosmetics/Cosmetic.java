package com.novaclient.core.cosmetics;

public class Cosmetic {
    private final String id;
    private final String name;
    private final CosmeticType type;
    private final String description;
    private boolean animated;
    private String textureUrl;
    private int rarity;

    public Cosmetic(String id, String name, CosmeticType type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.animated = false;
        this.rarity = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public CosmeticType getType() { return type; }
    public String getDescription() { return description; }
    public boolean isAnimated() { return animated; }
    public void setAnimated(boolean animated) { this.animated = animated; }
    public String getTextureUrl() { return textureUrl; }
    public void setTextureUrl(String url) { this.textureUrl = url; }
    public int getRarity() { return rarity; }
    public void setRarity(int rarity) { this.rarity = rarity; }
}
