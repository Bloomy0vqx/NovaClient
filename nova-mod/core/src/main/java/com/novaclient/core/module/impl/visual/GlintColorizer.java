package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class GlintColorizer extends Module {
    private final ColorSetting glintColor = new ColorSetting("Color", "Enchantment glint color", 100, 50, 255, 255);
    private final ModeSetting mode = new ModeSetting("Mode", "Color mode", "Static", "Rainbow", "Wave");
    private final ColorSetting color2 = new ColorSetting("Color 2", "Second color for wave mode", 255, 100, 50, 255);

    public GlintColorizer() {
        super("Glint Colorizer", "Changes enchantment glint color", ModuleCategory.VISUAL, 0);
        addSetting(glintColor);
        addSetting(mode);
        addSetting(color2);
    }
}
