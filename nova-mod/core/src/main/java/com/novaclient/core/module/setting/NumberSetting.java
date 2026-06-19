package com.novaclient.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NumberSetting extends Setting {
    private double value;
    private final double defaultValue;
    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, String description, double defaultValue, double min, double max, double step) {
        super(name, description);
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = defaultValue;
    }

    public double getValue() { return value; }
    public void setValue(double value) {
        this.value = Math.round(Math.max(min, Math.min(max, value)) / step) * step;
    }
    public int getIntValue() { return (int) value; }
    public float getFloatValue() { return (float) value; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }
    public double getDefaultValue() { return defaultValue; }

    public double getPercentage() {
        return (value - min) / (max - min);
    }

    @Override
    public JsonElement toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("value", value);
        return obj;
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonObject()) {
            this.value = element.getAsJsonObject().get("value").getAsDouble();
        }
    }
}
