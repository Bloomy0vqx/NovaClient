package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.BooleanSetting;
import com.novaclient.core.module.setting.ColorSetting;

public class ItemCounter extends Module {
    private final BooleanSetting showCount = new BooleanSetting("Count", "Show item count", true);
    private final ColorSetting textColor = new ColorSetting("Color", "Count text color", 255, 255, 255, 255);

    public ItemCounter() {
        super("Item Counter", "Shows item count in inventory", ModuleCategory.UTILITY, 0);
        addSetting(showCount);
        addSetting(textColor);
    }
}
