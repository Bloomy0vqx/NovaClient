package com.novaclient.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ColorSetting extends Setting {
    private int red, green, blue, alpha;
    private final int defaultColor;
    private boolean rainbow;

    public ColorSetting(String name, String description, int r, int g, int b, int a) {
        super(name, description);
        this.red = r;
        this.green = g;
        this.blue = b;
        this.alpha = a;
        this.defaultColor = (a << 24) | (r << 16) | (g << 8) | b;
        this.rainbow = false;
    }

    public ColorSetting(String name, String description, int r, int g, int b) {
        this(name, description, r, g, b, 255);
    }

    public int getRed() { return red; }
    public int getGreen() { return green; }
    public int getBlue() { return blue; }
    public int getAlpha() { return alpha; }

    public void setRed(int red) { this.red = clamp(red); }
    public void setGreen(int green) { this.green = clamp(green); }
    public void setBlue(int blue) { this.blue = clamp(blue); }
    public void setAlpha(int alpha) { this.alpha = clamp(alpha); }

    public void setColor(int r, int g, int b, int a) {
        this.red = clamp(r);
        this.green = clamp(g);
        this.blue = clamp(b);
        this.alpha = clamp(a);
    }

    public int toInt() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public boolean isRainbow() { return rainbow; }
    public void setRainbow(boolean rainbow) { this.rainbow = rainbow; }

    @Override
    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("r", red);
        obj.addProperty("g", green);
        obj.addProperty("b", blue);
        obj.addProperty("a", alpha);
        obj.addProperty("rainbow", rainbow);
        return obj;
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            this.red = obj.get("r").getAsInt();
            this.green = obj.get("g").getAsInt();
            this.blue = obj.get("b").getAsInt();
            this.alpha = obj.has("a") ? obj.get("a").getAsInt() : 255;
            this.rainbow = obj.has("rainbow") && obj.get("rainbow").getAsBoolean();
        }
    }

    private int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }
}
