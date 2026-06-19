package com.novaclient.core.module.impl.pvp;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;
import com.novaclient.core.module.setting.ColorSetting;
import com.novaclient.core.module.setting.BooleanSetting;

public class TNTCountdown extends Module {
    private final NumberSetting scale = new NumberSetting("Scale", "Text scale", 1.0, 0.5, 3.0, 0.1);
    private final ColorSetting textColor = new ColorSetting("Text Color", "Countdown text color", 255, 100, 0, 255);
    private final BooleanSetting showOnEntity = new BooleanSetting("On Entity", "Show countdown on TNT entity", true);
    private final BooleanSetting showOnBlock = new BooleanSetting("On Block", "Show countdown on TNT block", true);

    public TNTCountdown() {
        super("TNT Countdown", "Shows countdown timer on primed TNT", ModuleCategory.PVP, 0);
        addSetting(scale);
        addSetting(textColor);
        addSetting(showOnEntity);
        addSetting(showOnBlock);
    }
}
