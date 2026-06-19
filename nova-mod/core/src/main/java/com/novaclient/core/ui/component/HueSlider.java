package com.novaclient.core.ui.component;

import java.awt.Color;
import java.util.function.Consumer;

public class HueSlider extends GuiWidget {
    private double value;
    private Consumer<Integer> onColorChange;
    private boolean dragging;

    private static final int SLIDER_WIDTH = 60;
    private static final int SLIDER_BORDER_IDLE = 0xFFCCCCCC;
    private static final int SLIDER_BORDER_HOVER = 0xFF000000;
    private static final int THUMB_COLOR = 0xFF000000;

    public HueSlider(int x, int y, int width, int height, double defaultValue, Consumer<Integer> onColorChange) {
        super(x, y, width, height);
        this.value = defaultValue;
        this.onColorChange = onColorChange;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        hovered = isHovered(mouseX, mouseY);
        int sliderX = x + width - SLIDER_WIDTH;
        int centerY = y + height / 2;

        int borderColor = hovered || dragging ? SLIDER_BORDER_HOVER : SLIDER_BORDER_IDLE;
        drawPixelatedRoundedRect(sliderX, centerY - 2, SLIDER_WIDTH, 4, borderColor);

        int grayWidth = (int)(SLIDER_WIDTH * 0.1);
        for (int i = 0; i < grayWidth; i++) {
            float brightness = (float) i / grayWidth;
            int gray = (int)(brightness * 255);
            int color = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
            fill(sliderX + i, centerY - 1, sliderX + i + 1, centerY + 1, color);
        }

        int rainbowWidth = SLIDER_WIDTH - grayWidth;
        for (int i = 0; i < rainbowWidth; i++) {
            float hue = (float) i / rainbowWidth;
            int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            fill(sliderX + grayWidth + i, centerY - 1, sliderX + grayWidth + i + 1, centerY + 1, color | 0xFF000000);
        }

        int thumbSize = 8;
        int thumbX = sliderX + (int)(value * (SLIDER_WIDTH - thumbSize));
        drawPixelatedRoundedRect(thumbX, centerY - thumbSize / 2, thumbSize, thumbSize, THUMB_COLOR);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (button == 0) {
            int sliderX = x + width - SLIDER_WIDTH;
            int centerY = y + height / 2;
            if (mouseX >= sliderX && mouseX <= sliderX + SLIDER_WIDTH && mouseY >= centerY - 8 && mouseY <= centerY + 8) {
                dragging = true;
                updateValue(mouseX);
            }
        }
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int button) {
        if (button == 0) { dragging = false; }
    }

    private void updateValue(double mouseX) {
        int sliderX = x + width - SLIDER_WIDTH;
        this.value = Math.max(0, Math.min(1, (mouseX - sliderX) / SLIDER_WIDTH));
        if (onColorChange != null) {
            int color;
            if (value < 0.1) {
                float brightness = (float)(value / 0.1);
                color = Color.HSBtoRGB(0.0f, 0.0f, brightness);
            } else {
                float hue = (float)((value - 0.1) / 0.9);
                color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            }
            onColorChange.accept(color);
        }
    }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = Math.max(0, Math.min(1, value)); }
}
