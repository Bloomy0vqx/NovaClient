package com.novaclient.core.module.impl.visual;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ModeSetting;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.BooleanSetting;

public class CrosshairEditor extends Module {
    private final ModeSetting style = new ModeSetting("Style", "Crosshair style", "Default", "Circle", "Dot", "Triangle", "Custom");
    private final NumberSetting size = new NumberSetting("Size", "Crosshair size", 6.0, 2.0, 20.0, 1.0);
    private final NumberSetting gap = new NumberSetting("Gap", "Gap between lines", 2.0, 0.0, 10.0, 0.5);
    private final NumberSetting thickness = new NumberSetting("Thickness", "Line thickness", 1.5, 1.0, 5.0, 0.5);
    private final ColorSetting crosshairColor = new ColorSetting("Color", "Crosshair color", 255, 255, 255, 255);
    private final BooleanSetting dynamic = new BooleanSetting("Dynamic", "Dynamic crosshair (expands on action)", true);
    private final NumberSetting dynamicSize = new NumberSetting("Dynamic Size", "Expansion amount", 2.0, 0.5, 10.0, 0.5);
    private final BooleanSetting shadow = new BooleanSetting("Shadow", "Crosshair shadow", true);

    public CrosshairEditor() {
        super("Crosshair Editor", "Customizable crosshair", ModuleCategory.VISUAL, 0);
        addSetting(style);
        addSetting(size);
        addSetting(gap);
        addSetting(thickness);
        addSetting(crosshairColor);
        addSetting(dynamic);
        addSetting(dynamicSize);
        addSetting(shadow);
    }
}
