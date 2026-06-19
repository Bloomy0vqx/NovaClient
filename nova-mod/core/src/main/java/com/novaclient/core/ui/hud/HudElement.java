package com.novaclient.core.ui.hud;

import com.novaclient.core.ui.render.ColorUtil;

public abstract class HudElement {
    protected float x, y;
    protected float width, height;
    protected boolean visible = true;
    protected boolean dragging = false;
    protected float dragOffsetX, dragOffsetY;
    protected int bgColor = ColorUtil.withAlpha(0x000000, 100);
    protected int textColor = 0xFFFFFFFF;
    protected float scale = 1.0f;

    public HudElement(String name, float x, float y) {
        this.x = x;
        this.y = y;
    }

    public abstract String getName();
    public abstract void render(float partialTicks);
    public abstract void update();

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = scale; }

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void startDrag(float mouseX, float mouseY) {
        dragging = true;
        dragOffsetX = mouseX - x;
        dragOffsetY = mouseY - y;
    }

    public void drag(float mouseX, float mouseY) {
        if (dragging) {
            x = mouseX - dragOffsetX;
            y = mouseY - dragOffsetY;
        }
    }

    public void stopDrag() {
        dragging = false;
    }

    public boolean isDragging() {
        return dragging;
    }
}
