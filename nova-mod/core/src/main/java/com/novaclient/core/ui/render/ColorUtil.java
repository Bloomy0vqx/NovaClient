package com.novaclient.core.ui.render;

import java.awt.Color;

public class ColorUtil {
    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    public static int getRed(int color) { return (color >> 16) & 0xFF; }
    public static int getGreen(int color) { return (color >> 8) & 0xFF; }
    public static int getBlue(int color) { return color & 0xFF; }
    public static int getAlpha(int color) { return (color >> 24) & 0xFF; }

    public static int interpolate(int start, int end, float progress) {
        float a = getAlpha(start) / 255.0f;
        float r = getRed(start) / 255.0f;
        float g = getGreen(start) / 255.0f;
        float b = getBlue(start) / 255.0f;

        float ea = getAlpha(end) / 255.0f;
        float er = getRed(end) / 255.0f;
        float eg = getGreen(end) / 255.0f;
        float eb = getBlue(end) / 255.0f;

        float fa = a + (ea - a) * progress;
        float fr = r + (er - r) * progress;
        float fg = g + (eg - g) * progress;
        float fb = b + (eb - b) * progress;

        return ((int)(fa * 255) << 24) | ((int)(fr * 255) << 16) | ((int)(fg * 255) << 8) | (int)(fb * 255);
    }

    public static int rainbow(int offset, float saturation, float brightness) {
        float hue = (System.currentTimeMillis() + offset) % 3000L / 3000.0f;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static int fade(int color, int index, int speed, int offset) {
        float a = getAlpha(color) / 255.0f;
        float percent = (float) ((System.currentTimeMillis() + offset) % (speed * 2L)) / speed;
        if (percent > 1) percent = 2 - percent;
        a = (a * percent);
        return withAlpha(color, (int)(a * 255));
    }

    public static int withRainbow(int alpha) {
        float hue = (System.currentTimeMillis() % 3000L) / 3000.0f;
        return withAlpha(java.awt.Color.HSBtoRGB(hue, 0.8f, 1.0f), alpha);
    }
}
