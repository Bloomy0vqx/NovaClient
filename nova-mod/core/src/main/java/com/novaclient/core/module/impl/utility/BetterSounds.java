package com.novaclient.core.module.impl.utility;

import com.novaclient.core.module.Module;
import com.novaclient.core.module.ModuleCategory;
import com.novaclient.core.module.setting.NumberSetting;

public class BetterSounds extends Module {
    private final NumberSetting volume = new NumberSetting("Volume", "Sound volume multiplier", 1.0, 0.0, 3.0, 0.1);
    private final NumberSetting pitch = new NumberSetting("Pitch", "Pitch adjustment", 1.0, 0.5, 2.0, 0.05);

    public BetterSounds() {
        super("Better Sounds", "Improved sound system", ModuleCategory.UTILITY, 0);
        addSetting(volume);
        addSetting(pitch);
    }
}
