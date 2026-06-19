package com.novaclient.core.ui.component;

import com.novaclient.core.ui.render.AnimationUtil;

public class DarkToggleSwitch extends GuiWidget {
    private boolean enabled;
    private float animationPos;
    private boolean showLabel;
    private String label;
    private Runnable onToggle;

    private static final int BG_BORDER = 0xFFCCCCCC;
    private static final int BG_HOVER_BORDER = 0xFF000000;
    private static final int TRACK_OFF = 0xFFCCCCCC;
    private static final int TRACK_ON = 0xFF000000;
    private static final int THUMB_OFF = 0xFFFFFFFF;
    private static final int THUMB_ON = 0xFFFFFFFF;
    private static final int DIVIDER_OFF = 0xFFBBBBBB;
    private static final int DIVIDER_ON = 0xFF444444;
    private static final int LABEL_COLOR = 0xFF666666;

    private static final int TOGGLE_WIDTH = 40;

    public DarkToggleSwitch(int x, int y, int height, boolean initialState, String label, boolean showLabel, Runnable onToggle) {
        super(x, y, TOGGLE_WIDTH, height);
        this.enabled = initialState;
        this.animationPos = initialState ? 1.0f : 0.0f;
        this.showLabel = showLabel;
        this.label = label;
        this.onToggle = onToggle;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        hovered = isHovered(mouseX, mouseY);
        float target = enabled ? 1.0f : 0.0f;
        animationPos += (target - animationPos) * 0.3f;

        int toggleX = x;
        if (showLabel && label != null) {
            int labelWidth = getStringWidth(label);
            toggleX = x + labelWidth + 10;
            drawText(label, x, y + (height - 8) / 2, LABEL_COLOR);
        }

        int currentBorder = hovered ? BG_HOVER_BORDER : BG_BORDER;
        int currentTrack = lerpColor(animationPos, TRACK_OFF, TRACK_ON);
        int currentThumb = lerpColor(animationPos, THUMB_OFF, THUMB_ON);
        int currentDivider = lerpColor(animationPos, DIVIDER_OFF, DIVIDER_ON);

        drawPixelatedRoundedRect(toggleX, y, TOGGLE_WIDTH, height, currentBorder);
        drawPixelatedRoundedRect(toggleX + 1, y + 1, TOGGLE_WIDTH - 2, height - 2, currentTrack);

        int midX = toggleX + TOGGLE_WIDTH / 2;
        fill(midX, y + 4, midX + 1, y + height - 4, currentDivider);

        int thumbSize = height - 6;
        int trackMoveableWidth = TOGGLE_WIDTH - thumbSize - 6;
        int thumbX = toggleX + 3 + (int)(animationPos * trackMoveableWidth);

        drawPixelatedRoundedRect(thumbX, y + 3, thumbSize, thumbSize, currentThumb);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0 && isHovered(mouseX, mouseY)) {
            enabled = !enabled;
            if (onToggle != null) {
                onToggle.run();
            }
        }
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.animationPos = enabled ? 1.0f : 0.0f;
    }

    public void setShowLabel(boolean showLabel) { this.showLabel = showLabel; }

    private int lerpColor(float ratio, int color1, int color2) {
        int a1 = (color1 >> 24) & 0xFF, r1 = (color1 >> 16) & 0xFF, g1 = (color1 >> 8) & 0xFF, b1 = color1 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF, r2 = (color2 >> 16) & 0xFF, g2 = (color2 >> 8) & 0xFF, b2 = color2 & 0xFF;
        return ((int)(a1 + (a2 - a1) * ratio) << 24) |
               ((int)(r1 + (r2 - r1) * ratio) << 16) |
               ((int)(g1 + (g2 - g1) * ratio) << 8) |
               (int)(b1 + (b2 - b1) * ratio);
    }
}
