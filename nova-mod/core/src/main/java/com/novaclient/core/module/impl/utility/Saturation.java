package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.BooleanSetting;

public class Saturation extends Module {
    private final NumberSetting saturation = new NumberSetting("Saturation", "Saturation level", 20.0, 0.0, 20.0, 1.0);
    private final BooleanSetting showInHotbar = new BooleanSetting("Hotbar", "Show in hotbar", true);
    private final BooleanSetting showInHUD = new BooleanSetting("HUD", "Show saturation HUD", true);

    public Saturation() {
        super("Saturation", "Shows food saturation overlay", ModuleCategory.UTILITY, 0);
        addSetting(saturation);
        addSetting(showInHotbar);
        addSetting(showInHUD);
    }
}
