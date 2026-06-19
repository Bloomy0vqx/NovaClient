package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ModeSetting;

public class HitColor extends Module {
    private final ColorSetting hitColor = new ColorSetting("Hit Color", "Color when entity is hit", 255, 50, 50, 255);
    private final BooleanSetting rainbow = new BooleanSetting("Rainbow", "Rainbow hit color", false);
    private final ModeSetting mode = new ModeSetting("Mode", "Hit color mode", "Overlay", "Flash", "Fade");

    public HitColor() {
        super("Hit Color", "Changes entity color when hit", ModuleCategory.PVP, 0);
        addSetting(hitColor);
        addSetting(rainbow);
        addSetting(mode);
    }
}
