package com.novaclient.core.module.impl.hud;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class MemoryUsage extends Module {
    private final BooleanSetting showBar = new BooleanSetting("Bar", "Show memory bar", true);
    private final ColorSetting textColor = new ColorSetting("Color", "Text color", 255, 255, 255, 255);
    private final ColorSetting barColor = new ColorSetting("Bar Color", "Memory bar color", 50, 200, 50, 255);
    private final ColorSetting bgColor = new ColorSetting("Background", "Background color", 0, 0, 0, 100);

    public MemoryUsage() {
        super("Memory Usage", "Displays memory usage", ModuleCategory.HUD, 0);
        addSetting(showBar);
        addSetting(textColor);
        addSetting(barColor);
        addSetting(bgColor);
    }
}
