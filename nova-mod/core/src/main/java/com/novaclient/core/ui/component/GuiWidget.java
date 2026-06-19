package com.novaclient.core.ui.component;

import com.novaclient.core.platform.Platform;

public abstract class GuiWidget {
    protected int x, y, width, height;
    protected boolean visible = true;
    protected boolean hovered;

    public GuiWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(float mouseX, float mouseY, float partialTicks);
    public void mouseClicked(float mouseX, float mouseY, int button) {}
    public void mouseReleased(float mouseX, float mouseY, int button) {}
    public void mouseScrolled(float mouseX, float mouseY, float delta) {}
    public void keyTyped(char typedChar, int keyCode) {}

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public boolean isHovered() { return hovered; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setVisible(boolean visible) { this.visible = visible; }

    protected void fill(int left, int top, int right, int bottom, int color) {
        if (Platform.isInitialized()) {
            Platform.get().drawRect(left, top, right, bottom, color);
        }
    }

    protected void drawText(String text, float x, float y, int color) {
        if (Platform.isInitialized()) {
            Platform.get().drawString(text, x, y, color, false);
        }
    }

    protected int getStringWidth(String text) {
        if (Platform.isInitialized()) {
            return Platform.get().getStringWidth(text);
        }
        return text.length() * 6;
    }

    protected void drawPixelatedRoundedRect(int x, int y, int w, int h, int color) {
        fill(x + 1, y, x + w - 1, y + h, color);
        fill(x, y + 1, x + 1, y + h - 1, color);
        fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    protected void drawOutline(int left, int top, int right, int bottom, int color) {
        fill(left, top, right, top + 1, color);
        fill(left, bottom - 1, right, bottom, color);
        fill(left, top, left + 1, bottom, color);
        fill(right - 1, top, right, bottom, color);
    }
}
