package com.novaclient.core.ui.component;

import java.util.function.Consumer;

public class DarkSlider extends GuiWidget {
    private double value;
    private double minValue;
    private double maxValue;
    private double step;
    private Consumer<Double> onValueChange;
    private String label;
    private boolean dragging;

    private static final int SLIDER_WIDTH = 60;
    private static final int LABEL_COLOR = 0xFF666666;
    private static final int SLIDER_TRACK = 0xFFDDDDDD;
    private static final int SLIDER_THUMB = 0xFF000000;
    private static final int SLIDER_BORDER_IDLE = 0xFFCCCCCC;
    private static final int SLIDER_BORDER_HOVER = 0xFF000000;

    public DarkSlider(int x, int y, int width, int height, String label, double value, double min, double max, double step, Consumer<Double> onValueChange) {
        super(x, y, width, height);
        this.value = value;
        this.minValue = min;
        this.maxValue = max;
        this.step = step;
        this.onValueChange = onValueChange;
        this.label = label;
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        hovered = isHovered(mouseX, mouseY);

        int sliderX = x + width - SLIDER_WIDTH;
        int centerY = y + height / 2;

        drawText(label, x, centerY - 4, LABEL_COLOR);

        int borderColor = hovered || dragging ? SLIDER_BORDER_HOVER : SLIDER_BORDER_IDLE;
        drawPixelatedRoundedRect(sliderX, centerY - 2, SLIDER_WIDTH, 4, borderColor);
        fill(sliderX + 1, centerY - 1, sliderX + SLIDER_WIDTH - 1, centerY + 1, SLIDER_TRACK);

        int thumbSize = 8;
        double normalized = (value - minValue) / (maxValue - minValue);
        int thumbX = sliderX + (int)(normalized * (SLIDER_WIDTH - thumbSize));

        drawPixelatedRoundedRect(thumbX, centerY - thumbSize / 2, thumbSize, thumbSize, SLIDER_THUMB);
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

    @Override
    public void mouseScrolled(float mouseX, float mouseY, float delta) {
        if (isHovered(mouseX, mouseY)) {
            double range = maxValue - minValue;
            double scrollAmount = range * 0.05 * delta;
            setValue(value + scrollAmount);
        }
    }

    private void updateValue(double mouseX) {
        int sliderX = x + width - SLIDER_WIDTH;
        double normalized = Math.max(0, Math.min(1, (mouseX - sliderX) / SLIDER_WIDTH));
        double rawValue = minValue + normalized * (maxValue - minValue);
        setValue(rawValue);
    }

    private void setValue(double rawValue) {
        if (step > 0) {
            this.value = Math.round(rawValue / step) * step;
        } else {
            this.value = rawValue;
        }
        this.value = Math.max(minValue, Math.min(maxValue, this.value));
        if (onValueChange != null) { onValueChange.accept(this.value); }
    }

    public double getValue() { return value; }
    public float getPercentage() { return (float)((value - minValue) / (maxValue - minValue)); }
    public String getLabel() { return label; }
    public boolean isDragging() { return dragging; }
}
