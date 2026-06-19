package com.novaclient.core.ui.component;

import com.novaclient.core.platform.Platform;

public class DarkEditBox extends GuiWidget {
    private String text = "";
    private String placeholder;
    private boolean focused;
    private int cursorPos;
    private int maxLength = 256;

    private static final int BG_BORDER = 0xFFCCCCCC;
    private static final int BG_BORDER_FOCUSED = 0xFF000000;
    private static final int BG_FILL = 0xFFFFFFFF;
    private static final int TEXT_COLOR = 0xFF000000;
    private static final int PLACEHOLDER_COLOR = 0xFFAAAAAA;
    private static final int CURSOR_COLOR = 0xFF000000;

    public DarkEditBox(int x, int y, int width, int height, String placeholder) {
        super(x, y, width, height);
        this.placeholder = placeholder;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        int borderColor = focused ? BG_BORDER_FOCUSED : BG_BORDER;
        drawPixelatedRoundedRect(x, y, width, height, borderColor);
        drawPixelatedRoundedRect(x + 1, y + 1, width - 2, height - 2, BG_FILL);

        int padding = 4;
        int textX = x + padding;
        int textY = y + (height - 8) / 2;

        if (Platform.isInitialized()) {
            Platform.get().enableScissor(x + 1, y + 1, x + width - 1, y + height - 1);
        }

        if (text.isEmpty() && !focused) {
            drawText(placeholder, textX, textY, PLACEHOLDER_COLOR);
        } else {
            drawText(text, textX, textY, TEXT_COLOR);
            if (focused && System.currentTimeMillis() / 500 % 2 == 0) {
                int cursorX = textX + getStringWidth(text);
                fill(cursorX, textY, cursorX + 1, textY + 9, CURSOR_COLOR);
            }
        }

        if (Platform.isInitialized()) {
            Platform.get().disableScissor();
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0) { focused = isHovered(mouseX, mouseY); }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!focused) return;
        if (keyCode == 14) {
            if (!text.isEmpty() && cursorPos > 0) {
                text = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
                cursorPos--;
            }
        } else if (keyCode == 211 || keyCode == 212) {
            if (cursorPos < text.length()) {
                text = text.substring(0, cursorPos) + text.substring(cursorPos + 1);
            }
        } else if (keyCode == 203) {
            cursorPos = Math.max(0, cursorPos - 1);
        } else if (keyCode == 205) {
            cursorPos = Math.min(text.length(), cursorPos + 1);
        } else if (keyCode == 199) {
            cursorPos = 0;
        } else if (keyCode == 207) {
            cursorPos = text.length();
        } else if (typedChar != 0 && typedChar != 27 && typedChar != '\n' && typedChar != '\r') {
            if (text.length() < maxLength) {
                text = text.substring(0, cursorPos) + typedChar + text.substring(cursorPos);
                cursorPos++;
            }
        }
    }

    public String getValue() { return text; }
    public void setValue(String text) { this.text = text; this.cursorPos = text.length(); }
    public boolean isFocused() { return focused; }
    public void setFocused(boolean focused) { this.focused = focused; }
    public void setMaxLength(int maxLength) { this.maxLength = maxLength; }
    public void clear() { text = ""; cursorPos = 0; }
}
