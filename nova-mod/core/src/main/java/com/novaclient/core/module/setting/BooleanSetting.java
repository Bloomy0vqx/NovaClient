package com.novaclient.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanSetting extends Setting {
    private boolean value;
    private final boolean defaultValue;

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public boolean isEnabled() { return value; }
    public void setEnabled(boolean value) { this.value = value; }
    public void toggle() { this.value = !this.value; }
    public boolean getDefaultValue() { return defaultValue; }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            this.value = element.getAsBoolean();
        }
    }
}
