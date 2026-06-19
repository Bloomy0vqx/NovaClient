package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.NumberSetting;

public class ShinyPots extends Module {
    private final ColorSetting tintColor = new ColorSetting("Tint", "Potion tint color", 255, 255, 255, 255);
    private final NumberSetting shineIntensity = new NumberSetting("Intensity", "Shine intensity", 1.5, 0.5, 3.0, 0.1);

    public ShinyPots() {
        super("Shiny Pots", "Makes potions shiny and colorful", ModuleCategory.VISUAL, 0);
        addSetting(tintColor);
        addSetting(shineIntensity);
    }
}
