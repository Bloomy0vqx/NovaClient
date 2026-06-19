package com.novaclient.core.module.impl.hud;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class DirectionHUD extends Module {
    private final BooleanSetting showAngle = new BooleanSetting("Angle", "Show exact angle", true);
    private final BooleanSetting showDirection = new BooleanSetting("Direction", "Show cardinal direction", true);
    private final ColorSetting textColor = new ColorSetting("Color", "Text color", 255, 255, 255, 255);
    private final ColorSetting bgColor = new ColorSetting("Background", "Background color", 0, 0, 0, 100);

    public DirectionHUD() {
        super("Direction HUD", "Shows the direction you are facing", ModuleCategory.HUD, 0);
        addSetting(showAngle);
        addSetting(showDirection);
        addSetting(textColor);
        addSetting(bgColor);
    }
}
