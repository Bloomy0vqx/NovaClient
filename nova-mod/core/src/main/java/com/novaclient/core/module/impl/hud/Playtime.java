package com.novaclient.core.module.impl.hud;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class Playtime extends Module {
    private final ModeSetting format = new ModeSetting("Format", "Time format", "Hours:Minutes", "Hours:Minutes:Seconds", "Days:Hours");
    private final ColorSetting textColor = new ColorSetting("Color", "Text color", 255, 255, 255, 255);
    private final ColorSetting bgColor = new ColorSetting("Background", "Background color", 0, 0, 0, 100);

    public Playtime() {
        super("Playtime", "Shows total playtime", ModuleCategory.HUD, 0);
        addSetting(format);
        addSetting(textColor);
        addSetting(bgColor);
    }
}
