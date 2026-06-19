package com.novaclient.core.cosmetics;

import java.util.*;

public class PlayerCosmetics {
    private final UUID playerUUID;
    private final List<String> equippedCosmetics;
    private long lastUpdated;

    public PlayerCosmetics(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.equippedCosmetics = new ArrayList<>();
        this.lastUpdated = System.currentTimeMillis();
    }

    public UUID getPlayerUUID() { return playerUUID; }
    public List<String> getEquippedCosmetics() { return Collections.unmodifiableList(equippedCosmetics); }
    public long getLastUpdated() { return lastUpdated; }

    public void equip(String cosmeticId) {
        if (!equippedCosmetics.contains(cosmeticId)) {
            equippedCosmetics.add(cosmeticId);
            lastUpdated = System.currentTimeMillis();
        }
    }

    public void unequip(String cosmeticId) {
        equippedCosmetics.remove(cosmeticId);
        lastUpdated = System.currentTimeMillis();
    }

    public boolean isEquipped(String cosmeticId) {
        return equippedCosmetics.contains(cosmeticId);
    }

    public void clear() {
        equippedCosmetics.clear();
        lastUpdated = System.currentTimeMillis();
    }
}
