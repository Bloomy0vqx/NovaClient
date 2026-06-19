package com.novaclient.core.module.impl.hud;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ColorSetting;

public class FPSDisplay extends Module {
    private final ColorSetting textColor = new ColorSetting("Color", "Text color", 255, 255, 255, 255);
    private final ColorSetting bgColor = new ColorSetting("Background", "Background color", 0, 0, 0, 100);

    public FPSDisplay() {
        super("FPS", "Displays current frames per second", ModuleCategory.HUD, 0);
        addSetting(textColor);
        addSetting(bgColor);
    }
}
