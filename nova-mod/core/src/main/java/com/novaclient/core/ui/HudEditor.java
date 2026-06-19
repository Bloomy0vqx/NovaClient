package com.novaclient.core.ui;

import com.novaclient.core.platform.Platform;
import com.novaclient.core.ui.hud.HudManager;
import com.novaclient.core.ui.hud.HudElement;
import com.novaclient.core.ui.render.ColorUtil;

public class HudEditor {
    private boolean open = false;
    private float animProgress = 0;

    // White/gray/black color scheme
    private static final int TEXT_PRIMARY = 0xFF000000;
    private static final int TEXT_DIM = 0xFF666666;
    private static final int ACCENT = 0xFF000000;
    private static final int ACCENT_LIGHT = 0xFF333333;
    private static final int GRID_COLOR = 0xFFCCCCCC;
    private static final int PANEL_BG = 0xF0F8F8F8;
    private static final int PANEL_BORDER = 0xFF000000;

    public void toggle() {
        open = !open;
        if (!open) {
            animProgress = 0;
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void render(float mouseX, float mouseY, float partialTicks) {
        if (!open) return;

        animProgress = Math.min(1, animProgress + 0.1f);

        int gridAlpha = (int)(15 * animProgress);
        for (int gx = 0; gx < 3000; gx += 25) {
            fill(gx, 0, gx + 1, 3000, ColorUtil.withAlpha(GRID_COLOR, gridAlpha));
        }
        for (int gy = 0; gy < 3000; gy += 25) {
            fill(0, gy, 3000, gy + 1, ColorUtil.withAlpha(GRID_COLOR, gridAlpha));
        }

        HudManager.getInstance().renderAll(partialTicks);

        for (HudElement element : HudManager.getInstance().getElements()) {
            if (element.isVisible()) {
                boolean hovered = element.isHovered(mouseX, mouseY);
                int outlineColor = hovered ?
                    ColorUtil.withAlpha(ACCENT_LIGHT, (int)(220 * animProgress)) :
                    ColorUtil.withAlpha(ACCENT, (int)(80 * animProgress));

                drawOutline((int)(element.getX() - 2), (int)(element.getY() - 2),
                    (int)(element.getX() + element.getWidth() + 2),
                    (int)(element.getY() + element.getHeight() + 2),
                    outlineColor);
            }
        }

        // Info panel with Feather style
        drawPixelatedRoundedRect(8, 8, 340, 42, PANEL_BORDER);
        drawPixelatedRoundedRect(9, 9, 338, 40, ColorUtil.withAlpha(PANEL_BG, (int)(220 * animProgress)));
        drawText("HUD Editor", 16, 14, ColorUtil.withAlpha(TEXT_PRIMARY, (int)(255 * animProgress)));
        drawText("Drag elements to reposition | Right-click to toggle", 16, 28, ColorUtil.withAlpha(TEXT_DIM, (int)(180 * animProgress)));
    }

    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (!open) return;
        if (button == 0) {
            HudManager.getInstance().mouseClicked(mouseX, mouseY, button);
        } else if (button == 1) {
            for (HudElement element : HudManager.getInstance().getElements()) {
                if (element.isHovered(mouseX, mouseY)) {
                    element.setVisible(!element.isVisible());
                    return;
                }
            }
        }
    }

    public void mouseMoved(float mouseX, float mouseY) {
        if (!open) return;
        HudManager.getInstance().mouseMoved(mouseX, mouseY);
    }

    public void mouseReleased(int button) {
        if (!open) return;
        HudManager.getInstance().mouseReleased(button);
    }

    private void drawPixelatedRoundedRect(int x, int y, int w, int h, int color) {
        fill(x + 1, y, x + w - 1, y + h, color);
        fill(x, y + 1, x + 1, y + h - 1, color);
        fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    private void fill(int left, int top, int right, int bottom, int color) {
        if (Platform.isInitialized()) {
            Platform.get().drawRect(left, top, right, bottom, color);
        }
    }

    private void drawText(String text, float x, float y, int color) {
        if (Platform.isInitialized()) {
            Platform.get().drawString(text, x, y, color, false);
        }
    }

    private void drawOutline(int left, int top, int right, int bottom, int color) {
        fill(left, top, right, top + 1, color);
        fill(left, bottom - 1, right, bottom, color);
        fill(left, top, left + 1, bottom, color);
        fill(right - 1, top, right, bottom, color);
    }
}
