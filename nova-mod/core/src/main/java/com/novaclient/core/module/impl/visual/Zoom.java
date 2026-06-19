package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class Zoom extends Module {
    private final NumberSetting zoomLevel = new NumberSetting("Level", "Zoom level", 4.0, 2.0, 20.0, 0.5);
    private final ModeSetting smooth = new ModeSetting("Smooth", "Smooth zoom transition", "Linear", "EaseOut", "EaseInOut");
    private final NumberSetting smoothSpeed = new NumberSetting("Speed", "Transition speed", 0.3, 0.05, 1.0, 0.05);

    public Zoom() {
        super("Zoom", "OptiFine-style zoom", ModuleCategory.VISUAL, 0);
        addSetting(zoomLevel);
        addSetting(smooth);
        addSetting(smoothSpeed);
    }

    public float getZoomLevel() { return zoomLevel.getFloatValue(); }
}
