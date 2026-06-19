package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class PotionEffects extends Module {
    private final ModeSetting displayMode = new ModeSetting("Mode", "Display mode", "List", "Compact");
    private final BooleanSetting showDuration = new BooleanSetting("Duration", "Show remaining duration", true);
    private final BooleanSetting showLevel = new BooleanSetting("Level", "Show potion level", true);
    private final ColorSetting bgColor = new ColorSetting("Background", "Background color", 0, 0, 0, 150);

    public PotionEffects() {
        super("Potion Effects", "Displays active potion effects", ModuleCategory.PVP, 0);
        addSetting(displayMode);
        addSetting(showDuration);
        addSetting(showLevel);
        addSetting(bgColor);
    }
}
