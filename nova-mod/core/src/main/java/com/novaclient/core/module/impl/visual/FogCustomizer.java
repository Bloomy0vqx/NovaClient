package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class FogCustomizer extends Module {
    private final ModeSetting fogMode = new ModeSetting("Mode", "Fog mode", "Custom", "Remove");
    private final NumberSetting fogDistance = new NumberSetting("Distance", "Fog distance", 50.0, 10.0, 256.0, 5.0);
    private final NumberSetting fogDensity = new NumberSetting("Density", "Fog density", 1.0, 0.0, 3.0, 0.1);
    private final ColorSetting fogColor = new ColorSetting("Color", "Fog color", 180, 200, 255, 255);

    public FogCustomizer() {
        super("Fog Customizer", "Customizes fog rendering", ModuleCategory.VISUAL, 0);
        addSetting(fogMode);
        addSetting(fogDistance);
        addSetting(fogDensity);
        addSetting(fogColor);
    }
}
