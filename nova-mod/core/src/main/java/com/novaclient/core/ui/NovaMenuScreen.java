package com.novaclient.core.ui;

import com.novaclient.core.platform.Platform;
import com.novaclient.core.ui.render.ColorUtil;
import com.novaclient.core.ui.render.RenderUtil;
import com.novaclient.core.ui.render.AnimationUtil;
import java.util.ArrayList;
import java.util.List;

public class NovaMenuScreen {
    private float width, height;
    private float animProgress = 0;
    private float bgAnimOffset = 0;

    // White/gray/black color scheme
    private static final int BG_COLOR = 0xFFF8F8F8;
    private static final int BUTTON_BG = 0xFF000000;
    private static final int BUTTON_HOVER = 0xFF222222;
    private static final int BUTTON_BORDER = 0xFF000000;
    private static final int BUTTON_HOVER_BORDER = 0xFF000000;
    private static final int TEXT_WHITE = 0xFFFFFFFF;
    private static final int TEXT_DIM = 0xFF666666;
    private static final int TEXT_MUTED = 0xFF999999;
    private static final int ACCENT = 0xFF000000;
    private static final int HOVER_HIGHLIGHT = 0x22FFFFFF;

    private final List<MenuButton> buttons = new ArrayList<>();

    public NovaMenuScreen() {
        buttons.add(new MenuButton("Singleplayer", "Play singleplayer worlds"));
        buttons.add(new MenuButton("Multiplayer", "Join multiplayer servers"));
        buttons.add(new MenuButton("Realms", "Minecraft Realms"));
        buttons.add(new MenuButton("Mods", "Nova Client mod menu"));
        buttons.add(new MenuButton("Cosmetics", "Customize your character"));
        buttons.add(new MenuButton("Settings", "Client settings"));
        buttons.add(new MenuButton("Account", "Manage your accounts"));
    }

    public void init(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void render(float mouseX, float mouseY, float partialTicks) {
        animProgress = Math.min(1, animProgress + 0.04f);
        bgAnimOffset += 0.3f;

        RenderUtil.drawRect(0, 0, width, height, BG_COLOR);
        renderBackground();

        float logoY = height * 0.12f;
        float logoScale = AnimationUtil.easeOutBack(animProgress);
        int logoAlpha = (int)(255 * animProgress);
        drawText("Nova Client", width / 2 - 55, logoY, ColorUtil.withAlpha(TEXT_WHITE, logoAlpha), 1.8f);
        drawText("v1.0.0", width / 2 - 12, logoY + 28, ColorUtil.withAlpha(TEXT_DIM, (int)(200 * animProgress)), 0.8f);

        float buttonWidth = 240;
        float buttonHeight = 38;
        float buttonSpacing = 5;
        float startY = height * 0.30f;

        for (int i = 0; i < buttons.size(); i++) {
            MenuButton btn = buttons.get(i);
            float btnX = width / 2 - buttonWidth / 2;
            float btnY = startY + i * (buttonHeight + buttonSpacing);

            float delay = i * 0.06f;
            float btnAnim = AnimationUtil.clamp((animProgress - delay) * 2.5f, 0, 1);
            btnAnim = AnimationUtil.easeOutCubic(btnAnim);

            btn.x = btnX;
            btn.y = btnY;
            btn.width = buttonWidth;
            btn.height = buttonHeight;

            boolean hovered = mouseX >= btnX && mouseX <= btnX + buttonWidth
                && mouseY >= btnY && mouseY <= btnY + buttonHeight;
            btn.hovered = hovered;

            float animBtnY = btnY + (1 - btnAnim) * 15;

            int borderColor = hovered ? BUTTON_HOVER_BORDER : BUTTON_BORDER;
            int bgColor = hovered ? BUTTON_HOVER : BUTTON_BG;

            drawPixelatedRoundedRect((int) btnX, (int) animBtnY, (int) buttonWidth, (int) buttonHeight, borderColor);
            drawPixelatedRoundedRect((int) btnX + 1, (int) animBtnY + 1, (int) buttonWidth - 2, (int) buttonHeight - 2, bgColor);

            if (hovered) {
                fill((int) btnX + 2, (int) animBtnY + 1, (int) btnX + (int) buttonWidth - 2, (int) animBtnY + 2, HOVER_HIGHLIGHT);
            }

            drawText(btn.label, btnX + 16, animBtnY + 11, ColorUtil.withAlpha(TEXT_WHITE, (int)(255 * animProgress)));
            drawText(btn.description, btnX + 16, animBtnY + 24, ColorUtil.withAlpha(TEXT_DIM, (int)(180 * animProgress)));
        }

        renderBottomIcons(mouseX, mouseY);
    }

    private void renderBackground() {
        for (int i = 0; i < 25; i++) {
            float lineY = (i * 35 + bgAnimOffset) % height;
            int alpha = (int)(8 + Math.sin(i * 0.4 + bgAnimOffset * 0.008) * 6);
            fill(0, (int) lineY, (int) width, (int) lineY + 1, ColorUtil.withAlpha(ACCENT, alpha));
        }
    }

    private void renderBottomIcons(float mouseX, float mouseY) {
        String[] icons = {"Mods", "Cosmetics", "Social", "Settings", "Language"};
        float iconWidth = 60;
        float totalWidth = icons.length * iconWidth;
        float startX = width / 2 - totalWidth / 2;
        float iconY = height - 36;

        for (int i = 0; i < icons.length; i++) {
            float iconX = startX + i * iconWidth;
            boolean hovered = mouseX >= iconX && mouseX <= iconX + iconWidth
                && mouseY >= iconY && mouseY <= iconY + 28;
            int color = hovered ? ColorUtil.withAlpha(ACCENT, 255) : ColorUtil.withAlpha(TEXT_DIM, 180);
            drawText(icons[i], iconX + 6, iconY + 8, color);
        }
    }

    public int mouseClicked(float mouseX, float mouseY, int button) {
        if (button != 0) return -1;
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton btn = buttons.get(i);
            if (mouseX >= btn.x && mouseX <= btn.x + btn.width
                && mouseY >= btn.y && mouseY <= btn.y + btn.height) {
                return i;
            }
        }
        return -1;
    }

    public void reset() {
        animProgress = 0;
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

    private void drawText(String text, float x, float y, int color, float scale) {
        if (Platform.isInitialized()) {
            Platform.get().drawString(text, x, y, color, false);
        }
    }

    private static class MenuButton {
        String label;
        String description;
        float x, y, width, height;
        boolean hovered;

        MenuButton(String label, String description) {
            this.label = label;
            this.description = description;
        }
    }
}
