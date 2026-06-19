package com.novaclient.core.nametag;

import com.novaclient.core.platform.Platform;
import com.novaclient.core.platform.PlatformAdapter;
import com.novaclient.core.ui.render.RenderUtil;
import com.novaclient.core.ui.render.ColorUtil;
import com.novaclient.core.cosmetics.CosmeticEngine;
import com.novaclient.core.cosmetics.CustomCape;

import java.io.File;

public class NametagRenderer {
    private static final NametagRenderer INSTANCE = new NametagRenderer();
    private boolean enabled = true;
    private float scale = 1.0f;
    private boolean showHealth = true;
    private boolean showArmor = true;
    private boolean showPotion = true;
    private boolean showDistance = true;
    private boolean showLogo = true;
    private int bgColor = ColorUtil.withAlpha(0x000000, 150);
    private int textColor = 0xFFFFFFFF;
    private String logoTexturePath = null;

    public static NametagRenderer getInstance() {
        return INSTANCE;
    }

    public void init() {}

    public void setLogoTexture(String path) {
        this.logoTexturePath = path;
    }

    public String getLogoTexture() {
        return logoTexturePath;
    }

    public void renderNametag(String playerName, float x, float y, float z) {
        if (!enabled) return;
        PlatformAdapter pa = Platform.get();
        if (pa == null) return;

        int textWidth = RenderUtil.getStringWidth(playerName);
        int textHeight = RenderUtil.getStringHeight();
        int padding = 4;
        int logoSize = showLogo ? textHeight + 2 : 0;
        int totalWidth = textWidth + padding * 2 + logoSize;
        int totalHeight = textHeight + padding * 2;

        float bgLeft = x - (totalWidth / 2.0f);
        float bgTop = y - totalHeight - 2;
        float bgRight = x + (totalWidth / 2.0f);
        float bgBottom = y - 2;

        RenderUtil.drawRoundedRect(bgLeft, bgTop, bgRight, bgBottom, bgColor, 4.0f);

        float textX = x - (textWidth / 2.0f) + (showLogo ? logoSize / 2.0f : 0);
        float textY = bgTop + padding;
        RenderUtil.drawString(playerName, textX, textY, textColor, true);

        if (showLogo && logoTexturePath != null && !logoTexturePath.isEmpty()) {
            float logoX = bgLeft + padding;
            float logoY = bgTop + padding - 1;
            float size = textHeight;
            pa.drawTexture(logoTexturePath, logoX, logoY, size, size);
        }

        if (showHealth || showArmor || showPotion || showDistance) {
            int infoY = (int) bgBottom + 2;
            StringBuilder info = new StringBuilder();
            if (showHealth) info.append("\u2764");
            if (showArmor) info.append(" \u2694");
            if (showPotion) info.append(" \u2697");
            if (showDistance) info.append(" \u2194");
            if (info.length() > 0) {
                RenderUtil.drawCenteredString(info.toString().trim(), x, infoY, ColorUtil.withAlpha(0xCCCCCC, 200), true);
            }
        }
    }

    public void renderNametag(String playerName, float x, float y, float z, float health, float maxHealth) {
        if (!enabled) return;
        PlatformAdapter pa = Platform.get();
        if (pa == null) return;

        int textWidth = RenderUtil.getStringWidth(playerName);
        int textHeight = RenderUtil.getStringHeight();
        int padding = 4;
        int logoSize = showLogo ? textHeight + 2 : 0;
        int totalWidth = textWidth + padding * 2 + logoSize;
        int totalHeight = textHeight + padding * 2;

        float bgLeft = x - (totalWidth / 2.0f);
        float bgTop = y - totalHeight - 2;
        float bgRight = x + (totalWidth / 2.0f);
        float bgBottom = y - 2;

        RenderUtil.drawRoundedRect(bgLeft, bgTop, bgRight, bgBottom, bgColor, 4.0f);

        float textX = x - (textWidth / 2.0f) + (showLogo ? logoSize / 2.0f : 0);
        float textY = bgTop + padding;
        RenderUtil.drawString(playerName, textX, textY, textColor, true);

        if (showLogo && logoTexturePath != null && !logoTexturePath.isEmpty()) {
            float logoX = bgLeft + padding;
            float logoY = bgTop + padding - 1;
            float size = textHeight;
            pa.drawTexture(logoTexturePath, logoX, logoY, size, size);
        }

        if (showHealth) {
            float healthPercent = Math.max(0, Math.min(1, health / maxHealth));
            int healthColor = healthPercent > 0.5f ? 0xFF00CC00 : healthPercent > 0.25f ? 0xFFCCCC00 : 0xFFCC0000;
            String healthText = String.valueOf((int) health);
            int healthWidth = RenderUtil.getStringWidth(healthText);
            RenderUtil.drawString(healthText, bgRight + 3, bgTop + (totalHeight - textHeight) / 2, healthColor, true);
        }

        int infoY = (int) bgBottom + 2;
        StringBuilder info = new StringBuilder();
        if (showArmor) info.append("\u2694");
        if (showPotion) info.append(" \u2697");
        if (showDistance) info.append(" \u2194");
        if (info.length() > 0) {
            RenderUtil.drawCenteredString(info.toString().trim(), x, infoY, ColorUtil.withAlpha(0xCCCCCC, 200), true);
        }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = scale; }
    public boolean isShowHealth() { return showHealth; }
    public void setShowHealth(boolean show) { this.showHealth = show; }
    public boolean isShowArmor() { return showArmor; }
    public void setShowArmor(boolean show) { this.showArmor = show; }
    public boolean isShowPotion() { return showPotion; }
    public void setShowPotion(boolean show) { this.showPotion = show; }
    public boolean isShowDistance() { return showDistance; }
    public void setShowDistance(boolean show) { this.showDistance = show; }
    public boolean isShowLogo() { return showLogo; }
    public void setShowLogo(boolean show) { this.showLogo = show; }
    public int getBgColor() { return bgColor; }
    public void setBgColor(int color) { this.bgColor = color; }
    public int getTextColor() { return textColor; }
    public void setTextColor(int color) { this.textColor = color; }
}
