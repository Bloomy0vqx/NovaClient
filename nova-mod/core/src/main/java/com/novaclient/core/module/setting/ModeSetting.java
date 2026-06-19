package com.novaclient.core.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
    private int index;
    private final List<String> modes;
    private final String defaultMode;

    public ModeSetting(String name, String description, String... modes) {
        super(name, description);
        this.modes = Arrays.asList(modes);
        this.defaultMode = modes.length > 0 ? modes[0] : "";
        this.index = 0;
    }

    public String getMode() {
        return modes.get(index);
    }

    public void setMode(String mode) {
        int idx = modes.indexOf(mode);
        if (idx >= 0) this.index = idx;
    }

    public void cycle() {
        index = (index + 1) % modes.size();
    }

    public List<String> getModes() { return modes; }
    public int getIndex() { return index; }
    public String getDefaultMode() { return defaultMode; }

    public boolean is(String mode) {
        return getMode().equalsIgnoreCase(mode);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getMode());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            setMode(element.getAsString());
        }
    }
}
