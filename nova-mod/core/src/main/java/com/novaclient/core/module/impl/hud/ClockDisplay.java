package com.novaclient.core.module.impl.hud;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class ClockDisplay extends Module {
    private final ModeSetting timeFormat = new ModeSetting("Format", "Time format", "12 Hour", "24 Hour", "Military");
    private final ColorSetting textColor = new ColorSetting("Color", "Text color", 255, 255, 255, 255);
    private final ColorSetting bgColor = new ColorSetting("Background", "Background color", 0, 0, 0, 100);

    public ClockDisplay() {
        super("Clock", "Displays current time", ModuleCategory.HUD, 0);
        addSetting(timeFormat);
        addSetting(textColor);
        addSetting(bgColor);
    }
}
