package com.novaclient.core.module.impl.hud;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class PingDisplay extends Module {
    private final BooleanSetting showAverage = new BooleanSetting("Average", "Show average ping", false);
    private final ColorSetting textColor = new ColorSetting("Color", "Text color", 255, 255, 255, 255);
    private final ColorSetting bgColor = new ColorSetting("Background", "Background color", 0, 0, 0, 100);

    public PingDisplay() {
        super("Ping", "Displays your current ping", ModuleCategory.HUD, 0);
        addSetting(showAverage);
        addSetting(textColor);
        addSetting(bgColor);
    }
}
