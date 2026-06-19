package com.novaclient.core.module.impl.hud;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class Speedometer extends Module {
    private final ModeSetting unit = new ModeSetting("Unit", "Speed unit", "Blocks/sec", "km/h", "mph");
    private final ColorSetting textColor = new ColorSetting("Color", "Text color", 255, 255, 255, 255);
    private final ColorSetting bgColor = new ColorSetting("Background", "Background color", 0, 0, 0, 100);

    public Speedometer() {
        super("Speedometer", "Displays current movement speed", ModuleCategory.HUD, 0);
        addSetting(unit);
        addSetting(textColor);
        addSetting(bgColor);
    }
}
